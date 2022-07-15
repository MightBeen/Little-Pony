package com.io.portainer.Controller.ptr;


import com.io.core.common.wrapper.ResultWrapper;
import com.io.portainer.data.entity.ptr.PtrEndpoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 即gpu资源虚拟机 前端控制器
 * </p>
 *
 * @author me
 * @since 2022-07-10
 */
@RestController
@RequestMapping("/api/ptr/endpoint")
public class PtrEndpointController extends PtrBaseController {

    @GetMapping("/fresh")
    public ResultWrapper freshEndpoints() {
        // todo: 将更新交由UpdateManager处理
        return ResultWrapper.success(ptrEndpointService.updatePtrEndpointsDataFromPtr());
    }

}
