package penn.util.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileDumper {
    public static void save(String path, String content) {
        BufferedWriter bw;
        try {
            File file = new File(path);
            file.getParentFile().mkdirs();            
            bw = new BufferedWriter(new FileWriter(file, true));
            bw.write(content);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
