package com.io.portainer.common.factory.apply;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.io.core.common.wrapper.ResultWrapper;
import com.io.portainer.common.utils.connect.WosSysConnector;
import com.io.portainer.data.dto.wos.BusinessType;
import com.io.portainer.data.dto.wos.WosMessageDto;
import com.io.portainer.data.dto.wos.WosUser;
import com.io.portainer.data.entity.ptr.PtrUser;
import com.io.portainer.data.entity.ptr.PtrUserEndpoint;
import com.io.portainer.service.sys.SysLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GpuRenewalHandler extends BusinessHandler{

    @Autowired
    WosSysConnector wosSysConnector;
    final
    SysLogService sysLogService;

    public GpuRenewalHandler(SysLogService sysLogService) {
        this.sysLogService = sysLogService;
    }

    @Override
    protected String getBusinessCode() {
        return BusinessType.GPU_RENEWAL.code;
    }

    @Override
    public ResultWrapper process(WosUser wosUser) {
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
                sendNewUserInfo(ptrUser1, wosUser.getId());
            }
            else
                return ResultWrapper.fail(6001, "不存在的用户名：" + wosUser.getUsername());
            return ResultWrapper.success( "操作成功，延期申请已在处理",ptrUser1);
        } else
            return ResultWrapper.fail(6001,"不存在的用户名：" + wosUser.getUsername());
    }

    private void sendNewUserInfo(PtrUser ptrUser, Long wosUserId) {
        StringBuilder sb = new StringBuilder();
        WosMessageDto message = new WosMessageDto();
        message.setReceiver(wosUserId);
        message.setTitle("Gpu管理系统账户延期通知 ");
        sb.append("您在Gpu管理系统的账户").append("登录名称: ")
                .append(ptrUser.getUsername())
                .append("\n")
                .append("已通过延期申请！\n\t")
                .append("可登录系统进行确认。");

        message.setDescription(sb.toString());
        wosSysConnector.asyncSendMessage(message);
    }
}

