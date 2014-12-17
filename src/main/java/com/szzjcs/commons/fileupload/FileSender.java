package com.szzjcs.commons.fileupload;

import java.io.File;
import java.util.List;

import com.szzjcs.commons.fileupload.sftp.SftpFileTransfer;
import com.szzjcs.commons.fileupload.util.FileSavingUtils;
import com.szzjcs.commons.util.LogicUtil;

/**
 * 文件发送器：把本地文件发送到远程图片服务器
 * 
 * @author: Hualong
 * @date 2014年1月3日 上午9:44:27
 */
public class FileSender
{

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
    public void openConnection()
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
     * 发送一个目录下的所有文件和文件夹到远程服务器上
     * 
     * @param localDir
     *        本地目录
     * @param remoteBaseDir
     *        远程服务器目录
     * @date: 2014年1月6日下午3:25:09
     */
    public void sendDir(File localDir, String remoteBaseDir)
    {
        sftpFileTransfer.uploadDir(localDir, remoteBaseDir);
    }

    /**
     * 发送单个文件
     * 
     * @param localBaseDir
     *        文件保存在本地的基础路径
     * @param localFileRelativePaths
     *        文件保存在本地的全路径出去基础路径的剩余部分列表
     * @param remoteBaseDirPath
     *        远程服务器保存文件的基础路径
     * @date: 2014年1月6日下午3:23:43
     */
    public void sendFile(String localBaseDir, String localFileRelativePath, String remoteBaseDirPath)
    {
        localBaseDir = FileSavingUtils.formatDirPath(localBaseDir);
        localFileRelativePath = FileSavingUtils.formatFilePath(localFileRelativePath);

        remoteBaseDirPath = FileSavingUtils.formatDirPath(remoteBaseDirPath);
        String remoteDirPath = FileSavingUtils.getDirPath(remoteBaseDirPath + localFileRelativePath);

        sftpFileTransfer.uploadFile(localBaseDir + localFileRelativePath, remoteDirPath);
    }

    /**
     * 发送多个文件
     * 
     * @param localBaseDir
     *        文件保存在本地的基础路径
     * @param localFileRelativePaths
     *        文件保存在本地的全路径出去基础路径的剩余部分列表
     * @param remoteBaseDir
     *        远程服务器保存文件的基础路径
     * @date: 2014年1月6日下午3:23:43
     */
    public void sendFiles(String localBaseDir, List<String> localFileRelativePaths, String remoteBaseDir)
    {
        if (LogicUtil.isNullOrEmpty(localFileRelativePaths))
        {
            return;
        }

        for (String localFileRelativePath : localFileRelativePaths)
        {
            sendFile(localBaseDir, localFileRelativePath, remoteBaseDir);
        }
    }

    public static void main(String[] args)
    {
        FileSender fd = new FileSender();
        fd.setSftpFileTransfer(new SftpFileTransfer("113.106.90.188", 22, "root", "ddpanico.,ltd@~!@#2505c", 5000));

        fd.openConnection();
        fd.sendDir(new File("D:/test1/f1"), "/usr/local/src/test/f1");
        fd.closeConnection();

        System.out.println("end");
    }
}
