package com.example.lms.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.lms.dto.SysUserDTO;

import jakarta.servlet.http.HttpSession;

@ControllerAdvice
public class GlobalModelAdvice {

    @ModelAttribute("loginUserName")
    public String loginUserName(HttpSession session) {

        SysUserDTO user = (SysUserDTO) session.getAttribute("loginUser");

        if (user != null) {
            return user.getUserName();
        }

        return "";
    }
}
