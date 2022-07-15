package com.io.portainer.Controller.ptr;


import com.io.portainer.service.ptr.PtrEndpointService;
import com.io.portainer.service.ptr.PtrUserService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class PtrBaseController {
    @Autowired
    PtrEndpointService ptrEndpointService;

    @Autowired
    PtrUserService ptrUserService;
}
