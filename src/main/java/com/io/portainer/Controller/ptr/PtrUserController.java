package com.io.portainer.Controller.ptr;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.io.core.common.wrapper.ResultWrapper;
import com.io.portainer.common.exception.PortainerException;
import com.io.portainer.common.factory.ApplyHandlerFactory;
import com.io.portainer.common.factory.GpuResourceTypeFactory;
import com.io.portainer.common.timer.components.UpdateManager;
import com.io.portainer.common.utils.connect.WosSysConnector;
import com.io.portainer.data.dto.wos.BusinessType;
import com.io.portainer.data.dto.wos.WosMessageDto;
import com.io.portainer.data.dto.wos.WosUser;
import com.io.portainer.data.entity.ptr.PtrUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@Slf4j
@RequestMapping("/api/ptr/user")
public class PtrUserController extends PtrBaseController {

    @Autowired
    UpdateManager updateManager;


    @PostMapping("/apply")
    public ResultWrapper userApplyHandler(@Validated @RequestBody WosUser wosUser) throws IOException {

        return ApplyHandlerFactory.handleApply(wosUser);

    }

    @GetMapping("/fresh")
    public ResultWrapper freshPtrUserDb() {
        updateManager.updateAll();
        return ResultWrapper.success("更新成功");
    }

    @GetMapping("/error/demo")
    public ResultWrapper errorTest(){
        throw new PortainerException("啊啊啊啊啊啊啊啊啊啊！！！！", null, 400);
    }

}
