package Main;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SessionConverter {

    private  List<Session> listOfSessions;

    public  List<Session> getSessionInstance(Path xmlFile) {
        return getSessionInstance(xmlFile.toFile());
    }

    public  List<Session> getSessionInstance(File xmlFile) {
        try {
            XsdValidator.validate(xmlFile);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            NodeList nodes = doc.getElementsByTagName("log");
            listOfSessions = new ArrayList<>();
            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);
                String userId = element.getElementsByTagName("userId").item(0).getTextContent();
                String url = element.getElementsByTagName("url").item(0).getTextContent();
                String timestamp = element.getElementsByTagName("timestamp").item(0).getTextContent();
                String seconds = element.getElementsByTagName("seconds").item(0).getTextContent();
                Session session = new Session(timestamp, seconds, userId, url);
                listOfSessions.add(session);
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listOfSessions;
    }
}
