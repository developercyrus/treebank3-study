package penn.util.xml;

import static penn.util.constants.PennConstants.DOCUMENT_ROOT;
import static penn.util.constants.PennConstants.RAW_FILE_EXTENSION;
import static penn.util.constants.PennConstants.SENTENCEID_KEY;
import static penn.util.constants.PennConstants.SENTENCE_ROOT;
import static penn.util.constants.PennConstants.SOURCE_CODE_PATH;
import static penn.util.constants.PennConstants.WSJ_CORPUS_PATH;
import static penn.util.constants.PennConstants.XML_FILE_EXTENSION;
import static penn.util.constants.PennConstants.XML_INDENT;
import static penn.util.constants.PennConstants.XML_OUTPUT_PATH;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import penn.util.hierarchy.PennParser;
import penn.util.io.Dir;
import penn.util.io.FileDumper;

public class XmlExporter {
    private boolean showSentenceId = true;

    public static void main(String[] args) throws IOException, ParserConfigurationException, TransformerException, XPathExpressionException, TransformerFactoryConfigurationError {
        String root = WSJ_CORPUS_PATH;      
        XmlExporter l = new XmlExporter();
        l.exportAll();
    }

    public void exportAll() throws IOException, ParserConfigurationException, TransformerException, XPathExpressionException, TransformerFactoryConfigurationError {        
        String inPath = XmlExporter.class.getResource(WSJ_CORPUS_PATH).getPath();
        String[] sections = Dir.getDirectoryList(inPath);
        String[] files;
        
        for (int i=0; i<sections.length; i++) {
            inPath = XmlExporter.class.getResource(WSJ_CORPUS_PATH + "/" + sections[i]).getPath();
            files = Dir.getFileList(inPath);
            
            for (int j=0; j<files.length; j++) {
                inPath = XmlExporter.class.getResource(WSJ_CORPUS_PATH + "/" + sections[i] + "/" + files[j]).getPath();
                String outPath = SOURCE_CODE_PATH + XML_OUTPUT_PATH + WSJ_CORPUS_PATH + "/" + sections[i] + "/" + files[j].replaceAll(RAW_FILE_EXTENSION, XML_FILE_EXTENSION);          
                export(inPath, outPath);
            }
        }
    }   
    
    public void export(String inPath, String outPath) throws TransformerFactoryConfigurationError, TransformerException, IOException, XPathExpressionException {
        System.out.println(inPath);
        
        Raw2Sentence s = new Raw2Sentence(inPath);
        String[] sentences = s.split();
        
        PennParser p = new PennParser();
        p.setShowId(true);
        p.setStripTrace(true);
        
        Document[] docs = new Document[sentences.length];
        for (int k=0; k<docs.length; k++) {
            docs[k] = p.getDocument(sentences[k]);
            
            if (this.isShowSentenceId()) {
                NodeList nodes = docs[k].getElementsByTagName(SENTENCE_ROOT);        
                Element e = (Element)nodes.item(0);
                e.setAttribute(SENTENCEID_KEY, String.valueOf(k));
            }
        }        
        Document merged = DocumentMerger.mergeAll(docs);
        
        DocumentMeta m = new DocumentMeta(inPath).setSentenceSize(docs.length);
        merged = AttributeAssigner.addAttributes(merged, DOCUMENT_ROOT,  m);

        DOMSource xmlInput = new DOMSource(merged);        
        StreamResult xmlOutput = new StreamResult(new StringWriter());
        Transformer transformer = TransformerFactory.newInstance().newTransformer();         
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", XML_INDENT);
        transformer.transform(xmlInput, xmlOutput);
        
        String content = xmlOutput.getWriter().toString();
        System.out.println(content);

        FileDumper.save(outPath, content);        
    }
    
    public boolean isShowSentenceId() {
        return showSentenceId;
    }

    public void setShowSentenceId(boolean showSentenceId) {
        this.showSentenceId = showSentenceId;
    }

}
