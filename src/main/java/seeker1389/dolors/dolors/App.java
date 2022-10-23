package seeker1389.dolors.dolors;

import java.io.IOException;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws AppException, IOException {

       // HelloApplication.main(args);
        String url ="http://takeindianmovies.pro/porntube/indian-bhabhi-ne-devar-ko-diya-mauka-adultbhabhi-com/#!/back";
        Scanner input = new Scanner(System.in);
        System.out.println("Enter the url");
        url = input.nextLine();

        new Crawler().getPageLinks(url);

    }
}
