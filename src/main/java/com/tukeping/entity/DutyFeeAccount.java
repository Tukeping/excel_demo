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
@Table(name = "pt_duty_fee_account",
        uniqueConstraints = @UniqueConstraint(name = "uk_bank_account",
                columnNames = {"bank_account_no", "bank_account_name"}))
public class DutyFeeAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "bank_account_no", nullable = false,
            columnDefinition = "varchar(255) comment '银行卡号'")
    private String bankAccountNo;

    @Column(name = "bank_account_name",
            nullable = false, columnDefinition = "varchar(128) comment '银行卡户主名称'")
    private String bankAccountName;

    @Column(nullable = false)
    @CreationTimestamp
    private Timestamp gmtCreate;

    @Column(nullable = false)
    @UpdateTimestamp
    private Timestamp gmtUpdate;
}
