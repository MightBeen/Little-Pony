package com.io.portainer.service.sys;

import com.io.portainer.data.entity.ptr.PtrEndpoint;
import com.io.portainer.data.entity.sys.SysCheckList;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.tomcat.jni.Local;

import java.time.LocalDateTime;

/**
 * <p>
 * 代办事项清单，用于管理员人工进行操作 服务类
 * </p>
 *
 * @author me
 * @since 2022-07-12
 */
public interface SysCheckListService extends IService<SysCheckList> {

    SysCheckList AddItemToWaitList(SysCheckList item, PtrEndpoint endpoint, Integer day, Long jobId, LocalDateTime expectDate);
}
