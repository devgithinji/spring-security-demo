package com.densoft.sec;

import com.densoft.sec.model.User;
import com.densoft.sec.repository.UserRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class SecApplication implements CommandLineRunner {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public SecApplication(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public static void main(String[] args) {
        SpringApplication.run(SecApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        userRepo.deleteAll();
        userRepo.save(new User("dennis githinji", "wakahiad@gmail.com", passwordEncoder.encode("password"), "ROLE_ADMIN"));
        userRepo.save(new User("test user", "testuser@gmail.com", passwordEncoder.encode("password"), "ROLE_USER"));
    }
}
