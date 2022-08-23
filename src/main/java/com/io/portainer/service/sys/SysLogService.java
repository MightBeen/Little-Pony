package com.io.portainer.service.sys;

import com.io.portainer.data.entity.sys.SysLog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 日志类，包括操作日志和请求接收日志 服务类
 * </p>
 *
 * @author me
 * @since 2022-07-10
 */
public interface SysLogService extends IService<SysLog> {
    List<SysLog> selectSysLogByPageAndType(Integer pageNo, Integer pageSize, Integer type) ;

    List<SysLog> selectSysLogByPage(Integer pageNo, Integer pageSize) ;

    SysLog recordLog(String detail,Long operatorId,
                            String title, Integer type) ;
}