package com.io.portainer.data.dto.wos;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class WosUser {


    @NotNull
    private Long jobId;

    @NotBlank(message = "用户名不能为空")
    private String username;

    private String remark;

    /**
     * 申请的gpu资源类型
     */
    @NotNull
    private Integer resourceType;

    /**
     * 业务类型
     */
    @NotNull
    private String businessType;

    /**
     * 申请时长
     */
    @NotNull
    private Integer applyDays;
}
