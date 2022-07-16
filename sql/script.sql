create table if not exists ptr_endpoint
(
    id            bigint       not null
        primary key,
    name          varchar(50)  not null comment '节点名称',
    url           varchar(100) null,
    status        int          not null comment '1为up，2为down',
    updated       datetime     null,
    created       datetime     not null,
    description   varchar(100) null comment '描述',
    resource_type int          null comment '资源类型，1010为独占型，3030为共享型'
)
    comment '即gpu资源虚拟机';

create table if not exists ptr_user
(
    id       bigint       not null
        primary key,
    username varchar(20)  not null,
    password varchar(25)  null comment 'portainer密码，最小为12位',
    role     int          not null comment 'Portainer 用户权限，1为管理员，2为普通用户',
    job_id   bigint       null comment '学号或工号',
    updated  datetime     null,
    created  datetime     not null,
    remark   varchar(100) null comment '备注',
    constraint ptr_user_job_id_uindex
        unique (job_id),
    constraint ptr_user_username_uindex
        unique (username)
);

create table if not exists ptr_user_endpoint
(
    id          bigint auto_increment
        primary key,
    user_id     bigint   not null,
    endpoint_id bigint   not null,
    expired     datetime not null,
    created     datetime not null,
    updated     datetime null
);

create table if not exists sys_check_list
(
    id                    bigint auto_increment
        primary key,
    related_user_id       bigint           null comment '相关的ptr用户信息',
    related_operator_id   bigint           null comment '相关管理员信息',
    related_resource_type int default 1010 null,
    type                  bigint           null comment '类型，如1为发生异常，2为资源分配等待,0则为在等待队列中',
    message               varchar(300)     null comment '相关信息',
    status                int default 0    not null comment '处理状态，0为未处理，1为已完成',
    created               datetime         not null,
    wait_list_id          bigint           null comment '如果存在，则为对应等待队列中id',
    constraint sys_check_list_wait_list_id_uindex
        unique (wait_list_id)
)
    comment '代办事项清单，用于管理员人工进行操作';

create table if not exists sys_log
(
    id          bigint auto_increment
        primary key,
    detail      varchar(100) null comment '申请描述，如果是异常则为异常信息',
    operator_id bigint       null comment '操作者id',
    title       varchar(50)  null comment '标题，如：“紧急调度”、“异常发生”',
    created     datetime     not null
)
    comment '日志类，包括操作日志和请求接收日志';

create table if not exists sys_menu
(
    id        bigint auto_increment
        primary key,
    parent_id bigint       null comment '父菜单ID，一级菜单为0',
    name      varchar(64)  not null,
    path      varchar(255) null comment '菜单URL',
    perms     varchar(255) null comment '授权(多个用逗号分隔，如：user:list,user:create)',
    component varchar(255) null,
    type      int(5)       not null comment '类型     0：目录   1：菜单   2：按钮',
    icon      varchar(32)  null comment '菜单图标',
    orderNum  int          null comment '排序',
    created   datetime     not null,
    updated   datetime     null,
    statu     int(5)       not null,
    constraint name
        unique (name)
)
    charset = utf8;

create table if not exists sys_role
(
    id      bigint auto_increment
        primary key,
    name    varchar(64) not null,
    code    varchar(64) not null,
    remark  varchar(64) null comment '备注',
    created datetime    null,
    updated datetime    null,
    statu   int(5)      not null,
    constraint code
        unique (code),
    constraint name
        unique (name)
)
    charset = utf8;

create table if not exists sys_role_menu
(
    id      bigint auto_increment
        primary key,
    role_id bigint not null,
    menu_id bigint not null
);

create table if not exists sys_user
(
    id         bigint auto_increment
        primary key,
    username   varchar(64)  null,
    password   varchar(64)  null,
    avatar     varchar(255) null comment '用户头像',
    email      varchar(64)  null,
    city       varchar(64)  null,
    created    datetime     null,
    updated    datetime     null,
    last_login datetime     null,
    statu      int(5)       not null,
    constraint UK_USERNAME
        unique (username)
)
    charset = utf8;

create table if not exists sys_user_role
(
    id      bigint auto_increment
        primary key,
    user_id bigint not null,
    role_id bigint not null
);

create table if not exists sys_wait_list
(
    id              bigint auto_increment
        primary key,
    related_user_id bigint       not null,
    job_id          bigint       not null,
    resource_type   int          not null,
    apply_days      int          not null,
    remark          varchar(300) null,
    created         datetime     not null
);


