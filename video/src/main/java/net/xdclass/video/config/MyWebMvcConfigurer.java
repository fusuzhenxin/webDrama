package net.xdclass.video.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author hotte
 */
@Configuration
public class MyWebMvcConfigurer implements WebMvcConfigurer {
    //addResourceHandlers: 这个方法用于注册资源处理器，允许Spring MVC查找并提供静态资源（如图像、视频等）。
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/files/image/**").addResourceLocations("file:F:\\webDrama\\video\\src\\main\\resources\\files\\image\\");
        registry.addResourceHandler("/files/video/**").addResourceLocations("file:F:\\webDrama\\video\\src\\main\\resources\\files\\video\\");
        registry.addResourceHandler("/files/images/**").addResourceLocations("file:F:\\webDrama\\video\\src\\main\\resources\\files\\images\\");
    }
}

