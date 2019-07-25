package common.core.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * 基础异常
 *
 * @author chaoxy
 * @date 2019/05/13
 * @version 1.0
 */
@Getter
@Setter
public class BusException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** 错误码 */
    private String errorCode;
    /** 错误信息 */
    private String errorMsg;

    public BusException() {
        super();
    }

    public BusException(String errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public BusException(String errorMsg) {
        super(errorMsg);
        this.errorMsg = errorMsg;
    }

    public BusException(String errorMsg, Throwable cause) {
        super(errorMsg, cause);
        this.errorMsg = errorMsg;
    }
}
