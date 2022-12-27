package seeker1389.dolors.dolors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class ArticleScraper {

    Document page=null;
    String link=null;



    public String getTitle(String link){
        getPage(link);
        String heading = null;
        heading=page.title();
        return heading;
    }

    private void getPage(String link){
        try {
            page= Jsoup.connect(link).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
