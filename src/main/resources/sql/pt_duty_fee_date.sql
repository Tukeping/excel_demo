create table if not exists pt_duty_fee_date
(
    id                  int unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    account_id          int          not null comment '账号ID',
    reimbursement_year  int          not null comment '报销年份',
    reimbursement_month int          not null comment '报销月份',
    fee_detail_id       int          not null comment '报销费用明细ID',
    gmt_create          timestamp    not null comment '创建时间',
    gmt_update          timestamp    not null comment '更新时间',
    primary key (id),
    unique key `uk_account_year_month` (account_id, reimbursement_year, reimbursement_month)
) engine = InnoDB
  DEFAULT CHARSET = utf8;