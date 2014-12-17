package com.szzjcs.commons.conf;

import java.io.File;

import org.slf4j.Logger;

import com.szzjcs.commons.logutil.LogProxy;

class ProfileRoot
{
    private static Logger log = LogProxy.getLogger(ProfileRoot.class);
    private static final String AUTOCONF_DIR = "/autoconf";

    static String AUTOCONF_PATH = null;

    static void reInit()
    {
        boolean binit = false;
        try
        {
            // 尝试找到配置文件路径
            AUTOCONF_PATH = new File(ProfileRoot.class.getResource(ProfileRoot.AUTOCONF_DIR).toURI())
                    .getCanonicalPath();

            binit = ProfileRoot.isRightAutoconf(ProfileRoot.AUTOCONF_PATH);

            log.info("ConfigCenter client initialize the " + ProfileRoot.AUTOCONF_PATH + " to binit1=" + binit);
        }
        catch (Throwable e)
        {
            binit = false;
        }
        if (!binit)
        {
            System.out.println("ConfigCenter client reinitialize faile ! AUTOCONF_PATH = " + ProfileRoot.AUTOCONF_PATH);

        }
        System.out.println("ConfigCenter client reinitialize success ! AUTOCONF_PATH = " + ProfileRoot.AUTOCONF_PATH);
    }

    /**
     * 判断是正确的路径,防止出现形如 file:/usr/local/resin15/webapps/newcustomer/WEB-INF/lib/sutil
     * -1.0.0-SNAPSHOT.jar!/autoconf的路径创建,导致程序出错
     * 
     * @param confPath
     * @return
     */
    private static boolean isRightAutoconf(String confPath)
    {
        boolean isRight = false;
        if (confPath.endsWith(File.separatorChar + "WEB-INF" + File.separatorChar + "classes" + File.separatorChar
                + "autoconf")
                || confPath.endsWith(File.separatorChar + "classes" + File.separatorChar + "autoconf"))
        {
            isRight = true;
        }
        return isRight;
    }

}
