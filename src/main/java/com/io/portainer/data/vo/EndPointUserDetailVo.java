package com.io.portainer.data.vo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EndPointUserDetailVo {
    private Long id;
    private LocalDate started;
    private LocalDate updated;
    private LocalDate expired;
}
