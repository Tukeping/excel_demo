package com.tukeping.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.tukeping.base.Result;
import com.tukeping.controller.vo.DutyFeeRecordVO;
import com.tukeping.converter.DutyFeeConverter;
import com.tukeping.entity.DutyFeeDetail;
import com.tukeping.excel.entity.DutyFeeTable;
import com.tukeping.excel.operation.UploadExcelListener;
import com.tukeping.service.DutyFeeService;
import com.tukeping.service.StationApprovalService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.tukeping.constant.ExcelConstants.EXCEL_CONTENT_TYPE;
import static com.tukeping.constant.ExcelConstants.EXCEL_SHEET_NAME;
import static com.tukeping.constant.ExcelConstants.EXCEL_SUFFIX;
import static com.tukeping.constant.ExcelConstants.EXCEL_TEMPLATE_NAME;
import static com.tukeping.constant.ExcelConstants.JSON_CONTENT_TYPE;

/**
 * https://alibaba-easyexcel.github.io/index.html
 *
 * @author tukeping
 * @date 2019/11/26
 **/
@Slf4j
@RestController
@RequestMapping("/duty-fee")
public class DutyFeeController {

    @Autowired
    private DutyFeeService dutyFeeService;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private StationApprovalService stationApprovalService;

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

    @DeleteMapping("/excel/record/{recordId}")
    @ApiOperation("删除上传excel及数据")
    public String delExcelData(
            @ApiParam(value = "上传记录ID", required = true, example = "0")
            @PathVariable Integer recordId) {
        dutyFeeService.deleteRecordById(recordId);
        return Result.SUCCESS;
    }

    @GetMapping("/")
    @ApiOperation("查询所有上传记录信息")
    public List<DutyFeeRecordVO> findAll() {
        return dutyFeeService.findAllRecords()
                .stream()
                .map(DutyFeeConverter::toRecordVO)
                .collect(Collectors.toList());
    }

    @GetMapping("/fee-detail/{recordId}")
    @ApiOperation("根据上传记录ID查询上传数据")
    public List<DutyFeeDetail> findDetailListByRecordId(
            @ApiParam(value = "上传记录ID", required = true, example = "0")
            @PathVariable Integer recordId) {
        return dutyFeeService.findDetailListByRecordId(recordId);
    }

    /**
     * 文件下载（失败了会返回一个有部分数据的Excel）
     * <p>1. 创建excel对应的实体对象 参照{@link DutyFeeTable}
     * <p>2. 设置返回的 参数
     * <p>3. 直接写，这里注意，finish的时候会自动关闭OutputStream,当然你外面再关闭流问题不大
     */
    @GetMapping("/excel/download-template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        response.setContentType(EXCEL_CONTENT_TYPE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
        String fileName = URLEncoder.encode(EXCEL_TEMPLATE_NAME, StandardCharsets.UTF_8.displayName());
        String fileNameWithSuffix = fileName + EXCEL_SUFFIX;

        response.setHeader("Content-disposition",
                "attachment;filename=" + fileNameWithSuffix
                        + ";filename*=utf-8''" + fileNameWithSuffix);

//        List<List<String>> head = new ArrayList<>();
//        head.add(Lists.newArrayList("yyyy年m-m月窗口午间值班费发放表"));

        List<DutyFeeTable> dutyFeeTables = data();
        dutyFeeTables.forEach(System.out::println);

        EasyExcel.write(response.getOutputStream(), DutyFeeTable.class)
                .sheet(EXCEL_SHEET_NAME)
//                .head(head)
//                .registerWriteHandler(new LoopMergeStrategy(1, 2, 1))
                .doWrite(dutyFeeTables);
    }

    private List<DutyFeeTable> data() {
        AtomicInteger num = new AtomicInteger(1);
        return stationApprovalService.findAll().stream()
                .map(stationApproval -> {
                    DutyFeeTable table = DutyFeeConverter.toFeeTable(stationApproval);
                    table.setSerialNumber(num.getAndIncrement());
                    return table;
                }).collect(Collectors.toList());
    }
}
