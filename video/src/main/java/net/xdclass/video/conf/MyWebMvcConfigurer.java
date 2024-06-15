package net.xdclass.video.conf;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author hotte
 */
@Configuration
public class MyWebMvcConfigurer implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/files/image/**").addResourceLocations("file:F:\\webDrama\\video\\src\\main\\resources\\files\\image\\");
        registry.addResourceHandler("/files/video/**").addResourceLocations("file:F:\\webDrama\\video\\src\\main\\resources\\files\\video\\");
        registry.addResourceHandler("/files/images/**").addResourceLocations("file:F:\\webDrama\\video\\src\\main\\resources\\files\\images\\");
    }
}

