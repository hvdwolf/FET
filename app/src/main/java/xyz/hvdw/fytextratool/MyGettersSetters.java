package xyz.hvdw.fytextratool;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

public class MyGettersSetters {

    private final static MyGettersSetters staticInstance = new MyGettersSetters();
    private MyGettersSetters() {
    }

    private String logFileName;
    private File logFile;
    private Map<String, String> propsHashMap;
    private Boolean testVersion;
    private Boolean isRooted;
    private Boolean isMagiskRooted;
    //public boolean[] fytCanbusMonitorCheckboxes;

    public static String getLogFileName() {
        return staticInstance.logFileName;
    }
    public static void setLogFileName(String logFileName) {
        staticInstance.logFileName = logFileName;
    }

    public static File getLogFile() {
        return staticInstance.logFile;
    }
    public static void setLogFile(File logFile) {
        staticInstance.logFile = logFile;
    }

    public static Map<String, String> getPropsHashMap () { return staticInstance.propsHashMap; };
    public static void setPropsHashMap( Map<String, String> PropsHashMap) {staticInstance.propsHashMap = PropsHashMap; }

    public static Boolean getTestVersion() { return staticInstance.testVersion; }
    public static void setTestVersion(Boolean testVers) {staticInstance.testVersion = testVers; }

    public static Boolean getIsRooted() { return staticInstance.isRooted; }
    public static void setIsRooted(Boolean rooted) { staticInstance.isRooted = rooted; }

    public static Boolean getIsMagiskRooted() {return staticInstance.isMagiskRooted; }
    public static void setIsMagiskRooted(Boolean magiskRooted) { staticInstance.isMagiskRooted = magiskRooted; }

    //public static boolean[] getFytCanbusMonitorCheckboxes() { return Arrays.copyOf(staticInstance.fytCanbusMonitorCheckboxes, staticInstance.fytCanbusMonitorCheckboxes.length); }
    //public static void setFytCanbusMonitorCheckboxes(boolean[] ftcbmCheckboxes) { staticInstance.fytCanbusMonitorCheckboxes = Arrays.copyOf(ftcbmCheckboxes, ftcbmCheckboxes.length); }

}
