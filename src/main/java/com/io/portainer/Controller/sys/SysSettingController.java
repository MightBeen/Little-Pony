package com.io.portainer.Controller.sys;

import com.io.core.common.wrapper.ResultWrapper;
import com.io.portainer.common.config.FlexibleSetting;
import com.io.portainer.common.config.SettingManager;
import com.io.portainer.common.config.SysSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sys/setting")
public class SysSettingController {

    @Autowired
    SettingManager settingManager;

    @GetMapping()
    public ResultWrapper getCurrentSetting() {
        return ResultWrapper.success(settingManager.getCurrentSetting());
    }

    @PostMapping()
    public ResultWrapper changeSetting(@RequestBody FlexibleSetting newSetting) {
        SysSetting currentSetting = settingManager.getCurrentSetting();
        settingManager.setSetting(newSetting);
        return ResultWrapper.success(settingManager.getCurrentSetting());
    }

    @GetMapping("/default")
    public ResultWrapper toDefault() {
        settingManager.toDefault();
        return ResultWrapper.success(settingManager.getCurrentSetting());
    }

}
