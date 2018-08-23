package Main;


import org.w3c.dom.Document;
import org.w3c.dom.Element;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;


public class XmlBuilder {

    public static void createXmlReport(){

        File file = getXmlBlankFile();
//        HashMap<LocalDate, Map<User, Map<URL,Average>>> daysMap = XmlProcesser.daysMap;

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = docFactory.newDocumentBuilder();
            Document doc = documentBuilder.newDocument();

            Element root = doc.createElement("output");
            doc.appendChild(root);

            for (Map.Entry<LocalDate, Map<User, Map<URL, Average>>> mapEntry : XmlProcesser.daysMap.entrySet()) {

                String dateToWrite = mapEntry.getKey().toString();
                Element logday = doc.createElement("logday");
                root.appendChild(logday);

                Element day = doc.createElement("day");
                day.appendChild(doc.createTextNode(dateToWrite));
                logday.appendChild(day);

                Element users = doc.createElement("users");
                logday.appendChild(users);

                Map<User, Map<URL, Average>> usersMap = mapEntry.getValue();
                for (Map.Entry<User, Map<URL, Average>> userMapEntry : usersMap.entrySet()) {

                    String userName = userMapEntry.getKey().name;
                    Map<URL, Average> urlAverageMap = userMapEntry.getValue();

                    for (Map.Entry<URL, Average> urlAverageEntry : urlAverageMap.entrySet()) {

                        Element user = doc.createElement("user");
                        users.appendChild(user);

                        Element id = doc.createElement("id");
                        id.appendChild(doc.createTextNode(userName));

                        Element url = doc.createElement("url");
                        url.appendChild(doc.createTextNode(urlAverageEntry.getKey().toString()));

                        Element average = doc.createElement("average");
                        Integer averageVal = urlAverageEntry.getValue().getValue();
                        average.appendChild(doc.createTextNode(averageVal.toString()));

                        user.appendChild(id);
                        user.appendChild(url);
                        user.appendChild(average);
                    }
                }
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(doc);

            StreamResult streamResult = new StreamResult(file);
            transformer.transform(domSource,streamResult);
            System.out.println("File Saved!");

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    private static File getXmlBlankFile(){

        File file = null;
        String outputFolder = MyProperties.getOutputFolder();
        int num=0;
        boolean searching = true;
        while(searching){
            file = new File(String.format(outputFolder+"\\avg_report%d.xml", ++num));
            if (!file.exists())searching = false;
        }
        return file;
    }
}
