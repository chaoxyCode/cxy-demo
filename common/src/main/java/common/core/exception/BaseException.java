package common.core.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * 基础异常
 *
 * @auther chaoxy
 * @date 2019-05-13
 * @version 1.0
 */
@Getter
@Setter
public class BaseException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /** 错误码 */
  private String errorCode;
  /** 错误信息 */
  private String errorMsg;

  public BaseException() {
    this.errorCode = "000000";
    this.errorMsg = "交易成功";
  }

  public BaseException(String errorCode, String errorMsg) {
    this.errorCode = errorCode;
    this.errorMsg = errorMsg;
  }

  public BaseException(String errorMsg) {
    super(errorMsg);
    this.errorMsg = errorMsg;
  }

  public BaseException(String errorMsg, Throwable cause) {
    super(errorMsg, cause);
    this.errorMsg = errorMsg;
  }
}
