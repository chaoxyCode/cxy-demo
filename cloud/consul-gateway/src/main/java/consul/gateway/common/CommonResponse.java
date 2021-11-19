package consul.gateway.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一报文返回格式
 *
 * @author chaoxy
 * @date 2019/03/20
 * @version 1.0
 */
@Data
public class CommonResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private String code;

    private String msg;

    private Object data;
}
