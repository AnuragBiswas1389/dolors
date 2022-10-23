package seeker1389.dolors.dolors;

/*
 This class is responsible for extracting all the media related data
 */


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
public class Media {

    private  String url ; //main url for the extraction of required data...
    private  String link;  // link of the page to be crawled...
    private URL hostUrl; //host url
    Db db = new Db(); //class database instance...

    String links[]={}; //stores all the links in the page... used by getLinks method..
    int tempid=50;
    Media(String link){
        if(link!=""){
            this.link=link;
             hostUrl = null;
            try {
                hostUrl = new URL(link);
            } catch (MalformedURLException e) {
                System.err.println("url format error");
                System.err.println("cannot set link because link found is empty..");
                throw new RuntimeException(e);
            }
        }

    }//sets the source url
    Media(){
         //default cons.
    }
    int start(String link)  {
        System.out.println("[-log-]-----------newLik------------------");
        System.out.println("link- "+link);
        System.err.println("[-log-]Starting scraper!");

        this.link=link;
        try{
            hostUrl = new URL(link);
        }catch (Exception e){
            System.err.println("[-errCritical-]URL is malformatted, cannot create url object! ");
        }
       Document page = getPage();
//     getElements(page);   temporaryly
       getWebPlayerLink(page);
       return 0;
    }
    boolean checkUrl(String url) throws AppException {
       if(url.matches(" ")){                    // ------add regex here-------
           return true;
       }else{
           throw new AppException(400);
       }
    }
    Document getPage(){
        try {
            Document page = Jsoup.connect(link)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get();
            System.err.println("[-log-]Downloded target page successfully!");
            return page;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    String getAbs(String link){
         String absLink=link;
        if(!(link.contains("https://")||link.contains("http://"))){
            System.err.println("The link is not absolute... providing the absolute link..");
            System.out.println();
            if(link.startsWith("/")){
                absLink=absLink.strip().substring(1);
            }
            absLink= hostUrl.getProtocol().concat("://")+ hostUrl.getHost()+absLink;
        }
        else{
            return absLink;
        }
        return absLink;
    }
    void getElements(Document page){
         String vidSrc="not found", vidThumb="not found", vidType="not found";

        Elements video = page.getElementsByTag("video");
        for(Element vid : video){
            Elements src=vid.getElementsByTag("source");

            vidSrc = src.attr("src");
            vidThumb = video.attr("poster");
            vidType = video.attr("type");
        }
        //checking if the video link is an indirect link
        System.out.println("------------------------------------------------");
        System.out.println(getAbs(vidSrc));
        System.out.println(getAbs(vidThumb));
        System.out.println(vidType);
        System.out.println("------------------------------------------------");

        String absVidSrc=getAbs(vidSrc);
        String absVidThumb=getAbs(vidThumb);
        String absVidType=vidType;
        String id=String.valueOf(tempid);
        String site=hostUrl.getHost();

        if(absVidSrc.equalsIgnoreCase(" ")||absVidThumb.equalsIgnoreCase(" ")){
            System.err.println("cannot update because link empty "+absVidSrc+" and "+absVidThumb);

        }if(absVidType.equalsIgnoreCase(" ")){
            absVidType="htmlPlayer";
        }else{
            db.starter( new String[]{id, absVidSrc,site,absVidThumb,absVidType});
        }
        tempid++;
    }
    String getWebPlayerLink(Document page){

        System.err.println("[-log-]Starting webplayer scraper!");
        String frameLink = null;
        String webPlayerLink=null;
        String players[]={"dood","steamtape"};
        int i=0;

        Elements iframe = page.getElementsByTag("iframe");
        if(iframe.isEmpty()){
            System.out.println("---No iframe links found on the link----");
        }
        for(Element ifr : iframe){
            frameLink = ifr.attr("src");
            System.err.println("iframe link found "+frameLink);
            for(String player :players) {
                if (frameLink.contains(player)) {
                    webPlayerLink = frameLink;
                    System.out.println("link found "+webPlayerLink);

                    String id=String.valueOf(tempid);
                    String site=hostUrl.getHost();
                    String absVidType="webPlayer", absVidThumb = "webPlayer";

                    String data[]={id, webPlayerLink,site,absVidThumb,absVidType};
                    db.starter(data);
                    tempid++;
                }

            }
        }

        return webPlayerLink;
    }

    void getLinks(){
        int counter=0;
        Document page = getPage();
        Elements href = page.select("a[href]");
        for(Element hrf : href){
            links[counter]=hrf.attr("abs:href");
        }
        counter=0;
    }



}
