package cn.newphy.mate.util;

import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


public class XmlUtils {

	/**
	 * 将java bean转为xml
	 * @param bean
	 * @param encoding
	 * @return
	 * @throws JAXBException
	 */
	public static String bean2Xml(Object bean, String encoding) throws JAXBException {
		String result = null;
		JAXBContext context = JAXBContext.newInstance(bean.getClass());
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);

		StringWriter writer = new StringWriter();
		marshaller.marshal(bean, writer);
		result = writer.toString();
		return result;
	}


}
