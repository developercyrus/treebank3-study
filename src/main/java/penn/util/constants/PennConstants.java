package penn.util.constants;

public final class PennConstants {
    public static final String SENTENCE_ROOT = "S1";
    public static final String DOCUMENT_ROOT = "DOC";

    public static final String ROOT_NODE = "ROOT";    
    public static final String EMPTY_NODE = "-NONE-";
            
    public static final String SECTIONID_KEY = "sectionId";
    public static final String FILEID_KEY = "fileId";
    public static final String SENTENCESIZE_KEY = "sentenceSize";
    public static final String SENTENCEID_KEY = "sentenceId";
    public static final String ID_KEY = "id";
    public static final String PID_KEY = "pid";
    public static final String LEVEL_KEY = "level";
            
    public static final String XML_INDENT = "4";
    public static final String RAW_FILE_EXTENSION = ".MRG";
    public static final String XML_FILE_EXTENSION = ".xml";
    public static final String SQL_FILE_EXTENSION = ".sql";
    
    public static final String SENTENCE_BEGIN1 = "( (";
    public static final String SENTENCE_BEGIN2 = "((";
        
    public static final String WSJ_CORPUS_PATH = "/corpus/treebank-3/PARSED/MRG/WSJ";    
    public static final String XML_OUTPUT_PATH = "/result/xml";
    public static final String SQL_OUTPUT_PATH = "/result/sql";
    public static final String SECTION_SCRIPT_OUTPUT_PATH = "/SECTION.sql";
    public static final String FILE_SCRIPT_OUTPUT_PATH = "/FILE.sql";
    public static final String SENTENCE_SCRIPT_OUTPUT_PATH = "/SENTENCE.sql";
    public static final String PATH_SCRIPT_OUTPUT_PATH = "/PATH.sql";
    
    public static final String SOURCE_CODE_PATH = PennConstants.class.getProtectionDomain().getCodeSource().getLocation().getPath();
}
  
