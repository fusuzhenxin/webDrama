package net.xdclass.video;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
public class Test2 {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void testStringSet(){
        redisTemplate.opsForValue().set("name","xdclass.net");
    }

    @Test
    void testStringGet(){
        String str1= (String) redisTemplate.opsForValue().get("name");
         String str2=stringRedisTemplate.opsForValue().get("name");
        System.out.println(str2);
    }
@Test
    void m() {
    // 示例字符串
    String episode = "第1集";

    // 定义正则表达式以匹配数字
    Pattern pattern = Pattern.compile("\\d+");
    Matcher matcher = pattern.matcher(episode);

    if (matcher.find()) {
        // 提取并打印匹配到的数字
        String number = matcher.group();
        System.out.println("提取的数字是: " + number);
    } else {
        System.out.println("未找到数字");
    }
}


    public static String extractNumber(String text) {
        Pattern pattern = Pattern.compile("\\d+"); // 正则表达式匹配一个或多个数字
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(); // 返回第一个匹配的数字
        }
        return ""; // 如果没有找到数字，则返回空字符串
    }
}
