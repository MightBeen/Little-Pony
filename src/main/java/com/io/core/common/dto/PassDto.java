package com.io.core.common.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 用于修改密码
 */
@Data
public class PassDto implements Serializable {

	@NotBlank(message = "新密码不能为空")
	private String password;

	@NotBlank(message = "旧密码不能为空")
	private String currentPass;
}
