package util;

import logger.MyLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * 文件操作工具类
 *
 * @author 大定府羡民（QQ：1032694760）
 */
@SuppressWarnings({"UnusedReturnValue", "unused", "WeakerAccess"})
public final class FileUtils {

    private FileUtils() {
    }

    public static boolean writeText(String filePath, String content) {
        return writeText(filePath, content, "UTF-8");
    }

    public static boolean writeText(String filePath, String content, String charset) {
        boolean successful = true;
        FileOutputStream fout = null;
        try {
            File file = new File(filePath);
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                //noinspection ResultOfMethodCallIgnored
                parentFile.mkdirs();
            }
            fout = new FileOutputStream(file, false);
            //noinspection CharsetObjectCanBeUsed
            fout.write(content.getBytes(Charset.forName(charset)));
        } catch (IOException e) {
            MyLog.debug(e);
            successful = false;
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                    MyLog.debug(e);
                }
            }
        }
        MyLog.debug("write " + filePath + ": successful=" + successful);
        return successful;
    }

    public static String readText(String filePath) {
        FileInputStream inputStream = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                MyLog.debug(filePath + " not exists");
                return "";
            }
            StringBuilder sb = new StringBuilder();
            inputStream = new FileInputStream(file);
            byte[] buffer = new byte[2048];
            while (true) {
                int len = inputStream.read(buffer);
                if (len == -1) {
                    break;
                } else {
                    //noinspection CharsetObjectCanBeUsed
                    sb.append(new String(buffer, 0, len, Charset.forName("UTF-8")));
                }
            }
            MyLog.debug("read " + filePath + " success");
            return sb.toString();
        } catch (Exception e) {
            MyLog.debug(e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    MyLog.debug(e);
                }
            }
        }
        MyLog.debug("read " + filePath + " failure");
        return "";
    }

}
