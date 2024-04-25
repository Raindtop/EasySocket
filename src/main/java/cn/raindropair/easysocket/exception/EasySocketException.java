package cn.raindropair.easysocket.exception;

import org.springframework.http.HttpStatus;

public class EasySocketException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private int code = HttpStatus.INTERNAL_SERVER_ERROR.value();
    private String msg;

    public EasySocketException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public EasySocketException(String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
    }

    public EasySocketException(String msg, int code) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    public EasySocketException(String msg, int code, Throwable e) {
        super(msg, e);
        this.msg = msg;
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
