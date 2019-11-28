create table if not exists pt_duty_fee_record
(
    id           int unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    table_title  varchar(1024) comment '表格标题',
    total_amount int comment '总费用',
    creator      varchar(128) comment '制表人',
    auditor      varchar(128) comment '审核人',
    approver     varchar(128) comment '审批人',
    gmt_create   timestamp    not null comment '创建时间',
    gmt_update   timestamp    not null comment '更新时间',
    primary key (id)
) engine = InnoDB
  DEFAULT CHARSET = utf8;