package com.szzjcs.commons.fileupload;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.szzjcs.commons.fileupload.sftp.SftpFileTransfer;
import com.szzjcs.commons.fileupload.util.FileSavingUtils;
import com.szzjcs.commons.util.LogicUtil;

/**
 * 文件删除器：删除远程服务器上的指定文件
 * 
 * @author: Hualong
 * @date 2014年1月3日 上午9:45:35
 */
public class FileDeleter
{
    private final Logger log = LoggerFactory.getLogger(FileResender.class);

    private SftpFileTransfer sftpFileTransfer;

    /**
     * @param sftpFileTransfer
     *        sftp文件传输器
     */
    public void setSftpFileTransfer(SftpFileTransfer sftpFileTransfer)
    {
        this.sftpFileTransfer = sftpFileTransfer;
    }

    /**
     * @return sftp文件传输器
     * @date: 2014年1月13日上午9:57:27
     */
    public SftpFileTransfer getSftpFileTransfer()
    {
        return sftpFileTransfer;
    }

    /**
     * 获取连接
     * 
     * @date: 2014年1月6日下午3:01:41
     */
    public void getConnection()
    {
        if (LogicUtil.isNotNull(sftpFileTransfer))
        {
            sftpFileTransfer.getConnection();
        }
    }

    /**
     * 关闭连接
     * 
     * @date: 2014年1月6日下午3:02:35
     */
    public void closeConnection()
    {
        if (LogicUtil.isNotNull(sftpFileTransfer))
        {
            sftpFileTransfer.closeConnection();
        }
    }

    /**
     * 是否需要删除指定文件（不含目录）
     * 
     * @param localBaseDirPath
     *        本地文件所在基本目录路径
     * @param localFileRelativePath
     *        本地文件相对路径
     * @param remoteBaseDirPath
     *        远程存储文件所在基本目录径路
     * @return 需要删除为true，无需为false
     * @date: 2014年1月3日上午11:21:03
     */
    private boolean isNeedDelete(String localBaseDirPath, String localFileRelativePath, String remoteBaseDirPath)
    {
        File localFile = new File(localBaseDirPath + localFileRelativePath);
        if (localFile.exists())
        {
            long localFileSize = localFile.length();
            long remoteFileSize = sftpFileTransfer.getFileSize(remoteBaseDirPath, localFileRelativePath);
            if (localFileSize == remoteFileSize)
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 删除指定本地文件
     * 
     * @param localBaseDirPath
     *        本地文件所在基本目录路径
     * @param localFileRelativePath
     *        本地文件相对路径
     * @param remoteBaseDirPath
     *        远程存储文件所在基本目录径路
     * @return 删除成功为true，失败为false
     * @date: 2014年1月3日上午11:22:41
     */
    public boolean deleteLocalFile(String localBaseDirPath, String localFileRelativePath, String remoteBaseDirPath)
    {
        // 格式化目录和文件路径
        localBaseDirPath = FileSavingUtils.formatDirPath(localBaseDirPath);
        localFileRelativePath = FileSavingUtils.formatFilePath(localFileRelativePath);
        remoteBaseDirPath = FileSavingUtils.formatDirPath(remoteBaseDirPath);

        // 删除本地路径
        if (isNeedDelete(localBaseDirPath, localFileRelativePath, remoteBaseDirPath))
        {
            return new File(localBaseDirPath + localFileRelativePath).delete();
        }

        return false;
    }

    /**
     * 删除远程服务器上某个给出相对路径的文件
     * 
     * @param remoteBaseDirPath
     *        远程服务器上的基本路径
     * @param fileRelativePath
     *        文件相对路径。
     * @return 删除成功为true，失败为false
     * @date: 2014年1月3日下午4:30:21
     */
    public boolean deleteRemoteFile(String remoteBaseDirPath, String fileRelativePath)
    {
        // 格式化目录和文件路径
        remoteBaseDirPath = FileSavingUtils.formatDirPath(remoteBaseDirPath);
        fileRelativePath = FileSavingUtils.formatFilePath(fileRelativePath);

        return this.sftpFileTransfer.deleteFile(remoteBaseDirPath + fileRelativePath);
    }

    /**
     * 删除指定本地目录
     * 
     * @param localDir
     * @param remoteDirPath
     * @return
     * @date: 2014年1月3日上午11:24:24
     */
    public void deleteLocalDir(File localDir, String remoteDirPath)
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
                        {// 如果是文件
                            deleteLocalFile(localDir.getCanonicalPath(), f.getName(), remoteDirPath);

                        }
                        else
                        {// 如果是目录
                            String newRemoteDirPath = FileSavingUtils.formatDirPath(remoteDirPath) + f.getName();
                            deleteLocalDir(f, newRemoteDirPath);
                        }
                    }
                }
                localDir.delete();// 删除目录本身
            }
        }
        catch (Exception e)
        {
            log.error("resend dir is error", e);
        }
    }

    public static void main(String[] args)
    {
        FileDeleter fd = new FileDeleter();

        fd.setSftpFileTransfer(new SftpFileTransfer("113.106.90.188", 22, "root", "ddpanico.,ltd@~!@#2505c", 5000));

        fd.getConnection();
        fd.deleteLocalDir(new File("D:/test1/f1"), "/usr/local/src/test/f1");
        fd.closeConnection();
        // System.out.println(new File("D:/test1/f1/1.txt").isFile());
    }
}
