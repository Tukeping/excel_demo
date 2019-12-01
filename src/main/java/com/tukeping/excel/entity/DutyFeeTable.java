package com.tukeping.excel.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author tukeping
 * @date 2019/11/26
 **/
@Data
public class DutyFeeTable {

    @ExcelProperty(value = "序号", index = 0)
    private Integer serialNumber;

    @ExcelProperty(value = "公司名称", index = 1)
    private String companyName;

    @ExcelProperty(value = "雇员名称", index = 2)
    private String employeeName;

    @ExcelProperty(value = "银行卡号", index = 3)
    private String bankAccountNo;

    @ExcelProperty(value = "报销的月份区间", index = 4)
    private String reimbursementMonth;

    @ExcelProperty(value = "值班费", index = 5)
    private Integer dutyFee;

    @ExcelProperty(value = "考核奖", index = 6)
    private Integer assessmentFee;

    @ExcelProperty(value = "总费用", index = 7)
    private Integer totalAmount;

    @ExcelProperty(value = "备注", index = 8)
    private String remark;
}
