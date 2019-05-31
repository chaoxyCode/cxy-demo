package consul.gateway.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统一异常处理 参考{@link
 * org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler}
 *
 * @author chaoxy
 * @date 2019-03-23 15:08 @version 1.0
 */
public class CustomErrorWebExceptionHandler implements ErrorWebExceptionHandler {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(CustomErrorWebExceptionHandler.class);

  @Override
  public Mono<Void> handle(ServerWebExchange serverWebExchange, Throwable throwable) {
    // 按照异常类型进行处理
    HttpStatus httpStatus;
    String body;
    if (throwable instanceof NotFoundException) {
      httpStatus = HttpStatus.NOT_FOUND;
      body = "Service Not Found";
    } else if (throwable instanceof ResponseStatusException) {
      ResponseStatusException responseStatusException = (ResponseStatusException) throwable;
      httpStatus = responseStatusException.getStatus();
      body = responseStatusException.getMessage();
    } else {
      httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
      body = "Internal Server Error";
    }
    // TODO
    // 封装响应体,此body可修改为自己的jsonBody
    Map<String, Object> result = new HashMap<>(2, 1);
    result.put("httpStatus", httpStatus);

    String msg = "{\"code\":" + httpStatus + ",\"message\": \"" + body + "\"}";
    result.put("body", msg);
    // 错误记录
    ServerHttpRequest request = serverWebExchange.getRequest();
    LOGGER.error("[全局异常处理]异常请求路径:{},记录异常信息:{}", request.getPath(), throwable.getMessage());
    // 参考AbstractErrorWebExceptionHandler
    if (serverWebExchange.getResponse().isCommitted()) {
      return Mono.error(throwable);
    }
    exceptionHandlerResult.set(result);
    ServerRequest newRequest = ServerRequest.create(serverWebExchange, this.messageReaders);
    return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse)
        .route(newRequest)
        .switchIfEmpty(Mono.error(throwable))
        .flatMap((handler) -> handler.handle(newRequest))
        .flatMap((response) -> write(serverWebExchange, response));
  }

  /** MessageReader */
  private List<HttpMessageReader<?>> messageReaders = Collections.emptyList();

  /** MessageWriter */
  private List<HttpMessageWriter<?>> messageWriters = Collections.emptyList();

  /** ViewResolvers */
  private List<ViewResolver> viewResolvers = Collections.emptyList();

  /** 存储处理异常后的信息 */
  private ThreadLocal<Map<String, Object>> exceptionHandlerResult = new ThreadLocal<>();

  /** 参考AbstractErrorWebExceptionHandler */
  public void setMessageReaders(List<HttpMessageReader<?>> messageReaders) {
    Assert.notNull(messageReaders, "'messageReaders' must not be null");
    this.messageReaders = messageReaders;
  }

  /** 参考AbstractErrorWebExceptionHandler */
  public void setViewResolvers(List<ViewResolver> viewResolvers) {
    this.viewResolvers = viewResolvers;
  }

  /** 参考AbstractErrorWebExceptionHandler */
  public void setMessageWriters(List<HttpMessageWriter<?>> messageWriters) {
    Assert.notNull(messageWriters, "'messageWriters' must not be null");
    this.messageWriters = messageWriters;
  }

  /** 参考DefaultErrorWebExceptionHandler */
  protected Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
    Map<String, Object> result = exceptionHandlerResult.get();
    return ServerResponse.status((HttpStatus) result.get("httpStatus"))
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .body(BodyInserters.fromObject(result.get("body")));
  }

  /** 参考AbstractErrorWebExceptionHandler */
  private Mono<? extends Void> write(ServerWebExchange exchange, ServerResponse response) {
    exchange.getResponse().getHeaders().setContentType(response.headers().getContentType());
    return response.writeTo(exchange, new ResponseContext());
  }

  /** 参考AbstractErrorWebExceptionHandler */
  private class ResponseContext implements ServerResponse.Context {
    @Override
    public List<HttpMessageWriter<?>> messageWriters() {
      return CustomErrorWebExceptionHandler.this.messageWriters;
    }

    @Override
    public List<ViewResolver> viewResolvers() {
      return CustomErrorWebExceptionHandler.this.viewResolvers;
    }
  }
}
