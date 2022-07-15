package com.io.core.common.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SysMenuDto {

    private Long id;
    private String name;
    private String title;
    private String icon;
    private String path;
    private String component;
    private List<SysMenuDto> children = new ArrayList<>();
}
