package com.tukeping.service;

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
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tukeping
 * @date 2019/11/27
 **/
@Service
public class DutyFeeService {

    @Resource
    private DutyFeeDetailRepository dutyFeeDetailRepo;

    @Resource
    private DutyFeeAccountRepository dutyFeeAccountRepo;

    @Resource
    private DutyFeeDateRepository dutyFeeDateRepo;

    @Resource
    private DutyFeeRecordRepository dutyFeeRecordRep;

    @PersistenceContext
    private EntityManager em;

    public Integer saveFeeRecord(DutyFeeRecord record) {
        dutyFeeRecordRep.save(record);
        return record.getId();
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

    public Integer saveFeeDate(DutyFeeDate date) {
        if (!existFeeDateByUk(date)) {
            dutyFeeDateRepo.save(date);
        }
        return date.getId();
    }

    public List<Integer> saveFeeDateList(List<DutyFeeDate> dateList) {
        return dateList.stream().peek(this::saveFeeDate).map(DutyFeeDate::getId).collect(Collectors.toList());
    }

    public List<DutyFeeDate> queryFeeDateByUk(DutyFeeDate dutyFeeDate) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<DutyFeeDate> criteria = builder.createQuery(DutyFeeDate.class);
        Root<DutyFeeDate> root = criteria.from(DutyFeeDate.class);
        criteria.where(
                builder.equal(root.get("accountId"), dutyFeeDate.getAccountId()),
                builder.equal(root.get("reimbursementYear"), dutyFeeDate.getReimbursementYear()),
                builder.equal(root.get("reimbursementMonth"), dutyFeeDate.getReimbursementMonth())
        );
        return em.createQuery(criteria).getResultList();
    }

    public boolean existFeeDateByUk(DutyFeeDate dutyFeeDate) {
        return !CollectionUtils.isEmpty(queryFeeDateByUk(dutyFeeDate));
    }
}
