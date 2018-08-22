package Main;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static Path logsFolder = Paths.get(MyProperties.getInputFolder());

    public static void main(String[] args) {
        try {

            new LogScanner(logsFolder).startScanner();
            XmlProcesser.stopQueueProcessing();
            XmlBuilder.createXmlReport();

        } finally {
            XmlProcesser.moveFilesBack();
        }
    }
}

