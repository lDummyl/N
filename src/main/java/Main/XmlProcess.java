package Main;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.time.*;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class XmlProcess implements Runnable {

    public XmlProcess(Path path) {
        this.path = path;
    }

    private Path path;
    @Override
    public void run() {
        processFile(path);
    }

    public void processFile(Path path){
        List<Session> sessionInstance = new SessionConverter().getSessionInstance(path);
        for (Session session : sessionInstance) {
            processSession(session);
        }
        sendToArchive(path);
    }

    private  void processSession(Session session) {
        try {
            String timestamp = session.getTimestamp();
            String seconds = session.getSeconds();
            User user = new User(session.getUserId());
            URL url = new URL(session.getUrl());
            Map<LocalDate, Integer> daysQty = getDaysQty(timestamp, seconds);
            for (Map.Entry<LocalDate, Integer> entry : daysQty.entrySet()) {

                XmlProcesser.insertDataToMainMap(entry, user, url);

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private  Map<LocalDate, Integer> getDaysQty(String timeStamp, String seconds){
        // returns days of one session as key and seconds in this day, spent on session as value
        TreeMap<LocalDate, Integer> secondsByDays = new TreeMap<>();

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

    private void sendToArchive(Path path) {
        FileManager.sendFileTo(path, XmlProcesser.archive);
    }

}
