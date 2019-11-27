package com.tukeping.util;

import com.alibaba.excel.context.AnalysisContext;
import com.tukeping.excel.entity.DutyFeeContext;
import lombok.experimental.UtilityClass;

import java.util.function.Consumer;

/**
 * @author tukeping
 * @date 2019/11/27
 **/
@UtilityClass
public class ExcelContextUtil {

    public DutyFeeContext getDutyFeeContextData(AnalysisContext context, Consumer<DutyFeeContext> consumer) {
        DutyFeeContext dutyFeeContext = new DutyFeeContext();
        if (null != context.getCustom() && context.getCustom() instanceof DutyFeeContext) {
            dutyFeeContext = (DutyFeeContext) context.getCustom();
            if (null != consumer) {
                consumer.accept(dutyFeeContext);
            }
        }
        return dutyFeeContext;
    }
}
