package com.io.portainer.data.dto.wos;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class WosUser {

    /**
     * 工单系统中该用户的id
     */
    @NotNull(message = "id 不能为空")
    Long id;

//    @NotNull(message = "工/学号不能为空")
    private String studentJobId;

    @NotBlank(message = "用户名不能为空")
    private String username;

    private String remark;

    /**
     * 申请的gpu资源类型
     */
//    @NotNull(message = "申请资源类型不能为空")
            // 单独处理
    private Integer resourceType;
    // 申请使用的节点名称
    private String endpointName;

    /**
     * 业务类型
     */
    @NotNull(message = "业务类型不能为空")
    private String businessType;

    /**
     * 申请时长
     */
    @NotNull(message = "申请时长不能为空")
    private Integer applyDays;

    /**
     * 预期开始使用时间
     */
    private LocalDateTime expectDate;

}
