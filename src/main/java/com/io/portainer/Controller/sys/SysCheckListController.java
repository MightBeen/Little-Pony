package com.io.portainer.Controller.sys;


import com.io.core.common.wrapper.ResultWrapper;
import com.io.portainer.data.entity.sys.SysCheckList;
import com.io.portainer.service.sys.SysCheckListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 代办事项清单，用于管理员人工进行操作 前端控制器
 * </p>
 *
 * @author me
 * @since 2022-07-12
 */
@RestController
@RequestMapping("/sys/checklist")
public class SysCheckListController {

    @Autowired
    SysCheckListService sysCheckListService;

    @GetMapping
    public ResultWrapper getCheckList(){
        List<SysCheckList> list = sysCheckListService.list();
        return ResultWrapper.success(list);
    }

}
