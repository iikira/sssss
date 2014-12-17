package com.szzjcs.commons.thirdapi.push;

import java.util.Map;

public interface Provider
{
    /**
     * 发送通知到接入端
     * @param platform 发送对象平台
     * @param audience 接收信息对象, 参考tag和alias
     * @param alert 通知内容
     * @param ttl 消息离线保存的时间, 为0时不保存, 只发送在线用户. 单位秒
     * @param extras 自定义业务信息
     */
    void sendNotification(Platform platform, Audience audience, String alert, int ttl, Map<String, String> extras);

    void sendMessage(Platform platform, Audience audience, String title, String content, int ttl, Map<String, String> extras);
}
