package com.io.portainer.Controller.sys;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.io.core.common.wrapper.ResultWrapper;
import com.io.portainer.data.entity.sys.SysWaitList;
import com.io.portainer.service.sys.SysWaitListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author me
 * @since 2022-07-15
 */
@RestController
@RequestMapping("/sys/wait-list")
public class SysWaitListController {

    @Autowired
    SysWaitListService sysWaitListService;

    @GetMapping()
    public ResultWrapper getWaitList(Integer pageNumber, Integer amount) {
        return ResultWrapper.success(sysWaitListService.getWaitLists(pageNumber, amount));
    }
}
