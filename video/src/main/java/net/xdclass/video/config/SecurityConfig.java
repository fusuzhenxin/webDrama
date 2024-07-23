package net.xdclass.video.config;

import net.xdclass.video.filter.JwtAuthenticationTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity  //启用 Spring Security 的 Web 安全支持并提供 Spring MVC 集成。
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    //PasswordEncoder 用于编码和验证密码，这里使用的是 BCryptPasswordEncoder。
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    //注入我们在filter目录写好的类
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/resources/**");
    }

    //提供一个用于管理身份验证的 bean。
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                //禁用
                //基本身份验证
                .httpBasic().disable()
                //由于是前后端分离项目，所以要关闭csrf
                .csrf().disable()
                //自带的登录注册
                .formLogin().disable()
                .logout().disable()

                // //由于是前后端分离项目，所以session是失效的，我们就不通过Session获取SecurityContext
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                        .requestMatchers("/apiOne/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/user/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/user/register").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/crawler/page").permitAll()
//                        .requestMatchers(HttpMethod.POST,"/apiOne/user/userRegister").permitAll()
                        .requestMatchers("/api/file/cover").permitAll()
                        .requestMatchers("/api/user/export").permitAll()
                        .requestMatchers("/apiOne/file/uploadList").permitAll()
                        .requestMatchers("/files/image/**").permitAll()
                        .requestMatchers("/files/images/**").permitAll()
                        .requestMatchers("/files/video/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/details/selectTop10").permitAll()
//                        .requestMatchers( "/api/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        //拦截
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().authenticated());
        //---------------------------👇 设置security运行跨域访问 👇------------------

        http.cors();

        //---------------------------👆 设置security运行跨域访问 👆------------------


        //---------------------------认证过滤器的实现----------------------------------

        //把token校验过滤器添加到过滤器链中
        //第一个参数是上面注入的我们在filter目录写好的类，第二个参数表示你想添加到哪个过滤器之前
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

        //---------------------------异常处理的相关配置-------------------------------

        http.exceptionHandling()
//                //配置认证失败的处理器
                .authenticationEntryPoint(authenticationEntryPoint)
                //配置授权失败的处理器
                .accessDeniedHandler(accessDeniedHandler);

        return http.build();
    }
    @Autowired
    //注入Security提供的认证失败的处理器，这个处理器里面的AuthenticationEntryPointImpl实现类，用的不是官方的了，
    //而是用的是我们在handler目录写好的AuthenticationEntryPointImpl实现类，因为我们也是添加到容器把官方的这个实现类覆盖了
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    //注入Security提供的授权失败的处理器，这个处理器里面的AccessDeniedHandlerImpl实现类，用的不是官方的了，
    //而是用的是我们在handler目录写好的AccessDeniedHandlerImpl实现类，因为我们也是添加到容器把官方的这个实现类覆盖了
    private AccessDeniedHandler accessDeniedHandler;

//    --------------------------------------------------------------------------

}
