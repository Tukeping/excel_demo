package com.tukeping.excel.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.tukeping.constant.ExcelConstants;
import lombok.Data;

/**
 * @author tukeping
 * @date 2019/11/26
 **/
@Data
public class DutyFeeTable {

    @ExcelProperty(value = {ExcelConstants.EXCEL_MAIN_TITLE, "序号"}, index = 0)
    private Integer serialNumber;

    @ExcelProperty(value = {ExcelConstants.EXCEL_MAIN_TITLE, "公司名称"}, index = 1)
    private String companyName;

    @ExcelProperty(value = {ExcelConstants.EXCEL_MAIN_TITLE, "雇员名称"}, index = 2)
    private String employeeName;

    @ExcelProperty(value = {ExcelConstants.EXCEL_MAIN_TITLE, "银行卡号"}, index = 3)
    private String bankAccountNo;

    @ExcelProperty(value = {ExcelConstants.EXCEL_MAIN_TITLE, "报销的月份区间"}, index = 4)
    private String reimbursementMonth;

    @ExcelProperty(value = {ExcelConstants.EXCEL_MAIN_TITLE, "值班费"}, index = 5)
    private Integer dutyFee;

    @ExcelProperty(value = {ExcelConstants.EXCEL_MAIN_TITLE, "考核奖"}, index = 6)
    private Integer assessmentFee;

    @ExcelProperty(value = {ExcelConstants.EXCEL_MAIN_TITLE, "总费用"}, index = 7)
    private Integer totalAmount;

    @ExcelProperty(value = {ExcelConstants.EXCEL_MAIN_TITLE, "备注"}, index = 8)
    private String remark;
}
