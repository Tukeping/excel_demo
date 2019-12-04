package com.tukeping.converter;

import com.tukeping.constant.ExcelConstants;
import com.tukeping.controller.vo.DutyFeeRecordVO;
import com.tukeping.entity.DutyFeeAccount;
import com.tukeping.entity.DutyFeeDate;
import com.tukeping.entity.DutyFeeDetail;
import com.tukeping.entity.DutyFeeRecord;
import com.tukeping.entity.StationApproval;
import com.tukeping.excel.entity.DutyFeeContext;
import com.tukeping.excel.entity.DutyFeeTable;
import com.tukeping.util.BeanUtil;
import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    public DutyFeeDetail toFeeDetail(DutyFeeTable table, DutyFeeContext context) {
        DutyFeeDetail feeDetail = BeanUtil.copyProperties(table, new DutyFeeDetail());
        if (StringUtils.isEmpty(table.getCompanyName())) {
            feeDetail.setCompanyName(context.getCompanyName());
        }
        feeDetail.setRecordId(context.getRecordId());
        return feeDetail;
    }

    public List<DutyFeeDate> toFeeDateList(DutyFeeTable table, DutyFeeContext context) {
        if (null == table || StringUtils.isEmpty(table.getReimbursementMonth())) {
            return null;
        }

        String[] months = table.getReimbursementMonth().split(ExcelConstants.CN_DAWN);

        return Stream.of(months)
                .filter(month -> !StringUtils.isEmpty(month))
                .map(month -> toFeeDate(context, Integer.parseInt(month)))
                .collect(Collectors.toList());
    }

    private static DutyFeeDate toFeeDate(DutyFeeContext context, Integer month) {
        DutyFeeDate feeDate = new DutyFeeDate();
        feeDate.setReimbursementYear(context.getYear());
        feeDate.setRecordId(context.getRecordId());
        feeDate.setReimbursementMonth(month);
        return feeDate;
    }

    public DutyFeeRecordVO toRecordVO(DutyFeeRecord record) {
        DutyFeeRecordVO vo = new DutyFeeRecordVO();
        vo.setRecordId(record.getId());
        vo.setTitle(record.getTableTitle());
        LocalDateTime gmtCreate = record.getGmtCreate().toLocalDateTime();
        String uploadDate = gmtCreate.format(DateTimeFormatter.ofPattern("yyyy-dd-MM HH:mm:ss"));
        vo.setUploadDate(uploadDate);
        return vo;
    }

    public DutyFeeTable toFeeTable(StationApproval stationApproval) {
        DutyFeeTable table = new DutyFeeTable();
        table.setBankAccountNo(stationApproval.getBank_card_number_());
        table.setCompanyName(stationApproval.getDEPARTMENT_());
        table.setEmployeeName(stationApproval.getNAME_());
        return table;
    }
}
