package Main;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class MyProperties {

    public static final String PATH_TO_PROPERTIES = "src/main/resources/config.properties";

    private static String inputFolder;
    private static String outputFolder;
    private static String tempFolder;
    private static String logsArchiveFolder;

    private static int scanFriquencyInSec;


    static {
        FileInputStream fileInputStream;
        Properties prop = new Properties();
        try {
            fileInputStream = new FileInputStream(PATH_TO_PROPERTIES);
            prop.load(fileInputStream);
            inputFolder = prop.getProperty("inputFolder");
            outputFolder = prop.getProperty("outputFolder");
            tempFolder = prop.getProperty("tempFolder");
            logsArchiveFolder = prop.getProperty("archiveFolder");

            scanFriquencyInSec =  Integer.parseInt(prop.getProperty("scanFriquencyInSec"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getInputFolder() {
        return inputFolder;
    }

    public static String getOutputFolder() {
        return outputFolder;
    }

    public static int getScanFriquencyInSec() {
        return scanFriquencyInSec;
    }

    public static String getTempFolder() {
        return tempFolder;
    }

    public static String getLogsArchiveFolder() {
        return logsArchiveFolder;
    }
}
