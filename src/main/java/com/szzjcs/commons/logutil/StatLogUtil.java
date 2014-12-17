package com.szzjcs.commons.logutil;

import static com.google.common.base.Preconditions.checkArgument;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatLogUtil
{
    private static final Logger LOGGER = LoggerFactory.getLogger("analytics");
    private static final String SEPRATOR = "|";
    private static final String EMPTY_STRING = "";

    public static void log(int code, Long user, Object target, HttpServletRequest request)
    {
        try
        {
            checkArgument(code > 0);
            StringBuilder sb = new StringBuilder();
            sb.append(code).append(SEPRATOR).append(user == null ? EMPTY_STRING : user).append(SEPRATOR)
                    .append(target == null ? EMPTY_STRING : target).append(SEPRATOR);
            if (request != null)
            {
                sb.append(request.getHeader("User-Agent"));
                sb.append(SEPRATOR);
                sb.append(request.getHeader("Referer"));
            }
            else
            {
                sb.append(EMPTY_STRING);
            }
            sb.append(SEPRATOR);

            LOGGER.info(sb.toString());
        }
        catch (Exception ex)
        {
            // do nothing
        }

    }

}
