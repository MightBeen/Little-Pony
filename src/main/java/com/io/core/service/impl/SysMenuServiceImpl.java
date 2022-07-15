package com.io.core.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.io.core.common.dto.SysMenuDto;
import com.io.core.common.entity.SysMenu;
import com.io.core.common.entity.SysUser;
import com.io.core.mapper.SysMenuMapper;
import com.io.core.mapper.SysUserMapper;
import com.io.core.service.SysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.io.core.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author me
 * @since 2022-06-19
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Autowired
    @Lazy
    SysUserService userService;

    @Autowired
    SysUserMapper userMapper;

    /**
     * 获取当前用户权限能使用的导航栏
     * @return nav集合
     */
    @Override
    public List<SysMenuDto> getCurrentUserNav() {
        String username =
                (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        SysUser sysUser = userService.getByUsername(username);

        List<Long> menuIds = userMapper.getNavMenuIds(sysUser.getId());
        List<SysMenu> sysMenus = this.listByIds(menuIds);

        // 转树状结构
        List<SysMenu> menus = buildTreeMenu(sysMenus);

        // 转dto

        return convert(menus);
    }

    /**
     * 将实体对象转换为dto对象
     */
    private List<SysMenuDto> convert(List<SysMenu> menuTree) {
        List<SysMenuDto> menuDtos = new ArrayList<>();

        menuTree.forEach(m -> {
            SysMenuDto dto = new SysMenuDto();

            dto.setId(m.getId());
            dto.setName(m.getPerms());
            dto.setTitle(m.getName());
            dto.setComponent(m.getComponent());
            dto.setPath(m.getPath());

            if (m.getChildren().size() > 0) {

                // 子节点调用当前方法进行再次转换
                dto.setChildren(convert(m.getChildren()));
            }

            menuDtos.add(dto);
        });

        return menuDtos;
    }

    /**
     * 将菜单转换为树状结构
     */
    private List<SysMenu> buildTreeMenu(List<SysMenu> menus) {
        List<SysMenu> finalMenus = new ArrayList<>();

        // 寻找子节点
        for (SysMenu menu : menus) {

            for (SysMenu e : menus) {
                if (menu.getId() == e.getParentId()) {
                    menu.getChildren().add(e);
                }
            }

            // 提取出父节点
            if (menu.getParentId() == 0L) {
                finalMenus.add(menu);
            }
        }
        System.out.println(JSONUtil.toJsonStr(finalMenus));
        return finalMenus;
    }

    /**
     * 获取所有菜单信息并转换为树状结构
     */
    @Override
    public List<SysMenu> tree() {
        return buildTreeMenu(this.list(new QueryWrapper<SysMenu>().orderByAsc("orderNum")));
    }
}
