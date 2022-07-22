package com.io.portainer.data.entity.ptr;

import com.io.portainer.common.annotation.PtrMapper;
import com.io.portainer.common.timer.Checkable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @author me
 * @since 2022-07-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PtrUser extends PtrBaseEntity implements Checkable {

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

    @Override
    public LocalDateTime getExpired() {
        // 永不过期
        return LocalDateTime.now().plusDays(1);
    }
}
