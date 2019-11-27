create table if not exists pt_duty_fee_account
(
    id                int unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    bank_account_no   varchar(512) not null comment '银行卡号',
    bank_account_name varchar(128) not null comment '银行卡户主名称',
    gmt_create        timestamp    not null comment '创建时间',
    gmt_update        timestamp    not null comment '更新时间',
    primary key (id),
    unique key `uk_bank_account_no` (bank_account_no),
    key `idx_bank_account_no` (bank_account_no)
) engine = InnoDB
  DEFAULT CHARSET = utf8;