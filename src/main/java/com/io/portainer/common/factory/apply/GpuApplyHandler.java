package com.io.portainer.common.factory.apply;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.io.core.common.wrapper.ResultWrapper;
import com.io.portainer.common.timer.components.UpdateManager;
import com.io.portainer.common.utils.PasswordUtil;
import com.io.portainer.common.utils.connect.WosSysConnector;
import com.io.portainer.data.dto.wos.BusinessType;
import com.io.portainer.data.dto.wos.WosMessageDto;
import com.io.portainer.data.dto.wos.WosUser;
import com.io.portainer.data.entity.ptr.PtrUser;
import com.io.portainer.service.sys.SysLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
public class GpuApplyHandler extends BusinessHandler {


    @Autowired
    UpdateManager updateManager;

    @Autowired
    WosSysConnector wosSysConnector;

    @Autowired
    SysLogService sysLogService;


    @Override
    protected String getBusinessCode() {
        return BusinessType.GPU_APPLY.code;
    }

    @Override
    public ResultWrapper process(WosUser wosUser) {
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
            ptrUser.setPassword(PasswordUtil.randomPassword());

            try {
                ptrUserService.addPtrUserToPtr(ptrUser);
            } catch (IOException e) {
                log.error(e.getMessage());
            }

            log.info("学/工号为 ："+ wosUser.getStudentJobId() + "的用户自动创建账户成功");
            sysLogService.recordLog("添加用户到portainer：" + wosUser.getUsername() , null , "处理用户申请",0 );

            // 向工单系统中该用户发送信息

            sendNewUserInfo(ptrUser, wosUser.getId());
        }

        try {
            Boolean res = ptrUserService.getEndPointAccessById(ptrUser, wosUser.getResourceType(), wosUser.getApplyDays());
        } catch (IOException e) {
            log.error(e.getMessage());
        }


        return ResultWrapper.success( "操作成功，申请已在处理",ptrUser);
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
