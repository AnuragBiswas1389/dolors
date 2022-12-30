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
import java.util.ArrayList;
import java.util.LinkedHashSet;




public class MediaScraper implements Runnable {

    private String output="database";

    private  String url ;//main url for the extraction of required data...
    private  String link;// link of the page to be crawled...
    private URL hostUrl;//host url

    private String absVidThumb="notFound";// video thumbnail url

    private boolean allowLoggingToSinglePg=false;

    private Db db = new Db("root","seeker1389","aagmaal_life");//class database instance...

    private ArticleScraper as = new ArticleScraper();

    private  FileHandler fh = new FileHandler();


    private String links[]={};//stores all the links in the page... used by getLinks method..

    private LinkedHashSet<String>linkList= new LinkedHashSet<>();

    private String players[]={"dood","streamtape","streamtapeadblockuser","sbanh","vtube"};
    private String blockedPlayers[]={"tsyndicate"};

    private String pageInfo=null;

    private ArrayList<String> scrapUrls = new ArrayList<String>();


//----------------------------------------------------

    @Override
    public  void run(){
        fetchUrl();
    }



    private void fetchUrl(){
        scrapUrls=db.fetchLinks("50","scrap");
        System.err.println("fetching unscraped links-------");
        //============================================
        String upd="update links set scrap =\"true\" where id=";
        for(int i=0; i<scrapUrls.size(); i++){
            String[] rec =scrapUrls.get(i).split("#");
            for(int x=0; x<2; x++){
                int r=db.update(upd.concat(rec[0]).concat(";"));
                System.out.println(rec[1]+"from "+Thread.currentThread().getName());
                if(r>0){
                    System.out.println("updated success!");
                }
                //calling the link indexer for all links----

            }
        }
        //===================================================

    }
    int start()  {

        System.out.printf("\n\n");
        System.out.println("---------------Scraper----------------------");
        System.out.println("link- "+link);
        this.link=link;
        try{
            hostUrl = new URL(link);
            pageInfo=as.getTitle(link);
        }catch (Exception e){
            System.err.println("[-errCritical-]URL is malformed, cannot create url object! ");
        }
       Document page = getPage();
//     getElements(page);   temporaryly
       getWebPlayerLink(page);
       return 0;
    }

    private Document getPage(){
        try {
            Document page = Jsoup.connect(link)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get();
            System.err.println("[-log-]Downloaded target page successfully!");
            return page;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    MediaScraper setOutput(String output){
        this.output=output;
        return this;
    }

    private String getAbs(String link){
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
        String site=hostUrl.getHost();

        if(absVidSrc.equalsIgnoreCase(" ")||absVidThumb.equalsIgnoreCase(" ")){
            System.err.println("cannot update because link empty "+absVidSrc+" and "+absVidThumb);

        }if(absVidType.equalsIgnoreCase(" ")){
            absVidType="htmlPlayer";
        }else{
           // db.executeUpdate( "video",new String[]{id, absVidSrc,site,absVidThumb,absVidType});
        }

    }

    void setPageInfo(String data){
        this.pageInfo=data;
    }

    //used to contorl the creation of a single file for scraping single page

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
                for (String blkPlayers:blockedPlayers){
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
        String data[]={srcLink,site,absVidThumb,title,absVidType};

        if(output.equalsIgnoreCase("text")){
            if(allowLoggingToSinglePg){
            if(!linkList.isEmpty()){
                fh.writeData(hostUrl.getHost().concat(".txt"),new String[]{"$title$"+title,"$thumb$"+absVidThumb});
            }
        }if(!allowLoggingToSinglePg){
            if(!linkList.isEmpty()){
                fh.writeData(pageInfo.concat(".txt"),new String[]{"$title$"+title});
            }
        }
        }


        for(String finalLink:linkList){
             srcLink=finalLink;
            System.out.println(" final links : "+srcLink);

            if(allowLoggingToSinglePg){
                fh.writeData(hostUrl.getHost().concat(".txt"),new String[]{srcLink});
            }if(!allowLoggingToSinglePg){
                fh.writeData(pageInfo.concat(".txt"),new String[]{srcLink});
            }

            // db.executeUpdate("videoData",data);
        }
        return webPlayerLink;
    }




   private void getLinks(){
        int counter=0;
        Document page = getPage();
        Elements href = page.select("a[href]");
        for(Element hrf : href){
            links[counter]=hrf.attr("abs:href");
            counter++;
        }
        counter=0;
   }

   Crawler setSingleOutput(){
        allowLoggingToSinglePg=true;
       return null;
   }

   void setThumbnailData(String url){
        this.absVidThumb=url;
   }

}
