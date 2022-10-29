package seeker1389.dolors.dolors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.LinkedHashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Crawler {


    MediaScraper mediaScraper = new MediaScraper();
    private String baseUrl;
    private URL sourceUrl;
    boolean onlySource=false;
    int defaultPageLimit=100;
    String filterMethod=null;

    LinkedHashSet<String> pageList = new LinkedHashSet<>();

    public Crawler() {}
   public Crawler(String url,String mode,boolean onlySource, String filterMethod){

       this.onlySource=onlySource;
       this.baseUrl=url; //sets the baseUrl to the starter url...
       this.filterMethod=filterMethod;//sets the filter method for the object

       try {
           sourceUrl=new URL(baseUrl);  //sets the sorceUrl object...
       }catch(Exception e){
           System.err.println("url malformed exception occurred "+e);
       }

        if(mode.equalsIgnoreCase("sequence")){
                sequencePageNav(url);
        }

        /*
        executed procedures for the random mode
         */
        if(mode.equalsIgnoreCase("random")){
            randomPageNav(url);
        }
    }

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
            pageNumber++;
        }

    }

   private void getAllLinks(String url){
        int counter=0;
        System.err.println("[-log-]getting all links...");
        try {
            Document document = Jsoup.connect(url).get();
            Elements linksOnPage = document.select("a[href]");

            for (Element page : linksOnPage) {
                String link =(page.attr("abs:href"));
                if(!pageList.contains(link)){
                    pageList.add(link);
                    System.err.println("on link "+link);
                    scraperDriver(link);
                }


            }
        }catch (Exception e){
            System.err.println("[-errCritical-GetAllLinks-] "+ e);
        }
   }

   void setDefaultPageLimit(int limit){
        this.defaultPageLimit=limit;
   }

   private int scraperDriver(String link){
       String host = sourceUrl.getHost();

       int length=Integer.valueOf(filterMethod);

        if(onlySource && link.contains(host)){
            if(filterMethod!=null) {
                if ((length > 0) && link.length() >= length) {
                    mediaScraper.start(link);
                    return 0;
                } else {
                    return -1;
                }
            }
            System.out.println("[debug]sending without filtration");
            mediaScraper.start(link);
            return 0;
        }else{
            System.out.println("[-log-]Link rejected because the links dose not belong to the source URL");
        }

      return 0;
   }


}