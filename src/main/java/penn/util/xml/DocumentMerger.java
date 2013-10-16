package penn.util.xml;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DocumentMerger {
    public static Document mergeAll(Document[] docs) throws TransformerFactoryConfigurationError, TransformerException {
        Document d = docs[0];
        
        for (int i=0; i<docs.length; i++) {
            if (i+1 < docs.length) {
                d = mergeTwo(d, docs[i+1]);
            }
        }
        return d;
    }
    
    
    public static Document mergeTwo(Document d1, Document d2) {
        Element fromRoot = d2.getDocumentElement();
        Element toRoot = d1.getDocumentElement();

        org.w3c.dom.Node child = null;
        while ((child = fromRoot.getFirstChild()) != null) {
            d1.adoptNode(child);
            toRoot.appendChild(child);
        }

        return d1;
    }
}
