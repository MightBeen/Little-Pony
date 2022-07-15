package com.io;

import com.io.init.SysInitStarter;
import com.io.init.SysStartRegister;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class AdminSystemApplication {

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(AdminSystemApplication.class, args);
        applicationContext.getBean(SysStartRegister.class).init(applicationContext);
    }
}
