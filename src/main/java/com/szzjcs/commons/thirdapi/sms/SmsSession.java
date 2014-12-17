package com.szzjcs.commons.thirdapi.sms;

import java.util.Date;

public class SmsSession
{
    private String mobile;
    private int expireTime; // 秒
    private String content;
    private Date createTime;
    private String sendMessage;

    public String getMobile()
    {
        return mobile;
    }

    public void setMobile(String mobile)
    {
        this.mobile = mobile;
    }

    public int getExpireTime()
    {
        return expireTime;
    }

    public void setExpireTime(int expireTime)
    {
        this.expireTime = expireTime;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public boolean isExpired()
    {
        return new Date().getTime() - createTime.getTime() > expireTime * 1000;
    }

    public int getRemainTime()
    {
        return (int) (expireTime - (new Date().getTime() - createTime.getTime()) / 1000);
    }

    public String getSendMessage() {
        return sendMessage;
    }

    public void setSendMessage(String sendMessage) {
        this.sendMessage = sendMessage;
    }

    @Override
    public String toString()
    {
        return "SmsSession [mobile=" + mobile + ",  expireTime=" + expireTime + ", content="
                + content + ", createTime=" + createTime + "]";
    }

}
