package com.tukeping.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * @author tukeping
 * @date 2019/11/27
 **/
@Data
@Entity
@Table(name = "pt_duty_fee_detail")
public class DutyFeeDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "int comment '序号'")
    private Integer serialNumber;

    @Column(columnDefinition = "varchar(512) comment '公司名称'")
    private String companyName;

    @Column(nullable = false, columnDefinition = "int comment '[FK][duty_fee_record]上传记录ID'")
    private Integer recordId;

    @Column(nullable = false, columnDefinition = "int comment '[FK][duty_fee_account]账号ID'")
    private Integer accountId;

    @Column(columnDefinition = "int comment '值班费'")
    private Integer dutyFee;

    @Column(columnDefinition = "int comment '考核奖'")
    private Integer assessmentFee;

    @Column(columnDefinition = "int comment '总费用'")
    private Integer totalAmount;

    @Column(columnDefinition = "varchar(2048) comment '备注'")
    private String remark;

    @Column(nullable = false)
    @CreationTimestamp
    private Timestamp gmtCreate;

    @Column(nullable = false)
    @UpdateTimestamp
    private Timestamp gmtUpdate;
}
