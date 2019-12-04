package com.tukeping.excel.operation;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.util.ConverterUtils;
import com.google.common.collect.Lists;
import com.tukeping.constant.ExcelConstants;
import com.tukeping.converter.DutyFeeConverter;
import com.tukeping.dto.DutyFeeDTO;
import com.tukeping.dto.DutyFeeDetailDTO;
import com.tukeping.entity.DutyFeeDate;
import com.tukeping.entity.DutyFeeDetail;
import com.tukeping.entity.DutyFeeRecord;
import com.tukeping.excel.entity.DutyFeeContext;
import com.tukeping.excel.entity.DutyFeeTable;
import com.tukeping.exception.DuplicateRecordException;
import com.tukeping.exception.IllegalExcelTemplateException;
import com.tukeping.service.DutyFeeService;
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

    private static Pattern signRegular = Pattern.compile("(?<=制表人：)\\s*\\S*|(?<=审核人：)\\s*\\S*|(?<=审批人：)\\s*\\S*");
    private static Pattern yearRegular = Pattern.compile("^\\d{4}");
    private static Pattern monthRegular = Pattern.compile("\\d{1}-\\d{1}");

    private DutyFeeService dutyFeeService;

    /**
     * Local One Excel Data
     */
    private DutyFeeDTO dutyFeeDTO;

    public UploadExcelListener(DutyFeeService dutyFeeService) {
        this.dutyFeeService = dutyFeeService;

        dutyFeeDTO = new DutyFeeDTO();
        dutyFeeDTO.setDutyFeeDetail(Lists.newArrayList());
    }

    @Override
    public void invokeHead(Map<Integer, CellData> headMap, AnalysisContext context) {
        invokeHeadMap(ConverterUtils.convertToStringMap(headMap, context), context);
        if (!headMap.isEmpty()) {
            CellData cellData = headMap.values().stream().findFirst().get();
            if (!StringUtils.isEmpty(cellData.getStringValue())) {
                // extract year
                Matcher yearMatcher = yearRegular.matcher(cellData.getStringValue().trim());
                if (yearMatcher.find()) {
                    Integer year = Integer.parseInt(yearMatcher.group());
                    context.readWorkbookHolder().setCustomObject(DutyFeeContext.ofYear(year));
                    // set table title
                    DutyFeeContext dutyFeeContext = ExcelContextUtil.getDutyFeeContextData(context, null);
                    dutyFeeContext.setTableTitle(cellData.getStringValue().trim());
                    // insert record table
                    if (null == dutyFeeContext.getRecordId()) {

                        DutyFeeRecord record = new DutyFeeRecord();
                        record.setTableTitle(dutyFeeContext.getTableTitle());
                        record.setYear(year);

                        Matcher monthMatcher = monthRegular.matcher(cellData.getStringValue().trim());
                        if (monthMatcher.find()) {
                            record.setMonth(monthMatcher.group());
                        } else {
                            throw new IllegalExcelTemplateException("Excel模版标题少了月份");
                        }

                        if (dutyFeeService.existFeeRecordByUk(record.getYear(), record.getMonth())) {
                            throw new DuplicateRecordException(
                                    String.format("Excel中%d年%s月的报销表格已导入", record.getYear(), record.getMonth()));
                        }

                        // 缓存record
                        dutyFeeDTO.setRecord(record);
                    }
                }
            }
        }
    }

    @Override
    public void invoke(DutyFeeTable dutyFeeTable, AnalysisContext context) {
        DutyFeeContext dutyFeeContext = ExcelContextUtil.getDutyFeeContextData(context,
                (dfc) -> dutyFeeService.updateContext(dutyFeeTable, dfc));

        log.info("{}, dutyFeeContext = {}", dutyFeeTable, dutyFeeContext);

        DutyFeeDetailDTO dutyFeeDetailDTO = new DutyFeeDetailDTO();

        DutyFeeDetail feeDetail = DutyFeeConverter.toFeeDetail(dutyFeeTable, dutyFeeContext);
        List<DutyFeeDate> feeDateList = DutyFeeConverter.toFeeDateList(dutyFeeTable, dutyFeeContext);

        dutyFeeDetailDTO.setDutyFeeDetail(feeDetail);
        dutyFeeDetailDTO.setDutyFeeDateList(feeDateList);

        dutyFeeDTO.getDutyFeeDetail().add(dutyFeeDetailDTO);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 保存 record, detail, date 三张表数据, 一起执行保存动作, 需要具备事务性
        if (null != dutyFeeDTO.getRecord() && !CollectionUtils.isEmpty(dutyFeeDTO.getDutyFeeDetail())) {
            dutyFeeService.saveCompleteDutyFeeData(dutyFeeDTO);
        } else {
            throw new IllegalExcelTemplateException("上传的模版数据处理失败!");
        }
    }

    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        if (exception instanceof ExcelDataConvertException) {
            DutyFeeContext dutyFeeContext = ExcelContextUtil.getDutyFeeContextData(context, null);
            CellData cellData = ((ExcelDataConvertException) exception).getCellData();
            if (CellDataTypeEnum.STRING.equals(cellData.getType())) {
                String cellStr = cellData.getStringValue().replaceAll("\\s", "");
                if (ExcelConstants.TOTAL_AMOUNT.equals(cellStr)) {
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
                } else if (!StringUtils.isEmpty(cellStr) && cellStr.startsWith(ExcelConstants.UNIT_SEAL)) {
                    Matcher m = signRegular.matcher(cellData.getStringValue());
                    String[] persons = new String[3];
                    int i = 0;
                    while (m.find()) {
                        persons[i++] = m.group().trim();
                    }

                    log.info("制表人 = {} 审核人 = {} 审批人 = {}", persons[0], persons[1], persons[2]);

                    dutyFeeContext.setCreator(persons[0]);
                    dutyFeeContext.setAuditor(persons[1]);
                    dutyFeeContext.setApprover(persons[2]);

                    return;
                }
            }
        }

        throw exception;
    }
}
