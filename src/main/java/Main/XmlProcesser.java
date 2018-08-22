package Main;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

public class XmlProcesser {

    static Path archive = Paths.get(MyProperties.getLogsArchiveFolder());
    static Queue<Path> queue = new LinkedList<>();
    static List<Thread> onlineThreads = new ArrayList<>();
    static HashMap<LocalDate, Map<User, Map<URL,Average>>> daysMap = new HashMap<>();
    private static boolean processing;

    static {
        new Thread(() -> startQueueProcessing()).start();
    }



    public static void sendToQueue(Path path){
        Path tempFolder = Paths.get(MyProperties.getTempFolder());
        Path pathInTemp = sendFileTo(path, tempFolder);
        queue.add(pathInTemp);
    }

    private static void makeThread(Path path){
        Thread thread = new Thread( () -> processFile(path));
        thread.start();
        onlineThreads.add(thread);
    }

    public static void processFile(Path path){
        List<Session> sessionInstance = new SessionConverter().getSessionInstance(path);
        for (Session session : sessionInstance) {
            processSession(session);
        }
        sendToArchive(path);

    }

    private static void processSession(Session session) {

        try {
            String timestamp = session.getTimestamp();
            String seconds = session.getSeconds();
            User user = new User(session.getUserId());
            URL url = new URL(session.getUrl());
            Map<LocalDate, Integer> daysQty = getDaysQty(timestamp, seconds);
            for (Map.Entry<LocalDate, Integer> entry : daysQty.entrySet()) {

                insertDataToMainMap(entry, user, url);

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private static synchronized void  insertDataToMainMap(Map.Entry<LocalDate, Integer> entry, User user, URL url) {

        Integer seconds = entry.getValue();
        Average average = new Average(seconds,1);
        HashMap<URL, Average> singleAverage = new HashMap<>();
        singleAverage.put(url,average);
        HashMap<User, Map<URL, Average>> userSites = new HashMap<>();
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
            int count =0;
            for (Thread onlineThread : onlineThreads) {
                if (onlineThread.isAlive())count++;
            }
            if (count<10){
                Path path = getNextByQueue();
                if (path !=null) {
                    makeThread(path);
                }
            }
            if (LogScanner.counter >9) processing =false;
        }
    }
    public static void stopQueueProcessing() {
        processing = false;
    }


    private static synchronized Path getNextByQueue(){
        return queue.poll();
    }

    private static void sendToArchive(Path path) {
        sendFileTo(path, archive);
    }

    public static void sendToSourceFolder(Path path) {
        sendFileTo(path, Main.logsFolder);
    }

    public static void moveFilesBack(){

        try {
            Files.walk(archive)
                    .filter(p -> p.toString().endsWith(".xml"))
                    .distinct()
                    .forEach(XmlProcesser::sendToSourceFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Path sendFileTo(Path from, Path to){
        try {
            Path fileName = from.getFileName();
            Path directionPath =
                    Paths.get(to.toString() + "\\"+ fileName.toString());
            Files.move(from, directionPath);
            return directionPath;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static Map<LocalDate, Integer> getDaysQty(String timeStamp, String seconds){
        // returns days of one session as key and seconds in this day, spent on session as value
        HashMap<LocalDate, Integer> secondsByDays = new HashMap<>();

        final int SEC_IN_DAY = 86400;
        long sec = Long.parseLong(seconds);
        long timeStampMilli = Long.parseLong(timeStamp)*1000;

        LocalDateTime sessionStart =
                Instant.ofEpochMilli(timeStampMilli).atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime sessionEnd =
                Instant.ofEpochMilli(timeStampMilli+sec*1000).atZone(ZoneId.systemDefault()).toLocalDateTime();

        LocalDate startDay = sessionStart.toLocalDate();
        LocalDate nextAfterStartDay = startDay.plusDays(1);
        LocalDate endDay = sessionEnd.toLocalDate();

        LocalDateTime nextDayAtStartOfDay = nextAfterStartDay.atStartOfDay();
        LocalDateTime endDayAtStartOfDay = endDay.atStartOfDay();

        long st = sessionStart.toEpochSecond(OffsetDateTime.now().getOffset());
        long newDay = nextDayAtStartOfDay.toEpochSecond(OffsetDateTime.now().getOffset());
        Long secondsInFirstDay = newDay - st;

        long end = sessionEnd.toEpochSecond(OffsetDateTime.now().getOffset());
        long endDayBegin = endDayAtStartOfDay.toEpochSecond(OffsetDateTime.now().getOffset());
        Long secondsInLastDay = end - endDayBegin;

        List<LocalDate> collect = startDay.datesUntil(endDay).collect(Collectors.toList());
        if (collect.size()==0){
            secondsByDays.put(startDay,(int)(long) sec);
        }else{
            collect.remove(startDay);
            for (LocalDate localDate : collect) {
                secondsByDays.put(localDate, SEC_IN_DAY);
            }
            secondsByDays.put(startDay,secondsInFirstDay.intValue());
            secondsByDays.put(endDay,secondsInLastDay.intValue());
        }
        return secondsByDays;
    }
}
