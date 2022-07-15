package com.io.portainer.service.sys.impl;

import com.io.portainer.data.entity.sys.SysCheckList;
import com.io.portainer.mapper.sys.SysCheckListMapper;
import com.io.portainer.service.sys.SysCheckListService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 代办事项清单，用于管理员人工进行操作 服务实现类
 * </p>
 *
 * @author me
 * @since 2022-07-12
 */
@Service
public class SysCheckListServiceImpl extends ServiceImpl<SysCheckListMapper, SysCheckList> implements SysCheckListService {

    @Override
    public SysCheckList AddItemToCheckList(SysCheckList item) {

        this.save(item);

        return item;
    }
}
