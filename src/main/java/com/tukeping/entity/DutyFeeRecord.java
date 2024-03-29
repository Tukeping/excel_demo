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
import javax.persistence.UniqueConstraint;
import java.sql.Timestamp;

/**
 * @author tukeping
 * @date 2019/11/27
 **/
@Data
@Entity
@Table(name = "pt_duty_fee_record",
        uniqueConstraints = @UniqueConstraint(name = "uk_record_year_month",
                columnNames = {"year", "month"}))
public class DutyFeeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "int comment '报销年份'")
    private Integer year;

    @Column(columnDefinition = "varchar(128) comment '报销月份区间'")
    private String month;

    @Column(columnDefinition = "int comment '总计金额'")
    private Integer totalAmount;

    @Column(columnDefinition = "varchar(1024) comment '表格标题'")
    private String tableTitle;

    @Column(columnDefinition = "varchar(128) comment '制表人'")
    private String creator;

    @Column(columnDefinition = "varchar(128) comment '审核人'")
    private String auditor;

    @Column(columnDefinition = "varchar(128) comment '审批人'")
    private String approver;

    @Column(nullable = false)
    @CreationTimestamp
    private Timestamp gmtCreate;

    @Column(nullable = false)
    @UpdateTimestamp
    private Timestamp gmtUpdate;
}
