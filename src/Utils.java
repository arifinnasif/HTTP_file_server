import java.io.File;
import java.lang.reflect.Array;


public class Utils {
    private static String[] img_exts={".jpeg", ".png", ".jpg", ".gif", ".webp"};
    private static String[] text_exts={".text", ".txt"};
    private static String[] all_exts= {".jpeg", ".png", ".jpg", ".gif", ".webm", ".text", ".txt"};
    private static String[] upload_exts= {".jpeg", ".png", ".jpg", ".gif", ".webm", ".text", ".txt", ".mp4"};

    public static int CHUNK_SIZE = 100;

    public static boolean isImage(File file) {
        return isImage(file.getName());
    }

    public static boolean isImage(String filepath) {
        String file_ext = getExt(filepath);
        for (String ext : img_exts) {
            if(ext.equals(file_ext)) return true;
        }
        return false;
    }

    public static boolean isText(File file) {
        return isText(file.getName());
    }

    public static boolean isText(String filepath) {
        String file_ext = getExt(filepath);
        for (String ext : text_exts) {
            if(ext.equals(file_ext)) return true;
        }
        return false;
    }

    public static String getExt(File file) {
        return getExt(file.getName());
    }

    public static String getExt(String filepath) {
        if(!filepath.contains(".")) return null;
        return filepath.substring(filepath.lastIndexOf("."));
    }

    public static boolean isUploadFilenameValid(String filepath) {
        String file_ext = getExt(filepath);
        for (String ext : upload_exts) {
            if(ext.equals(file_ext)) return true;
        }
        return false;
    }
}
