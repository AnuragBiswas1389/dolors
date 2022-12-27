package seeker1389.dolors.dolors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Crawler {


    private MediaScraper mediaScraper=null;
    private String baseUrl;
    private URL sourceUrl;
    private boolean onlySource=false;
    private int defaultPageLimit=200;
    private String filterMethod=null;

    private int urLength=30;
    private  String mode="sitemap";

    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    Date dt = new Date();
    private String date= formatter.format(dt);

    private Db db=null;

    ArrayList<String> crawlUrls = new ArrayList<String>();

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

    Crawler start(){

       mediaScraper = new MediaScraper();

       if(mode.equalsIgnoreCase("sequence")){
           sequencePageNav(baseUrl);
       }
       if(mode.equalsIgnoreCase("sitemap")){
           randomPageNav(baseUrl);
       }
       if(mode.equalsIgnoreCase("single")){
           getAllLinks(baseUrl);
       }else{
           getAllLinks(baseUrl);
       }
        return null;
    }


    Crawler scrapSingleSite(){
        mode="single";
        return this;
    }

    Crawler scrapSequentially(){
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

    Crawler crawlOnlyBaseUrl(){
        onlySource=true;
        return this;
    }

    void setDefaultPageLimit(int limit){

        this.defaultPageLimit=limit;
    }
//----------------------------------------------------

   private void randomPageNav(String URL) {
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
            System.err.printf("---------------------------[Crawler]---------------------------\n");
            System.err.println("[-log-]generated page: "+genPageLink);

            getAllLinks(genPageLink);
           // mediaScraper.setPageInfo(sourceUrl.getHost().concat("_Page_")+pageNumber);
            pageNumber++;
        }

    }


   private void getAllLinks(String url){
        int counter=0;
        System.err.println("[-log-]getting all links...");
        try {
            Document document = Jsoup.connect(url).get();
            Elements linksOnPage = document.select("a[href]");

            for (Element lin : linksOnPage) {
                String link =(lin.attr("abs:href"));
                if(!db.contains("links","url=".concat("\"")+link.concat("\""))){
                    System.err.println("link added: "+link);
                    filter(lin,link);
                }else{
                    System.err.println("[log]link Exist");
                }
            }
        }catch (Exception e){
            System.err.println("[-errCritical-GetAllLinks-] "+ e);
        }
   }


    private String getThumbnailData(Element link){
        Elements thumb = link.select("a>img");
        String thumbnailUrl= thumb.attr("src");
        //System.out.println("_________________________thumbnail data: "+thumbnailUrl);
        //mediaScraper.setThumbnailData(thumbnailUrl);
        return thumbnailUrl;
    }


   private void filter(Element linkElement,String link){
        String filteredLink=null;
       String host = sourceUrl.getHost();
       String thumbUrl;

       if(link.contains(host)){
           filteredLink=link;
       }
       if(link.length()>urLength){
           filteredLink=link;
       }

       if(filteredLink!=null){
           thumbUrl=getThumbnailData(linkElement);
           String type="media";
           String scraped="false";
           String crawled="false";
           String data="null";
            String res[]={link,type,date,data,scraped,crawled};
            db.executeUpdate("links",res);
       }else{
          System.out.println("link rejected because it failed filteration : "+link);
       }


   }


    public void fetchUrl(){
        crawlUrls=db.fetchLinks("50");
        //============================================
       String upd="update links set crawl =\"true\" where id=";
        for(int i=0; i<crawlUrls.size(); i++){
            String[] rec =crawlUrls.get(i).split("#");
            for(int x=0; x<2; x++){
                System.out.println(rec[x]);
                int r=db.update(upd.concat(rec[0]).concat(";"));
                if(r>0){System.out.println("updated success!");}
            }
        }
        //===================================================

    }

    //gets the link as crawled..
//    private void getCrawled(String link){
//
//    }





}