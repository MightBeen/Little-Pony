package com.io.portainer.service.sys.impl;

import com.io.portainer.data.entity.sys.SysLog;
import com.io.portainer.mapper.sys.SysLogMapper;
import com.io.portainer.service.sys.SysLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 日志类，包括操作日志和请求接收日志 服务实现类
 * </p>
 *
 * @author me
 * @since 2022-07-10
 */
@Service
public class SysLogServiceImpl extends ServiceImpl<SysLogMapper, SysLog> implements SysLogService {

}
