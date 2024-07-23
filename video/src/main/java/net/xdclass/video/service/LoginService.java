package net.xdclass.video.service;

import net.xdclass.video.common.Result;
import net.xdclass.video.controller.dto.UserDTO;
import net.xdclass.video.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface LoginService {
    Result login(UserDTO userDTO);
    //-----------------------------------退出登录--------------------------------

    Result yylogout();


}
