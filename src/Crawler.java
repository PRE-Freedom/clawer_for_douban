import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Crawler {
    public static WebDriver webDriver = new ChromeDriver();
    Map<String, String> cityUrlMap = new HashMap<String, String>();
    Map<String, String> header = new HashMap<String, String>();

    public void initParams(){
        cityUrlMap.put("beijing", "https://www.douban.com/group/beijingzufang/discussion?start=");
        cityUrlMap.put("shanghai", "https://www.douban.com/group/shanghaizufang/discussion?start=");
        cityUrlMap.put("hangzhou","https://www.douban.com/group/HZhome/discussion?start=");
        cityUrlMap.put("nanjing","https://www.douban.com/group/zf365/discussion?start=");
        cityUrlMap.put("shenzhen","https://www.douban.com/group/SZhouse/discussion?start=");
        cityUrlMap.put("guangzhou","https://www.douban.com/group/gz_rent/discussion?start=");
        cityUrlMap.put("suzhou","https://www.douban.com/group/szzf/discussion?start=");
        cityUrlMap.put("hefei","https://www.douban.com/group/276507/discussion?start=");

        header.put("Host","www.douban.com");
        header.put("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36");
        header.put("Accept","  text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        header.put("Accept-Language","zh-CN,zh;q=0.9");
        header.put("Accept-Encoding","gzip, deflate, br");
        header.put("Connection","keep-alive");
        header.put("Upgrade-Insecure-Requests","1");
    }

    public void getTailerInfo(String city) {
        initParams();

        try {
            //获取到该话题小组的总页数
            Connection getTotalPageConnection = Jsoup.connect(cityUrlMap.get(city) + 0);
            Document firstDocument  = getTotalPageConnection.headers(header).get();
            int totalPage = Integer.parseInt(firstDocument.select("span.thispage").attr("data-total-page"));
            //从第一页开始抓取，循环totalPage次
            int nowPage = 17;
            for (; nowPage <= totalPage; nowPage++) {
                System.out.println("当前页数："+nowPage);
                int nowStart = (nowPage - 1) * 25;
                Connection connect = Jsoup.connect(cityUrlMap.get(city) + nowStart).timeout(100000);
                webDriver.get(cityUrlMap.get(city) + nowStart);
                Set<Cookie> hasLoginCookie = webDriver.manage().getCookies();
                header.put("Cookie", hasLoginCookie.toString());
                //抓取每页的列表
                Document document = connect.headers(header).get();
                Elements table = document.getElementsByClass("olt");
                Elements list = table.select("tr");
                //抓取每个标题下的相关内容
                for (int i = 1; i < list.size(); i++) {
                    String title = list.get(i).select("td.title a").attr("title");
                    String url = list.get(i).select("td.title a").attr("href");
                    System.out.println(url);
                    String username = list.get(i).getElementsByTag("td").get(1).select("a").text();
                    //打开标题对应url，抓取手机号和时间
                    Connection secConnection = Jsoup.connect(url).timeout(100000);
                    webDriver.get(url);
                    Set<Cookie> secDocCookie = webDriver.manage().getCookies();
                    header.put("Cookie", secDocCookie.toString());
                    Document secDocument =  secConnection.headers(header).get();
                    String content = secDocument.select("div.topic-content p").text();
                    String phone = getPhoneNumbers(content);
                    if(phone.length() == 0){
                        continue;
                    }
                    String dateStr = secDocument.select("div.topic-doc span.color-green").text().toString();
                    int date = 0;
                    if (dateStr.length() >= 10) {
                        date = Integer.parseInt(dateStr.substring(0, 10).replace("-", ""));
                    }
                    Item item = new Item(title, url, phone, username, date);
                    try {
                        DBConnector.saveItem(item, city);
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                        continue;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getPhoneNumbers(String text) {
        Pattern pattern = Pattern.compile("(1\\d{10})");
        Matcher matcher = pattern.matcher(text);
        StringBuffer bf = new StringBuffer(20);
        if (matcher.find()) {
            bf.append(matcher.group());
        }

        return bf.toString();
    }
}
