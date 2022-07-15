package com.io.portainer.data.entity.ptr;

import com.io.portainer.common.annotation.PtrMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author me
 * @since 2022-07-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PtrUser extends PtrBaseEntity{

    private static final long serialVersionUID = 1L;

    @PtrMapper
    private String username;

    @PtrMapper(persisted = true)
    private String password;

    /**
     * Portainer 用户权限，1为管理员，2为普通用户
     */
    @PtrMapper
    private Integer role = 2;

    /**
     * 学号或工号
     */
    private Long jobId;

    /**
     * 备注
     */
    private String remark;

}
