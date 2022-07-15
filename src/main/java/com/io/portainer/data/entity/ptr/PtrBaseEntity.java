package com.io.portainer.data.entity.ptr;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.io.portainer.common.annotation.PtrMapper;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public abstract class PtrBaseEntity {
    @PtrMapper
//    @TableId(type = IdType.)
    private Long id;

    private LocalDateTime created;

    private LocalDateTime updated;
}
