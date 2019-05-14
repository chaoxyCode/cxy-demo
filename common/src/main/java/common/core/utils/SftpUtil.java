package common.core.utils;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * sftp连接工具类
 *
 * @auther chaoxy
 * @date 2019-05-13
 * @version 1.0
 */
public class SftpUtil {

  private static Logger log = LoggerFactory.getLogger(SftpUtil.class);

  /** 登录用户名 */
  private String userName;
  /** 登录密码 */
  private String passWord;
  /** 私钥 */
  private String privateKey;
  /** 服务器IP地址 */
  private String host;
  /** 端口 */
  private int port;

  /** sftp主服务 */
  private ChannelSftp sftp = null;

  /** 通道 */
  private Channel channel;

  /** 会话 */
  private Session session = null;

  /**
   * 构造sftp对象
   *
   * @param userName 登录用户名
   * @param passWord 登录密码
   * @param privateKey 私钥
   * @param host IP地址
   * @param port 端口
   */
  public SftpUtil(String userName, String passWord, String privateKey, String host, int port)
      throws Exception {
    this.userName = userName;
    this.passWord = passWord;
    this.privateKey = privateKey;
    this.host = host;
    this.port = port;
    init();
  }

  /**
   * 初始化连接
   *
   * @throws Exception
   */
  private void init() throws Exception {
    this.sftp = connect();
  }

  /**
   * 连接sftp服务器
   *
   * @return ChannelSftp
   * @throws Exception
   */
  private ChannelSftp connect() throws Exception {
    JSch jsch = new JSch();
    if (null != privateKey) {
      jsch.addIdentity(privateKey);
      log.info("SFTP connect by privateKey, path of private key file：{}", privateKey);
    }
    log.info("SFTP connect by host:{} username:{}", host, userName);
    session = jsch.getSession(userName, host, port);
    log.info("SFTP Session is build");
    if (null != passWord) {
      session.setPassword(passWord);
    }
    // 设置第一次登陆的时候提示，可选值:(ask | yes | no)
    session.setConfig("StrictHostKeyChecking", "no");
    session.connect();
    log.info("SFTP Session is connected");
    String type = "sftp";
    channel = session.openChannel(type);
    channel.connect();
    log.info("SFTP channel is connected");
    log.info("SFTP server host:{} port:{} is connect success ", host, port);
    return (ChannelSftp) channel;
  }

  /**
   * 获取文件绝对路径
   *
   * @param path 文件路径
   * @return String
   */
  public static String getAbsolutePath(String path) {
    if (StringUtil.isNullOrBlock(path)) {
      return null;
    }
    if (path.contains("\\")) {
      path = path.replaceAll("\\\\", "/");
    }
    if (!path.endsWith("/")) {
      path = path + "/";
    }
    return path;
  }

  /** 关闭连接 */
  public void disConnect() {
    if (null != this.sftp && this.sftp.isConnected()) {
      this.sftp.disconnect();
    }
    if (null != this.channel && this.channel.isConnected()) {
      this.channel.disconnect();
    }
    if (null != this.session && this.session.isConnected()) {
      this.session.disconnect();
    }
    log.info("sftp is already closed");
  }

  /**
   * 判断远程目录是否存在
   *
   * @param dir 远程目录
   * @return boolean
   */
  private boolean dirIsExist(String dir) {
    try {
      SftpATTRS attrs = this.sftp.lstat(dir);
      if (attrs.isDir()) {
        return true;
      } else {
        log.info("远程[{}] 不是目录！", dir);
      }
    } catch (Exception e) {
      log.error("远程目录[{}]不存在！", dir);
      log.error("判断远程目录是否存在, 异常：", e);
    }
    return false;
  }

  /**
   * 判断远程文件是否存在
   *
   * @param remoteFile 远程文件
   * @return boolean
   */
  private boolean fileIsExist(String remoteFile) {
    try {
      SftpATTRS attrs = this.sftp.lstat(remoteFile);
      if (attrs.isReg()) {
        return true;
      }
      log.info("远程[{}] 不是文件！", remoteFile);
    } catch (Exception e) {
      log.error("远程文件[{}] 不存在！", remoteFile);
      log.error("判断远程文件是否存在 异常：", e);
    }
    return false;
  }

  /**
   * 获取远程文件大小
   *
   * @param remoteFile 远程文件
   * @return long
   * @throws Exception
   */
  private long getFileSize(String remoteFile) throws Exception {
    try {
      SftpATTRS attrs = this.sftp.lstat(remoteFile);
      if (null == attrs || !attrs.isReg()) {
        throw new Exception("远程[" + remoteFile + "] 不是文件！");
      }
      return attrs.getSize();
    } catch (Exception e) {
      log.error("获取远程文件大小 异常：", e);
      throw new Exception("获取远程文件[" + remoteFile + "]大小 异常!");
    }
  }

  /**
   * 创建指定文件夹
   *
   * @param remotePath 文件夹绝对路径
   * @throws Exception
   */
  private boolean createRemoteDir(String remotePath) throws Exception {
    remotePath = getAbsolutePath(remotePath);
    if (null == remotePath || "".equals(remotePath)) {
      throw new Exception("文件夹路径[" + remotePath + "]不能为空！");
    }
    if (this.dirIsExist(remotePath)) {
      this.sftp.cd(remotePath);
      return true;
    }
    String[] str = remotePath.trim().split("/");
    StringBuilder filePath = new StringBuilder("/");
    for (String path : str) {
      if ("".equals(path)) {
        continue;
      }
      filePath.append(path);
      filePath.append("/");
      if (this.dirIsExist(filePath.toString())) {
        this.sftp.cd(filePath.toString());
      } else {
        this.sftp.mkdir(filePath.toString());
        this.sftp.cd(filePath.toString());
      }
    }
    this.sftp.cd(remotePath);
    return true;
  }

  /**
   * 上传文件
   *
   * @param localPath 本地文件路径
   * @param localFileName 文件名称
   * @param remotePath 远程目录
   * @param remoteFileName 保存文件名
   * @throws Exception
   */
  public boolean uploadFile(
      String localPath, String localFileName, String remotePath, String remoteFileName)
      throws Exception {
    log.info(
        "Sftp文件上传--->本地文件[{},{}]，远程目录[{},{}]--->开始！",
        localPath,
        localFileName,
        remotePath,
        remoteFileName);
    // 判断本地文件是否存在
    String filePath = getAbsolutePath(localPath) + localFileName;
    File file = new File(filePath);
    if (!file.exists() || !file.isFile()) {
      throw new Exception("本地文件[" + localFileName + "]不存在或不是文件！");
    }
    // 判断远程文件夹是否存在
    createRemoteDir(remotePath);
    this.sftp.put(filePath, remoteFileName);
    log.info(
        "Sftp文件上传--->本地文件[{},{}]，远程目录[{},{}]--->成功！",
        localPath,
        localFileName,
        remotePath,
        remoteFileName);
    return true;
  }

  /**
   * 上传文件
   *
   * @param input 文件流
   * @param remoteFileName 文件名称
   * @param remotePath 远程目录
   * @throws Exception
   */
  public void uploadFile(InputStream input, String remoteFileName, String remotePath)
      throws Exception {
    log.info("Sftp文件上传---文件流-->本地文件[{}], 远程目录[{}]--->开始！", remoteFileName, remotePath);
    try {
      createRemoteDir(remotePath);
      this.sftp.put(input, remoteFileName);
      log.info("Sftp文件上传---文件流-->本地文件[{}], 远程目录[{}]--->成功！", remoteFileName, remotePath);
    } finally {
      if (null != input) {
        try {
          input.close();
        } catch (Exception e) {
          log.info("Sftp文件上传关闭文件流时异常，", e);
        }
      }
    }
  }

  /**
   * 批量上传文件
   *
   * @param remotePath 远程目录
   * @param localPath 本地目录
   * @param isDel 上传后是否删除本地文件
   * @return boolean
   * @throws Exception
   */
  public boolean uploadBatchFiles(String remotePath, String localPath, boolean isDel)
      throws Exception {
    log.info("Sftp批量文件上传--->本地目录[{}], 远程目录[{}]--->开始处理！", localPath, remotePath);
    File dirLocal = new File(localPath);
    File[] files = dirLocal.listFiles();
    if (null == files || 0 == files.length) {
      log.info("Sftp批量文件上传--->本地目录[{}] 无 文件！", localPath);
      return false;
    }
    for (File file : files) {
      if (file.isFile()) {
        if (!".".equals(file.getName()) && !"..".equals(file.getName())) {
          boolean bool = this.uploadFile(localPath, file.getName(), remotePath, file.getName());
          if (bool && isDel) {
            deleteFile(localPath + file.getName());
          }
        }
      }
    }
    log.info(
        "Sftp批量文件上传--->本地目录[{}], 文件数量[{}], 远程目录[{}]--->上传成功！", localPath, files.length, remotePath);
    return true;
  }

  /**
   * 删除本地文件
   *
   * @param filePath 文件路径
   * @return boolean
   */
  private boolean deleteFile(String filePath) {
    File file = new File(filePath);
    if (!file.exists() || !file.isFile()) {
      return false;
    }
    boolean rs = file.delete();
    log.info("delete file {} from local.", rs);
    return rs;
  }

  /**
   * SFTP下载单个文件
   *
   * @param remotePath 远程目录
   * @param remoteFileName 文件名称
   * @param localPath 本地目录
   * @param localFileName 保存文件名
   * @return boolean
   * @throws Exception
   */
  public boolean downloadFile(
      String remotePath, String remoteFileName, String localPath, String localFileName)
      throws Exception {
    log.info(
        "Sftp文件下载--->远程目录[{}], 文件名称[{}], 本地目录[{},{}]--->开始处理！",
        remotePath,
        remoteFileName,
        localPath,
        localFileName);
    String fileRemotePath = getAbsolutePath(remotePath) + remoteFileName;
    String fileLocalPath = getAbsolutePath(localPath) + localFileName;
    this.sftp.get(fileRemotePath, fileLocalPath);
    log.info(
        "Sftp文件下载--->远程目录[{}], 文件名称[{}], 本地目录[{},{}]--->处理成功！",
        remotePath,
        remoteFileName,
        localPath,
        localFileName);
    return true;
  }

  /**
   * Sftp文件下载到本地流
   *
   * @param remotePath 远程目录
   * @param fileName 文件名称
   * @return InputStream
   * @throws Exception
   */
  public InputStream downFile(String remotePath, String fileName) throws Exception {
    log.info("Sftp文件下载到本地流--->远程目录[{}], 文件名称[{}]--->开始处理！", remotePath, fileName);
    String fileRemotePath = getAbsolutePath(remotePath) + fileName;
    if (!fileIsExist(fileRemotePath)) {
      throw new Exception("Sftp文件下载到本地流 异常：远程文件[" + fileRemotePath + "] 不存在！");
    }
    InputStream ins = this.sftp.get(fileRemotePath);
    log.info("Sftp文件下载到本地流--->远程目录[{}], 文件名称[{}]--->处理成功！", remotePath, fileName);
    return ins;
  }

  /**
   * 批量下载文件
   *
   * @param remotePath 远程下载目录
   * @param localPath 本地保存目录
   * @param fileFormat 下载文件格式(以特定字符开头,为空不做检验)
   * @param fileEndFormat 下载文件格式(以特定字符结尾,为空不做检验)
   * @param isDel 下载后是否删除sftp文件
   * @return List
   * @throws Exception
   */
  public List<String> downBatchFiles(
      String remotePath, String localPath, String fileFormat, String fileEndFormat, boolean isDel)
      throws Exception {
    log.info(
        "SFTP批量下载文件 远程下载目录[{}], 本地保存目录[{}], 下载文件格式：{}, 下载文件格式：{}, 下载后是否删除sftp文件：{}  下载开始！",
        remotePath,
        localPath,
        fileFormat,
        fileEndFormat,
        isDel);
    fileFormat = fileFormat == null ? "" : fileFormat.trim();
    fileEndFormat = fileEndFormat == null ? "" : fileEndFormat.trim();
    if (!dirIsExist(remotePath)) {
      throw new Exception("Sftp批量下载文件 异常：远程目录[" + remotePath + "] 不存在！");
    }
    List<String> fileNameList = new ArrayList<>();
    try {
      sftp.cd(remotePath);
      Vector v = listFiles(remotePath);
      log.info("SFTP 批量下载文件个数:{}", v.size());
      if (v.isEmpty()) {
        return fileNameList;
      }
      Iterator it = v.iterator();
      while (it.hasNext()) {
        ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) it.next();
        String fileName = entry.getFilename();
        SftpATTRS attrs = entry.getAttrs();
        if (!attrs.isDir()) {
          String localFileName = localPath + fileName;
          boolean flag = false;
          if (fileFormat.length() > 0 && fileEndFormat.length() > 0) {
            if (fileName.startsWith(fileFormat) && fileName.endsWith(fileEndFormat)) {
              flag = downloadFile(remotePath, fileName, localPath, fileName);
            }
          } else if (fileFormat.length() > 0 && "".equals(fileEndFormat)) {
            if (fileName.startsWith(fileFormat)) {
              flag = downloadFile(remotePath, fileName, localPath, fileName);
            }
          } else if (fileEndFormat.length() > 0 && "".equals(fileFormat)) {
            if (fileName.endsWith(fileEndFormat)) {
              flag = downloadFile(remotePath, fileName, localPath, fileName);
            }
          } else {
            if (!isSpecialSFileName(fileName)) {
              flag = downloadFile(remotePath, fileName, localPath, fileName);
            }
          }
          if (flag) {
            log.info("SFTP 批量下载文件 下载单个文件[{}] 成功！", localFileName);
            fileNameList.add(localFileName);
            if (isDel) {
              deleteSFTPFile(remotePath, fileName);
            }
          }
        } else {
          log.info("SFTP 批量下载文件 远程下载目录：{} 下存在子目录：{} ", remotePath, fileName);
        }
      }
      log.info(
          "SFTP 批量下载文件 远程下载目录：{}, 本地保存目录：{}, 下载文件格式：{}, 下载文件格式：{}, 下载后是否删除sftp文件：{}  下载成功！",
          remotePath,
          localPath,
          fileFormat,
          fileEndFormat,
          isDel);
    } catch (SftpException e) {
      log.error("SFTP 批量下载文件 异常: ", e);
    }
    return fileNameList;
  }

  /**
   * 批量下载文件
   *
   * @param remotePath 远程目录
   * @param localPath 本地目录
   * @param isMonitor 是否记录文件传输进度
   * @throws Exception
   */
  public void downBatchFiles(String remotePath, String localPath, boolean isMonitor)
      throws Exception {
    log.info("Sftp批量下载文件--->远程目录[{}], 本地目录[{}]--->开始处理！", remotePath, localPath);
    if (!dirIsExist(remotePath)) {
      throw new Exception("Sftp批量下载文件 异常：远程目录[" + remotePath + "] 不存在！");
    }
    LinkedList<SftpLsEntry> list = null;
    try {
      list = getAllSftpLsEntry4Dir(remotePath);
    } catch (Exception e) {
      String msg = "Sftp批量下载文件 异常：远程目录[" + remotePath + "] 遍历文件时出现异常！";
      log.error(msg, e);
      throw new Exception(msg);
    }
    if (null == list || list.isEmpty()) {
      throw new Exception("Sftp批量下载文件 异常：远程目录[" + remotePath + "] 不存在可下载的文件！");
    }
    mkLocalDirs(localPath);
    for (SftpLsEntry sftpLsEntry : list) {
      String fileName = sftpLsEntry.getFileName();
      String fileRemotePath = getAbsolutePath(sftpLsEntry.getPath()) + fileName;
      String fileLocalPath = getAbsolutePath(localPath) + fileName;
      if (!isMonitor) {
        this.sftp.get(fileRemotePath, fileLocalPath);
      } else {
        this.sftp.get(
            fileRemotePath,
            fileLocalPath,
            new SftpProgressMonitor(sftpLsEntry.getFileSize()),
            ChannelSftp.OVERWRITE);
      }
    }
    log.info(
        "Sftp批量下载文件--->远程目录[{}], 本地目录[{}], 文件数量[{}]--->处理成功！", remotePath, localPath, list.size());
  }

  /**
   * 删除文件
   *
   * @param dir 要删除文件所在目录
   * @param fileName 要删除的文件
   * @return boolean
   * @throws SftpException
   */
  public boolean deleteSFTPFile(String dir, String fileName) throws SftpException {
    this.sftp.cd(dir);
    this.sftp.rm(fileName);
    log.info("delete file[{}] success from sftp.", dir + fileName);
    return true;
  }

  /**
   * 更改文件名
   *
   * @param dir 文件所在目录
   * @param oldFileName 原文件名
   * @param newFileName 新文件名
   * @throws Exception
   */
  public void reName(String dir, String oldFileName, String newFileName) throws Exception {
    this.sftp.cd(dir);
    this.sftp.rename(oldFileName, newFileName);
  }

  /**
   * 如果目录不存在就创建目录
   *
   * @param path 本地路径
   */
  private void mkLocalDirs(String path) {
    File pathDir = new File(path);
    if (!pathDir.exists() && !pathDir.isDirectory()) {
      // mkdirs()可以建立多级文件夹， mkdir()只会建立一级的文件夹
      boolean flag = pathDir.mkdirs();
      log.info("mkLocalDirs {} is {}", path, flag);
    }
  }

  /**
   * 列出目录下的文件
   *
   * @param directory 要列出的目录
   * @return Vector
   * @throws SftpException
   */
  @SuppressWarnings("unchecked")
  private Vector<ChannelSftp.LsEntry> listFiles(String directory) throws SftpException {
    return this.sftp.ls(directory);
  }

  /**
   * 获取远程目录下的文件名称列表（含文件夹）
   *
   * @param directory 目录
   * @return List<String>
   * @throws Exception
   */
  public List<String> getList4FileNamesByDir(String directory) throws Exception {
    Vector<ChannelSftp.LsEntry> sftpFile = listFiles(directory);
    List<String> nameList = new ArrayList<>();
    sftpFile.forEach(
        lsEntry -> {
          String fileName = lsEntry.getFilename();
          if (!isSpecialSFileName(fileName)) {
            nameList.add(fileName);
          }
        });
    return nameList;
  }

  /**
   * 遍历目录
   *
   * @param sftpLsEntry 待处理目录Bean
   * @param dirList 子目录List
   * @param pathList 文件List
   * @throws Exception
   */
  private void getSftpLsEntry4Dir(
      SftpLsEntry sftpLsEntry, LinkedList<SftpLsEntry> dirList, LinkedList<SftpLsEntry> pathList)
      throws Exception {
    if (null == sftpLsEntry || !sftpLsEntry.isDir) {
      return;
    }
    String path = getAbsolutePath(sftpLsEntry.getPath()) + sftpLsEntry.getFileName();
    Vector<ChannelSftp.LsEntry> sftpFiles = listFiles(path);
    sftpFiles.forEach(
        lsEntry -> {
          String fileName = lsEntry.getFilename().trim();
          if (!isSpecialSFileName(fileName)) {
            SftpATTRS attr = lsEntry.getAttrs();
            SftpLsEntry newLsEntry = new SftpLsEntry(fileName, path, true, attr.getSize());
            if (attr.isDir()) {
              dirList.add(newLsEntry);
            } else if (attr.isReg()) {
              newLsEntry.setDir(false);
              pathList.add(newLsEntry);
            }
          }
        });
  }

  /**
   * 遍历目录下所有文件(含子目录)
   *
   * @param remotePath 远程目录
   * @return LinkedList<SftpLsEntry>
   * @throws Exception
   */
  public LinkedList<SftpLsEntry> getAllSftpLsEntry4Dir(String remotePath) throws Exception {
    Path path = Paths.get(remotePath);
    String fileName = path.getFileName() == null ? "" : path.getFileName().toString();
    String fileParent = path.getParent() == null ? "" : path.getParent().toString();
    SftpLsEntry sftpLsEntry = new SftpLsEntry();
    sftpLsEntry.setDir(true);
    sftpLsEntry.setFileSize(0L);
    sftpLsEntry.setFileName(fileName);
    sftpLsEntry.setPath(fileParent);
    // 保存待遍历文件夹的列表
    LinkedList<SftpLsEntry> dirList = new LinkedList<>();
    // 文件列表
    LinkedList<SftpLsEntry> pathList = new LinkedList<>();
    // 调用遍历文件夹根目录文件的方法
    getSftpLsEntry4Dir(sftpLsEntry, dirList, pathList);
    SftpLsEntry temp = null;
    while (!dirList.isEmpty()) {
      temp = (SftpLsEntry) dirList.removeFirst();
      getSftpLsEntry4Dir(temp, dirList, pathList);
    }
    return pathList;
  }

  /**
   * 判断是否为特殊文件名
   *
   * @param fileName 文件名
   * @return boolean
   */
  private boolean isSpecialSFileName(String fileName) {
    fileName = fileName == null ? "" : fileName;
    String name1 = ".";
    String name2 = "..";
    return name1.equals(fileName) || name2.equals(fileName);
  }
}
