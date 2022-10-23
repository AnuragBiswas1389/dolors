package seeker1389.dolors.dolors;

import java.io.IOException;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws AppException, IOException {

       // HelloApplication.main(args);
        Scanner input = new Scanner(System.in);
        System.out.println("Enter the url");
        String url = input.nextLine();

        Crawler crawler = new Crawler(url,"sequence",true);

    }
}
