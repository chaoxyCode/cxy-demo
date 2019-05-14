package common.core.utils;

import java.io.Serializable;

/**
 * Sftp文件实体Bean
 *
 * @auther chaoxy
 * @date 2019-05-13
 * @version 1.0
 */
public class SftpLsEntry implements Serializable {

  private static final long serialVersionUID = 1L;

  /** 文件名称 */
  private String fileName;
  /** 文件父级路径 */
  private String path;
  /** 是否为目录 */
  protected boolean isDir;
  /** 文件大小 */
  private long fileSize;

  public SftpLsEntry() {
    super();
  }

  public SftpLsEntry(String fileName, String path, boolean isDir, long fileSize) {
    super();
    this.fileName = fileName;
    this.path = path;
    this.isDir = isDir;
    this.fileSize = fileSize;
  }

  protected String getFileName() {
    return fileName;
  }

  protected void setFileName(String fileName) {
    this.fileName = fileName;
  }

  protected String getPath() {
    return path;
  }

  protected void setPath(String path) {
    this.path = path;
  }

  protected boolean isDir() {
    return isDir;
  }

  protected void setDir(boolean isDir) {
    this.isDir = isDir;
  }

  protected long getFileSize() {
    return fileSize;
  }

  protected void setFileSize(long fileSize) {
    this.fileSize = fileSize;
  }

  @Override
  public String toString() {
    return "SftpLsEntry [fileName="
        + fileName
        + ", path="
        + path
        + ", isDir="
        + isDir
        + ", fileSize="
        + fileSize
        + "]";
  }
}
