package penn.util.xml;

import static penn.util.constants.PennConstants.FILEID_KEY;
import static penn.util.constants.PennConstants.SECTIONID_KEY;
import static penn.util.constants.PennConstants.SENTENCESIZE_KEY;

import java.util.HashMap;

public class DocumentMeta extends HashMap<String, String>{
    private String fileId;
    private String sectionId;
    private int sentenceSize;
    
    public static void main(String[] args) {
        String filename = "D:/github/treebank3-study/src/main/resources/corpus/treebank-3/PARSED/MRG/WSJ/00/WSJ_0001.MRG";        
        DocumentMeta map = new DocumentMeta(filename);
        for (String key : map.keySet()) {
            System.out.println(key + ":" + map.get(key));
        }
    }

    public DocumentMeta(String filename) {
        this.fileId = filename.substring(filename.lastIndexOf('/')+1, filename.length());
        this.sectionId = filename.substring(filename.lastIndexOf('_')+1, filename.lastIndexOf('_')+1 + 2);
        
        this.put(SECTIONID_KEY, sectionId);
        this.put(FILEID_KEY, fileId);
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }
    
    public int getSentenceSize() {
        return sentenceSize;
    }

    public DocumentMeta setSentenceSize(int sentenceSize) {
        this.sentenceSize = sentenceSize;
        this.put(SENTENCESIZE_KEY, String.valueOf(sentenceSize));
        
        return this;
    }

}
