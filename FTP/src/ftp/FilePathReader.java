package ftp;

import java.io.*;
import java.util.ArrayList;
import javax.swing.JFileChooser;

public class FilePathReader {
    public static void main(String[] args) {
        ArrayList fileNames = getFilePath();
        for(int i=0; i<fileNames.size(); i++){
            System.out.println((String) fileNames.get(i));
        }
    }
    public static ArrayList getFilePath(){
        ArrayList fileNames = new ArrayList();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));
        fileChooser.setMultiSelectionEnabled(true);
        int result = fileChooser.showOpenDialog(fileChooser);

        if(result == JFileChooser.APPROVE_OPTION){
            File [] selectedFiles = fileChooser.getSelectedFiles();

            for(File file: selectedFiles){
                fileNames.add(file.getAbsolutePath());
            }
        }
        return fileNames;
    }

}
