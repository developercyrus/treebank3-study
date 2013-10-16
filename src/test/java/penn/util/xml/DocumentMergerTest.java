package penn.util.xml;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;

import org.junit.Test;
import org.w3c.dom.Document;

import penn.util.hierarchy.PennParser;
import penn.util.xml.DocumentMerger;
import penn.util.xml.Raw2Sentence;

public class DocumentMergerTest {
    
    @Test
    public void test1() throws IOException, TransformerFactoryConfigurationError, TransformerException, XPathExpressionException {
        String path = Raw2SentenceTest.class.getResource("/corpus/treebank-3/PARSED/MRG/WSJ/00/WSJ_0001a.MRG").getFile();
        Raw2Sentence s = new Raw2Sentence(path);
        String[] sentences = s.split();
        
        PennParser p = new PennParser();
        
        Document[] docs = new Document[sentences.length];
        for (int i=0; i<docs.length; i++) {
            docs[i] = p.getDocument(sentences[i]);
        }

        Document merged = DocumentMerger.mergeAll(docs);
        
        DOMSource xmlInput = new DOMSource(merged);        
        StreamResult xmlOutput = new StreamResult(new StringWriter());
        Transformer transformer = TransformerFactory.newInstance().newTransformer();         
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.transform(xmlInput, xmlOutput);
        
        System.out.println(xmlOutput.getWriter().toString());
    }
}
