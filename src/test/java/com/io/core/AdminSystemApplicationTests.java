package com.io.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.io.portainer.common.utils.CommonUtils;
import com.io.portainer.common.utils.PortainerConnector;
import com.io.portainer.common.utils.PtrJsonParser;
import com.io.portainer.data.entity.ptr.PtrUser;
import com.io.portainer.service.ptr.PtrUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
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

        String jsonArray = "[\n" +
                "    {\n" +
                "        \"Id\": 1,\n" +
                "        \"Username\": \"admin\",\n" +
                "        \"UserTheme\": \"auto\",\n" +
                "        \"Role\": 1,\n" +
                "        \"TokenIssueAt\": 0,\n" +
                "        \"PortainerAuthorizations\": null,\n" +
                "        \"EndpointAuthorizations\": null\n" +
                "    },\n" +
                "    {\n" +
                "        \"Id\": 18,\n" +
                "        \"Username\": \"admin-system\",\n" +
                "        \"UserTheme\": \"\",\n" +
                "        \"Role\": 1,\n" +
                "        \"TokenIssueAt\": 0,\n" +
                "        \"PortainerAuthorizations\": null,\n" +
                "        \"EndpointAuthorizations\": null\n" +
                "    },\n" +
                "    {\n" +
                "        \"Id\": 19,\n" +
                "        \"Username\": \"suserasdasd\",\n" +
                "        \"UserTheme\": \"\",\n" +
                "        \"Role\": 2,\n" +
                "        \"TokenIssueAt\": 0,\n" +
                "        \"PortainerAuthorizations\": null,\n" +
                "        \"EndpointAuthorizations\": null\n" +
                "    },\n" +
                "    {\n" +
                "        \"Id\": 21,\n" +
                "        \"Username\": \"javva\",\n" +
                "        \"UserTheme\": \"\",\n" +
                "        \"Role\": 2,\n" +
                "        \"TokenIssueAt\": 0,\n" +
                "        \"PortainerAuthorizations\": null,\n" +
                "        \"EndpointAuthorizations\": null\n" +
                "    },\n" +
                "    {\n" +
                "        \"Id\": 22,\n" +
                "        \"Username\": \"fsa\",\n" +
                "        \"UserTheme\": \"\",\n" +
                "        \"Role\": 2,\n" +
                "        \"TokenIssueAt\": 0,\n" +
                "        \"PortainerAuthorizations\": null,\n" +
                "        \"EndpointAuthorizations\": null\n" +
                "    }\n" +
                "]";
        String demo2 = "{\n" +
                "    \"Id\": 1,\n" +
                "    \"Username\": \"admin\",\n" +
                "    \"UserTheme\": \"auto\",\n" +
                "    \"Role\": 1,\n" +
                "    \"TokenIssueAt\": 0,\n" +
                "    \"PortainerAuthorizations\": null,\n" +
                "    \"EndpointAuthorizations\": null\n" +
                "}";
//        String demo3 = portainerConnector.getRequest("/users/1" ).body().string();
//        System.out.println(demo3);
        List<PtrUser> ptrUsers = new PtrJsonParser<PtrUser>(PtrUser.class).parseJsonArray(jsonArray);
        PtrUser ptrUser = new PtrJsonParser<PtrUser>(PtrUser.class).parseJson(demo2);
        System.out.println(ptrUsers.toString());
        System.out.println(ptrUser);
    }

}
