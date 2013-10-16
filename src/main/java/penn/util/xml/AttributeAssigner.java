package penn.util.xml;

import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class AttributeAssigner {
    public static Document addAttributes(Document d, String nodeName, Map<String, String> map) {        
        NodeList nodes = d.getElementsByTagName(nodeName);        
        Element e = (Element)nodes.item(0);
        for (String key : map.keySet()) {
            e.setAttribute(key, map.get(key));
        }        
        return d;
    }
}
