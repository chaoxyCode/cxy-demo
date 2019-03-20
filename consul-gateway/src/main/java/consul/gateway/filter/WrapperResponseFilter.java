package consul.gateway.filter;


import com.alibaba.fastjson.JSON;
import consul.gateway.common.CommonResponse;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;

/**
 * @desc: 统一报文返回格式的过滤器
 * @author: chaoxy
 * @date: 2019/03/20
 * @version: 1.0
 */
@Component
public class WrapperResponseFilter implements GlobalFilter, Ordered {

    Logger logger = LoggerFactory.getLogger(WrapperResponseFilter.class);

    @Override
    public int getOrder() {
        // -1 is response write filter, must be called before that
        return -2;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse originalResponse = exchange.getResponse();
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();
        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux) {
                    Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
                    return super.writeWith(fluxBody.map(dataBuffer -> {
                        // probably should reuse buffers
                        byte[] content = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(content);
                        // 释放掉内存
                        DataBufferUtils.release(dataBuffer);
                        String rs = new String(content, Charset.forName("UTF-8"));
                        logger.info("网关响应报文统一格式开始----");
                        logger.info("网关响应报文统一格式处理前响应信息：[{}]", rs);
                        CommonResponse response = new CommonResponse();
                        // TODO
                        response.setCode("000000");
                        response.setMsg("请求成功");
                        response.setData(rs);
                        String jsonStr = JSON.toJSONString(response);
                        logger.info("网关响应报文统一格式处理后响应信息：[{}]", jsonStr);
                        byte[] newRs = jsonStr.getBytes(Charset.forName("UTF-8"));
                        //如果不重新设置长度则收不到消息。
                        originalResponse.getHeaders().setContentLength(newRs.length);
                        logger.info("网关响应报文统一格式结束----");
                        return bufferFactory.wrap(newRs);
                    }));
                }
                // if body is not a flux. never got there.
                return super.writeWith(body);
            }
        };
        // replace response with decorator
        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }


}
