package com.io.portainer.data.entity.sys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 代办事项清单，用于管理员人工进行操作
 * </p>
 *
 * @author me
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SysCheckList implements Serializable{

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 相关的ptr用户信息
     */
    private Long relatedUserId;

    /**
     * 相关管理员信息
     */
    private Long relatedOperatorId;

    /**
     * 类型，如1为发生异常，2为资源分配等待
     */
    private Long type;

    /**
     * 相关信息
     */
    private String message;

    /**
     * 处理状态
     */
    private Integer status;

    private LocalDateTime created;

    private LocalDateTime updated;

    /**
     * 相关资源类型
     */
    private Integer relatedResourceType;

    /**
     * 对应等待队列中id
     *
     */
    private Long waitListId;

}
