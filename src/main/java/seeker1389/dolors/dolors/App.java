package seeker1389.dolors.dolors;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws AppException, IOException {

       // Scanner input = new Scanner(System.in);
        String url = "https://aagmaal.life/page/2/";
        Crawler crawler = new Crawler(url).pagelimit(500).setUrLength(0).crawlOnlyBaseUrl().setLoggingOff().start();


    }
}
