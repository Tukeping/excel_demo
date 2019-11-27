package com.tukeping.excel.entity;

import lombok.Data;

/**
 * @author tukeping
 * @date 2019/11/26
 **/
@Data
public class DutyFeeTable {
    /**
     * 序号
     */
    private Integer serialNumber;
    /**
     * 公司名称
     */
    private String companyName;
    /**
     * 雇员名称
     */
    private String employeeName;
    /**
     * 银行卡号
     */
    private String bankAccountNo;
    /**
     * 报销的月份区间
     */
    private String reimbursementMonth;
    /**
     * 值班费
     */
    private Integer dutyFee;
    /**
     * 考核奖
     */
    private Integer assessmentFee;
    /**
     * 总费用
     */
    private Integer totalAmount;
    /**
     * 备注
     */
    private String remark;
}
