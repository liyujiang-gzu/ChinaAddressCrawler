package util;

import java.io.File;

/**
 * @author 大定府羡民（QQ：1032694760）
 */
public class MessyUtils {

    public static boolean existsFile(String filePath) {
        File file = new File(System.getProperty("user.dir"), filePath);
        return file.exists();
    }

    public static String readFromFile(String filePath) {
        File file = new File(System.getProperty("user.dir"), filePath);
        return FileUtils.readText(file.getAbsolutePath());
    }

    public static void saveToFile(String filePath, String content) {
        File saveDir = new File(System.getProperty("user.dir"));
        if (!saveDir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            saveDir.mkdirs();
        }
        File file = new File(saveDir, filePath);
        FileUtils.writeText(file.getAbsolutePath(), content, "UTF-8");
    }

    public static String paddingZero(String code) {
        StringBuilder sb = new StringBuilder();
        sb.append(code);
        for (int i = 0, n = code.length(); i < 12 - n; i++) {
            sb.append("0");
        }
        return sb.toString();
    }

}
