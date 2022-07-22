package com.io.portainer.data.entity.ptr;

import java.time.LocalDateTime;

import com.io.portainer.common.timer.Checkable;
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
