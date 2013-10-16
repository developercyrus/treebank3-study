package penn.util.hierarchy;

import static penn.util.constants.PennConstants.DOCUMENT_ROOT;
import static penn.util.constants.PennConstants.SENTENCEID_KEY;
import static penn.util.constants.PennConstants.SENTENCE_ROOT;
import static penn.util.constants.PennConstants.XML_INDENT;

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
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import penn.util.xml.AttributeAssigner;
import penn.util.xml.DocumentMerger;
import penn.util.xml.DocumentMeta;
import penn.util.xml.Raw2Sentence;

public class PennParserTest {

    @Test
    public void test1() throws IOException, TransformerFactoryConfigurationError, TransformerException, XPathExpressionException {
        //String inPath = PennParserTest.class.getResource("/corpus/treebank-3/PARSED/MRG/WSJ/01/WSJ_0108a.MRG").getFile();
        //String inPath = PennParserTest.class.getResource("/corpus/treebank-3/PARSED/MRG/WSJ/00/WSJ_0005.MRG").getFile();
        String inPath = PennParserTest.class.getResource("/corpus/treebank-3/PARSED/MRG/WSJ/01/WSJ_0100.MRG").getFile();
        //String inPath = PennParserTest.class.getResource("/corpus/treebank-3/PARSED/MRG/BROWN/CF/CF01a.MRG").getFile();
     
        Raw2Sentence s = new Raw2Sentence(inPath);
        String[] sentences = s.split();
        
        PennParser p = new PennParser();
        p.setVerbose(true);
        p.setShowId(true);
        p.setStripTrace(true);
                
        Document[] docs = new Document[sentences.length];
        for (int k=0; k<docs.length; k++) {
            docs[k] = p.getDocument(sentences[k]);
            
            if (true) {
                NodeList nodes = docs[k].getElementsByTagName(SENTENCE_ROOT);        
                Element e = (Element)nodes.item(0);
                e.setAttribute(SENTENCEID_KEY, String.valueOf(k));
            }
        }
        Document merged = DocumentMerger.mergeAll(docs);
        
        DocumentMeta m = new DocumentMeta("/WSJ_0108a.MRG").setSentenceSize(docs.length);               
        merged = AttributeAssigner.addAttributes(merged, DOCUMENT_ROOT, m);

        DOMSource xmlInput = new DOMSource(merged);        
        StreamResult xmlOutput = new StreamResult(new StringWriter());
        Transformer transformer = TransformerFactory.newInstance().newTransformer();         
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", XML_INDENT);
        transformer.transform(xmlInput, xmlOutput);
        
        String content = xmlOutput.getWriter().toString();
        System.out.println(content);
    }
}
