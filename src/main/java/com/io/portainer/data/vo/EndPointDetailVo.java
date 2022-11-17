package com.io.portainer.data.vo;

import com.io.portainer.data.entity.ptr.PtrEndpoint;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class EndPointDetailVo extends PtrEndpoint {
    private List<EndPointUserDetailVo> usingList = new ArrayList<>();
    private List<EndPointUserDetailVo> bookedList = new ArrayList<>();

    public EndPointDetailVo(PtrEndpoint endpoint) {
        this.setId(endpoint.getId());
        this.setName(endpoint.getName());
        this.setResourceType(endpoint.getResourceType());
        this.setCapacity(endpoint.getCapacity());
        this.setDescription(endpoint.getDescription());
        this.setStatus(endpoint.getStatus());
        this.setUpdated(endpoint.getUpdated());
        this.setCreated(endpoint.getCreated());
    }

    @Override
    public Integer getSpace(){
        return Math.max(this.getCapacity() - usingList.size(), 0);
    }
}
