package com.szzjcs.commons.thirdapi.qiniu;

import static com.google.common.base.Preconditions.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Strings;
import com.qiniu.api.auth.digest.Mac;
import com.qiniu.api.io.IoApi;
import com.qiniu.api.io.PutExtra;
import com.qiniu.api.io.PutRet;
import com.qiniu.api.rs.EntryPath;
import com.qiniu.api.rs.EntryPathPair;
import com.qiniu.api.rs.PutPolicy;
import com.qiniu.api.rs.RSClient;
import com.qiniu.api.rsf.ListItem;
import com.qiniu.api.rsf.ListPrefixRet;
import com.qiniu.api.rsf.RSFClient;
import com.szzjcs.commons.concurrent.poolutil.AsyncProcessManager;
import com.szzjcs.commons.json.JsonConverter;
import com.szzjcs.commons.net.http.HttpInvokeUtil;
import com.szzjcs.commons.thirdapi.qiniu.config.UploadConfig;
import com.szzjcs.commons.util.IdWorkerCreator;

/**
 * 资源上传, 下载, 操作工具类
 */
public class QiniuClient
{
    /**
     * 申请上传资源的凭证
     * 
     * @param directory
     *        资源要上传的目录
     * @param filename
     *        上传成功后最终服务器上的资源文件名
     */
    public static UploadToken getUploadToken(DirectoryEnum directory, String filename)
    {
        Mac mac = new Mac(UploadConfig.getAccessKey(), UploadConfig.getSecretKey());
        String bucket = getDirectoryName(directory);
        PutPolicy putPolicy = new PutPolicy(bucket);
        String uptoken;
        try
        {
            // 限制文件格式
            checkDirectoryMimeType(directory, putPolicy);

            putPolicy.expires = UploadConfig.getExpire_time();
            if (Strings.isNullOrEmpty(filename))
            {
                filename = IdWorkerCreator.nextId() + "";
            }
            putPolicy.saveKey = filename;
            uptoken = putPolicy.token(mac);

        }
        catch (Exception e)
        {
            throw new UploadException("getToken error, params: directory=" + directory, e);
        }
        UploadToken uploadToken = new UploadToken();
        uploadToken.setAccessDomain(UploadConfig.getUploadURL());
        uploadToken.setToken(uptoken);
        uploadToken.setExpire((int) putPolicy.expires);
        return uploadToken;

    }

    /**
     * <删除七牛上的图片>
     * 
     * @param directory
     * @param key
     */
    public static void deleteFile(final DirectoryEnum directory, final String key)
    {
        AsyncProcessManager.addTask(new Runnable()
        {
            @Override
            public void run()
            {
                Mac mac = new Mac(UploadConfig.getAccessKey(), UploadConfig.getSecretKey());
                RSClient client = new RSClient(mac);
                String bucket = getDirectoryName(directory);
                client.delete(bucket, key);
            }
        });
    }

    /**
     * <批量删除图片>
     * 
     * @param directory
     *        bucket
     * @param imgUrlList
     *        图片集合
     */
    public static void batchDelete(final DirectoryEnum directory, final List<String> imgUrlList)
    {
        AsyncProcessManager.addTask(new Runnable()
        {
            @Override
            public void run()
            {
                Mac mac = new Mac(UploadConfig.getAccessKey(), UploadConfig.getSecretKey());
                RSClient rs = new RSClient(mac);
                List<EntryPath> entries = new ArrayList<EntryPath>();

                for (String imgurl : imgUrlList)
                {
                    EntryPath ep = new EntryPath();
                    ep.bucket = getDirectoryName(directory);
                    ep.key = imgurl;
                    entries.add(ep);
                }
                rs.batchDelete(entries);
            }
        });
    }

    /**
     * <移动单个文件>
     * 
     * @param bucketSrc
     *        文件bucket
     * @param bucketDest
     *        目标bucket
     * @param imgurl
     *        文件名
     */
    public static void moveFile(final DirectoryEnum bucketSrc, final DirectoryEnum bucketDest, final String imgurl)
    {
        AsyncProcessManager.addTask(new Runnable()
        {
            @Override
            public void run()
            {
                Mac mac = new Mac(UploadConfig.getAccessKey(), UploadConfig.getSecretKey());
                RSClient client = new RSClient(mac);
                client.move(getDirectoryName(bucketSrc), imgurl, getDirectoryName(bucketDest), imgurl);
            }
        });
    }

    /**
     * <批量移动文件>
     * 
     * @param bucketSrc
     *        原bucket
     * @param destBucket
     *        目标bucket
     * @param imgUrlList
     *        要移动的文件集合
     */
    public static void batchMove(final DirectoryEnum bucketSrc, final DirectoryEnum destBucket,
            final List<String> imgUrlList)
    {
        AsyncProcessManager.addTask(new Runnable()
        {
            @Override
            public void run()
            {
                Mac mac = new Mac(UploadConfig.getAccessKey(), UploadConfig.getSecretKey());
                RSClient rs = new RSClient(mac);
                List<EntryPathPair> entries = new ArrayList<EntryPathPair>();
                for (String imgurl : imgUrlList)
                {
                    EntryPathPair pair = new EntryPathPair();
                    EntryPath src = new EntryPath();
                    src.bucket = getDirectoryName(bucketSrc);
                    src.key = imgurl;

                    EntryPath dest = new EntryPath();
                    dest.bucket = getDirectoryName(destBucket);
                    dest.key = imgurl;
                    pair.src = src;
                    pair.dest = dest;
                    entries.add(pair);
                }
                rs.batchMove(entries);
            }
        });
    }

    /**
     * <查询指定bucket 下所有文件>
     * 
     * @param bucket
     * @return List<ListItem>
     */
    public static List<ListItem> ListPrefix(final DirectoryEnum bucket)
    {
        Mac mac = new Mac(UploadConfig.getAccessKey(), UploadConfig.getSecretKey());
        RSFClient client = new RSFClient(mac);
        String marker = "";
        List<ListItem> all = new ArrayList<ListItem>();
        ListPrefixRet ret = null;
        while (true)
        {
            ret = client.listPrifix(getDirectoryName(bucket), "", marker, 10);
            marker = ret.marker;
            all.addAll(ret.results);
            if (!ret.ok())
            {
                break;
            }
        }
        return all;
    }

    // 限制上传文件的格式和大小
    private static void checkDirectoryMimeType(DirectoryEnum directory, PutPolicy putPolicy)
    {
        switch (directory)
        {
        case USER_PROFILE:
        case CIRCLE:
        case TICKET:
        case WEEKEND:
            putPolicy.mimeLimit = "image/jpeg;image/png;image/gif";
            putPolicy.fsizeLimit = 3145728;
            break;
        default:
            break;
        }
    }

    /**
     * 生成完整的七牛资源URL路径.注意请求的资源是公共的.
     * 
     * @param directory
     *        资源所在的目录
     * @param fileName
     *        资源的文件名
     * @param extend
     *        目前这个字段统一传null吧. 后续会有相关的指令处理
     * @return 完整的URL
     */
    public static String getPublicResourceURL(DirectoryEnum directory, String fileName, Object extend)
    {
        if (Strings.isNullOrEmpty(fileName))
        {
            return null;
        }
        String domain = getResourceDomain(directory);
        return domain.endsWith("/") ? domain + fileName : domain + "/" + fileName;

    }

    /**
     * 上传本地资源文件到七牛服务
     * 
     * @param directory
     *        七牛文件目录
     * @param localFilePath
     *        本地文件路径
     * @return 七牛文件最终名称
     */
    public static String uploadLocalFile(DirectoryEnum directory, String localFilePath)
    {
        String token = getUploadToken(directory, null).getToken();
        PutExtra putExtra = new PutExtra();
        PutRet ret = IoApi.putFile(token, null, localFilePath, putExtra);
        if (ret.ok())
        {
            return ret.getKey();
        }
        else
        {
            throw new RuntimeException("remoteUrl: " + localFilePath + "; error: " + ret.toString(), ret.getException());
        }
    }

    /**
     * 把标准URL指定的资源移动七牛存储上去
     * 
     * @param directory
     *        资源上传的空间名称
     * @param remoteUrl
     *        资源URL
     * @param filename
     *        上件七牛后的文件名
     * @return 返回七牛上传成功后的文件名
     */
    public static String uploadRemoteFile(DirectoryEnum directory, String remoteUrl, String filename)
    {
        String token = getUploadToken(directory, filename).getToken();
        PutExtra putExtra = new PutExtra();
        File tmpFile = null;
        PutRet ret;
        try
        {
            InputStream in = new URL(remoteUrl).openStream();
            tmpFile = copyToTmpFile(in);
            ret = IoApi.putFile(token, null, tmpFile, putExtra);
        }
        catch (IOException e)
        {
            throw new RuntimeException("upload qiniu error, url: " + remoteUrl, e);
        }
        finally
        {
            if (tmpFile != null)
            {
                try
                {
                    tmpFile.delete();
                }
                catch (Exception e)
                {
                }
            }
        }
        if (ret.ok())
        {
            return ret.getKey();
        }
        else
        {
            throw new RuntimeException("remoteUrl: " + remoteUrl + "; error: " + ret.toString(), ret.getException());
        }
    }

    public static AviInfo getAviInfo(String url)
    {
        checkNotNull(url);
        String str = HttpInvokeUtil.httpGet(url + "?avinfo", null);
        if (StringUtils.contains(str, "error"))
        {
            return null;
        }
        AviInfo aviInfo = JsonConverter.parse(str, AviInfo.class);
        return aviInfo;
    }

    private static String getResourceDomain(DirectoryEnum directory)
    {
        String resourceDomain = null;
        switch (directory)
        {
        case USER_PROFILE:
            resourceDomain = UploadConfig.getUser_domain();
            break;
        case TRAFFIC:
            resourceDomain = UploadConfig.getTraffic_domain();
            break;
        case WEEKEND:
            resourceDomain = UploadConfig.getWeekend_domain();
            break;
        case TICKET:
            resourceDomain = UploadConfig.getTicket_domain();
            break;
        case CIRCLE:
            resourceDomain = UploadConfig.getCircle_domain();
            break;
        case ADV:
            resourceDomain = UploadConfig.getAdv_domain();
            break;
        }
        return resourceDomain;
    }

    private static String getDirectoryName(DirectoryEnum directory)
    {
        String bucket = null;
        switch (directory)
        {
        case USER_PROFILE:
            bucket = UploadConfig.getUser_directory();
            break;
        case TRAFFIC:
            bucket = UploadConfig.getTraffic_directory();
            break;
        case WEEKEND:
            bucket = UploadConfig.getWeekend_directory();
            break;
        case TICKET:
            bucket = UploadConfig.getTicket_directory();
            break;
        case CIRCLE:
            bucket = UploadConfig.getCircle_directory();
            break;
        case ADV:
            bucket = UploadConfig.getAdv_directory();
            break;
        }
        return bucket;
    }

    private static File copyToTmpFile(InputStream from)
    {
        FileOutputStream os = null;
        try
        {
            File to = File.createTempFile("qiniu_", ".tmp");
            os = new FileOutputStream(to);
            byte[] b = new byte[64 * 1024];
            int l;
            while ((l = from.read(b)) != -1)
            {
                os.write(b, 0, l);
            }
            os.flush();
            return to;
        }
        catch (Exception e)
        {
            throw new RuntimeException("create tmp file failed.", e);
        }
        finally
        {
            if (os != null)
            {
                try
                {
                    os.close();
                }
                catch (Exception e)
                {
                }
            }
            if (from != null)
            {
                try
                {
                    from.close();
                }
                catch (Exception e)
                {
                }
            }
        }
    }

}
