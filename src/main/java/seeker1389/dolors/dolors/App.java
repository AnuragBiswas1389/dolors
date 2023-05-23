package seeker1389.dolors.dolors;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws MalformedURLException {

        String url =" https://www.google.com/";
        URL sourceUrl = new URL(url);

        String dbname=sourceUrl.getHost().replace('.','_');

        Runnable mediaScrap = new MediaScraper(url,"root","seeker1389");

        Runnable crawler = new Crawler(url ,new Db("root","seeker1389",dbname))
                .crawlOnlyBaseUrl()
                .enableThumbnailExtractor();



        Thread th1 = new Thread(crawler, "My new thread1");
      //  th1.start();


        Thread th2 = new Thread(mediaScrap, "My new thread2");
        th2.start();
/*
        Scanner input = new Scanner(System.in);
        Db db=null;

        String dbUserName,dbPassword,dbUrl;
        String url="";String config;
        String dbname="";

    System.err.println("----------------Welcome to Dolors\t [V1.5 nightQueen]-------------------- ");
        System.out.println("Entering into assistive config menu :)");

  //--------------------------------------configuration-----------------------
        System.out.println("enter the URL:");
        url = input.nextLine();
        try {
            URL sourceUrl = new URL(url);
            dbname=sourceUrl.getHost().replace('.','_');
        } catch (MalformedURLException e) {
            System.out.println("error in the url, please correct and enter.");
            System.exit(-1);
            throw new RuntimeException(e);
        }
//-----------------------------------------[DB config]-------------------------------------------------------
        System.out.println("Configuration for database:");
        System.out.println("enter userName");
        dbUserName=input.nextLine();

        System.out.println("enter password");
        dbPassword=input.nextLine();

        System.out.println("enter Connection url, press enter to skip to default connection (default localhost:3306)");
        dbUrl=input.nextLine();

        if(!(dbUrl.equalsIgnoreCase(""))){
            db = new Db(dbUserName,dbPassword,dbname,url);
        }else {
             db = new Db(dbUserName,dbPassword,dbname);
        }


//-----------------------------------------[crawler config]-------------------------------------------------------
        System.out.println("Config crawler-----------------------------");
        Crawler crawler = new Crawler(url, db);
        System.out.println("enter the  Crawl type: \n (1)CRAWL BY PAGE SEQUENCE \n (2)CRAWL SINGLEPAGE ONLY \n (3)CRAWL URL  ");
        String crawlConfig = input.nextLine();
        switch (crawlConfig){
            case "1":
                crawler.crawlSequentially();
                break;
            case "2":
                crawler.crawlSinglePage();
                break;
            default:
                System.out.println("Error");
                break;
        }

        System.out.println("Enter page limit for sequential crawling");
        int pageLimit = input.nextInt();
        crawler.pagelimit(pageLimit);

        System.out.println("Enter minimum URL length, press enter for none");
        int urLenght = input.nextInt();
        if(urLenght>0){
            crawler.pagelimit(urLenght);
        }

        System.out.println("Do you want to crawl only the SOURCE URL?[Y=yes, N=no]");
        String onlySource = input.next();
        if(onlySource.equalsIgnoreCase("y")){
            crawler.crawlOnlyBaseUrl();
        }

        System.out.println("Do you want to DISABLE logging data in the console?[Y=yes, N=no]");
        String logging = input.next();
        if(onlySource.equalsIgnoreCase("y")){
            crawler.setLoggingOff();
        }

        System.out.println("Do you want to Enable thumbnail data?[Y=yes, N=no]");
        String thum = input.next();
        if(onlySource.equalsIgnoreCase("y")){
            crawler.enableThumbnailExtractor();
        }


        crawler.start();

*/


    }
}
