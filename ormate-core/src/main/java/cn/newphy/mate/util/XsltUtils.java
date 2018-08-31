package cn.newphy.mate.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * xslt转换工具
 *
 * @author Newphy
 * @createTime 2018/7/31
 */
public class XsltUtils {

    /**
     * 转换xsl为xml
     * @param templates
     * @param xmldata
     * @return
     * @throws TransformerException
     */
    public static String transform(Templates templates, String xmldata) throws TransformerException {
        // Use the template to create a transformer
        Transformer xformer = templates.newTransformer();

        // Prepare the input and output files
        Source source = new StreamSource(new StringReader(xmldata));
        StringWriter writer = new StringWriter();
        Result result = new StreamResult(writer);

        // Apply the xsl file to the source file and write the result to the
        // output file
        xformer.transform(source, result);
        return writer.toString();

    }

}
