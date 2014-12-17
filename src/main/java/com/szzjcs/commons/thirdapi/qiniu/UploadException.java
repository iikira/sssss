package com.szzjcs.commons.thirdapi.qiniu;

public class UploadException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public UploadException(String msg, Exception e)
    {
        super(msg, e);
    }
}
