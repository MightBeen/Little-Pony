package com.io.core;

import com.io.portainer.common.utils.CommonUtils;
import com.io.portainer.data.dto.wos.WosMessageDto;
import com.io.portainer.data.entity.ptr.PtrUser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

//@SpringBootTest
class AdminSystemApplicationTests {


    @Test
    void initTest() {
        System.out.println(CommonUtils.portainerFormatWrapper(new ArrayList<>()));
    }


    @Test
    void contextLoads() throws IOException {
        List<A> l = new ArrayList<>();
        l.sort(Comparator.comparingInt(o -> o.val));
    }
    class A{
        int val;
    }
}
