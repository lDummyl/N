package Main;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static Main.Main.logsFolder;

public class LogScanner {

    private final Path logsFolder;
    private boolean active = true;
    private long timePeriod = //in millis
            1000 * MyProperties.getScanFriquencyInSec();

    public static int counter = 0;//temp

    public LogScanner(Path logsFolder) {
        this.logsFolder = logsFolder;
    }

    public void startScanner()  {
        while (active) {

            scanFolder();
            counter++;//temp
            if (counter>7) shutDown();//temp

        }
    }
    private void scanFolder(){
        try {
            Files.walk(logsFolder)
                    .filter(p -> p.toString().endsWith(".xml"))
                    .distinct()
                    .forEach(XmlProcesser::sendToQueue);
            Thread.sleep(timePeriod);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void shutDown(){
        active = false;
    }
}
