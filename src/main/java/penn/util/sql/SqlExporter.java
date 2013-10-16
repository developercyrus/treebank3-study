package penn.util.sql;

import static penn.util.constants.PennConstants.DOCUMENT_ROOT;
import static penn.util.constants.PennConstants.FILE_SCRIPT_OUTPUT_PATH;
import static penn.util.constants.PennConstants.ID_KEY;
import static penn.util.constants.PennConstants.PATH_SCRIPT_OUTPUT_PATH;
import static penn.util.constants.PennConstants.SECTION_SCRIPT_OUTPUT_PATH;
import static penn.util.constants.PennConstants.SENTENCE_ROOT;
import static penn.util.constants.PennConstants.SENTENCE_SCRIPT_OUTPUT_PATH;
import static penn.util.constants.PennConstants.SOURCE_CODE_PATH;
import static penn.util.constants.PennConstants.SQL_FILE_EXTENSION;
import static penn.util.constants.PennConstants.SQL_OUTPUT_PATH;
import static penn.util.constants.PennConstants.WSJ_CORPUS_PATH;
import static penn.util.constants.PennConstants.XML_FILE_EXTENSION;
import static penn.util.constants.PennConstants.XML_OUTPUT_PATH;
import static penn.util.constants.SqlScripts.GO;
import static penn.util.constants.SqlScripts.INSERT_FILE;
import static penn.util.constants.SqlScripts.INSERT_NODE;
import static penn.util.constants.SqlScripts.INSERT_PATH;
import static penn.util.constants.SqlScripts.INSERT_SECTION;
import static penn.util.constants.SqlScripts.INSERT_SENTENCE;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import penn.util.io.Dir;
import penn.util.io.FileDumper;
import penn.util.xml.DocumentSplitter;
import penn.util.xml.NodeMeta;

public class SqlExporter {
    Map<String, Integer> keyId = new HashMap<String, Integer>();
    
    StringBuffer sectionStr = new StringBuffer();
    StringBuffer fileStr = new StringBuffer();
    StringBuffer sentenceStr = new StringBuffer();
    StringBuffer nodeStr = new StringBuffer();
    StringBuffer pathStr = new StringBuffer();

    int g_snid=0;   //section id
    int g_fid=0;    //file id    
    int g_seid=0;   //sentence id
    int g_nid=0;    //node id
    
    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, TransformerFactoryConfigurationError, TransformerException {
        SqlExporter g = new SqlExporter();
        g.exportAll();
    }
    
    public void exportAll() throws ParserConfigurationException, SAXException, IOException, TransformerFactoryConfigurationError, TransformerException {
        String inPath;
        String xmlPath = SOURCE_CODE_PATH + XML_OUTPUT_PATH + WSJ_CORPUS_PATH; 
        String[] sections = Dir.getDirectoryList(xmlPath);
        String[] files;
        Document[] sentences;

        for (int i=0; i<sections.length; i++) {
            inPath = xmlPath + "/" + sections[i];
            files = Dir.getFileList(inPath);
            
            String outPath = SOURCE_CODE_PATH + SQL_OUTPUT_PATH + WSJ_CORPUS_PATH + SECTION_SCRIPT_OUTPUT_PATH;
            this.exportSection(sections[i], outPath);

            for (int j=0; j<files.length; j++) {
                inPath = xmlPath + "/" + sections[i] + "/" + files[j];
                sentences = DocumentSplitter.divide(inPath);
                
                outPath = SOURCE_CODE_PATH + SQL_OUTPUT_PATH + WSJ_CORPUS_PATH + "/" + sections[i] + FILE_SCRIPT_OUTPUT_PATH;
                this.exportFile(files[j], outPath);

                for (int k=0; k<sentences.length; k++) { 
                    
                    outPath = SOURCE_CODE_PATH + SQL_OUTPUT_PATH + WSJ_CORPUS_PATH + "/" + sections[i] + SENTENCE_SCRIPT_OUTPUT_PATH;
                    this.exportSentence(sentences[k], outPath);

                    outPath = SOURCE_CODE_PATH + SQL_OUTPUT_PATH + WSJ_CORPUS_PATH + "/" + sections[i] + "/" + files[j].replaceAll(XML_FILE_EXTENSION, SQL_FILE_EXTENSION);           
                    this.exportNode(sentences[k], outPath);
                    
                    outPath = SOURCE_CODE_PATH + SQL_OUTPUT_PATH + WSJ_CORPUS_PATH + "/" + sections[i] + PATH_SCRIPT_OUTPUT_PATH;           
                    this.exportPath(sentences[k], outPath);
                }
            }

        }
    }
    
    
    public void exportSection(String sectionName, String outPath) {
        g_snid++;
        String ID = String.valueOf(g_snid);
        String NAME = sectionName;
        String content = String.format(INSERT_SECTION, ID, NAME) + System.getProperty("line.separator") + GO + System.getProperty("line.separator");
        
        System.out.println(content);
        sectionStr.append(content);        

        
        FileDumper.save(outPath, sectionStr.toString());
        sectionStr.setLength(0);
    }
    
    public void exportFile(String fileName, String outPath) {
        g_fid++;
        String ID = String.valueOf(g_fid);
        String SECTION_ID = String.valueOf(g_snid);
        String NAME = fileName;
        String content = String.format(INSERT_FILE, ID, SECTION_ID, NAME) + System.getProperty("line.separator") + GO + System.getProperty("line.separator");
        
        System.out.println(content);
        fileStr.append(content);
        
        FileDumper.save(outPath, fileStr.toString());
        fileStr.setLength(0);
    }
    
    public void exportSentence(Document sentence, String outPath) throws TransformerFactoryConfigurationError, TransformerException, ParserConfigurationException {
        g_seid++;
        String ID = String.valueOf(g_seid);
        String FILE_ID = String.valueOf(g_fid);
        String XML = this.escapeQuote(this.getLinearizedXmlStr(sentence.getElementsByTagName("*").item(0)));
        String content = String.format(INSERT_SENTENCE, ID, FILE_ID, XML) + System.getProperty("line.separator") + GO + System.getProperty("line.separator");
        
        System.out.println(content);
        sentenceStr.append(content);

        FileDumper.save(outPath, sentenceStr.toString());
        sentenceStr.setLength(0);
    }
    
    public void exportNode(Document document, String outPath) throws TransformerFactoryConfigurationError, TransformerException, ParserConfigurationException {
        NodeList nodeList = document.getElementsByTagName(SENTENCE_ROOT);
        for (int i=0; i<nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);
            this.traveseNode(g_seid, currentNode); 
        }
        
        FileDumper.save(outPath, nodeStr.toString());
        nodeStr.setLength(0);
    }

    public void traveseNode(int sentenceId, Node node) throws TransformerFactoryConfigurationError, TransformerException, ParserConfigurationException {        
        g_nid++;
        
        NodeList nodeList = node.getChildNodes();
        String ID = String.valueOf(g_nid);
        String SENTENCE_ID = String.valueOf(sentenceId);
        String NID = node.getAttributes().getNamedItem(ID_KEY).getNodeValue();
        String TYPE = ((nodeList.getLength() == 1) ? "T" : "NT");
        String VALUE = node.getNodeName();
        String WORD = ((nodeList.getLength() == 1) ?  node.getTextContent().trim() : "");          

        String content = String.format(INSERT_NODE, ID, SENTENCE_ID, NID, TYPE, VALUE, this.escapeQuote(WORD)) + System.getProperty("line.separator") + GO + System.getProperty("line.separator");
        System.out.println(content);
        nodeStr.append(content);
        
        String key = NodeMeta.getKey(node);
        keyId.put(key, g_nid);
                
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                traveseNode(sentenceId, currentNode);
            }
        }        
    }
    
    
    public String getLinearizedXmlStr(Node node) throws TransformerFactoryConfigurationError, TransformerException, ParserConfigurationException {
        //String separator = System.getProperty("line.separator");
        //System.setProperty("line.separator", "");
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document newDocument = builder.newDocument();
        Node importedNode = newDocument.importNode(node, true);
        newDocument.appendChild(importedNode);

        DOMSource xmlInput = new DOMSource(newDocument);        
        StreamResult xmlOutput = new StreamResult(new StringWriter());
        TransformerFactory transformerFactory = TransformerFactory.newInstance();        
        Transformer transformer = transformerFactory.newTransformer();     
        
        //It didn't work to remove the indent
        /*
        transformerFactory.setAttribute("indent-number", new Integer(0));  
        transformer.setOutputProperty(OutputKeys.INDENT, "no");      
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "0");
        */
        
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");        
        transformer.transform(xmlInput, xmlOutput);        

        String content = xmlOutput.getWriter().toString();
        // a hack to remove indent by regular expression
        content = xmlOutput.getWriter().toString().replaceAll("(?m)^\\s+|\\n|\\r", "");
        
        //System.setProperty("line.separator", separator);

        return content;        
    }
    
    public void exportPath(Document document, String outPath) throws TransformerFactoryConfigurationError, TransformerException, ParserConfigurationException {
        NodeList nodeList = document.getElementsByTagName(SENTENCE_ROOT);
        Node currentNode = nodeList.item(0);        
        String NODE_ID = String.valueOf(keyId.get(NodeMeta.getKey(currentNode)));
        this.traveseParent(g_seid, NODE_ID, currentNode, 0, 0, "/" + currentNode.getNodeName()); 
        this.travesePath(currentNode); 

        FileDumper.save(outPath, pathStr.toString());
        pathStr.setLength(0);
    }

    public void travesePath(Node node) throws TransformerFactoryConfigurationError, TransformerException, ParserConfigurationException {        
        NodeList nodeList = node.getChildNodes();
        int siblingId= 0;
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                String NODE_ID = String.valueOf(keyId.get(NodeMeta.getKey(currentNode)));
                this.traveseParent(g_seid, NODE_ID, currentNode, siblingId, 0, "/" + currentNode.getNodeName()); 
                siblingId++;
                this.travesePath(currentNode);
                
            }
        }        
    }
    
    
    /*
    public void exportPath(Document document, String outPath) throws TransformerFactoryConfigurationError, TransformerException, ParserConfigurationException {
        NodeList nodeList = document.getElementsByTagName("*");
        for (int i=0; i<nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i); 
            if (!currentNode.getNodeName().equals(DOCUMENT_ROOT)) {
                String NODE_ID = String.valueOf(keyId.get(NodeMeta.getKey(currentNode)));
                this.traveseParent(g_seid, NODE_ID, currentNode, 0, "/" + currentNode.getNodeName()); 
            }
        }
        
        FileDumper.save(outPath, pathStr.toString());
        pathStr.setLength(0);
    }
    */
    
    public void traveseParent(int sentenceId, String NODE_ID, Node node, int siblingId, int height, String xpath) {        
        Node parent = (height == 0 ? node : node.getParentNode()); 
        
        if (!parent.getNodeName().equals(DOCUMENT_ROOT)) {
            String SENTENCE_ID = String.valueOf(sentenceId);
            String NODE_PID = (height == 0 ? NODE_ID : String.valueOf(keyId.get(NodeMeta.getKey(parent))));  
            String SIBLING_ID = String.valueOf(siblingId);
            String HEIGHT = String.valueOf(height);
            String XPATH = (height == 0 ? xpath : "/" + parent.getNodeName() + xpath);
            String content = String.format(INSERT_PATH, SENTENCE_ID, NODE_ID, NODE_PID, SIBLING_ID, HEIGHT, XPATH) + System.getProperty("line.separator") + GO + System.getProperty("line.separator");
            
            System.out.println(content);
            pathStr.append(content);

            this.traveseParent(sentenceId, NODE_ID, parent, siblingId, height+1, XPATH);
        }
    }    
    
    public String escapeQuote(String s) {
        return s.replaceAll("'", "''");
    }
}
