package seeker1389.dolors.dolors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
*Methods of the crawler class---
* 1. setUrl(String)
*   sets the base url
* 2. setPageLimit(int)
*   sets the page limit to be crawled sequentially
* 3. setUrlenth(int)
*   sets the limit of the url length for filteration of the url
* 4. crawlOnlyBaseUrl()
*   limits the crawling to the base url only
* 5. crawlSinglePage()
*   Limits the crawling to only the single page of the url provided
* 6. crawlSequentially()
*   Crawls the site by following a page sequence
*  7. setLoggingOff()
*   sets the logging off (by default its ON)
*  8. enableThumbnailExtractor()
*   enables the thumbnail extractor and stores the url in the data field
*  */
public class Crawler implements Runnable{



    private String baseUrl;
    private URL sourceUrl;
    private boolean onlySource=false;
    private int defaultPageLimit=200;

    private String filterMethod=null;

    private boolean logging = true;

    private int urLength=30;
    private  String mode="sitemap";

    private  boolean enableThumbExtractor = false;

    private SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private Date dt = new Date();
    private String date= formatter.format(dt);

    private Db db=null;

    private ArrayList<String> crawlUrls = new ArrayList<String>();



//-------------------------------[Object Modifier methods]----------------------------------------
    Crawler crawlSinglePage(){
        mode="single";
        return this;
    }

    Crawler crawlSequentially(){
        mode="sequence";
        return this;
    }

    Crawler setUrLength(int length){
        this.urLength=length;
        return this;
    }

    Crawler pagelimit(int limit){
        defaultPageLimit=limit;
        return this;
    }

    Crawler setUrl(String url){
        baseUrl=url;
        return this;
    }

    Crawler setLoggingOff(){
        this.logging=false;
        return this;
    }

    Crawler crawlOnlyBaseUrl(){
        onlySource=true;
        return this;
    }

    Crawler enableThumbnailExtractor(){
        this.enableThumbExtractor=true;
        return this;
    }

    Crawler configDatabase(String userName, String password, String siteName){
        try {
            String dbname=sourceUrl.getHost().replace('.','_');
            db = new Db(userName,password, dbname);
        }catch(Exception e){
            System.err.println("unable to create db connection: "+e);
        }
        return this;
    }


//---------------------------------------------------------------------
    public Crawler() {}

    public Crawler(String url){
        this.baseUrl=url;
        try {
            sourceUrl=new URL(baseUrl);
            String dbname=sourceUrl.getHost().replace('.','_');
            db = new Db("root","seeker1389", dbname);
        }catch(Exception e){
            System.err.println("url malformed exception occurred "+e);
        }
    }

    public Crawler(String url, Db db){
        this.baseUrl=url;
        this.db=db;
        try {
            sourceUrl=new URL(baseUrl);

        }catch(Exception e){
            System.err.println("url malformed exception occurred "+e);
        }
    }


    public void run(){
        start();
    }
    Crawler start(){
        if(mode.equalsIgnoreCase("sequence")){
            sequencePageNav(baseUrl);
        }
        if(mode.equalsIgnoreCase("sitemap")){
            getAllLinks(baseUrl);
        }
        if(mode.equalsIgnoreCase("single")){
            getAllLinks(baseUrl);
        }
        return null;
    }

   private int[] getPageSequenceInfo(String url){
        Pattern pattern = Pattern.compile("[^a-zA-Z][0-9]+[^a-zA-Z]?");
        Matcher matcher = pattern.matcher(url);

        boolean matchFound = matcher.find();
        if(!matchFound){
            return new int[]{-1};
        }
        String match = matcher.group();
        String res[] = match.split("[^0-9]");

        int start=matcher.start();
        int end= matcher.end();
        int pageNum=Integer.valueOf(res[1]);
        int[] out= new int[]{pageNum, start, end};
        return out;
        //String target[]=new String[]{url.substring(0,res[1]+1),url.substring(res[2]-1)};
    }


   private void sequencePageNav(String baseUrl){

        String url = baseUrl;
        int res[]=getPageSequenceInfo(baseUrl);
        if(res[0]==-1){
            System.err.println("[-ErrorCritcal-sequencePageNav-]No page sequence found...");
            //operations if the page does not contain any sequence pages
        }
        int pageNumber=res[0];
        String pageFrag[]=new String[]{url.substring(0,res[1]+1),url.substring(res[2]-1)};

        while(pageNumber<=defaultPageLimit){
            String genPageLink=pageFrag[0]+pageNumber+pageFrag[1];

            System.out.printf("\n\n\n");
            System.err.printf("---------------------------[--sequence page Crawler--]---------------------------\n");
            System.out.println("[-log-]generated page: "+genPageLink);

            getAllLinks(genPageLink);

            pageNumber++;
        }

    }


   private void getAllLinks(String url){
        int counter=0;
        System.out.println("[-log-] scanning all links... ");
        try {
            Document document = Jsoup.connect(url).get();
            Elements linksOnPage = document.select("a[href]");

            for (Element lin : linksOnPage) {
                String link =(lin.attr("abs:href"));
                if(!db.contains("links","url=".concat("\"")+link.concat("\""))){
                    filter(lin,link);
                }else{
                   // System.err.println("[log]link Exist");
                }
            }
            //used for fetching the uncrawled links for further link indexing
            if(mode.equalsIgnoreCase("sitemap")){
                fetchUrl();
            }

        }catch (Exception e){
            System.err.println("[-errCritical-GetAllLinks-] link: "+url+" "+ e);
        }
   }


    private String getThumbnailData(Element link){
        Elements thumb = link.select("a>img");
        String thumbnailUrl= thumb.attr("src");
        printr("thumbDataExtractor","_________________________thumbnail data: "+thumbnailUrl,"message");
        return thumbnailUrl;
    }


   private void filter(Element linkElement,String link){
        String filteredLink=null;

        System.out.println(Thread.currentThread().getName());

       String thumbUrl="";
       if(link.length()>urLength){
           filteredLink=link;
       }
       if (onlySource) {
           try {
               String host = sourceUrl.getHost();
               URL trgtLink = new URL(link);
               if (trgtLink.getHost().equalsIgnoreCase(host)) {
                   filteredLink = link;
               }else{
                   filteredLink=null;
               }
           } catch (MalformedURLException e) {
               System.err.println("[-filter-]link error "+link);
               throw new RuntimeException(e);

           }

       }

       if(filteredLink!=null){

           //========================================[adding link to database here]==============================
           String type="media";
           String scraped="false";
           String crawled="false";
           String data="null";

           if(enableThumbExtractor){
               thumbUrl=getThumbnailData(linkElement);
               if(!(thumbUrl.equalsIgnoreCase(""))){
                   data="thumbnail#"+thumbUrl;
               }else{
                   System.err.println("thumbnail not found for the link : "+link);
               }

           }
            String res[]={link,type,date,data,scraped,crawled};
            db.executeUpdate("links",res);
            System.out.println("Link added to Database: "+link);
            //===============================================[end]=================================================
       }else{
          printr("filter","link rejected because it failed filteration : "+link,"log");
       }


   }


    private void fetchUrl(){
        crawlUrls=db.fetchLinks("50", "crawl");
        System.err.println("fetching uncrawled links-------");
        //============================================
       String upd="update links set crawl =\"true\" where id=";
        for(int i=0; i<crawlUrls.size(); i++){
            String[] rec =crawlUrls.get(i).split("#");
            for(int x=0; x<2; x++){
                int r=db.update(upd.concat(rec[0]).concat(";"));
                if(r>0){printr("fetchURL","updated success!","log");}
                //calling the link indexer for all links----
                getAllLinks(rec[1]);
            }
        }
        //===================================================

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