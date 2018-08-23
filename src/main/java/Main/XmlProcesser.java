package Main;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class XmlProcesser {

    static Path archive = Paths.get(MyProperties.getLogsArchiveFolder());
    static Queue<Path> queue = new LinkedList<>();
    static ExecutorService threadService = Executors.newFixedThreadPool(10);
    static TreeMap<LocalDate, Map<User, Map<URL,Average>>> daysMap = new TreeMap<>();

    private static boolean processing;

    static {
        new Thread(() -> startQueueProcessing()).start();
    }


    public static void sendToQueue(Path path){
        Path tempFolder = Paths.get(MyProperties.getTempFolder());
        Path pathInTemp = FileManager.sendFileTo(path, tempFolder);
        queue.add(pathInTemp);
    }

    private static void callThread(Path path){
        threadService.submit(new XmlProcess(path));
    }

     static synchronized void  insertDataToMainMap(Map.Entry<LocalDate, Integer> entry, User user, URL url) {

        Integer seconds = entry.getValue();
        Average average = new Average(seconds,1);
        HashMap<URL, Average> singleAverage = new HashMap<>();
        singleAverage.put(url,average);
        TreeMap<User, Map<URL, Average>> userSites = new TreeMap<>();
        userSites.put(user,singleAverage);
        Map<User, Map<URL, Average>> map = daysMap.get(entry.getKey());
        if (map == null || map.isEmpty()){
            daysMap.put(entry.getKey(),userSites);
        }else{
            Map<URL, Average> urlAverageMap = map.get(user);
            if(urlAverageMap == null || urlAverageMap.isEmpty()){
                map.put(user,singleAverage);
            }else{
                Average averageInMap = urlAverageMap.get(url);
                if (averageInMap == null){
                    urlAverageMap.put(url,average);
                }else{
                    averageInMap.addOneVisit(seconds);
                }
            }
        }
    }

    private static void startQueueProcessing() {
        processing = true;
        while(processing){
            Path path = getNextByQueue();
            if (path !=null){
                callThread(path);
            }else{
                // without it, resources are not consumed in large quantities but just in case.
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void stopQueueProcessing() {
        processing = false;
    }

    private static synchronized Path getNextByQueue(){
        return queue.poll();
    }

//// TODO: 23.08.2018 delete from here...
//    public static void sendToSourceFolder(Path path) {
//        sendFileTo(path, Main.logsFolder);
//    }
//
//    public static void moveFilesBack(){
//        try {
//            Files.walk(archive)
//                    .filter(p -> p.toString().endsWith(".xml"))
//                    .distinct()
//                    .forEach(XmlProcesser::sendToSourceFolder);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//// TODO: 23.08.2018 ..to here.

//    public static Path sendFileTo(Path from, Path to){
//        try {
//            Path fileName = from.getFileName();
//            Path directionPath =
//                    Paths.get(to.toString() + "\\"+ fileName.toString());
//            Files.move(from, directionPath);
//            return directionPath;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
}
