package com.io.core;

import com.io.portainer.common.utils.CommonUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//@SpringBootTest
class AdminSystemApplicationTests {


    @Test
    void initTest() {
        System.out.println(CommonUtils.portainerFormatWrapper(new ArrayList<>()));
    }


    @Test
    void contextLoads() throws IOException {
        List<LocalDateTime> l = new ArrayList<>();
        l.add(LocalDateTime.now());
        l.add(LocalDateTime.now().plusDays(1));
        l.add(LocalDateTime.now().plusDays(2));
        l.add(LocalDateTime.now().plusDays(3));
        l.add(LocalDateTime.now().plusDays(4));

        System.out.println(l.stream().min(LocalDateTime::compareTo).get());
    }

}
