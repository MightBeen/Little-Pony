package com.io.core.common.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 
 * </p>
 *
 * @author me
 * @since 2022-06-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysUser extends CoreBaseEntity {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "用户名不能为空")
    private String username;

    private String password;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 学/工号
     */
    private String studentJobId;

    /**
     * 工单系统中对应id
     */
    private Long wosId;

    private String city;

    private LocalDateTime lastLogin;


    @TableField(exist = false)
    private List<SysRole> sysRoles = new ArrayList<>();
}
