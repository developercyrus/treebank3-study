package penn.util.xml;

import static penn.util.constants.PennConstants.DOCUMENT_ROOT;
import static penn.util.constants.PennConstants.FILEID_KEY;
import static penn.util.constants.PennConstants.SECTIONID_KEY;
import static penn.util.constants.PennConstants.SENTENCESIZE_KEY;
import static penn.util.constants.PennConstants.SENTENCE_ROOT;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DocumentSplitter {
    public static Document[] divide(Document document) throws ParserConfigurationException {
        NodeList nodeList = document.getElementsByTagName(DOCUMENT_ROOT);        
        Element root = (Element)nodeList.item(0);
        
        nodeList = document.getElementsByTagName(SENTENCE_ROOT);
        Document[] documents = new Document[nodeList.getLength()];
        
        for (int i=0; i<nodeList.getLength(); i++) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document newDocument = builder.newDocument();
            Node importedNode = newDocument.importNode(nodeList.item(i), true);
            newDocument.appendChild(root);
            newDocument.appendChild(importedNode);
            
            documents[i] = newDocument;
        }
        
        return documents;
    }
    
    
    public static Document[] divide(String inPath) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document document = docBuilder.parse(new File(inPath));
        
        NodeList nodes = document.getElementsByTagName(DOCUMENT_ROOT);        
        Element originalRoot = (Element)nodes.item(0);
       
        nodes = document.getElementsByTagName(SENTENCE_ROOT);
        Document[] documents = new Document[nodes.getLength()];
        
        for (int i=0; i<nodes.getLength(); i++) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document newDocument = builder.newDocument();            
            Element newRoot = newDocument.createElement(DOCUMENT_ROOT);
            newRoot.setAttribute(SECTIONID_KEY, originalRoot.getAttribute(SECTIONID_KEY));
            newRoot.setAttribute(FILEID_KEY, originalRoot.getAttribute(FILEID_KEY));
            newRoot.setAttribute(SENTENCESIZE_KEY, originalRoot.getAttribute(SENTENCESIZE_KEY));
            newDocument.appendChild(newRoot);
            newRoot.appendChild(newDocument.importNode(nodes.item(i), true));

            documents[i] = newDocument;
        }
        
        return documents;
    }
    
}
