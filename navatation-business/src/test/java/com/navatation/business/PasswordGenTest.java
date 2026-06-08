package com.navatation.business;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenTest {
    @Test
    public void generateHash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println("====== HASH_START ======");
        System.out.println(encoder.encode("admin123"));
        System.out.println("====== HASH_END ======");
    }
}
