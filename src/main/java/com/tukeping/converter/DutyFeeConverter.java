package com.tukeping.converter;

import com.tukeping.entity.DutyFeeAccount;
import com.tukeping.entity.DutyFeeDate;
import com.tukeping.entity.DutyFeeDetail;
import com.tukeping.excel.entity.DutyFeeContext;
import com.tukeping.excel.entity.DutyFeeTable;
import com.tukeping.util.BeanUtil;
import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author tukeping
 * @date 2019/11/27
 **/
@UtilityClass
public class DutyFeeConverter {

    public DutyFeeAccount toAccount(DutyFeeTable table) {
        DutyFeeAccount account = BeanUtil.copyProperties(table, new DutyFeeAccount());
        account.setBankAccountName(table.getEmployeeName());
        return account;
    }

    public DutyFeeDetail toFeeDetail(Integer accountId, DutyFeeTable table, DutyFeeContext context) {
        DutyFeeDetail feeDetail = BeanUtil.copyProperties(table, new DutyFeeDetail());
        if (StringUtils.isEmpty(table.getCompanyName())) {
            feeDetail.setCompanyName(context.getCompanyName());
        }
        feeDetail.setAccountId(accountId);
        return feeDetail;
    }

    public List<DutyFeeDate> toFeeDateList(Integer accountId, Integer feeDetailId, DutyFeeContext context, DutyFeeTable table) {
        if (null == table || StringUtils.isEmpty(table.getReimbursementMonth())) {
            return null;
        }

        String[] months = table.getReimbursementMonth().split("、");

        return Stream.of(months)
                .filter(month -> !StringUtils.isEmpty(month))
                .map(month -> toFeeDate(accountId, feeDetailId, context, Integer.parseInt(month)))
                .collect(Collectors.toList());
    }

    private static DutyFeeDate toFeeDate(Integer accountId, Integer feeDetailId, DutyFeeContext context, Integer month) {
        DutyFeeDate feeDate = new DutyFeeDate();
        feeDate.setAccountId(accountId);
        feeDate.setFeeDetailId(feeDetailId);
        feeDate.setReimbursementYear(context.getYear());
        feeDate.setReimbursementMonth(month);
        return feeDate;
    }
}
