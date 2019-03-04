import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Attr;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

 class ParseManager {

    private final static String TABLE_ID = "titles-table";
    private final String PATH_TO_SAVE = "/home/eugen/Desktop/parsingResult";
    private final int countOfTitles = 9;
    String extension = ".xml";

    void parseHtmlSource(String url){

        try {
            List<Element> rows = getTableFromSite(url);

            for (Element tr : rows) {

                XMLdocumentDuilder XMLdocumentDuilder = new XMLdocumentDuilder().factory();
                org.w3c.dom.Document documentXML = XMLdocumentDuilder.getDocumentXML();
                org.w3c.dom.Element document = XMLdocumentDuilder.getDocument();

                String nameOfDocument = tr.children().get(1).getElementsByTag("a").text();//  nameOfDocument
                String heading1 = tr.children().get(3).text();//Heading 1 of document

                org.w3c.dom.Element level1 = buildLevel1(documentXML, heading1);
                document.appendChild(level1);

                Elements tbody2lvl = tr.children().get(2).getElementsByTag("tbody").get(0).children();
                parseAndBuildLevel2(tbody2lvl, documentXML, heading1, level1);

                String xmlFilePath = PATH_TO_SAVE + "/" + nameOfDocument + extension;
                XMLdocumentDuilder.saveXMLdocument(documentXML, xmlFilePath);
                System.out.println("//////////////////////////////////////////////////");
            }

        } catch (IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    private org.w3c.dom.Element buildLevel1(org.w3c.dom.Document documentXML, String heading1) {
        org.w3c.dom.Element level1 = documentXML.createElement("level");
        Attr attr1 = documentXML.createAttribute("path");
        attr1.setValue(heading1);
        level1.setAttributeNode(attr1);

        org.w3c.dom.Element heading1El = documentXML.createElement("heading");
        heading1El.appendChild(documentXML.createTextNode(heading1));
        level1.appendChild(heading1El);
        return level1;
    }

    private List<Element> getTableFromSite(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        System.out.println("Start parsing of site :" + doc.title());
        Elements tbody = doc.getElementById(TABLE_ID).getElementsByTag("tbody").get(0).children();
        return tbody.stream().limit(countOfTitles).collect(Collectors.toList());
    }

    private void parseAndBuildLevel2(Elements tbody2lvl, org.w3c.dom.Document documentXML, String heading1,
                                                    org.w3c.dom.Element level1) throws IOException {

        for (int i = 0; i < tbody2lvl.size() ; i++) {
            org.w3c.dom.Element level2 = documentXML.createElement("level");

            if(i % 2 == 0) { //if odd it is child element of row so  skip it
                String heading2 = tbody2lvl.get(i).child(2).text();
                String path2 = heading1+"/"+heading2;

                Attr attr2 = documentXML.createAttribute("path");
                attr2.setValue(path2);
                level2.setAttributeNode(attr2);

                    org.w3c.dom.Element heading2El = documentXML.createElement("heading");
                    heading2El.appendChild(documentXML.createTextNode(heading2));
                    level2.appendChild(heading2El);

                Elements tbody3lvl = tbody2lvl.get(i).child(1).getElementsByTag("a");
                parseLevel3AndFillEntity(path2, tbody3lvl, documentXML, level2);

            }
            level1.appendChild(level2);
        }
    }

    private void parseLevel3AndFillEntity(String path2, Elements tbody3lvl, org.w3c.dom.Document documentXML,
                                                         org.w3c.dom.Element level2) throws IOException {

        for (int i = 0; i < tbody3lvl.size(); i++) {
            org.w3c.dom.Element level3 = documentXML.createElement("level");

            String heading3 = tbody3lvl.get(i).text();
            String path3 = path2+"/"+heading3;

            Attr attr3 = documentXML.createAttribute("path");
            attr3.setValue(path3);
            level3.setAttributeNode(attr3);

                org.w3c.dom.Element heading3El = documentXML.createElement("heading");
                heading3El.appendChild(documentXML.createTextNode(heading3));
                level3.appendChild(heading3El);

            //level4
            String urlLvl4 = tbody3lvl.get(i).attr("href");
            Elements tbody4lvl = Jsoup.connect(urlLvl4).get().getElementsByTag("tbody").get(i).children();
            parseLevel4AndFillEntity(path3, tbody4lvl, documentXML, level3);

            level2.appendChild(level3);
        }
    }

    private void parseLevel4AndFillEntity(String path3, List<Element> tbody4lvl, org.w3c.dom.Document documentXML, org.w3c.dom.Element level3) throws IOException {

        for (Element row : tbody4lvl) {
            if (row.attr("class").equals("odd-parent")) {

                org.w3c.dom.Element level4 = documentXML.createElement("level");

                String heading4 = row.child(2).text();
                String path4 = path3 + "/" + heading4;

                Attr attr4 = documentXML.createAttribute("path");
                attr4.setValue(path4);
                level4.setAttributeNode(attr4);

                org.w3c.dom.Element heading4El = documentXML.createElement("heading");
                heading4El.appendChild(documentXML.createTextNode(heading4));
                level4.appendChild(heading4El);

                //level4
                String urlLvl5 = row.child(1).getElementsByAttribute("href").get(0).attr(("href"));
                try {
                    Elements tbody5lvl = Jsoup.connect(urlLvl5).get().getElementsByTag("tbody").get(0).children();
                    parseLevel5AndFillEntity(path3, tbody5lvl, documentXML, level4);
                } catch (IndexOutOfBoundsException e) {
                    //"There are no Parts in this Subchapter."
                    org.w3c.dom.Element p = documentXML.createElement("p");
                    p.appendChild(documentXML.createTextNode("There are no Parts in this Subchapter."));
                    level4.appendChild(heading4El);
                }
                level3.appendChild(level4);
            }
        }
    }

    private void parseLevel5AndFillEntity(String path4, List<Element> tbody5lvl, org.w3c.dom.Document documentXML, org.w3c.dom.Element level4) throws IOException {

        for (Element row : tbody5lvl) {
            if (row.attr("class").equals("odd-parent")) {
                Element td = row.child(2);

                org.w3c.dom.Element level5 = documentXML.createElement("level");

                String heading5 = td.text();
                String path5 = path4 + "/" + heading5;

                Attr attr5 = documentXML.createAttribute("path");
                attr5.setValue(path5);
                level5.setAttributeNode(attr5);

                org.w3c.dom.Element heading5El = documentXML.createElement("heading");
                heading5El.appendChild(documentXML.createTextNode(heading5));
                level5.appendChild(heading5El);

//                level6
                String urlLvl5 = td.getElementsByAttribute("href").get(0).attr(("href"));
                Element div6lvl = Jsoup.connect(urlLvl5).get().getElementById("text-details");

                try {
                    parseLevel6AndFillEntity(path5, div6lvl, documentXML, level5);

                }catch (NullPointerException e){
                    System.out.println("There is no content to parse");
                }

                level4.appendChild(level5);
            }
        }
    }

     private void parseLevel6AndFillEntity(String path5, Element div6lvl, org.w3c.dom.Document documentXML, org.w3c.dom.Element level5) {
         Elements citations = div6lvl.getElementsByTag("h1");
         Elements text = div6lvl.getElementsByTag("section");

         for (int i = 0; i < citations.size(); i++) {
             org.w3c.dom.Element level6 = documentXML.createElement("level");

             String citation = citations.get(i).text().split(" ")[0];
             String heading = citations.get(i).text().substring(3);
             String path = path5 + "/" + heading;

             Attr attr = documentXML.createAttribute("path");
             attr.setValue(path);
             level6.setAttributeNode(attr);

             Attr attr2 = documentXML.createAttribute("text_exist");
             attr2.setValue("True");
             level6.setAttributeNode(attr2);

             org.w3c.dom.Element citationEl = documentXML.createElement("citation");
             citationEl.appendChild(documentXML.createTextNode("Citation " + citation));
             level6.appendChild(citationEl);

             org.w3c.dom.Element heading6El = documentXML.createElement("heading");
             heading6El.appendChild(documentXML.createTextNode(heading));
             level6.appendChild(heading6El);

             org.w3c.dom.Element textElement = documentXML.createElement("text");

             for (Element section : text) {
                 org.w3c.dom.Element p = documentXML.createElement("p");

                 p.appendChild(documentXML.createTextNode(section.getElementsByTag("p").text()));

                 textElement.appendChild(p);
             }

             level6.appendChild(textElement);

             level5.appendChild(level6);
         }
     }
 }
