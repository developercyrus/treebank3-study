package penn.util.constants;

public class SqlScripts {
    public static final String GO = "GO";
    public static final String INSERT_SECTION = "insert into SECTION (ID, NAME) values (%s, '%s');";
    public static final String INSERT_FILE = "insert into [FILE] (ID, SECTION_ID, NAME) values (%s, %s, '%s');";
    public static final String INSERT_SENTENCE = "insert into SENTENCE (ID, FILE_ID, XML) values (%s, %s, '%s');";
    public static final String INSERT_NODE = "insert into NODE (ID, SENTENCE_ID, NID, TYPE, VALUE, WORD) values (%s, %s, %s, '%s', '%s', '%s');";
    public static final String INSERT_PATH = "insert into [PATH] (SENTENCE_ID, NODE_ID, NODE_PID, SIBLING_SEQ, HEIGHT, XPATH) values (%s, %s, %s, %s, %s, '%s');";    
}
