package com.io.core.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CoreBaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private LocalDateTime created;

    private LocalDateTime updated;

    private Integer statu;
}
