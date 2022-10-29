package seeker1389.dolors.dolors;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileHandler {

    FileHandler(){
        createFile();
    }
    File file=null;
    private void createFile(){
            try{
                 file = new File("data.txt");
            }catch(Exception e){
                System.err.println(e);
            }

    }

    void writeUrl(String filename ,String []data) {

        String text = data[0].concat("\n");
        FileWriter fw = null;
        try {
            fw = new FileWriter("data.txt", true);
            fw.write(text);
            fw.close();
            System.out.println("file written successfully");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }




}
