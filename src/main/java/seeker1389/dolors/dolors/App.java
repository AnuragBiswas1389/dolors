package seeker1389.dolors.dolors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws AppException, IOException {

       // HelloApplication.main(args);
        String url ="http://takeindianmovies.pro/porntube/indian-bhabhi-ne-devar-ko-diya-mauka-adultbhabhi-com/#!/back";
        Scanner input = new Scanner(System.in);
        System.out.println("Enter the url");
        url = input.nextLine();

        Document page = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 6.1; rv:80.0) Gecko/27132701 Firefox/78.7")
                .data("name", "jsoup").get();

        Media media = new Media();
        media.dataExtractor(page);

    }
}
