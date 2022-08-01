package web.utils;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlParser extends DefaultHandler {
    static HashMap hashMap;
    Map<String, List<String>> hashMap1=new HashMap<>();
    ArrayList<String> Accounts = new ArrayList<>();
    String XmlFileName;
    String tmpValue;

    public XmlParser(String IntXmlFileName){
        this.XmlFileName=IntXmlFileName;
        hashMap = new HashMap();
        parseDocument();
    }
    private void parseDocument(){
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = factory.newSAXParser();
            parser.parse(XmlFileName, this);

        }
        catch(ParserConfigurationException e){
            System.out.println("ParserConfig Error");
        }
        catch(SAXException e){
            System.out.println("SAXException: xml not well formed");
        }
        catch (IOException e){
            System.out.println("IO Exception");
        }
    }
}
