package com.tukeping.service;

import com.tukeping.dto.DutyFeeDTO;
import com.tukeping.dto.DutyFeeDetailDTO;
import com.tukeping.entity.DutyFeeAccount;
import com.tukeping.entity.DutyFeeDate;
import com.tukeping.entity.DutyFeeDetail;
import com.tukeping.entity.DutyFeeRecord;
import com.tukeping.excel.entity.DutyFeeContext;
import com.tukeping.excel.entity.DutyFeeTable;
import com.tukeping.repository.DutyFeeAccountRepository;
import com.tukeping.repository.DutyFeeDateRepository;
import com.tukeping.repository.DutyFeeDetailRepository;
import com.tukeping.repository.DutyFeeRecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tukeping
 * @date 2019/11/27
 **/
@Slf4j
@Service
public class DutyFeeService {

    @Resource
    private DutyFeeDetailRepository dutyFeeDetailRepo;

    @Resource
    private DutyFeeAccountRepository dutyFeeAccountRepo;

    @Resource
    private DutyFeeDateRepository dutyFeeDateRepo;

    @Resource
    private DutyFeeRecordRepository dutyFeeRecordRepo;

    @PersistenceContext
    private EntityManager em;

    @Modifying
    @Transactional(rollbackOn = Throwable.class)
    public void saveCompleteDutyFeeData(DutyFeeDTO dutyFeeDTO) {
        DutyFeeRecord record = dutyFeeDTO.getRecord();

        Integer recordId = saveFeeRecord(record);
        if (null == record || recordId <= 0) {
            log.error("save duty fee record failure. record:{}", record);
            throw new RuntimeException("save duty fee record failure.");
        }

        for (DutyFeeDetailDTO feeDetailDTO : dutyFeeDTO.getDutyFeeDetail()) {
            feeDetailDTO.getDutyFeeDetail().setRecordId(recordId);

            Integer dutyFeeDetailId = saveFeeDetail(feeDetailDTO.getDutyFeeDetail());
            if (null == dutyFeeDetailId || dutyFeeDetailId <= 0) {
                log.error("save duty fee detail failure. detail:{}", feeDetailDTO.getDutyFeeDetail());
                throw new RuntimeException("save duty fee detail failure.");
            }

            for (DutyFeeDate feeDate : feeDetailDTO.getDutyFeeDateList()) {
                feeDate.setRecordId(recordId);
                feeDate.setFeeDetailId(dutyFeeDetailId);

                saveFeeDate(feeDate);
            }
        }
    }

    public List<DutyFeeRecord> findFeeRecordByUk(Integer year, String month) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<DutyFeeRecord> criteria = builder.createQuery(DutyFeeRecord.class);
        Root<DutyFeeRecord> root = criteria.from(DutyFeeRecord.class);
        criteria.where(
                builder.equal(root.get("year"), year),
                builder.equal(root.get("month"), month)
        );
        return em.createQuery(criteria).getResultList();
    }

    public boolean existFeeRecordByUk(Integer year, String month) {
        return !CollectionUtils.isEmpty(findFeeRecordByUk(year, month));
    }

    public Integer saveFeeRecord(DutyFeeRecord record) {
        dutyFeeRecordRepo.save(record);
        return record.getId();
    }

    public DutyFeeRecord getFeeRecord(Integer recordId) {
        return dutyFeeRecordRepo.getOne(recordId);
    }

    public void updateContext(DutyFeeTable dutyFeeTable, DutyFeeContext dutyFeeContext) {
        if (StringUtils.isEmpty(dutyFeeContext.getCompanyName())
                && !StringUtils.isEmpty(dutyFeeTable.getCompanyName())) {
            dutyFeeContext.setCompanyName(dutyFeeTable.getCompanyName());
        } else if (!StringUtils.isEmpty(dutyFeeTable.getCompanyName())
                && !dutyFeeContext.getCompanyName().equals(dutyFeeTable.getCompanyName())) {
            dutyFeeContext.setCompanyName(dutyFeeTable.getCompanyName());
        }
    }

    public List<DutyFeeAccount> getAccountByBankNo(String bankNo) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<DutyFeeAccount> criteria = builder.createQuery(DutyFeeAccount.class);
        Root<DutyFeeAccount> root = criteria.from(DutyFeeAccount.class);
        criteria.where(
                builder.equal(root.get("bankAccountNo"), bankNo)
        );
        return em.createQuery(criteria).getResultList();
    }

    public Integer saveAccount(DutyFeeAccount account) {
        dutyFeeAccountRepo.save(account);
        return account.getId();
    }

    public Integer saveFeeDetail(DutyFeeDetail detail) {
        dutyFeeDetailRepo.save(detail);
        return detail.getId();
    }

    public List<Integer> saveFeeDetailList(List<DutyFeeDetail> detailList) {
        return detailList.stream().peek(this::saveFeeDetail).map(DutyFeeDetail::getId).collect(Collectors.toList());
    }

    public Integer saveFeeDate(DutyFeeDate date) {
        if (!existFeeDateByUk(date)) {
            dutyFeeDateRepo.save(date);
        }
        return date.getId();
    }

    public List<Integer> saveFeeDateList(List<DutyFeeDate> dateList) {
        return dateList.stream().peek(this::saveFeeDate).map(DutyFeeDate::getId).collect(Collectors.toList());
    }

    public List<DutyFeeDate> findFeeDateByUk(DutyFeeDate dutyFeeDate) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<DutyFeeDate> criteria = builder.createQuery(DutyFeeDate.class);
        Root<DutyFeeDate> root = criteria.from(DutyFeeDate.class);
        criteria.where(
                builder.equal(root.get("feeDetailId"), dutyFeeDate.getFeeDetailId()),
                builder.equal(root.get("reimbursementYear"), dutyFeeDate.getReimbursementYear()),
                builder.equal(root.get("reimbursementMonth"), dutyFeeDate.getReimbursementMonth())
        );
        return em.createQuery(criteria).getResultList();
    }

    public boolean existFeeDateByUk(DutyFeeDate dutyFeeDate) {
        return !CollectionUtils.isEmpty(findFeeDateByUk(dutyFeeDate));
    }

    @Modifying
    @Transactional(rollbackOn = Throwable.class)
    public void deleteRecordById(Integer recordId) {
        dutyFeeRecordRepo.deleteById(recordId);

        List<DutyFeeDetail> feeDetailList = findDetailListByRecordId(recordId);
        dutyFeeDetailRepo.deleteInBatch(feeDetailList);

        List<DutyFeeDate> feeDateList = findDateListByRecordId(recordId);
        dutyFeeDateRepo.deleteInBatch(feeDateList);
    }

    public List<DutyFeeDate> findDateListByRecordId(Integer recordId) {
        DutyFeeDate queryFeeDate = new DutyFeeDate();
        queryFeeDate.setRecordId(recordId);

        Example<DutyFeeDate> feeDateCondition = Example.of(queryFeeDate);
        return dutyFeeDateRepo.findAll(feeDateCondition);
    }

    public List<DutyFeeRecord> findAllRecords() {
        return dutyFeeRecordRepo.findAll();
    }

    public List<DutyFeeDetail> findDetailListByRecordId(Integer recordId) {
        DutyFeeDetail queryFeeDetail = new DutyFeeDetail();
        queryFeeDetail.setRecordId(recordId);

        Example<DutyFeeDetail> feeDetailCondition = Example.of(queryFeeDetail);
        return dutyFeeDetailRepo.findAll(feeDetailCondition);
    }
}
