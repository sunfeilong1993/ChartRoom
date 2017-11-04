package com.sun.xiaotian.nioChatRoom.exception;

public class ChatRomException extends RuntimeException {

    public ChatRomException() {
    }

    public ChatRomException(String message) {
        super(message);
    }

    public ChatRomException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChatRomException(Throwable cause) {
        super(cause);
    }

    public ChatRomException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
