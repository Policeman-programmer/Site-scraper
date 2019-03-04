import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.Date;

class XMLdocumentDuilder {
    final String TASK_NAME = "Introlab-Systems test task";
    final String MY_NAME = "Eugen Trokhiyk";

    private org.w3c.dom.Document documentXML;
    private org.w3c.dom.Element document;

    public org.w3c.dom.Document getDocumentXML() {
        return documentXML;
    }

    public org.w3c.dom.Element getDocument() {
        return document;
    }

    public XMLdocumentDuilder factory() throws ParserConfigurationException {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
        documentXML = documentBuilder.newDocument();
        document = documentXML.createElement("document");
        documentXML.appendChild(document);

        org.w3c.dom.Element taskname = documentXML.createElement("taskname");
        taskname.appendChild(documentXML.createTextNode(TASK_NAME));
        document.appendChild(taskname);

        org.w3c.dom.Element workerName = documentXML.createElement("workername");
        workerName.appendChild(documentXML.createTextNode(MY_NAME));
        document.appendChild(workerName);

        org.w3c.dom.Element date = documentXML.createElement("date");
        date.appendChild(documentXML.createTextNode(new Date().toString()));
        document.appendChild(date);
        return this;
    }

    public void saveXMLdocument(org.w3c.dom.Document documentXML, String xmlFilePath){
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        try {
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(documentXML);
            StreamResult streamResult = new StreamResult(new File(xmlFilePath));
            transformer.transform(domSource, streamResult);
        } catch (TransformerException e) {
            e.printStackTrace();
        }

    }
}