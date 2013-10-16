package penn.util.xml;

import static penn.util.constants.PennConstants.DOCUMENT_ROOT;
import static penn.util.constants.PennConstants.FILEID_KEY;
import static penn.util.constants.PennConstants.ID_KEY;
import static penn.util.constants.PennConstants.SECTIONID_KEY;
import static penn.util.constants.PennConstants.SENTENCEID_KEY;
import static penn.util.constants.PennConstants.SENTENCE_ROOT;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NodeMeta {  
    public static String getKey (Node node) {
        Document document = node.getOwnerDocument();
        NodeList nodeList = document.getElementsByTagName(DOCUMENT_ROOT);        
        Element root = (Element)nodeList.item(0);
        
        nodeList = document.getElementsByTagName(SENTENCE_ROOT);        
        Element sentenceRoot = (Element)nodeList.item(0);

        String sectionId = root.getAttribute(SECTIONID_KEY);
        String fileId = root.getAttribute(FILEID_KEY);
        String sentenceId = sentenceRoot.getAttribute(SENTENCEID_KEY);
        String id = node.getAttributes().getNamedItem(ID_KEY).getNodeValue();
        
        String key = sectionId + fileId + padding(sentenceId) + padding(id); 
        System.out.println(key);
        return key;
    }
    
    public static String padding(String s) {
        return String.format("%04d", Integer.parseInt(s));
    }

}
