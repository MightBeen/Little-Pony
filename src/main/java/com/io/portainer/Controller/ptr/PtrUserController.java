package com.io.portainer.Controller.ptr;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.io.core.common.wrapper.ResultWrapper;
import com.io.portainer.common.check.components.UpdateManager;
import com.io.portainer.data.dto.wos.BusinessType;
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
        // TODO：用工厂模式优化
        if (wosUser.getBusinessType().equals(BusinessType.GPU_APPLY.code)) {

            PtrUser ptrUser = ptrUserService.getOne(new QueryWrapper<PtrUser>().eq("job_id",
                    wosUser.getJobId()));

            if (ptrUser == null) {
                log.info("为学/工号为 ："+ wosUser.getJobId() + "的用户自动创建账户");
                ptrUser = new PtrUser();
                ptrUser.setJobId(wosUser.getJobId());
                ptrUser.setCreated(LocalDateTime.now());
                ptrUser.setUsername(wosUser.getUsername());
                ptrUser.setRemark(wosUser.getRemark());
                // TODO ：设置密码生成
                ptrUser.setPassword("20210110722021011072");

                ptrUserService.addPtrUserToPtr(ptrUser);

                log.info("学/工号为 ："+ wosUser.getJobId() + "的用户自动创建账户成功");
            }

            Boolean res = ptrUserService.getEndPointAccessById(ptrUser, wosUser.getResourceType(), wosUser.getApplyDays());


            return ResultWrapper.success( "操作成功，申请已在处理",ptrUser);
        } else if (wosUser.getBusinessType().equals(BusinessType.GPU_RENEWAL.code)) {
            // TODO：添加续期业务
            throw new IllegalArgumentException("不支持的业务类型："+wosUser.getBusinessType());
        } else
            throw new IllegalArgumentException("不支持的业务类型："+wosUser.getBusinessType());
    }

    @GetMapping("/fresh")
    public ResultWrapper freshPtrUserDb() {
        updateManager.updateAll();
        return ResultWrapper.success("更新成功");
    }

}
