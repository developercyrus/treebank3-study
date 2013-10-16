package penn.util.io;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

public class Dir {
    public static String[] getDirectoryList(String path) {
        File file = new File(path);
        String[] directories = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return new File(dir, name).isDirectory();
            }
        });
        System.out.println(Arrays.toString(directories));
        
        return directories;
    }
    
    public static String[] getFileList(String path) {
        File file = new File(path);
        String[] files = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return new File(dir, name).isFile();
            }
        });
        System.out.println(Arrays.toString(files));
        
        return files;
    }   
}
