package com.huex.bamboohub.util;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordUtil {
    // 创建一个BCryptPasswordEncoder实例
    private final PasswordEncoder passwordEncoder;

    public PasswordUtil() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // 加密密码
    public String encryptPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    // 校验密码
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
