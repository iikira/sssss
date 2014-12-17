package com.szzjcs.commons.thirdapi.sms;

/**
 * 短信业务异常
 */
public class SmsException extends RuntimeException
{
    public SmsException(String message, Exception exception)
    {
        super(message, exception);
    }
}
