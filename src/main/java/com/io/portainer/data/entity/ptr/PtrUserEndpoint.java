package com.io.portainer.data.entity.ptr;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.io.portainer.common.check.Checkable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 该数据会随着Endpoint更新
 * </p>
 *
 * @author me
 * @since 2022-07-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PtrUserEndpoint extends PtrBaseEntity implements Checkable {

    private static final long serialVersionUID = 1L;


    private Long userId;

    private Long endpointId;

    private LocalDateTime expired;


}
