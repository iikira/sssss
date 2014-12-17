package com.szzjcs.commons.thirdapi.sms;

/**
 * 短信服务提供商统一服务接口定义
 */
public interface Provider
{
    /**
     * 给特定短信发送消息
     * @param mobile 接收短信手机号
     * @param content 短信内容
     * @throws SmsException
     */
    void send(String mobile, String content) throws SmsException;
}
