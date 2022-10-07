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
import reactor.util.annotation.Nullable;

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

    private Long wosId;

    private Long expectEndpointId;

    private Integer applyDays;

    private String remark;

    /**
     * 默认为当前时间
     */
    private LocalDateTime created = LocalDateTime.now();

    /**
     *    用户期望使用时间
      */
    @Nullable
    private LocalDateTime expectDate;

    /**
     *      系统对其处理时间;
     *      默认为用户期望使用时间;
     *      如果在期望时间内目标资源不可用，则设为目标资源最近可用时间
     */
    @TableField(exist = false)
    private LocalDateTime expired;

    public SysWaitList(SysCheckList checkList, Long expectEndPointId, Integer days, Long wosId, LocalDateTime expectDate){
        this.relatedUserId = checkList.getRelatedUserId();
        this.remark = checkList.getMessage();
        this.expectEndpointId = expectEndPointId;
        this.applyDays = days;
        this.wosId = wosId;
        this.expectDate = expectDate;
        this.expired = expectDate;
    }


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
