package com.io.portainer.data.dto.wos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseDto {
    Integer code;
    String message;
}
