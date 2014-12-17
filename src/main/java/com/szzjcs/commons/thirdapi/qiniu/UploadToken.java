package com.szzjcs.commons.thirdapi.qiniu;

import java.io.Serializable;

public class UploadToken implements Serializable
{
    private static final long serialVersionUID = 8546974743484620279L;
    private String accessDomain; // 文件访问的域名前缀
    private String token; // 上传凭证
    private int expire; // 此token的有效时间,单位:秒

    public String getAccessDomain()
    {
        return accessDomain;
    }

    public void setAccessDomain(String accessDomain)
    {
        this.accessDomain = accessDomain;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    public int getExpire()
    {
        return expire;
    }

    public void setExpire(int expire)
    {
        this.expire = expire;
    }
}
