package com.io.portainer.data.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.io.portainer.common.annotation.PtrMapper;
import com.io.portainer.data.entity.ptr.PtrUser;
import com.io.portainer.data.entity.sys.SysWaitList;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class WaitListVo extends SysWaitList {
    private String username;

    /**
     * 工单系统中id
     */
    private Long wosId;


    /**
     * 学号或工号
     */
    private String studentJobId;


    @TableId(value = "name")
    String targetEndpointName;
}
