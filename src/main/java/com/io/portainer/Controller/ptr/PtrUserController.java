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
import com.io.portainer.data.entity.ptr.PtrEndpoint;
import com.io.portainer.data.entity.ptr.PtrUser;
import com.io.portainer.data.entity.ptr.PtrUserEndpoint;
import com.io.portainer.data.entity.sys.SysWaitList;
import com.io.portainer.data.vo.EndPointDetailVo;
import com.io.portainer.data.vo.EndPointUserDetailVo;
import com.io.portainer.service.sys.SysWaitListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/ptr/user")
public class PtrUserController extends PtrBaseController {

    @Autowired
    UpdateManager updateManager;
    @Autowired
    private SysWaitListService sysWaitListService;


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


    @GetMapping("/use/info/{type}")
    public ResultWrapper endpointsInfo(@PathVariable String type){
        List<PtrEndpoint> list = ptrEndpointService.list(new QueryWrapper<PtrEndpoint>().eq("resource_type", type));
        List<EndPointDetailVo> vos = new ArrayList<>();

        list.forEach(ep -> {
            EndPointDetailVo vo = new EndPointDetailVo(ep);
            vos.add(vo);

            // 从ue表中获取当前正在使用的用户
            List<PtrUserEndpoint> using = ptrUserEndpointService
                    .list(new QueryWrapper<PtrUserEndpoint>().eq("endpoint_id", ep.getId()));
            using.forEach(ue -> {
                EndPointUserDetailVo userVo = new EndPointUserDetailVo();
                userVo.setUpdated(ue.getUpdated().toLocalDate());
                userVo.setStarted(ue.getCreated().toLocalDate());
                userVo.setExpired(ue.getExpired().toLocalDate());
                vo.getUsingList().add(userVo);
            });

            // 从waitList 中获取预定的用户
            List<SysWaitList> booked = sysWaitListService
                    .list(new QueryWrapper<SysWaitList>().eq("expect_endpoint_id", ep.getId()));
            booked.forEach(wl -> {
                EndPointUserDetailVo userVO = new EndPointUserDetailVo();
                userVO.setUpdated(wl.getUpdated().toLocalDate());
                userVO.setStarted(wl.getCreated().toLocalDate());
                userVO.setExpired(wl.getExpired().toLocalDate());
                vo.getBookedList().add(userVO);
            });

        });

        return ResultWrapper.success(vos);
    }

}
