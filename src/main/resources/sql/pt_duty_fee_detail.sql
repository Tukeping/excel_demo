create table if not exists pt_duty_fee_detail
(
    id                    int unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    account_id            int          not null comment '[FK][duty_fee_account]账号ID',
    reimbursement_date_id int          not null comment '[FK][duty_fee_date]报销年月表主键ID',
    serial_number         int comment '序号',
    company_name          varchar(255) comment '公司名称',
    employee_name         varchar(255) comment '雇员名称',
    duty_fee              int comment '值班费',
    assessment_fee        int comment '考核奖',
    total_amount          int comment '总费用',
    remark                varchar(2048) comment '备注',
    gmt_create            timestamp    not null comment '创建时间',
    gmt_update            timestamp    not null comment '更新时间',
    primary key (id)
) engine = InnoDB
  DEFAULT CHARSET = utf8mb4;