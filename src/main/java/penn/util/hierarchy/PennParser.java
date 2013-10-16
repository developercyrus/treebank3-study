package penn.util.hierarchy;

import static penn.util.constants.PennConstants.DOCUMENT_ROOT;
import static penn.util.constants.PennConstants.EMPTY_NODE;
import static penn.util.constants.PennConstants.ID_KEY;
import static penn.util.constants.PennConstants.LEVEL_KEY;
import static penn.util.constants.PennConstants.PID_KEY;
import static penn.util.constants.PennConstants.ROOT_NODE;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class PennParser {
    private boolean verbose = false;
    private boolean showAttr = false; 
    private boolean stripTrace = false;

    public Document getDocument(String s) throws XPathExpressionException {
        Tree tree = new Tree(s);
        Node node = tree.getNode();
        if (verbose) {
            System.out.println(node);
        }
        try {
            return this.toXml(node);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    private Document toXml(Node node) throws ParserConfigurationException, XPathExpressionException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();
        
        Element element = doc.createElement(DOCUMENT_ROOT);
        Object[] o = this.traverse(node, doc, element);                
        doc.appendChild((Element)o[1]);
        
        //this.removeEmptyNode(doc);
        //this.removeEmptyNode2(doc);
        this.removeEmptyNode3(doc);
        
        return doc;
    }   
    
    private Object[] traverse(Node node, Document doc, Element element) {
        Object[] o;
        
        List<Node> childs = node.getChilds();
        Node n = node;

        Element e = null;
        try {            
            /*
             * do not create 
             * 1. an tree node, i.e. a hidden node (indicated []) before each sentence begin  [(]( (S
             * 2. an empty node, e.g., (NP-SBJ (-NONE- *-10) )
             */
            if (!node.getName().equals(ROOT_NODE) && !node.getName().equals(EMPTY_NODE)) { 
                String nodeName = escapeName(node.getName());
                if (stripTrace) {
                    nodeName = stripTrace(nodeName);
                }                
                e = doc.createElement(nodeName);

                if (node.getValue()!=null) {
                    e.setTextContent(node.getValue());
                }

                if (showAttr) {                
                    Attr id = doc.createAttribute(ID_KEY);
                    id.setValue(String.valueOf(node.getId()));
                    e.setAttributeNode(id);
                    
                    Attr pid = doc.createAttribute(PID_KEY);
                    pid.setValue(String.valueOf(node.getParentId()));
                    e.setAttributeNode(pid);
                    
                    Attr level = doc.createAttribute(LEVEL_KEY);
                    level.setValue(String.valueOf(node.getLevel()));
                    e.setAttributeNode(level);
                }
                    
                element.appendChild(e);
            }
            else {          
                e = element;
            }
        }
        catch (Exception ex) {
            System.out.println(node.getName());
            ex.printStackTrace();
            System.exit(1);
        }
                     
        for (int j=0; j<childs.size(); j++) {  
            o = this.traverse(childs.get(j), doc, e);            
        }
        
        o = new Object[2];
        o[0] = node;
        o[1] = element;
        
        return o;           
    }
    
    
    /**
     * remove empty nodes of non-terminals e.g., {@code <NP-SBJ/> }
     * <pre>
     * {@code
     *  <S-NOM>
     *      <NP-SBJ/>
     *      <VP>
     *          <VBG word="conforming"/>
     *          <NP>
     *              <PRP word="it"/>
     *          </NP>
     *          <ADVP-MNR>
     *              <RBR word="more"/>
     *              <RB word="closely"/>
     *          </ADVP-MNR>
     *          <PP-CLR>
     *              <TO word="to"/>
     *              <NP>
     *                  <JJ word="contemporary"/>
     *                  <NN word="business"/>
     *                  <NNS word="realities"/>
     *              </NP>
     *          </PP-CLR>
     *      </VP>
     *  </S-NOM>
     * }  
     * </pre>
     * @param  node  org.w3c.dom.Node
     *
     */
    private void removeEmptyNode(org.w3c.dom.Node node) {
        /*
        if (node.getChildNodes().getLength() == 0 && node.getAttributes().getNamedItem("word") == null) {
            node.getParentNode().removeChild(node);
            return;
        }
        */

        if (node.getAttributes() != null && node.getAttributes().getNamedItem(ID_KEY) != null) {
            System.out.println(node.getAttributes().getNamedItem("id").getNodeValue() + "="+ node.getNodeName());
        }
        
        if (node.getChildNodes().getLength() == 0 && node.getTextContent().equals("")) {           
            node.getParentNode().removeChild(node);
            return;
        }
        
        NodeList list = node.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            removeEmptyNode(list.item(i));
        } 
    }
    
    /**
     * didn't work as expected by XPath
     */
    private void removeEmptyNode2(Document doc) throws XPathExpressionException {
        XPathFactory xpathFactory = XPathFactory.newInstance();
        //XPathExpression xpathExp = xpathFactory.newXPath().compile("//text()[normalize-space(.) = '']");  
        XPathExpression xpathExp = xpathFactory.newXPath().compile("//text()[normalize-space(.) = '']");
        NodeList emptyTextNodes = (NodeList) xpathExp.evaluate(doc, XPathConstants.NODESET);

        for (int i = 0; i < emptyTextNodes.getLength(); i++) {            
            org.w3c.dom.Node emptyTextNode = emptyTextNodes.item(i);
            System.out.println(emptyTextNode.getAttributes().getNamedItem("id").getNodeValue());
            emptyTextNode.getParentNode().removeChild(emptyTextNode);
        }
    }
    
    
    /**
     * from bottom to top, from inner to outer, by SAX
     */
    private void removeEmptyNode3(Document doc) throws XPathExpressionException {
        NodeList nodeList = doc.getElementsByTagName("*");
        //for (int i = 0; i < nodeList.getLength(); i++) {
        for (int i = nodeList.getLength()-1; i >= 0; i--) {
            org.w3c.dom.Node node = nodeList.item(i);
            if (node.getAttributes() != null && node.getAttributes().getNamedItem(ID_KEY) != null) {
                System.out.println( 
                        node.getAttributes().getNamedItem("id").getNodeValue() + "="+ node.getNodeName() + ", " +
                        "node.getChildNodes().getLength()" + "=" + node.getChildNodes().getLength() + ", " + 
                        "node.getTextContent().equals(\"\")" + "=" + node.getTextContent().equals("")         
                );
            }
            
            if (node.getChildNodes().getLength() == 0 && node.getTextContent().equals("")) {           
                node.getParentNode().removeChild(node);
            }
        }
    }
    
    private String escapeName(String s) {
        Matcher matcher = Pattern.compile("[A-Z]*=").matcher(s);
        if (matcher.find()) {
             return s.replaceAll("=", "-");
        }
        
        if (s.equals(",")) {
            return "COMMA";
        }
        if (s.equals(":")) {
            return "COLON";
        }
        if (s.equals("$")) {
            return "DOLLARSIGN";
        }
        if (s.equals("#")) {
            return "DOLLARSIGN";
        }
        if (s.equals(".")) {
            return "FULLSTOP";
        }
        if (s.equals("``")) {
            return "LDQ";
        }
        if (s.equals("''")) {
            return "RDQ";
        }
        if (s.equals("PRP$")) {
            return "PRPS";
        }
        if (s.equals("WP$")) {
            return "WPS";
        }
        if (s.equals("-LRB-")) {
            return "LRB";
        }
        if (s.equals("-RRB-")) {
            return "RRB";
        }
        if (s.equals("-LCB-")) {
            return "LRB";
        }
        if (s.equals("-RCB-")) {
            return "RRB";
        }
        if (s.equals("-NONE-")) {
            return "NONE";
        }
        if (s.equals("ADVP|PRT")) {
            return "ADVP";
        }
        if (s.equals("PRT|ADVP")) {
            return "PRT";
        }

        return s;
    }
    
    private String escapeValue(String s) {
        if (s.equals("-LCB-")) {
            return "{";
        }
        else if (s.equals("-RCB-")) {
            return "}";
        }
        else {
            return s;
        }
    }
    
    
    /**
     * remove node that has functional tag, e.g {@code <NP-SBJ> }
     */
    private String stripTrace(String s) {
        int p = s.indexOf("-");
        if (p != -1) {
            s = s.substring(0, p);
        }
        return s;        
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public boolean isShowId() {
        return showAttr;
    }

    public void setShowId(boolean showId) {
        this.showAttr = showId;
    }
    
    public boolean isStripTrace() {
        return stripTrace;
    }

    public void setStripTrace(boolean stripTrace) {
        this.stripTrace = stripTrace;
    }

}
