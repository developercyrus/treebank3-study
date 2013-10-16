package penn.util.xml;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import penn.util.xml.Raw2Sentence;

public class Raw2SentenceTest {

    @Test
    public void test1() throws IOException {
        String path = Raw2SentenceTest.class.getResource("/corpus/treebank-3/PARSED/MRG/WSJ/00/WSJ_0001.MRG").getFile();
        Raw2Sentence s = new Raw2Sentence(path);
        String[] sentences = s.split();
                
        String expected1 = "( (S     (NP-SBJ       (NP (NNP Pierre) (NNP Vinken) )      (, ,)       (ADJP         (NP (CD 61) (NNS years) )        (JJ old) )      (, ,) )    (VP (MD will)       (VP (VB join)         (NP (DT the) (NN board) )        (PP-CLR (IN as)           (NP (DT a) (JJ nonexecutive) (NN director) ))        (NP-TMP (NNP Nov.) (CD 29) )))    (. .) ))";
        String expected2 = "( (S     (NP-SBJ (NNP Mr.) (NNP Vinken) )    (VP (VBZ is)       (NP-PRD         (NP (NN chairman) )        (PP (IN of)           (NP             (NP (NNP Elsevier) (NNP N.V.) )            (, ,)             (NP (DT the) (NNP Dutch) (VBG publishing) (NN group) )))))    (. .) ))";
        
        assertEquals(expected1, sentences[0]);
        assertEquals(expected2, sentences[1]);        
    }
}
