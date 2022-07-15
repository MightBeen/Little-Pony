package com.io.core.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

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
public class SysRole extends CoreBaseEntity {

    private static final long serialVersionUID = 1L;
    @NotBlank(message = "身份名不能为空")
    private String name;

    @NotBlank(message = "角色编码不能为空")
    private String code;

    /**
     * 备注
     */
    private String remark;

    @TableField(exist = false)
    private List<Long> menuIds = new ArrayList<>();
}
