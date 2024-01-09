package xyz.hvdw.fytextratool;

import java.io.File;
import java.util.Map;

public class MyGettersSetters {

    private final static MyGettersSetters staticInstance = new MyGettersSetters();
    private MyGettersSetters() {

    }

    private String logFileName;
    private File logFile;
    private Map<String, String> propsHashMap;

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

}
