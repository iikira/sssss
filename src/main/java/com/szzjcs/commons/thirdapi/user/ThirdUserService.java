package com.szzjcs.commons.thirdapi.user;

/**
 * 第三方开放平台用户操作接口
 */
public interface ThirdUserService {
    /**
     * 根据token获得用户的唯一id
     * @param token
     * @return 用户id
     */
    String getUid(String token);

    /**
     * 根据uid查询用户详细信息
     * @return 用户详细信息
     */
    ThirdUser getUser(String token);


}
