package com.szzjcs.commons.thirdapi.sms.provider.iems;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import com.google.common.collect.Lists;
import com.szzjcs.commons.net.http.HttpInvokeUtil;
import com.szzjcs.commons.thirdapi.sms.Provider;
import com.szzjcs.commons.thirdapi.sms.SmsException;
import com.szzjcs.commons.thirdapi.sms.config.SmsConfig;

/**
 * 网关短息发送，作为slaver
 * 
 * @author smartlv
 */
public class IemsProvider implements Provider
{

    @Override
    public void send(String mobile, String content) throws SmsException
    {
        String url = SmsConfig.getIemsUrl();

        List<NameValuePair> params = Lists.newArrayListWithCapacity(30);
        params.add(new BasicNameValuePair("username", SmsConfig.getIemsUsrname()));
        params.add(new BasicNameValuePair("password", SmsConfig.getIemsPassword()));
        params.add(new BasicNameValuePair("from", ""));// 发短信的端口扩展号
        params.add(new BasicNameValuePair("to", mobile));// 接收短信的手机号 非空 支持多个(<=100)手机号码，中间以“,”分割
        params.add(new BasicNameValuePair("content", content));// 短信内容 不能为空(只支持GBK编码,若使用其它编码需要转换一下)

        HttpPost post = new HttpPost(url);
        UrlEncodedFormEntity entity = null;
        try
        {
            entity = new UrlEncodedFormEntity(params, "gbk");
            post.setEntity(entity);
            String resp = HttpInvokeUtil.httpPost(post, "gbk");// result=0&description=发送短信成功&taskid=22862872433
            if (!StringUtils.contains(resp, "OK:"))
            {
                throw new RuntimeException(resp);
            }
        }
        catch (Exception e)
        {
            throw new SmsException("send sms fail:" + mobile, e);
        }

    }

}
