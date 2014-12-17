package com.szzjcs.commons.thirdapi.push;

public class PushException extends RuntimeException
{
    public PushException(String message, Exception exception)
    {
        super(message, exception);
    }
}
