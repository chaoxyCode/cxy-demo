package common.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Sftp文件传输进度记录类
 *
 * @author chaoxy
 * @date 2019/05/13
 * @version 1.0
 */
public class SftpProgressMonitor extends TimerTask implements com.jcraft.jsch.SftpProgressMonitor {

  private static final Logger LOG = LoggerFactory.getLogger(SftpProgressMonitor.class);

  /** 默认间隔时间为5秒 */
  private long progressInterval = 5 * 1000;

  /** 记录传输是否结束 */
  private boolean isEnd = false;

  /** 记录已传输的数据总大小 */
  private long transFered;

  /** 记录文件总大小 */
  private long fileSize;

  /** 定时器对象 */
  private Timer timer;

  /** 记录是否已启动timer记时器 */
  private boolean isScheduled = false;

  public SftpProgressMonitor(long fileSize) {
    this.fileSize = fileSize;
  }

  @Override
  public void run() {
    LOG.info("文件传输状态：{}！", !isEnd()? "正在传输中" : "已经结束");
    if (isEnd()) {
      // 如果传输结束，停止timer记时器
      stop();
      return;
    }
    long transFered = getTransFered();
    // 判断当前已传输数据大小是否等于文件总大小
    if (transFered == fileSize) {
      // 如果当前已传输数据大小等于文件总大小，说明已完成，设置end
      setEnd(true);
    }
    logProgressMessage(transFered);
  }

  /** 启动timer记时器 */
  public void start() {
    if (null == timer) {
      timer = new Timer();
    }
    timer.schedule(this, 1000, progressInterval);
    isScheduled = true;
    LOG.info("启动时间间隔为[{}]秒 文件传输进度 记时器 !", progressInterval / 1000);
  }

  /** 终止timer记时器 */
  public void stop() {
    if (null != timer) {
      timer.cancel();
      timer.purge();
      timer = null;
      isScheduled = false;
    }
    LOG.info("终止时间间隔为[{}]秒 文件传输进度 记时器 !", progressInterval / 1000);
  }

  /**
   * 记录进度信息
   *
   * @param transFered 字节数
   */
  private void logProgressMessage(long transFered) {
    String str = "100";
    if (0 < fileSize) {
      BigDecimal bg =
          new BigDecimal(transFered).multiply(new BigDecimal(100)).divide(new BigDecimal(fileSize));
      DecimalFormat df = new DecimalFormat("#.##");
      str = df.format(bg);
    }
    LOG.info("传输文件共：{} bytes，已经传输：{} bytes，传输进度：{}%", fileSize, transFered, str);
  }

  @Override
  public void init(int op, String src, String dest, long max) {
    // Not used for putting InputStream
  }

  @Override
  public boolean count(long count) {
    if (isEnd()) {
      return false;
    }
    if (!isScheduled) {
      start();
    }
    add(count);
    return true;
  }

  @Override
  public void end() {
    setEnd(true);
  }

  private synchronized void add(long count) {
    transFered = transFered + count;
  }

  private synchronized long getTransFered() {
    return transFered;
  }

  public synchronized void setTransFered(long transFered) {
    this.transFered = transFered;
  }

  private synchronized void setEnd(boolean isEnd) {
    this.isEnd = isEnd;
  }

  private synchronized boolean isEnd() {
    return isEnd;
  }
}
