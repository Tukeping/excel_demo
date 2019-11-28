package com.tukeping.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.tukeping.base.Result;
import com.tukeping.controller.vo.DutyFeeRecordVO;
import com.tukeping.converter.DutyFeeConverter;
import com.tukeping.excel.entity.DutyFeeTable;
import com.tukeping.excel.operation.UploadExcelListener;
import com.tukeping.service.DutyFeeService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.tukeping.constant.ExcelConstants.EXCEL_CONTENT_TYPE;
import static com.tukeping.constant.ExcelConstants.EXCEL_SUFFIX;
import static com.tukeping.constant.ExcelConstants.EXCEL_TEMPLATE_NAME;
import static com.tukeping.constant.ExcelConstants.JSON_CONTENT_TYPE;

/**
 * @author tukeping
 * @date 2019/11/26
 **/
@RestController("/duty-fee")
public class DutyFeeController {

    @Autowired
    private DutyFeeService dutyFeeService;

    @Autowired
    private ResourceLoader resourceLoader;

    private static final int BUFFER_BYTE_SIZE = 1048;

    /**
     * Excel文件上传
     * <p>1. 创建excel对应的实体对象 参照{@link DutyFeeTable}
     * <p>2. 由于默认异步读取excel，所以需要创建excel一行一行的回调监听器，参照{@link UploadExcelListener}
     * <p>3. 直接读即可
     */
    @PostMapping("/excel/upload")
    @ResponseBody
    @ApiOperation(value = "Excel上传")
    public String upload(MultipartFile file) throws IOException {
        UploadExcelListener uploadExcelListener = new UploadExcelListener(dutyFeeService);

        EasyExcel.read(file.getInputStream(), DutyFeeTable.class, uploadExcelListener)
                .headRowNumber(3)
                .ignoreEmptyRow(Boolean.TRUE)
                .autoTrim(Boolean.TRUE)
                .sheet()
                .doRead();

        return Result.SUCCESS;
    }

    /**
     * 模版Excel下载
     */
    @GetMapping("/excel/download")
    @ApiOperation(value = "模版Excel下载", produces = "application/vnd.ms-excel")
    public void download(HttpServletResponse response) throws IOException {
        String encodingUtf8 = StandardCharsets.UTF_8.displayName();
        String fileName = URLEncoder.encode(EXCEL_TEMPLATE_NAME, encodingUtf8);
        String fileNameWithSuffix = fileName + EXCEL_SUFFIX;
        String file = ResourceUtils.CLASSPATH_URL_PREFIX + "/excel/" + EXCEL_TEMPLATE_NAME + EXCEL_SUFFIX;

        try {
            response.setContentType(EXCEL_CONTENT_TYPE);
            response.setCharacterEncoding(encodingUtf8);
            response.setHeader("Content-disposition",
                    "attachment;filename=" + fileNameWithSuffix
                            + ";filename*=utf-8''" + fileNameWithSuffix);

            Resource resource = resourceLoader.getResource(file);
            try (InputStream in = resource.getInputStream();
                 OutputStream out = response.getOutputStream()) {
                byte[] buffer = new byte[BUFFER_BYTE_SIZE];
                int numBytesRead;
                while ((numBytesRead = in.read(buffer)) > 0) {
                    out.write(buffer, 0, numBytesRead);
                }
            }
        } catch (Exception e) {
            response.reset();
            response.setContentType(JSON_CONTENT_TYPE);
            response.setCharacterEncoding(encodingUtf8);
            Map<String, String> map = new HashMap<>(2);
            map.put("status", Result.FAILURE);
            map.put("message", "下载文件失败" + e.getMessage());
            response.getWriter().println(JSON.toJSONString(map));
        }
    }

//    @DeleteMapping("/excel/record/{recordId}")
//    @ApiImplicitParam(name = "recordId", value = "记录ID", required = true, dataType = "Integer", paramType = "path")
//    public String delExcelData(@PathVariable Integer recordId) {
//        dutyFeeService.deleteRecordById(recordId);
//        return Result.SUCCESS;
//    }

    @GetMapping("/")
    public List<DutyFeeRecordVO> findAll() {
        return dutyFeeService.findAllRecords()
                .stream()
                .map(DutyFeeConverter::toRecordVO)
                .collect(Collectors.toList());
    }

//    @GetMapping("/{recordId}")
//    @ApiImplicitParam(name = "recordId", value = "记录ID", required = true, dataType = "Integer", paramType = "path")
//    public List<DutyFeeDetail> findDetailListByRecordId(
//            @ApiParam(example = "0", required = true)
//            @PathVariable Integer recordId) {
//        return dutyFeeService.findDetailListByRecordId(recordId);
//    }
}
