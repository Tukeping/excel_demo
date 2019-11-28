package com.tukeping.base;

import com.tukeping.exception.DuplicateRecordException;
import com.tukeping.exception.IllegalExcelTemplateException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author tukeping
 * @date 2019/11/28
 **/
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = DuplicateRecordException.class)
    @ResponseBody
    public String excelDuplicateRecord(HttpServletRequest req, DuplicateRecordException e) {
        return "导入Excel文件重复";
    }

    @ExceptionHandler(value = IllegalExcelTemplateException.class)
    @ResponseBody
    public String excelIllegalExcelTemplate(HttpServletRequest req, IllegalExcelTemplateException e) {
        return "导入Excel模版文件格式不对";
    }
}
