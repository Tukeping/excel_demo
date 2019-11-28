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
@Table(name = "pt_duty_fee_date",
        uniqueConstraints = @UniqueConstraint(name = "uk_fee_date",
                columnNames = {"account_id", "reimbursement_year", "reimbursement_month"}))
public class DutyFeeDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false,
            columnDefinition = "int comment '[FK][duty_fee_record]上传记录ID'")
    private Integer recordId;

    @Column(name = "account_id", nullable = false,
            columnDefinition = "int comment '账号ID'")
    private Integer accountId;

    @Column(name = "reimbursement_year", nullable = false,
            columnDefinition = "int comment '报销年份'")
    private Integer reimbursementYear;

    @Column(name = "reimbursement_month", nullable = false,
            columnDefinition = "int comment '报销月份'")
    private Integer reimbursementMonth;

    @Column(nullable = false, columnDefinition = "int comment '报销费用明细ID'")
    private Integer feeDetailId;

    @Column(nullable = false)
    @CreationTimestamp
    private Timestamp gmtCreate;

    @Column(nullable = false)
    @UpdateTimestamp
    private Timestamp gmtUpdate;
}
