package Main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileManager {

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

    // TODO: 23.08.2018 delete from here...
    public static void sendToSourceFolder(Path path) {
        sendFileTo(path, Main.logsFolder);
    }

    public static void moveFilesBack(){
        try {
            Files.walk(XmlProcesser.archive)
                    .filter(p -> p.toString().endsWith(".xml"))
                    .distinct()
                    .forEach(FileManager::sendToSourceFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // TODO: 23.08.2018 ..to here.

}
