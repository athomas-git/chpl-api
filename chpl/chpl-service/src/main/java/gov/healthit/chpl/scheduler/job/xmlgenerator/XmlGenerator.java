package gov.healthit.chpl.scheduler.job.xmlgenerator;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class XmlGenerator {
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssXXX";
    public static void createSimpleElement(Long value, String nodeName, XMLStreamWriter sw) throws XMLStreamException {
        if (value != null) {
            sw.writeStartElement(nodeName);
            sw.writeCharacters(value.toString());
            sw.writeEndElement();
        }
        
    }
    
    public static void createSimpleElement(String value, String nodeName, XMLStreamWriter sw) throws XMLStreamException {
        if (value != null) {
            sw.writeStartElement(nodeName);
            sw.writeCharacters(value);
            sw.writeEndElement();
        }
    }
    
    public static void createSimpleElement(Integer value, String nodeName, XMLStreamWriter sw) throws XMLStreamException {
        if (value != null) {
            sw.writeStartElement(nodeName);
            sw.writeCharacters(value.toString());
            sw.writeEndElement();
        }
    }
    
    public static void createSimpleElement(Boolean value, String nodeName, XMLStreamWriter sw) throws XMLStreamException {
        if (value != null) {
            sw.writeStartElement(nodeName);
            sw.writeCharacters(value.toString());
            sw.writeEndElement();
        }
        
    }
    
    public static void createSimpleElement(Date value, String format, String nodeName, XMLStreamWriter sw) throws XMLStreamException {
        if (value != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            sw.writeStartElement(nodeName);
            sw.writeCharacters(sdf.format(value));
            sw.writeEndElement();
        }
    }
    
    public static void createSimpleElement(Date value, String nodeName, XMLStreamWriter sw) throws XMLStreamException {
        if (value != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            sw.writeStartElement(nodeName);
            sw.writeCharacters(sdf.format(value));
            sw.writeEndElement();
        }
        
    }

    public static void createSimpleElement(Float value, String nodeName, XMLStreamWriter sw) throws XMLStreamException {
        if (value != null) {
            sw.writeStartElement(nodeName);
            sw.writeCharacters(Float.toString(value));
            sw.writeEndElement();
        }
        
    }
}