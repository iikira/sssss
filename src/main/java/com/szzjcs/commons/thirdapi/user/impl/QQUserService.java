package com.szzjcs.commons.thirdapi.user.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Strings;
import com.szzjcs.commons.json.JsonConverter;
import com.szzjcs.commons.net.http.HttpInvokeUtil;
import com.szzjcs.commons.thirdapi.user.Config;
import com.szzjcs.commons.thirdapi.user.ThirdUser;
import com.szzjcs.commons.thirdapi.user.ThirdUserService;

/**
 * QQ
 */
public class QQUserService implements ThirdUserService
{
    private static final Pattern QQ_OPENID_REG = Pattern.compile("\"openid\":\"(\\w+)\"");

    @Override
    public String getUid(String token) {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", token);
        String resp = HttpInvokeUtil.httpGet(Config.getQq_get_uid_url(), params);
        Matcher matcher = QQ_OPENID_REG.matcher(resp);
        String openId = null;
        if (matcher.find())
        {
            openId = matcher.group(1);
        }
        if (StringUtils.isEmpty(openId))
        {
            throw new IllegalArgumentException("QQ token is invalid:" + token);
        }
        return openId;
    }

    @Override
    public ThirdUser getUser(String token) {
        String uid = getUid(token);
        Map<String, String> params = new HashMap<>();
        params.put("access_token", token);
        params.put("oauth_consumer_key", Config.getQq_openid());
        params.put("openid", uid);
        params.put("format", "json");
        String resp = HttpInvokeUtil.httpGet(Config.getQq_get_user_url(), params);
        try {

            QQUser user = JsonConverter.parse(resp, QQUser.class);
            ThirdUser _u = new ThirdUser();
            _u.setUid(uid);
            _u.setNickname(user.getNickname());
            String profileImg = user.getFigureurl_qq_2();
            if(Strings.isNullOrEmpty(profileImg)){
                profileImg = user.getFigureurl_qq_1();
            }
            _u.setProfileImg(profileImg);
            return _u;
        } catch (Exception e) {
            throw new IllegalArgumentException("QQ token is invalid:" + token + ", response: " + resp, e);
        }
    }
}
