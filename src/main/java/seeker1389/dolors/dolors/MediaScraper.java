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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashSet;




public class MediaScraper implements Runnable {

    private String output="database";
    private boolean logging=true;//member for logging method

    private  String link;// link of the page to be crawled...
    private URL hostUrl;//mai urlObject initialized on startup url


    private Db scraperDb;//db object for scraper
    private Db siteDb; // db of the target site


    private Document page=null; //Target page of the current scraper instance
    private String thumb="notFound";// video thumbnail url
    private  String type ="undefined";//type of the source url
    private String pageTitle="undefined"; //page title of the current page



    private boolean allowLoggingToSinglePg=false;

    private ArticleScraper as = new ArticleScraper();
    private  FileHandler fh = new FileHandler();


    private String links[]={};//stores all the links in the page... used by getLinks method..

    private LinkedHashSet<String>linkList= new LinkedHashSet<>();

    private ArrayList<String> players=new ArrayList<String>();
   // String st = "(id int NOT NULL AUTO_INCREMENT, url varchar(225), type varchar(225), date varchar(225), data varchar(225), data2 varchar(225), data3 varchar(225), PRIMARY KEY(id))";
    private ArrayList<String> spam=new ArrayList<String>();

    private ArrayList<String> scrapUrls = new ArrayList<String>();


//===================================[object config]=================
    MediaScraper(Db db){
        this.siteDb=db;
    }
    MediaScraper(String url, String username, String password){
        URL sourceUrl = null;
        try {
            sourceUrl = new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        String dbname=sourceUrl.getHost().replace('.','_');
        this.siteDb=new Db(username, password,dbname);
        this.scraperDb=new Db(username, password,"scraper");
        assignDB();
    }
    MediaScraper setOutput(String output){
        this.output=output;
        return this;
    }

    Crawler setSingleOutput(){
        allowLoggingToSinglePg=true;
        return null;
    }
    Crawler enableLogging(){
        this.logging=true;
        return null;
    }

  
//===================================================================

    //entry point of the application----
    @Override
    public  void run(){
        fetchUrl();
    }


    private void fetchUrl(){
        scrapUrls=siteDb.fetchLinks("50","scrap");
        System.err.println("fetching unscraped links-------");
        //============================================
        String upd="update links set scrap =\"true\" where id=";
        for(int i=0; i<scrapUrls.size(); i++){
            
            // id# url# thumbUrl# typ# data
            
            String[] rec =scrapUrls.get(i).split("#");
            for(int x=0; x<2; x++){
                int r=siteDb.update(upd.concat(rec[0]).concat(";"));
                printr("[fetchUrl]",rec[1]+"from "+Thread.currentThread().getName(),"log");
                //===================calling the extraction procedures=================
                siteDb.setScraped(rec[0]);
                type =rec[3];
                thumb=rec[2];
                start(rec[1]);
                //=====================================================================
                if(!(r>0)){
                    System.out.println("updated failed!");
                }
            }
        }
    }




    int start(String link)  {

        System.out.println("\n---------------Scraper----------------------");
        System.out.println("link- "+link);
        this.link=link;
        try{
            hostUrl = new URL(link);
            pageTitle=as.getTitle(link);
        }catch (Exception e){
            System.err.println("[-errCritical-]URL is malformed, cannot create url object! ");
        }
        getPage();
        extractorStarter();
        return 0;
    }

    private Document getPage(){
        try {
             page = Jsoup.connect(link)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get();
              printr("[getPage]","Downloaded target page successfully!","message");
            return page;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    //initiate the extractor -------
    private void extractorStarter(){

        System.out.println("----initiated extractor ----- "+link);
        GetHtmlPlayer();
    }


    private void assignDB(){
        players=scraperDb.getScraperTables("select * from players","player");
        spam=scraperDb.getScraperTables("select * from spam","url");
        System.out.println("loaded the spam and players db into memory!");
    }


    private String getAbs(String link){
         String absLink=link;
        if(!(link.contains("https://")||link.contains("http://"))){
            System.err.println("The link is not absolute... providing the absolute link..");
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

     private int GetHtmlPlayer(){

     //   System.out.println(page);
        String vidSrc="not found", vidThumb="not found", vidType="not found";

        Elements video = page.getElementsByTag("video");
         System.out.println(video);
        for(Element vid : video){
            Elements src=vid.getElementsByTag("source");
            vidSrc = src.attr("src");
            vidThumb = video.attr("poster");
            vidType = src.attr("type");
        }

//        String absVidSrc=getAbs(vidSrc);
//        String absVidThumb=getAbs(vidThumb);
//        String absVidType=vidType;

        String site=hostUrl.getHost();

//        if(absVidSrc.length()<1){
//            //fatal condition because no video link is found so no meaning of further processing...
//            System.err.println("cannot update because, link empty "+absVidSrc+" and "+absVidThumb);
//            return 0;
//        }
//        if(absVidType.equalsIgnoreCase(" ")){
//            absVidType="html5VideoPlayer";
//        }
//        if(absVidThumb.equalsIgnoreCase(" ")){
//            //assigning the previous extracted thumb if a poster is not found!
//             absVidType=thumb;
//        }
        //=====================================data exit point==================================
        System.out.println(vidSrc);
        //=====================================data exit point==================================
        return 1;

    }



    //scans the webPlayers links
    String getWebPlayerLink(Document page){

        System.err.println("[-log-]Starting webplayer scraper!");
        String frameLink = null;
        String webPlayerLink=null;

        String site=hostUrl.getHost();
        String absVidType="webPlayer";
        String title = as.getTitle(link);
        String srcLink="";
        linkList.clear();


        //processing all the iframe links
        Elements iframe = page.getElementsByTag("iframe");
        if(iframe.isEmpty()){
            System.out.println("---No iframe links found on the link----");
        }
        for(Element ifr : iframe){
            frameLink = ifr.attr("src");

            for(String player :players){
                if (frameLink.contains(player)){
                    webPlayerLink = frameLink;
                    System.out.println("iframe link found: "+webPlayerLink);
                    if(!linkList.contains(link)){
                        linkList.add(webPlayerLink);
                    }
                }
            }
            if(frameLink.startsWith("/")){
                String absLink="https://"+hostUrl.getHost().concat(frameLink);
                for (String blkPlayers:spam){
                    if(!absLink.contains(blkPlayers)){
                        if(!linkList.contains(absLink)){
                            linkList.add(absLink);
                        }
                    }
                }

            }
        }

        //processing all the links on the page...
        Elements linksOnPage = page.select("a[href]");
        for (Element Srcpage : linksOnPage) {
                String li =(Srcpage.attr("abs:href"));
                for(String player : players){
                    if(li.contains(player)){
                        if(!linkList.contains(li)){
                            System.out.println("On page link :"+li);
                            linkList.add(li);
                        }
                    }
                }

        }
       // String data[]={srcLink,site,absVidThumb,title,absVidType};

        if(output.equalsIgnoreCase("text")){
            if(allowLoggingToSinglePg){
            if(!linkList.isEmpty()){
                //fh.writeData(hostUrl.getHost().concat(".txt"),new String[]{"$title$"+title,"$thumb$"+absVidThumb});
            }
        }if(!allowLoggingToSinglePg){
            if(!linkList.isEmpty()){
                //fh.writeData(pageTitle.concat(".txt"),new String[]{"$title$"+title});
            }
        }
        }


        for(String finalLink:linkList){
             srcLink=finalLink;
            System.out.println(" final links : "+srcLink);

            if(allowLoggingToSinglePg){
                fh.writeData(hostUrl.getHost().concat(".txt"),new String[]{srcLink});
            }if(!allowLoggingToSinglePg){
                fh.writeData(pageTitle.concat(".txt"),new String[]{srcLink});
            }

            // db.executeUpdate("videoData",data);
        }
        return webPlayerLink;
    }




   private void getLinks(){
        int counter=0;
        Elements href = page.select("a[href]");
        for(Element hrf : href){
            links[counter]=hrf.attr("abs:href");
            counter++;
        }
        counter=0;
   }





    private void printr(String source,String message, String type){
        if(!logging){
            return;
        }
        String text = "{"+type+"}[-"+source+"-] :"+message;

        if(type.equalsIgnoreCase("message")){
            System.out.println(text);
        } if(type.equalsIgnoreCase("warning")|| type.equalsIgnoreCase("log")){
            System.err.println(text);
        }

    }
    private void printr(String source,String message){
        if(logging==false){
            return;
        }
        String text = "["+source+"-] :"+message;
        System.out.println(text);
    }

}
