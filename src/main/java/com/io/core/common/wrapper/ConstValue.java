package com.io.core.common.wrapper;

public final class ConstValue {

    // 管理系统部分
    public static final String CAPTCHA_KEY = "captcha";
    public static final Integer STATUS_ON = 0;
    public static final Integer STATUS_OFF = 1;
    public static final String DEFAULT_PSW = "888888";
    public static final String DEFAULT_AVATAR = "https://image-1300566513.cos.ap-guangzhou.myqcloud.com/upload/images/5a9f48118166308daba8b6da7e466aab.jpg";

    // GPU资源类型代码，1010为独占型，3030 为共享型
    public static final Integer GROUP_RESOURCE = 3030;
    public static final Integer SINGLE_RESOURCE = 1010;

    // 独占型资源最大容量
    public static final Integer SINGLE_RESOURCE_CAPACITY = 1;

    //CheckList 中类型，等待队列为0，错误队列为1，已完成为2
    public static final Long WAIT_LIST_TYPE = 0L;
    public static final Long ERROR_LIST_TYPE = 1L;
    public static final Long FINISHED_LIST_TYPE = 2L;
}
