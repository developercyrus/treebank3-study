package penn.util.hierarchy;

import static penn.util.constants.PennConstants.SENTENCE_ROOT;
import static penn.util.constants.PennConstants.ROOT_NODE;

public class Tree {
    private boolean verbose = false;
    private String sentence = "";   
    private String token = "";
    private int i = 0;
    private int nid = 0;

    public Tree(String sentence) {
        this.sentence = sentence;
    }

    public Node getNode() {        
        Node root = new Node(nid++, 0);   
        root.setLeftBracketAssigned(true);  
        root.setName(ROOT_NODE);
        root = this.build(root, 0);               
        root.setRightBracketAssigned(true);
        
        return root;
    }

    private Node build(Node node, int level) {
        
        while (i<sentence.length()) {
            char c = sentence.charAt(i);      
            
            if (this.isVerbose()) {
                System.out.println(i + "=" + c);
            }
            
            if (c == '(') { 
                i++;
                Node n = new Node(nid++, level+1);
                n.setLeftBracketAssigned(true);  
                
                n = this.build(n, level+1);
                n.setParentId(node.getId());
                node.addChild(n);
            }
            else if (c == ')') {                  
                node.setValue(token); 
                node.setRightBracketAssigned(true);                
                token = "";             

                if (!node.isNameAssigned()) {
                    node.setName(SENTENCE_ROOT);
                }                              
                return node;
            }
            else if (c == ' ') {
                if (!node.isNameAssigned()) {
                    node.setName(token); 
                    token = "";
                }                    
            }
            else {    
                token = token + c;
            }
            i++;
        }
        
        return node;
    }
    
    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

     
}
