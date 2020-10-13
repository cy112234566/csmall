package com.mall.promo.constants;

/**
 * @author cy
 * @date 2020/5/25 14:52
 */
public enum  PromoRetCode {
    // 系统公用
    SUCCESS                             ("000000", "成功"),
    REQUISITE_PARAMETER_NOT_EXIST       ("005073", "必要的参数不能为空"),

    PROMO_NOT_EXIST                     ("005074","秒杀活动不存在"),
    PROMO_ITEM_NOT_EXIST                ("005075","秒杀活动商品不存在"),
    PROMO_ITEM_STOCK_NOT_ENOUGH         ("005076","商品库存不足"),

    PIPELINE_RUN_EXCEPTION              ("005094","系统异常"),
    DB_SAVE_EXCEPTION                   ("005096","数据保存异常"),
    DB_EXCEPTION                        ("005097", "数据库异常"),
    SYSTEM_TIMEOUT                      ("005098", "系统超时"),
    SYSTEM_ERROR                        ("005099", "系统错误");
    private String code;
    private String message;

    PromoRetCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * Getter method for property <tt>code</tt>.
     *
     * @return property value of code
     */
    public String getCode() {
        return code;
    }

    /**
     * Setter method for property <tt>code</tt>.
     *
     * @param code value to be assigned to property code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Getter method for property <tt>message</tt>.
     *
     * @return property value of message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Setter method for property <tt>message</tt>.
     *
     * @param message value to be assigned to property message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    public static String getMessage(String code) {
        for (PromoRetCode s : PromoRetCode.values()) {
            if (null == code)
                break;
            if (s.code.equals(code)) {
                return s.message;
            }
        }
        return null;
    }
}