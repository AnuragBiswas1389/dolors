package seeker1389.dolors.dolors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Crawler {


    private HashSet<String> links;
    Media media = new Media();
    private String baseUrl;
    public Crawler() {}
   public Crawler(String url,String mode){
        this.baseUrl=url; //sets the baseUrl to the starter url...
        /*
        When the crawling mode is set to the sequence,
         then this method of crawling will be
        invoked...
         */
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


    public void randomPageNav(String URL) {
        //4. Check if you have already crawled the URLs
        //(we are intentionally not checking for duplicate content in this example)
        links = new HashSet<String>();
        if (!links.contains(URL)) {
            try {
                //4. (i) If not add it to the index
                if (links.add(URL)) {
                    System.out.println("--Crawled link is "+URL+" ----------------------");
                   media.start(URL);
                }

                //2. Fetch the HTML code
                Document document = Jsoup.connect(URL).get();
                //3. Parse the HTML to extract links to other URLs
                Elements linksOnPage = document.select("a[href]");

                //5. For each extracted URL... go back to Step 4.
                for (Element page : linksOnPage) {
                    randomPageNav(page.attr("abs:href"));
                }
            } catch (IOException e) {
                System.err.println("For '" + URL + "': " + e.getMessage());
            }
        }
    }
    public int[] getPageSequenceInfo(String url){
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

    void sequencePageNav(String baseUrl){

        String url = baseUrl;
        int res[]=getPageSequenceInfo(baseUrl);
        if(res[0]==-1){
            System.err.println("[-Error-]No page sequence found...");
            //operations if the page dosenot conains any sequence pages
        }
        int pageNumber=res[0];
        String pageFrag[]=new String[]{url.substring(0,res[1]+1),url.substring(res[2]-1)};
        int defaultPageLimit=50;
        while(pageNumber<=defaultPageLimit){
            String genPageLink=pageFrag[0]+pageNumber+pageFrag[1];
            System.err.println("[-log-]generated page with page number: "+genPageLink);
            //getting all the links for the generated link...
            getAllLinks(genPageLink);
            pageNumber++;
        }

    }


    void getAllLinks(String url){
        System.err.println("[-log-]getting all links...");
        LinkedHashSet<String>pageList = new LinkedHashSet<>();
        try {
            Document document = Jsoup.connect(url).get();
            Elements linksOnPage = document.select("a[href]");
            for (Element page : linksOnPage) {
                String link =(page.attr("abs:href"));
                if(!pageList.contains(link)){
                   media.start(link);
                }
                pageList.add(link);
            }
        }catch (Exception e){
            System.err.println("[-errCritical-]Url malformatted"+ e);
        }



    }


}