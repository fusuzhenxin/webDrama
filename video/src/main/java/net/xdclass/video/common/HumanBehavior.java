package net.xdclass.video.common;

import lombok.SneakyThrows;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Random;

public class HumanBehavior {

    // 随机生成请求间隔时间
    private static void randomDelay() {
        try {
            Random random = new Random();
            int delay = random.nextInt(2000) + 500; // 500ms到2500ms之间的随机延迟
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 随机生成请求头部信息
    private static String randomUserAgent() {
        String[] userAgents = {
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36",
                // 添加更多的用户代理信息
        };
        Random random = new Random();
        return userAgents[random.nextInt(userAgents.length)];
    }

    // 发送请求
    private static Document sendRequest(String url) {
        try {
            randomDelay(); // 发送请求前先等待随机时间
            Connection connection = Jsoup.connect(url);
            connection.userAgent(randomUserAgent()); // 设置随机User-Agent
            Connection.Response response = connection.execute();
            if (response.statusCode() == 200) {
                return response.parse();
            } else {
                System.out.println("Failed to fetch the page. Status code: " + response.statusCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 示例：爬取页面内容
    @SneakyThrows
    public static void main(String[] args) {
        String url = "https://v.ijujitv.cc/detail/51748.html";
        Document document = Jsoup.connect(url).get();
        Elements elements = document.getElementsByClass("albumDetailMain-left");
        for (Element element:elements){
            String img = element.select("img").attr("data-original");
            System.out.println(img);
        }
    }
}

