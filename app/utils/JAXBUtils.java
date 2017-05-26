package utils;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.MarshalException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;

public class JAXBUtils {
    public static String marshallToString(Object object)
            throws MarshalException {
        return marshallToString(object, true);
    }

    private static String marshallToString(Object object,
            boolean includeXMLHeader) throws MarshalException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(
                100);
        try {
            JAXBContext context = JAXBContext.newInstance(object.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, !includeXMLHeader);
            marshaller.marshal(object, byteArrayOutputStream);
            return new String(byteArrayOutputStream.toByteArray());
        } catch (Throwable e) {
            throw new MarshalException("Unable to marshal object to stream", e);
        } finally {
            IOUtils.closeQuietly(byteArrayOutputStream);
        }
    }

    private static <T> T unmarshallFromString(String sourceString,
            Class<T> resultingClass) throws UnmarshalException {
        try {
            JAXBContext context = JAXBContext.newInstance(resultingClass);
            return (T) context.createUnmarshaller().unmarshal(
                    new StringReader(sourceString));
        } catch (Throwable e) {
            throw new UnmarshalException("Unable to unmarshal string", e);
        }
    } 
    
    public static  <T> T unmarshallUploadFromDocument(Document dom,
            String rootElement, Class<T> resultingClass)
            throws UnmarshalException {
        try {           
            String xmlString = getStringFromXML(dom, rootElement); 
            xmlString = xmlString.replaceAll(" xmlns(?:.*?)?=\".*?\"", "");            
            
            if (rootElement != null) {
                xmlString = "<" + rootElement + ">" + xmlString + "</"
                        + rootElement + ">";
            }

            return (T) unmarshallFromString(xmlString, resultingClass);
        } catch (Throwable e) {
            throw new UnmarshalException("Unable to unmarshal string", e);
        }
    }

    public static final <T> T unmarshallUploadFromDocumentString(String xmlString,
                                                           String rootElement, Class<T> resultingClass)
            throws UnmarshalException {
        try {
            xmlString = xmlString.replaceAll(" xmlns(?:.*?)?=\".*?\"", "");

            if (rootElement != null) {
                xmlString = "<" + rootElement + ">" + xmlString + "</"
                        + rootElement + ">";
            }

            return (T) unmarshallFromString(xmlString, resultingClass);
        } catch (Throwable e) {
            throw new UnmarshalException("Unable to unmarshall string", e);
        }
    }
    
    private static String getStringFromXML(Document dom,
            String rootElement) throws UnmarshalException {
        try {
            DOMSource domSource = new DOMSource(dom);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty("omit-xml-declaration", "yes");
            transformer.transform(domSource, result);
            String xmlString = writer.toString();            
            
            if (rootElement != null) {
                xmlString = "<" + rootElement + ">" + xmlString + "</"
                        + rootElement + ">";
            }
            
            return xmlString;
        } catch (Throwable e) {
            throw new UnmarshalException("Unable to unmarshall string", e);
        }
    }
}