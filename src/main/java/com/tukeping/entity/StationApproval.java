package com.tukeping.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * @author tukeping
 * @date 2019/11/29
 **/
@Data
@Entity
@Table(name = "pt_station_approval")
public class StationApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ID_;

    @Column(columnDefinition = "varchar(64) comment '流程实例id'")
    private String PROC_INST_ID_;

    @Column(nullable = false)
    @CreationTimestamp
    private Timestamp CREATE_TIME_;

    @Column(columnDefinition = "varchar(200) comment '标题'")
    private String TITLE_;

    @Column(columnDefinition = "varchar(50) comment '姓名'")
    private String NAME_;

    @Column(columnDefinition = "varchar(100) comment '身份证号'")
    private String IDCARD_;

    @Column(columnDefinition = "tinyint(1) comment '性别'")
    private Integer SEX_;

    @Column(columnDefinition = "date comment '出生日期'")
    private Date DATEOFBIRTH;

    @Column(columnDefinition = "varchar(200) comment '照片'")
    private String PHOTO;

    @Column(columnDefinition = "varchar(20) comment '名族'")
    private String NATION_;

    @Column(columnDefinition = "tinyint(1) comment '婚姻状况'")
    private Integer MARITAL_STATUS_;

    @Column(columnDefinition = "varchar(100) comment '政治面貌'")
    private String POLITICAL_STATUS_;

    @Column(columnDefinition = "varchar(50) comment '手机号码'")
    private String MOBILE_PHOTO_;

    @Column(columnDefinition = "varchar(50) comment '固定电话'")
    private String TELEPHOTO;

    @Column(columnDefinition = "varchar(100) comment '电子邮箱'")
    private String EMAIL_;

    @Column(columnDefinition = "varchar(200) comment '户口所在地'")
    private String ACCOUNT_LOCATION_;

    @Column(columnDefinition = "varchar(200) comment '家庭地址'")
    private String FAMILY_ADDRESS_;

    @Column(columnDefinition = "varchar(50) comment '毕业院校'")
    private String GRADUATED_SCHOOL_;

    @Column(columnDefinition = "varchar(200) comment '毕业年份'")
    private String GRADUATION_YEAR_;

    @Column(columnDefinition = "varchar(100) comment '专业'")
    private String PROFESSION_;

    @Column(columnDefinition = "varchar(100) comment '学历'")
    private String EDUCATION_;

    @Column(columnDefinition = "varchar(100) comment '用工性质'")
    private String LABOR_NATURE_;

    @Column(columnDefinition = "date comment '进驻日期'")
    private Date DATEOFENTRY_;

    @Column(columnDefinition = "varchar(5000) comment '工作经历'")
    private String WORF_EXPERIENCE_;

    @Column(columnDefinition = "varchar(500) comment '个人技能及特长'")
    private String SPECIALITY_;

    @Column(columnDefinition = "tinyint(1) comment '人员进驻审核结束状态'")
    private Integer IS_DONE_;

    @Column(columnDefinition = "tinyint(1) comment '是否离职'")
    private Integer IS_LEAVE_;

    @Column(columnDefinition = "varchar(500) comment '短信内容'")
    private String NOTICE_CONTENT_;

    @Column(columnDefinition = "varchar(50) comment '短信接收手机号码'")
    private String NOTICE_PHONE_;

    @Column(columnDefinition = "varchar(100) comment '所在单位'")
    private String DEPARTMENT_;

    @Column(columnDefinition = "varchar(100) comment '窗口编号'")
    private String WINDOW_NUMBER_;

    @Column(columnDefinition = "varchar(255) comment '银行卡号'")
    private String bank_card_number_;

    @Column(columnDefinition = "varchar(255) comment ''")
    private String place_;
}
