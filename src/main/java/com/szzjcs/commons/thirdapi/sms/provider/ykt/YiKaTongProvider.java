package com.szzjcs.commons.thirdapi.sms.provider.ykt;

import org.apache.commons.lang.StringUtils;

import com.szzjcs.commons.crypto.MD5Coding;
import com.szzjcs.commons.thirdapi.sms.Provider;
import com.szzjcs.commons.thirdapi.sms.SmsException;
import com.szzjcs.commons.thirdapi.sms.config.SmsConfig;

/**
 * 一卡通短信服务提供商
 */
public class YiKaTongProvider implements Provider
{
    @Override
    public void send(String mobile, String content) throws SmsException
    {
        String usrname = SmsConfig.getYktUsrname();
        String mdrSalt = SmsConfig.getYktMdrSalt();
        String sign = SmsConfig.getYktSign();

        try
        {
            ISendWs ws = (new SenderService()).getSenderPort();
            String resultCode = ws.send(usrname, MD5Coding.encode2HexStr((content + mdrSalt).getBytes("UTF-8"))
                    .toLowerCase(), mobile, content, sign, "");
            System.out.println(resultCode);
            if (!StringUtils.startsWith(resultCode, "00"))
            {
                throw new RuntimeException(resultCode);
            }
        }
        catch (Exception e)
        {
            throw new SmsException("send sms fail:" + mobile, e);
        }
    }
}
