package com.tukeping.controller;

import com.alibaba.excel.EasyExcel;
import com.tukeping.excel.entity.DutyFeeTable;
import com.tukeping.excel.operation.UploadExcelListener;
import com.tukeping.service.DutyFeeService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author tukeping
 * @date 2019/11/26
 **/
@RestController("/excel")
public class UploadExcelController {

    @Resource
    private DutyFeeService dutyFeeService;

    /**
     * 文件上传
     * <p>1. 创建excel对应的实体对象 参照{@link DutyFeeTable}
     * <p>2. 由于默认异步读取excel，所以需要创建excel一行一行的回调监听器，参照{@link UploadExcelListener}
     * <p>3. 直接读即可
     */
    @PostMapping("/upload")
    @ResponseBody
    public String upload(MultipartFile file) throws IOException {
        UploadExcelListener uploadExcelListener = new UploadExcelListener(dutyFeeService);

        EasyExcel.read(file.getInputStream(), DutyFeeTable.class, uploadExcelListener)
                .headRowNumber(3)
                .ignoreEmptyRow(Boolean.TRUE)
                .autoTrim(Boolean.TRUE)
                .sheet()
                .doRead();

        return "success";
    }
}
