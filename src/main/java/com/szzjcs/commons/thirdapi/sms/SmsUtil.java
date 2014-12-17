package com.szzjcs.commons.thirdapi.sms;

import java.util.Date;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.szzjcs.commons.string.RandomUtil;
import org.slf4j.Logger;
import org.springframework.util.StringUtils;

import com.szzjcs.commons.json.JsonConverter;
import com.szzjcs.commons.logutil.LogProxy;
import com.szzjcs.commons.logutil.LogUtil;
import com.szzjcs.commons.redis.JedisProxy;

/**
 * 短信服务工具类
 */
public class SmsUtil
{
    private static final Logger log = LogProxy.getLogger(SmsUtil.class);

    /**
     * 发送短信
     * 
     * @param mobile
     *        手机号
     * @param content
     *        短信内容
     */
    public static void sendSMS(String mobile, String content)
    {
        Provider provider = ProviderFactory.getDefaultProvider();
        provider.send(mobile, content);
        log.info("send sms, mobile:{} content{}", mobile, content);
    }

    /**
     * 创建一个短信业务会话session, 并发送会话中指定的短信内容
     * 
     * @param session
     */
    public static void createSessionAndSendSMS(SmsSession session)
    {
        // 先发送短信
        sendSMS(session.getMobile(), session.getSendMessage());
        // 保存session信息
        session.setCreateTime(new Date());
        session.setSendMessage(null); // 这个东西不用存到cache里. 节约下.
        JedisProxy jedis = JedisProxy.getLRUCache();
        jedis.setex("sms" + session.getMobile(), session.getExpireTime(),
                JsonConverter.format(session));
    }

    /**
     * 根据业务代码type和手机号mobile获取当前有效的会话session
     * 
     * @param mobile
     *        手机号
     * @return 会话session
     */
    public static SmsSession getSession(String mobile)
    {
        JedisProxy jedis = JedisProxy.getLRUCache();
        String sessionJson = jedis.get("sms" + mobile);
        if (StringUtils.isEmpty(sessionJson))
        {
            return null;
        }
        return JsonConverter.parse(sessionJson, SmsSession.class);
    }

    /**
     * 验证手机接收到的内容是否和session在的一致
     * 
     * @param mobile
     *        手机号
     * @param content
     *        手机接收到的内容
     * @return true表示一致
     */
    public static boolean equalsContent(String mobile, String content)
    {
        if(Strings.isNullOrEmpty(content)){
            return false;
        }
        SmsSession smsSession = SmsUtil.getSession(mobile);
        if (smsSession == null || smsSession.isExpired() || !smsSession.getContent().equals(content))
        {
            return false;
        }
        return true;
    }
}
