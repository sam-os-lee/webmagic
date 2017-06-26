package us.codecraft.webmagic.utils;

import java.io.File;

/**
 * Base object of file persistence.
 * 
 * 文件持久化类
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.2.0
 */
public class FilePersistentBase {

    protected String path;

    public static String PATH_SEPERATOR = "/";

    static {
    	// 系统平台分隔符
        String property = System.getProperties().getProperty("file.separator");
        if (property != null) {
            PATH_SEPERATOR = property;
        }
    }

    public void setPath(String path) {
        if (!path.endsWith(PATH_SEPERATOR)) {
            path += PATH_SEPERATOR;
        }
        this.path = path;
    }

    public File getFile(String fullName) {
        checkAndMakeParentDirecotry(fullName);
        return new File(fullName);
    }

    /**
     * 创建父目录
     * 
     * @param fullName
     */
    public void checkAndMakeParentDirecotry(String fullName) {
        int index = fullName.lastIndexOf(PATH_SEPERATOR);
        if (index > 0) {
            String path = fullName.substring(0, index);
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
    }

    public String getPath() {
        return path;
    }
}
