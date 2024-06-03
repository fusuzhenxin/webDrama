//package net.xdclass.video.common;
//
//import org.openqa.selenium.By;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.chrome.ChromeOptions;
//import org.openqa.selenium.devtools.DevTools;
//
//
//import org.openqa.selenium.support.ui.ExpectedConditions;
//import org.openqa.selenium.support.ui.WebDriverWait;
//import org.springframework.beans.factory.annotation.Value;
//
//import java.time.Duration;
//import java.util.Optional;
//
//public class WebScraper {
//    @Value("${chromedriverPath}")
//    public static String chromedriverPath;
//    public static void main(String[] args) {
//
//        // 设置ChromeDriver的路径
//        System.setProperty("webdriver.chrome.driver","${chromedriverPath}");
//
//        // 设置Chrome选项
//        ChromeOptions options = new ChromeOptions();
////        options.addArguments("--headless"); // 无头模式
//
//        // 初始化WebDriver
//        WebDriver driver = new ChromeDriver(options);
//
//        // 启用DevTools
//        DevTools devTools = ((ChromeDriver) driver).getDevTools();
//        devTools.createSession();
//
//        // 启用网络跟踪
//        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
//
//        // 监听网络响应
//        devTools.addListener(Network.responseReceived(), responseReceived -> {
//            Response response = responseReceived.getResponse();
//            RequestId requestId = responseReceived.getRequestId();
//            if (response.getUrl().contains("desired_endpoint")) { // 检查请求的URL
//                String responseBody = devTools.send(Network.getResponseBody(requestId)).getBody();
//                System.out.println("URL: " + response.getUrl());
//                System.out.println("Status: " + response.getStatus());
//                System.out.println("Response: " + responseBody);
//            }
//        });
//
//        try {
//            // 打开网页
//            driver.get("https://v.ijujitv.cc/play/35236-2-1.html");
//
//            // 等待页面加载
//            WebElement dynamicElement = new WebDriverWait(driver, Duration.ofSeconds(30))
//                    .until(ExpectedConditions.presenceOfElementLocated(By.id("dynamicContentId")));
//
//            // 获取页面源代码
//            String pageSource = driver.getPageSource();
//            System.out.println(pageSource);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            // 关闭浏览器
//            driver.quit();
//        }
//    }
//}
