package com.szzjcs.commons.thirdapi.push.provider;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.springframework.util.CollectionUtils;

import cn.jpush.api.JPushClient;
import cn.jpush.api.common.APIRequestException;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.AudienceTarget;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;

import com.szzjcs.commons.json.JsonConverter;
import com.szzjcs.commons.logutil.LogProxy;
import com.szzjcs.commons.thirdapi.push.Audience;
import com.szzjcs.commons.thirdapi.push.Platform;
import com.szzjcs.commons.thirdapi.push.Provider;
import com.szzjcs.commons.thirdapi.push.PushException;
import com.szzjcs.commons.thirdapi.push.config.JpushConfig;

public class JPushProvider implements Provider
{
    private final JPushClient jPushClient;
    private final Logger log = LogProxy.getLogger(JPushProvider.class);

    public JPushProvider()
    {
        this.jPushClient = new JPushClient(JpushConfig.getSecret(), JpushConfig.getAppkey(), JpushConfig.getRetry());
    }

    @Override
    public void sendNotification(Platform platform, Audience audience, String alert, int ttl, Map<String, String> extras)
    {
        PushPayload.Builder builder = PushPayload.newBuilder();

        // 发送平台
        buildPlatform(platform, builder);

        // 通知内容
        Notification.Builder notifyBuilder = Notification.newBuilder()
                .addPlatformNotification(AndroidNotification.newBuilder().setAlert(alert).addExtras(extras).build())
                .addPlatformNotification(IosNotification.newBuilder().setAlert(alert).addExtras(extras).build());
        builder.setNotification(notifyBuilder.build());

        // 接收者
        buildAudience(audience, builder);

        // 通知离线保存时间与ios apns环境
        builder.setOptions(Options.newBuilder().setTimeToLive(ttl).setApnsProduction(JpushConfig.isApnsProduction())
                .build());

        try
        {
            jPushClient.sendPush(builder.build());
        }
        catch (APIRequestException e)
        {
            String msg = e.getMessage();
            JsonNode resjson = JsonConverter.parse(msg, JsonNode.class);
            String code = resjson.path("error").path("code").getValueAsText();
            if ("1011".equals(code))
            {
                log.error("code 1011 error:" + e.getErrorMessage());
            }
            else
            {
                StringBuilder esb = new StringBuilder();
                esb.append("platform:").append(platform).append(";audience:").append(audience).append(";alert:")
                        .append(alert).append(";extras").append(extras);
                throw new PushException(esb.toString(), e);
            }
        }
        catch (Exception e)
        {
            StringBuilder esb = new StringBuilder();
            esb.append("platform:").append(platform).append(";audience:").append(audience).append(";alert:")
                    .append(alert).append(";extras").append(extras);
            throw new PushException(esb.toString(), e);
        }

    }

    @Override
    public void sendMessage(Platform platform, Audience audience, String title, String content, int ttl,
            Map<String, String> extras)
    {
        PushPayload.Builder builder = PushPayload.newBuilder();
        // 发送平台
        buildPlatform(platform, builder);
        // 接收对象
        buildAudience(audience, builder);
        // 发送消息内容
        Message.Builder messageBuilder = Message.newBuilder();
        if (StringUtils.isNotBlank(title))
        {
            messageBuilder.setTitle(title);
        }
        messageBuilder.setMsgContent(content);
        // 发送extras
        if (!CollectionUtils.isEmpty(extras))
        {
            messageBuilder.addExtras(extras);
        }
        builder.setMessage(messageBuilder.build());

        // 通知离线保存时间与ios apns环境
        builder.setOptions(Options.newBuilder().setTimeToLive(ttl).setApnsProduction(JpushConfig.isApnsProduction())
                .build());

        try
        {
            jPushClient.sendPush(builder.build());
        }
        catch (Exception e)
        {
            StringBuilder esb = new StringBuilder();
            esb.append("platform:").append(platform).append(";audience:").append(audience).append(";title:")
                    .append(title).append(";content:").append(content).append(";extras").append(extras);
            throw new PushException(esb.toString(), e);
        }
    }

    private void buildPlatform(Platform platform, PushPayload.Builder builder)
    {
        switch (platform)
        {
        case ALL:
            builder.setPlatform(cn.jpush.api.push.model.Platform.all());
            break;
        case IOS:
            builder.setPlatform(cn.jpush.api.push.model.Platform.ios());
            break;
        case ANDROID:
            builder.setPlatform(cn.jpush.api.push.model.Platform.android());
            break;
        }
    }

    private void buildAudience(Audience audience, PushPayload.Builder builder)
    {
        cn.jpush.api.push.model.audience.Audience.Builder audienceBuilder = cn.jpush.api.push.model.audience.Audience
                .newBuilder();
        if (audience == null)
        {
            audienceBuilder.setAll(true);
        }
        else
        {
            if (!CollectionUtils.isEmpty(audience.getAndTags()))
            {
                audienceBuilder.addAudienceTarget(AudienceTarget.tag_and(audience.getAndTags()));
            }
            if (!CollectionUtils.isEmpty(audience.getOrTags()))
            {
                audienceBuilder.addAudienceTarget(AudienceTarget.tag(audience.getOrTags()));
            }
            if (!CollectionUtils.isEmpty(audience.getAlias()))
            {
                audienceBuilder.addAudienceTarget(AudienceTarget.alias(audience.getAlias()));
            }
        }
        builder.setAudience(audienceBuilder.build());
    }
}
