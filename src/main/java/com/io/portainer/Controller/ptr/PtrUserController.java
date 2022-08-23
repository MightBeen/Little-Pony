package com.io.portainer.Controller.ptr;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.io.core.common.wrapper.ResultWrapper;
import com.io.portainer.common.exception.PortainerException;
import com.io.portainer.common.timer.components.UpdateManager;
import com.io.portainer.common.utils.WosSysConnector;
import com.io.portainer.data.dto.wos.BusinessType;
import com.io.portainer.data.dto.wos.WosMessageDto;
import com.io.portainer.data.dto.wos.WosUser;
import com.io.portainer.data.entity.ptr.PtrUser;
import com.io.portainer.data.entity.ptr.PtrUserEndpoint;
import com.io.portainer.service.sys.SysLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import static com.io.portainer.common.utils.PasswordUtil.randomPassword;

@RestController
@Slf4j
@RequestMapping("/api/ptr/user")
public class PtrUserController extends PtrBaseController {

    @Autowired
    UpdateManager updateManager;

    @Autowired
    WosSysConnector wosSysConnector;

    @Autowired
    SysLogService sysLogService;



    @PostMapping("/apply")
    public ResultWrapper userApplyHandler(@Validated @RequestBody WosUser wosUser) throws IOException {
        if (wosUser.getBusinessType().equals(BusinessType.GPU_APPLY.code)) {
            // 先执行更新
            updateManager.updateByType(PtrUser.class);

            PtrUser ptrUser = ptrUserService.getOne(new QueryWrapper<PtrUser>().eq("wos_id",
                    wosUser.getId()));

            if (ptrUser == null) {
                log.info("为学/工号为 ："+ wosUser.getStudentJobId() + "的用户自动创建账户");
                ptrUser = new PtrUser();
                ptrUser.setWosId(wosUser.getId());
                ptrUser.setStudentJobId(wosUser.getStudentJobId());
                ptrUser.setCreated(LocalDateTime.now());
                ptrUser.setUsername(wosUser.getUsername());
                ptrUser.setRemark(wosUser.getRemark());

                // TODO ：设置密码生成
                ptrUser.setPassword(randomPassword());

                ptrUserService.addPtrUserToPtr(ptrUser);

                log.info("学/工号为 ："+ wosUser.getStudentJobId() + "的用户自动创建账户成功");

                // 向工单系统中该用户发送信息

                sendNewUserInfo(ptrUser, wosUser.getId());
            }

            Boolean res = ptrUserService.getEndPointAccessById(ptrUser, wosUser.getResourceType(), wosUser.getApplyDays());


            return ResultWrapper.success( "操作成功，申请已在处理",ptrUser);
        } else if (wosUser.getBusinessType().equals(BusinessType.GPU_RENEWAL.code)) {
            // TODO：添加续期业务
            //finished_7.28
            PtrUser ptrUser1 = ptrUserService.getOne(new QueryWrapper<PtrUser>().eq("wos_id",
                    wosUser.getId()));
            PtrUser ptrUser2 = ptrUserService.getOne(new QueryWrapper<PtrUser>().eq("username",
                    wosUser.getUsername()));
            if(ptrUser1 != null && ptrUser2 != null){
                PtrUserEndpoint ptrUserEndpoint = ptrUserEndpointService.getOne(new QueryWrapper<PtrUserEndpoint>().eq("user_id",
                        ptrUser1.getId()));
                if(ptrUserEndpoint !=null) {
                    ptrUserEndpoint.setExpired(ptrUserEndpoint.getExpired().plusDays(wosUser.getApplyDays()));
                    ptrUserEndpointService.updateById(ptrUserEndpoint);
                    sysLogService.recordLog("处理用户申请延期：" + wosUser.getApplyDays(),null, "用户申请", 0);
                }
                else
                    return ResultWrapper.fail(6000, "不存在的用户名：" + wosUser.getUsername());
            return ResultWrapper.success( "操作成功，延期申请已在处理",ptrUser1);
            } else
                return ResultWrapper.fail(6000,"不存在的用户名：" + wosUser.getUsername());

        } else
            throw new IllegalArgumentException("不支持的业务类型：" + wosUser.getBusinessType());
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

    private void sendNewUserInfo(PtrUser ptrUser, Long wosUserId) {
        StringBuilder sb = new StringBuilder();
        WosMessageDto message = new WosMessageDto();
        message.setReceiver(wosUserId);
        message.setTitle("Gpu管理系统账户创建通知 ");
        sb.append("您在Gpu管理系统的账户").append("登录名称: ")
                .append(ptrUser.getUsername())
                .append("\n")
                .append("默认密码: ")
                .append(ptrUser.getPassword())
                .append("\n")
                .append("已自动创建完成！\n\t")
                .append("可登录系统进行确认。");

        message.setDescription(sb.toString());

        wosSysConnector.asyncSendMessage(message);
    }
}
