package com.szzjcs.commons.fileupload.sftp;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.*;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.szzjcs.commons.fileupload.util.FileSavingUtils;
import com.szzjcs.commons.util.LogicUtil;

/**
 * 使用sftp文件传输器
 * 
 * @author: Hualong
 * @date 2013年12月31日 下午4:56:58
 */
public class SftpFileTransfer
{

    private final Logger log = LoggerFactory.getLogger(SftpFileTransfer.class);

    private String host;
    private int port = 22;
    private String username;
    private String password;
    private int timeout = 5000;

    private Session session;
    private ChannelSftp channel;

    /**
     * 创建一个文件传输对象，不获取连接
     */
    public SftpFileTransfer()
    {

    }

    /**
     * 创建一个文件传输对象，并且获取连接
     */
    public SftpFileTransfer(String host, int port, String username, String password, int timeout)
    {
        this.setHost(host);
        this.setPort(port);
        this.setUsername(username);
        this.setPassword(password);
        this.setTimeout(timeout);
    }

    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public int getTimeout()
    {
        return timeout;
    }

    public void setTimeout(int timeout)
    {
        this.timeout = timeout;
    }

    public Logger getLog()
    {
        return log;
    }

    /**
     * 获取连接
     * 
     * @param host
     *        主机ip
     * @param port
     *        主机端口
     * @param username
     *        用户名
     * @param password
     *        密码
     * @param timeout
     *        连接超时时间
     * @date: 2014年1月6日下午3:01:41
     */
    public void getConnection()
    {
        try
        {
            // 根据用户名，主机ip，端口获取一个Session对象
            JSch jsch = new JSch();
            session = jsch.getSession(username, host, port);
            log.debug("Session created.");
            if (LogicUtil.isNotNullAndEmpty(password))
            {
                session.setPassword(password); // 设置密码
            }
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config); // 为Session对象设置properties
            session.setTimeout(timeout); // 设置timeout时间
            session.connect(); // 通过Session建立链接
            log.debug("Session connected.");

            log.debug("Opening Channel.");
            channel = (ChannelSftp) session.openChannel("sftp"); // 打开SFTP通道
            channel.connect(); // 建立SFTP通道的连接
            log.debug("Connected successfully to ftpHost = " + host + ",as ftpUserName = " + username);
        }
        catch (JSchException e)
        {
            log.error("get connection error", e);
        }
    }

    /**
     * 断开连接
     */
    public void closeConnection()
    {
        if (LogicUtil.isNotNull(channel) && channel.isConnected())
        {
            channel.disconnect();
        }

        if (LogicUtil.isNotNull(session) && session.isConnected())
        {
            session.disconnect();
        }
    }

    /**
     * 上传单个文件
     * 
     * @param localFile
     *        待上传的文件
     * @param remoteDirPath
     *        文件存在远程服务器上的目录路径
     */
    public void uploadFile(File localFile, String remoteDirPath)
    {
        try
        {
            this.uploadFile(localFile.getCanonicalPath(), remoteDirPath, true);
        }
        catch (IOException e)
        {
            log.error("file upload fail!", e);
        }
    }

    /**
     * 上传单个文件
     * 
     * @param localFilePath
     *        待上传的文件路径
     * @param remoteDirPath
     *        文件存在远程服务器上的目录路径
     */
    public void uploadFile(String localFilePath, String remoteDirPath)
    {
        this.uploadFile(localFilePath, remoteDirPath, true);
    }

    /**
     * 上传单个文件
     * 
     * @param localFilePath
     *        待上传的文件路径
     * @param remotePath
     *        文件存在远程服务器上的目录或文件路径
     * @param isRemoteDir
     *        远程服务器上的路径是否为远程目录路径
     */
    public void uploadFile(String localFilePath, String remotePath, boolean isRemoteDir)
    {
        try
        {
            if (isRemoteDir)
            {
                createDir(remotePath);
            }
            else
            {
                createDir(FileSavingUtils.getDirPath(remotePath));
            }

            log.info("start file transfer");
            log.info("localFile:" + localFilePath);
            log.info("remoteFile:" + remotePath);
            this.channel.put(localFilePath, remotePath);
            // 这里远程路径不能为目录，只能为文件
            // this.channel.put(new FileInputStream(localFile),remotePath);
            log.info("upload remoteFile:" + remotePath);
        }
        catch (Exception e)
        {
            log.error("file upload fail!", e);
        }
    }

    /**
     * 批量上传指定目录下的所有文件
     * 
     * @param localDir
     *        待上传的目录
     * @param remoteDirPath
     *        远程存储文件的目录
     * @throws com.jcraft.jsch.SftpException
     */
    public void uploadDir(File localDir, String remoteDirPath)
    {
        try
        {
            if (localDir.isDirectory())
            {
                File[] fs = localDir.listFiles();
                if (LogicUtil.isNotNullAndEmpty(fs))
                {
                    for (File f : fs)
                    {

                        if (f.isFile())
                        {
                            uploadFile(f, remoteDirPath);
                        }
                        else
                        {
                            String newRemoteDirPath = FileSavingUtils.formatDirPath(remoteDirPath) + f.getName();
                            uploadDir(f, newRemoteDirPath);
                        }
                    }
                }
            }
            else
            {
                log.info("file or directory path not exists：" + localDir);
            }
        }
        catch (Exception e)
        {
            log.error("upload directory is error!", e);
        }
    }

    /**
     * 创建文件路径
     * 
     * @param dirPath
     * @throws com.jcraft.jsch.SftpException
     */
    private void createDir(String dirPath) throws SftpException
    {
        String[] tokens = dirPath.replace("\\", "/").split("/");
        tokens[1] = "/" + tokens[1];// 进入根目录需要加“/”

        for (int i = 1; i < tokens.length; i++)
        {
            if (LogicUtil.isNotNullAndEmpty(tokens[i]))
            {
                try
                {
                    channel.cd(tokens[i]);
                }
                catch (SftpException e)
                {
                    channel.mkdir(tokens[i]);
                    channel.cd(tokens[i]);
                }
            }
        }
    }

    /**
     * 删除单个文件
     * 
     * @param filePath
     *        要删除的文件路径
     * @return 成功为true，失败为false
     */
    public boolean deleteFile(String filePath)
    {
        try
        {
            channel.rm(filePath);
            return true;
        }
        catch (SftpException e)
        {
            log.error("Delete remote file error", e);
        }
        return false;
    }

    /**
     * 删除指定目录路径下的文件夹
     * 
     * @param dirPath
     *        目录路径
     * @return 成功为true，失败为false
     */
    @SuppressWarnings("rawtypes")
    public boolean deleteDir(String dirPath)
    {
        try
        {
            channel.cd(dirPath);
            Vector ls = channel.ls(channel.pwd());
            if (LogicUtil.isNotNullAndEmpty(ls))
            {
                for (int i = 0; i < ls.size(); i++)
                {

                    LsEntry f = (LsEntry) ls.get(i);
                    String nm = f.getFilename();
                    if (nm.equals(".") || nm.equals(".."))
                    {
                        continue;
                    }

                    if (f.getAttrs().isDir())
                    {
                        if (deleteDir(nm))
                        {
                            channel.rmdir(nm);
                        }
                    }
                    else
                    {
                        channel.rm(nm);
                    }
                }
            }
            return true;

        }
        catch (Exception e)
        {
            log.error("Delete remote directory error", e);
        }

        return false;
    }

    /**
     * 下载单个文件
     * 
     * @param remotePath
     *        要下载的文件路径
     * @param filePath
     *        指定文件存在路径
     */
    public void download(String remotePath, String filePath)
    {
        try
        {
            channel.get(remotePath, filePath);
        }
        catch (SftpException e)
        {
            log.error("download remote file error", e);
        }
    }

    /**
     * 判断某个相对路径的文件是否存在远程服务器上
     * 
     * @param baseRemoteDirPath
     *        远程服务器上的基础路径
     * @param fileRelativePath
     *        文件相对路径
     * @return 存在为true，不存在为false
     * @date: 2014年1月2日下午5:51:17
     */
    @SuppressWarnings("rawtypes")
    public boolean existFile(String baseRemoteDirPath, String fileRelativePath)
    {
        try
        {
            // 处理路径
            fileRelativePath = FileSavingUtils.formatFilePath(fileRelativePath);
            baseRemoteDirPath = FileSavingUtils.formatDirPath(baseRemoteDirPath);
            if (null != fileRelativePath && fileRelativePath.startsWith("./"))
            {
                fileRelativePath = fileRelativePath.substring(2);// 一如“./”开头的字符串
            }

            // 获取文件名称
            String fileName = FileSavingUtils.getFileName(fileRelativePath);

            // 验证文件是否存在远程服务器上
            Vector v = channel.ls(baseRemoteDirPath + fileRelativePath);
            for (Object res : v)
            {
                LsEntry f = (LsEntry) res;
                if (LogicUtil.isNotNullAndEmpty(fileName) && fileName.equals(f.getFilename()))
                {
                    return true;
                }
            }
        }
        catch (SftpException e)
        {
            log.error("Delete remote directory error", e);
        }

        return false;
    }

    /**
     * 获取远程文件的大小，如果不存在则为0字节
     * 
     * @param remoteBaseDirPath
     *        远程服务器上的基础路径
     * @param fileRelativePath
     *        文件相对路径
     * @return 返回远程文件的大小（单位：字节）
     * @date: 2014年1月3日上午9:33:26
     */
    @SuppressWarnings("rawtypes")
    public long getFileSize(String remoteBaseDirPath, String fileRelativePath)
    {
        try
        {
            // 处理路径
            fileRelativePath = FileSavingUtils.formatFilePath(fileRelativePath);
            remoteBaseDirPath = FileSavingUtils.formatDirPath(remoteBaseDirPath);
            if (LogicUtil.isNotNull(fileRelativePath) && fileRelativePath.startsWith("./"))
            {
                fileRelativePath = fileRelativePath.substring(2);// 一如“./”开头的字符串
            }

            // 获取文件名称
            String fileName = FileSavingUtils.getFileName(fileRelativePath);

            // 验证文件是否存在远程服务器上
            Vector v = channel.ls(remoteBaseDirPath + fileRelativePath);
            for (Object res : v)
            {
                LsEntry f = (LsEntry) res;
                if (LogicUtil.isNotNullAndEmpty(fileName) && fileName.equals(f.getFilename()))
                {
                    return f.getAttrs().getSize();
                }
            }
        }
        catch (SftpException e)
        {
            log.error("Delete remote directory error", e);
        }

        return 0;
    }

    public static void main(String[] args) throws SftpException
    {
        SftpFileTransfer sftp = new SftpFileTransfer("113.106.90.188", 22, "root", "ddpanico.,ltd@~!@#2505c", 5000);

        sftp.getConnection();
        // sftp.uploadFile("D:\\test1\\1.html",
        // "/usr/local/src/test/test/3.html");
        // sftp.uploadDir("D:\\test1\\", "/usr/local/src/test");
        // sftp.uploadFile(new File("D:\\test1\\1.html"),
        // "/usr/local/src/test");

        // boolean isExist = sftp.existFile("/usr/local/src/test", "1.html");
        // System.out.println("isExist:" + isExist);

        // boolean isDelete = sftp.deleteFile("/usr/local/src/test/12 3.txt");
        // System.out.println("isDelete:" + isDelete);

        // boolean isDelete = sftp.deleteDir("/usr/local/src/test/f1");
        // System.out.println("isDelete:" + isDelete);

        // sftp.download("/usr/local/src/test/s2.jpg", "d:/s2.jpg");

        long size = sftp.getFileSize("/usr/local/src/test", "1.html");
        System.out.println("size:" + size);

        sftp.closeConnection();

        // File f = new File("D:\\test1\\");
        // File[] ff = f.listFiles();
        // System.out.println(sftp.getParentDir(f, ff[3]));
        // sftp.createDir("/1/2/3//");

    }
}
