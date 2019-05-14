package common.core.enums;

import common.core.doc.Description;
import lombok.Getter;

/**
 * 系统异常信息
 *
 * @version 1.0
 * @auther chaoxy
 * @date 2019-05-13
 */
@Getter
@Description("系统异常信息")
public enum SysException {

  /** 默认成功码 */
  SUCCESS("000000", "默认成功码"),
  /** 默认失败码 */
  ERROR("999999", "默认失败码"),
  /** 系统异常 */
  SYSTEM_ERROR("999991", "系统异常"),
  /** 系统异常 */
  SYSTEM_BUSY("999992", "系统繁忙,请稍候再试"),
  /** 系统异常 */
  SYSTEM_NO_PERMISSION("999993", "无权限"),
  /** 系统异常 */
  GATEWAY_NOT_FOUND_SERVICE("999994", "服务未找到"),
  /** 系统异常 */
  GATEWAY_ERROR("999995", "网关异常"),
  /** 系统异常 */
  GATEWAY_CONNECT_TIME_OUT("999996", "网关超时"),
  /** 系统异常 */
  ARGUMENT_NOT_VALID("999997", "请求参数校验不通过"),
  /** 系统异常 */
  UPLOAD_FILE_SIZE_LIMIT("999998", "上传文件大小超过限制");

  /** 代码 */
  private String code;
  /** 描述 */
  private String desc;

  SysException(String code, String desc) {
    this.code = code;
    this.desc = desc;
  }
}
