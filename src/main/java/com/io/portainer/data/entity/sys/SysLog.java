package com.io.portainer.data.entity.sys;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

// TODO: 2022/7/16 添加执行顺序
/**
 * <p>
 * 日志类，包括操作日志和请求接收日志
 * </p>
 *
 * @author me
 * @since 2022-07-10
 */
@Data
public class SysLog{

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 申请描述，如果是异常则为异常信息
     */
    private String detail;

    /**
     * 操作者id
     */
    private Long operatorId;

    /**
     * 标题，如：“紧急调度”、“异常发生”
     */
    private String title;

    private LocalDateTime created;
}
