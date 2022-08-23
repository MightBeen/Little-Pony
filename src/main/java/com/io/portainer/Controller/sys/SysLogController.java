package com.io.portainer.Controller.sys;


import com.io.core.common.wrapper.ResultWrapper;
import com.io.portainer.Controller.ptr.PtrBaseController;
import com.io.portainer.service.sys.SysLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * <p>
 * 日志类，包括操作日志和请求接收日志 前端控制器
 * </p>
 *
 * @author me
 * @since 2022-07-10
 */
@RestController
@RequestMapping("/sys/log")
public class SysLogController extends PtrBaseController {
    @Autowired
    SysLogService sysLogService;


    @GetMapping("/{type}/{pageNumber}/{amount}")
    public ResultWrapper selectSysLog
            (                     @PathVariable(name = "type") @RequestBody  Integer sysLogType,
                                  @PathVariable(name = "pageNumber") @RequestBody Integer pageNumber ,
                                  @PathVariable(name = "amount") @RequestBody Integer amount)
            throws IOException {
            return ResultWrapper.success("查看日志第" + pageNumber + "页，每页展示" + amount + "条记录",
                    sysLogService.selectSysLogByPageAndType(pageNumber,amount,sysLogType));

    }

    @GetMapping("/{pageNumber}/{amount}")
    public ResultWrapper selectALLSysLog
            (
                    @PathVariable(name = "pageNumber") @RequestBody Integer pageNumber ,
                    @PathVariable(name = "amount") @RequestBody Integer amount)
            throws IOException {
        return ResultWrapper.success("查看日志第" + pageNumber + "页，每页展示" + amount + "条记录",
                sysLogService.selectSysLogByPage(pageNumber,amount));

    }

}
