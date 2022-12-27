package seeker1389.dolors.dolors;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileHandler {




    private String createFile(String fileName){
            try{
                File file = new File(fileName);
            }catch(Exception e){
                System.err.println(e);
            }
            return fileName;
    }

    void writeData(String fileName ,String []data) {
        if(fileName.length()>25) fileName = fileName.substring(0, 25).concat("...");
        try {
            FileWriter fw = new FileWriter("output/"+fileName,true);
            for(String str:data){
                fw.write(str.concat("\n"));
            }
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}
