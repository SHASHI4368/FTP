package ftp;

import java.io.*;
import javax.swing.JFileChooser;

public class FilePathReader {
    public static String getFilePath(){
        String filePath = "";
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));
        int result = fileChooser.showOpenDialog(fileChooser);

        if(result == JFileChooser.APPROVE_OPTION){
            File selectedFile = fileChooser.getSelectedFile();
            filePath = selectedFile.getAbsolutePath();
        }
        return filePath;
    }

}
