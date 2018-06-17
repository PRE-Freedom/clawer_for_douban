import org.openqa.selenium.Cookie;


public class Main {

    public static void main(String[] args) {
        LoginSimulate.loginDouban();

        System.getProperties().setProperty("webdriver.chrome.driver","/usr/local/bin/chromedriver");
        Crawler.webDriver.get("https://www.douban.com");
        LoginSimulate.cookieMap.forEach((key, value) -> {
            Cookie cookie = new Cookie(key, value );
            Crawler.webDriver.manage().addCookie(cookie);

        });

        Crawler crawler = new Crawler();
        crawler.getTailerInfo("beijing");
        System.out.println("------the end------");


    }


}
