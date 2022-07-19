package seeker1389.dolors.dolors;

/*
 This class is responsible for extracting all the media related data
 */


import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

public class Media {

    private  String url ; //main url for the extraction of required data...

    String vidSrc="-Null-", vidThumb="-Null-", vidType="-Null-";
    boolean chaning=false;

    static HashMap<String,String> mediaData = new HashMap<>();  //used for storing the metadata of the media....

   /* Media(String url){
        try {
            if(checkUrl(url)){
                this.url=url;
            }
        }catch (AppException e){
            //add terminal text pipeline here______________________________________________

            System.out.println(e);
            url="";
        }

    }
    */



     boolean checkUrl(String url) throws AppException {
       if(url.matches(" ")){                    // ------add regex here-------
           return true;
       }else{
           throw new AppException(400);
       }
    }

    void Extract(String url){    //-----------------calling method--------------------
         try {
            if(checkUrl(url)){
                this.url=url;
                //---add calling methods here....

            }
         }catch (AppException e){
             System.out.println("Exception occured "+e);
         }
    }

      void GetElements(Document page){

        Elements video = page.getElementsByTag("video");
        for(Element vid : video){

            Elements src=vid.getElementsByTag("source");

            vidSrc = src.attr("src");
            vidThumb = video.attr("poster");
            vidType = video.attr("type");
        }

        if( chaning && video.isEmpty()){
            //invoking next extractor method...

        }
     }

    // get the media url by searching a specific string in the page
    void dataExtractor(Document page){

         String html =page.toString();
         System.out.println(html);
        System.out.println("--------------------------------------------------"+html.contains("setVideoUrlHigh"));
        System.out.println(html.indexOf("setVideoUrlHigh("));
    }
    //setVideoUrlHigh






}
