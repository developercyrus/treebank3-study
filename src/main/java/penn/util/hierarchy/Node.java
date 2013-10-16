package penn.util.hierarchy;
import java.util.ArrayList;
import java.util.List;

public class Node {    
    private boolean verbose = true;
    private Integer id;
    private Integer parentId;
    private Integer level;
    private List<Node> childs = null;
    private String name = null;
    private String value = null;
    private boolean leftBracketAssigned = false;
    private boolean rightBracketAssigned = false;
    boolean isNameAssigned = false;
    
    public Node(int id, int level) {
        this.id = id;
        this.level = level;
        this.childs = new ArrayList<Node>();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }   

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public void setName(String name) {          
        if (!name.equals("")) {
            if (this.isVerbose()) {
                System.out.println(id + ": setting name=" + name);
            }
            this.name = name;
            this.isNameAssigned = true;
        }        
    }

    public String getName() {
        return this.name;
    }
    
    public List<Node> getChilds() {
        return childs;
    }
    
    public void addChild(Node node) {
        childs.add(node);
    }
    
    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        if (!value.equals("")) {
            if (this.isVerbose()) {
                System.out.println(id + ": setting value=" + value);
            }
            this.value = value;
        }
    }
    
    public boolean isNameAssigned() {
        return isNameAssigned;
    }

    public void setNameAssigned(boolean isNameAssigned) {
        this.isNameAssigned = isNameAssigned;
    }
    
    public boolean isLeftBracketAssigned() {
        return leftBracketAssigned;
    }

    public void setLeftBracketAssigned(boolean leftBracketAssigned) {
        if (this.isVerbose()) {
            System.out.println(id + ": setting (");
        }
        this.leftBracketAssigned = leftBracketAssigned;
    }

    public boolean isRightBracketAssigned() {
        return rightBracketAssigned;
    }

    public void setRightBracketAssigned(boolean rightBracketAssigned) {
        if (this.isVerbose()) {
            System.out.println(id + ": setting )");
        }
        this.rightBracketAssigned = rightBracketAssigned;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        this.toStringHelper(this, s);
        return s.toString();
    }

    /**
     * Recursive help method for toString. 
     * 
     * @param node
     * @param string
     */
    private void toStringHelper(Node node, StringBuilder s) {
        if (node == null) {
            return; // Tree is empty, so leave.
        }
        s.append("[");
        s.append("id="+node.getId() + ", pid="+node.getParentId() + ", level="+node.getLevel() + ", name="+node.getName() + ", value="+node.getValue() + ", (=" + node.isLeftBracketAssigned() + ", )="+node.isRightBracketAssigned());

        List<Node> childs = node.getChilds();
        for (int j=0; j<childs.size(); j++) {
            s.append(System.getProperty("line.separator"));
            this.toStringHelper(childs.get(j), s);            
        }
        s.append("]");
    }
}
