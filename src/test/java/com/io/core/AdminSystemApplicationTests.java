package com.io.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.io.portainer.common.utils.CommonUtils;
import com.io.portainer.common.utils.PortainerConnector;
import com.io.portainer.common.utils.PtrJsonParser;
import com.io.portainer.data.entity.ptr.PtrEndpoint;
import com.io.portainer.data.entity.ptr.PtrUser;
import com.io.portainer.service.ptr.PtrUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
