package com.io.portainer.data.dto.wos;

import lombok.Data;

@Data
public class WosMessageDto {
    private String title;
    private String description;
    private Long receiver;
}
