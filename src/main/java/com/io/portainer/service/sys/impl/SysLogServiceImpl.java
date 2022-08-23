package com.io.portainer.service.sys.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.io.portainer.data.entity.sys.SysLog;
import com.io.portainer.mapper.sys.SysLogMapper;
import com.io.portainer.service.sys.SysLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 日志类，包括操作日志和请求接收日志 服务实现类
 * </p>
 *
 * @author me
 * @since 2022-07-10
 */
@Service
@Slf4j

public class SysLogServiceImpl extends ServiceImpl<SysLogMapper, SysLog> implements SysLogService {


    @Override
    public List<SysLog> selectSysLogByPageAndType(Integer pageNo, Integer pageSize, Integer type) {
        IPage<SysLog> page = new Page<>(pageNo, pageSize);
        QueryWrapper<SysLog> wrapper = new QueryWrapper<>();
            wrapper.eq("type",type);
        wrapper.orderByDesc("created");
        IPage<SysLog> list = this.page(page,wrapper);
        List<SysLog> gc=list.getRecords();
        return gc;
    }

    @Override
    public List<SysLog> selectSysLogByPage(Integer pageNo, Integer pageSize) {
        //分页
        IPage<SysLog> page = new Page<>(pageNo, pageSize);
        //查询All不填eq.()就默认ALL
        QueryWrapper<SysLog> wrapper = new QueryWrapper<>();
        //排序
        wrapper.orderByDesc("created");
        IPage<SysLog> list = this.page(page,wrapper);
        //这里是实际查询出来的集合
        List<SysLog> gc=list.getRecords();
        return gc;
    }


    @Override
    public SysLog recordLog(String detail, Long operatorId, String title,
                             Integer type) {
            SysLog sysLog = new SysLog();
            sysLog.setCreated(LocalDateTime.now());
            sysLog.setTitle(title);
            sysLog.setDetail(detail);
            sysLog.setType(type);
            log.info(detail);
            this.save(sysLog);
            return sysLog;
    }
}
