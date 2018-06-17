import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.*;
import java.util.*;

public class LoginSimulate {
    private static HttpClient httpClient = new DefaultHttpClient();
    public static Map<String, String> cookieMap = new HashMap<String, String>();

    public static void loginDouban() {
        Connection conn = Jsoup.connect("https://accounts.douban.com/login");

        String login_src = "https://accounts.douban.com/login";
        String form_email = "18235101067";
        String form_password = "loveme12!";
        String captcha_id = getImgID();
        String login = "登录";
        String captcha_solution = "";

        System.out.println("请输入验证码：");
        BufferedReader buff = new BufferedReader(new InputStreamReader(System.in));
        try {
            captcha_solution = buff.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        conn.data("form_email", form_email);
        conn.data("form_password", form_password);
        conn.data("captcha-solution", captcha_solution);
        conn.data("captcha-id", captcha_id);
        conn.data("login", login);
        HttpPost httpPost = new HttpPost(login_src);
        try {
            //向后台请求数据,登陆网站
            Connection.Response resp = conn.method(Connection.Method.POST).execute();
            cookieMap = resp.cookies();
            System.out.println(cookieMap);

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String getImgID() {
        //Json的地址[数据中包含验证码的地址]
        String src = "https://www.douban.com/j/misc/captcha";
        HttpGet httpGet = new HttpGet(src);
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36");
        httpGet.setHeader("Accept-Encoding", "gzip, deflate, br");
        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        httpGet.setHeader("Connection", "keep-alive");

        String token = "";
        try {
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            //将json数据转化为map，对应的是key，value的形式。不理解json数据的，请看我前面的关于json解析的博客
            GzipDecompressingEntity decompressEntity = new GzipDecompressingEntity(entity);
            String content = EntityUtils.toString(decompressEntity, "utf-8");
            System.out.println("验证码相关信息：" + content);
            Map<String, String> mapList = null;
            mapList = getResultList(content);
            token = mapList.get("token");
            //获取验证码的地址
            String url = "https:" + mapList.get("url");
            //下载验证码并存储到本地
            downImg(url);
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return token;
    }

    /**
     * 用JSON 把数据格式化，并生成迭代器，放入Map中返回
     *
     * @param content 请求验证码时服务器返回的数据
     * @return Map集合
     */
    public static Map<String, String> getResultList(String content) {
        Map<String, String> maplist = new HashMap<String, String>();

        JSONObject jo = null;
        try {
            jo = new JSONObject(content.replaceAll(",\\\"r\\\":false", ""));
            Iterator it = jo.keys();
            String key = "";
            String value = "";
            while (it.hasNext()) {
                key = (String) it.next();
                value = jo.getString(key);
                maplist.put(key, value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return maplist;
    }

    /**
     * 此方法是下载验证码图片到本地
     *
     * @param src 给个验证图片完整的地址
     * @throws IOException
     */
    private static void downImg(String src) throws IOException {
        File fileDir = new File("/Users/guohaifang");
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        //图片下载保存地址
        File file = new File("/Users/guohaifang/yzm.png");
        if (file.exists()) {
            file.delete();
        }
        InputStream input = null;
        FileOutputStream out = null;
        HttpGet httpGet = new HttpGet(src);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            input = entity.getContent();
            int i = -1;
            byte[] byt = new byte[1024];
            out = new FileOutputStream(file);
            while ((i = input.read(byt)) != -1) {
                out.write(byt);
            }
            System.out.println("download picture success！");
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.close();
    }
}
