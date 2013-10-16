package penn.util.xml;

import static penn.util.constants.PennConstants.SENTENCE_BEGIN1;
import static penn.util.constants.PennConstants.SENTENCE_BEGIN2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Raw2Sentence {
    private String path = "";
    
    public Raw2Sentence(String path) {
        this.path = path;
    }
    
    public String[] split() throws IOException {
        String line = "";
        String lines = "";
        List<String> sentences = new ArrayList<String>();
        
        BufferedReader br1 = new BufferedReader(new FileReader(this.path));       
        while ((line = br1.readLine()) != null) {
            if ((line.startsWith(SENTENCE_BEGIN1) || line.startsWith(SENTENCE_BEGIN2)) && !lines.equals("")) {
                sentences.add(lines);
                lines = "";
            }
            lines = lines + line;
        } 
        br1.close();
        sentences.add(lines);
        
        String[] sent = sentences.toArray(new String[sentences.size()]);
        return sent;
    }
}
