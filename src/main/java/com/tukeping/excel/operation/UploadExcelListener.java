package com.tukeping.excel.operation;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.util.ConverterUtils;
import com.tukeping.converter.DutyFeeConverter;
import com.tukeping.entity.DutyFeeAccount;
import com.tukeping.entity.DutyFeeDate;
import com.tukeping.entity.DutyFeeDetail;
import com.tukeping.entity.DutyFeeRecord;
import com.tukeping.excel.entity.DutyFeeContext;
import com.tukeping.excel.entity.DutyFeeTable;
import com.tukeping.service.DutyFeeService;
import com.tukeping.util.BeanUtil;
import com.tukeping.util.ExcelContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author tukeping
 * @date 2019/11/26
 **/
@Slf4j
public class UploadExcelListener extends AnalysisEventListener<DutyFeeTable> {

    private static Pattern signRegular = Pattern.compile("(?<=单位盖章：)\\s*\\S*|(?<=制表人：)\\s*\\S*|(?<=审核人：)\\s*\\S*|(?<=审批人：)\\s*\\S*");
    private static Pattern yearRegular = Pattern.compile("^\\d{4}");

    private DutyFeeService dutyFeeService;

    public UploadExcelListener(DutyFeeService dutyFeeService) {
        this.dutyFeeService = dutyFeeService;
    }

    @Override
    public void invokeHead(Map<Integer, CellData> headMap, AnalysisContext context) {
        invokeHeadMap(ConverterUtils.convertToStringMap(headMap, context), context);
        if (!headMap.isEmpty()) {
            CellData cellData = headMap.values().stream().findFirst().get();
            if (!StringUtils.isEmpty(cellData.getStringValue())) {
                // extract year
                Matcher matcher = yearRegular.matcher(cellData.getStringValue().trim());
                if (matcher.find()) {
                    Integer year = Integer.parseInt(matcher.group());
                    context.readWorkbookHolder().setCustomObject(DutyFeeContext.ofYear(year));
                    // set table title
                    DutyFeeContext dutyFeeContext = ExcelContextUtil.getDutyFeeContextData(context, null);
                    dutyFeeContext.setTableTitle(cellData.getStringValue().trim());
                }
            }
        }
    }

    @Override
    public void invoke(DutyFeeTable dutyFeeTable, AnalysisContext context) {
        DutyFeeContext dutyFeeContext = ExcelContextUtil.getDutyFeeContextData(context,
                (dfc) -> dutyFeeService.updateContext(dutyFeeTable, dfc));

        log.info("{}, dutyFeeContext = {}", dutyFeeTable, dutyFeeContext);

        DutyFeeAccount account = DutyFeeConverter.toAccount(dutyFeeTable);
        List<DutyFeeAccount> dutyFeeAccountList = dutyFeeService.getAccountByBankNo(account.getBankAccountNo());
        Integer accountId;
        if (!CollectionUtils.isEmpty(dutyFeeAccountList)) {
            accountId = dutyFeeAccountList.get(0).getId();
        } else {
            accountId = dutyFeeService.saveAccount(account);
        }

        DutyFeeDetail feeDetail = DutyFeeConverter.toFeeDetail(accountId, dutyFeeTable, dutyFeeContext);
        Integer feeDetailId = dutyFeeService.saveFeeDetail(feeDetail);

        List<DutyFeeDate> feeDateList = DutyFeeConverter.toFeeDateList(accountId, feeDetailId, dutyFeeContext, dutyFeeTable);
        dutyFeeService.saveFeeDateList(feeDateList);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        DutyFeeContext dutyFeeContext = ExcelContextUtil.getDutyFeeContextData(context, null);
        DutyFeeRecord record = BeanUtil.copyProperties(dutyFeeContext, new DutyFeeRecord());
        dutyFeeService.saveFeeRecord(record);
    }

    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        if (exception instanceof ExcelDataConvertException) {
            DutyFeeContext dutyFeeContext = ExcelContextUtil.getDutyFeeContextData(context, null);
            CellData cellData = ((ExcelDataConvertException) exception).getCellData();
            if (CellDataTypeEnum.STRING.equals(cellData.getType())) {
                String cellStr = cellData.getStringValue().replaceAll("\\s", "");
                if ("总计".equals(cellStr)) {
                    LinkedHashMap row = (LinkedHashMap) context.readRowHolder().getCurrentRowAnalysisResult();
                    Iterator iterator = row.entrySet().iterator();
                    Map.Entry tail = null;
                    while (iterator.hasNext()) {
                        tail = (Map.Entry) iterator.next();
                    }

                    log.info("总计 = {}", (tail == null ? "null" : tail.getValue()));

                    if (null != tail) {
                        dutyFeeContext.setTotalAmount(Integer.parseInt(tail.getValue().toString()));
                    }

                    return;
                } else if (!StringUtils.isEmpty(cellStr) && cellStr.startsWith("单位盖章")) {
                    Matcher m = signRegular.matcher(cellData.getStringValue());
                    String[] persons = new String[4];
                    int i = 0;
                    while (m.find()) {
                        persons[i++] = m.group().trim();
                    }

                    log.info("单位盖章 = {} 制表人 = {} 审核人 = {} 审批人 = {}", persons[0], persons[1], persons[2], persons[3]);

                    dutyFeeContext.setUnitSeal(persons[0]);
                    dutyFeeContext.setCreator(persons[1]);
                    dutyFeeContext.setAuditor(persons[2]);
                    dutyFeeContext.setApprover(persons[3]);

                    return;
                }
            }

        }
        throw exception;
    }
}