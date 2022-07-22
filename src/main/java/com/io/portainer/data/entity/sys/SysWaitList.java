package com.io.portainer.data.entity.sys;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import com.io.portainer.common.timer.Checkable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 
 * </p>
 *
 * @author me
 * @since 2022-07-15
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SysWaitList implements Serializable, Checkable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id")
    private Long id;

    private Long relatedUserId;

    private Long jobId;

    private Integer resourceType;

    private Integer applyDays;

    private String remark;

    /**
     * 默认为当前时间
     */
    private LocalDateTime created = LocalDateTime.now();

    public SysWaitList(SysCheckList checkList, Integer resourceType, Integer days, Long jobId){
        this.relatedUserId = checkList.getRelatedUserId();
        this.remark = checkList.getMessage();
        this.resourceType = resourceType;
        this.applyDays = days;
        this.jobId = jobId;
    }

    /**
     * 用于存储最近对应资源用户最近过期时间
     */
    @TableField(exist = false)
    private LocalDateTime Expired;

    /**
     * 由于没有更新时间，将创建时间作为更新时间
     * @return
     */
    @Override
    public LocalDateTime getUpdated() {
        return this.getCreated();
    }

    // TODO: 2022/7/18 处理紧急调度情况，使紧急调度在等待队列顶部 
    /**
     *     重写，使最早（创建）更新的在检查队列最上方
     */
    @Override
    public int compareTo(Checkable o) {
        if (o == null) {
            throw new IllegalArgumentException("参数不能为null");
        }

        Long updated = this.getUpdated().toEpochSecond(ZoneOffset.of("+8"));

        return updated.compareTo(o.getUpdated().toEpochSecond(ZoneOffset.of("+8")));
    }
}
