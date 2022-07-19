package com.io.portainer.data.entity.ptr;

import com.baomidou.mybatisplus.annotation.TableField;
import com.io.portainer.common.annotation.PtrMapper;
import com.io.portainer.common.utils.CommonUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 即gpu资源虚拟机
 * </p>
 *
 * @author me
 * @since 2022-07-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PtrEndpoint extends PtrBaseEntity{

    private static final long serialVersionUID = 1L;

    /**
     * 节点名称
     */
    @PtrMapper
    private String name;

    @PtrMapper(ptrAlias = "URL")
    private String url;

    @PtrMapper
    private Integer status;

    /**
     * 资源类型，1010为独占型，3030为共享型
     */
    private Integer resourceType;


    private String description;


    /**
     * 用于存储从userIds中转化的user对象
     */
    @TableField(exist = false)
    private List<PtrUser> users = new ArrayList<>();

    /**
     * 用于从json数据中接收用户id
     */
    @TableField(exist = false)
    @PtrMapper(ptrAlias = "UserAccessPolicies")
    private List<Long> userIds = new ArrayList<>();



    public boolean available(Integer resourceType) {
        return this.getStatus().equals(1)
                && resourceType.equals(this.getResourceType())
                && this.getUserIds().size() < CommonUtils.getCapacity(resourceType);
    }

    public int getSpace(){
        int res = CommonUtils.getCapacity(this.getResourceType()) - this.getUserIds().size();
        return Math.max(res, 0);
    }
}
