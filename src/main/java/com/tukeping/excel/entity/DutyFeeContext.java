package com.tukeping.excel.entity;

import lombok.Data;

/**
 * @author tukeping
 * @date 2019/11/27
 **/
@Data
public class DutyFeeContext {

    /**
     * 上传记录ID
     */
    private Integer recordId;

    /**
     * 报销年份
     */
    private Integer year;

    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 总计金额
     */
    private Integer totalAmount;

    /**
     * 表格标题
     */
    private String tableTitle;

    /**
     * 制表人
     */
    private String creator;

    /**
     * 审核人
     */
    private String auditor;

    /**
     * 审批人
     */
    private String approver;

    public static DutyFeeContext ofYear(Integer year) {
        DutyFeeContext context = new DutyFeeContext();
        context.setYear(year);
        return context;
    }
}
