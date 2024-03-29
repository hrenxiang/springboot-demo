package com.huangrx.easyexcel.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.huangrx.easyexcel.annotation.SheetName;
import com.huangrx.easyexcel.listener.ExcelListener;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author        hrenxiang
 * @since         2022/4/26 4:15 PM
 */
public class ExcelUtil {
    /**
     * 私有化构造方法
     */
    private ExcelUtil() {
    }

    /**
     * 读取 Excel(多个 sheet)
     */
    public static <T> List<T> readExcel(ExcelReader reader, Class<T> rowModel, int sheetCount) {
        if (reader == null) {
            return new ArrayList<>();
        }
        List<ReadSheet> readSheetList = new ArrayList<>();
        ExcelListener<T> excelListener = new ExcelListener<>();
        ReadSheet readSheet = EasyExcel.readSheet(sheetCount)
                .head(rowModel)
                .registerReadListener(excelListener)
                .build();
        readSheetList.add(readSheet);
        reader.read(readSheetList);
        return getExtendsBeanList(excelListener.getDataList(), rowModel);
    }

    /**
     * 读取 Excel(多个 sheet)
     * 将多sheet合并成一个list数据集，通过自定义ExcelReader继承AnalysisEventListener
     * 重写invoke doAfterAllAnalysed方法
     * getExtendsBeanList 主要是做Bean的属性拷贝 ，可以通过ExcelReader中添加的数据集直接获取
     *
     * @param excel    文件
     * @param rowModel 实体类映射
     */
    private static List[] readExcel(MultipartFile excel, Integer sheetNo, Class<?>[] rowModel) throws Exception {
        ExcelReader reader = getReader(excel);
        if (reader == null) {
            return new ArrayList[rowModel.length];
        }
        List[] result = new ArrayList[rowModel.length];
        for (int sheetCount = 0; sheetCount < rowModel.length; sheetCount++) {
            if (sheetNo != null && sheetNo != sheetCount) {
                continue;
            }
            result[sheetCount].addAll(readExcel(reader, rowModel[sheetCount], sheetCount));
        }
        return result;
    }

    /**
     * 读取 Excel(多个 sheet)
     * 将多sheet合并成一个list数据集，通过自定义ExcelReader继承AnalysisEventListener
     * 重写invoke doAfterAllAnalysed方法
     * getExtendsBeanList 主要是做Bean的属性拷贝 ，可以通过ExcelReader中添加的数据集直接获取
     *
     * @param excel    文件
     * @param rowModel 实体类映射
     */
    public static List[] readExcel(MultipartFile excel, Class<?>... rowModel) throws Exception {
        ExcelReader reader = getReader(excel);
        if (reader == null) {
            return new ArrayList[rowModel.length];
        }
        List[] result = new ArrayList[rowModel.length];
        for (int sheetCount = 0; sheetCount < rowModel.length; sheetCount++) {
            result[sheetCount] = new ArrayList<>(readExcel(reader, rowModel[sheetCount], sheetCount));
        }
        return result;
    }

    /**
     * 读取 Excel(单个 sheet)
     * 将多sheet合并成一个list数据集，通过自定义ExcelReader继承AnalysisEventListener
     * 重写invoke doAfterAllAnalysed方法
     * getExtendsBeanList 主要是做Bean的属性拷贝 ，可以通过ExcelReader中添加的数据集直接获取
     */
    public static <T> List<T> readFirstSheetExcel(MultipartFile excel, Class<T> rowType) throws Exception {
        ExcelReader reader = getReader(excel);
        if (reader == null) {
            return new ArrayList<>();
        }
        return readExcel(reader, rowType, 1);
    }

    /**
     * 读取某个 sheet 的 Excel
     *
     * @param excel    文件
     * @param rowModel 实体类映射
     * @param sheetNo  sheet 的序号 从1开始
     * @return Excel 数据 list
     */
    public static <T> List readExcel(MultipartFile excel, Class<T> rowModel, int sheetNo) throws Exception {
        Class[] classes = {rowModel};
        return ExcelUtil.readExcel(excel, sheetNo, classes)[0];
    }

    /**
     * 导出 Excel ：一个 sheet，带表头
     * 自定义WriterHandler 可以定制行列数据进行灵活化操作
     *
     * @param response  HttpServletResponse
     * @param list      数据 list
     * @param fileName  导出的文件名
     * @param sheetName 导入文件的 sheet 名
     */
    public static <T> void writeExcel(HttpServletResponse response, List<T> list,
                                      String fileName, String sheetName, ExcelTypeEnum excelTypeEnum) throws Exception {
        if (sheetName == null || "".equals(sheetName)) {
            sheetName = "sheet1";
        }
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        EasyExcel.write(getOutputStream(fileName, response, excelTypeEnum), list.get(0).getClass()).sheet(sheetName).doWrite(list);
    }

    /**
     * 导出 Excel ：一个 sheet，带表头
     * 自定义WriterHandler 可以定制行列数据进行灵活化操作
     *
     * @param response HttpServletResponse
     * @param list     数据 list
     * @param fileName 导出的文件名
     */
    public static <T> void writeExcel(HttpServletResponse response, List<T> list,
                                      String fileName, ExcelTypeEnum excelTypeEnum) throws Exception {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        String sheetName = list.get(0).getClass().getAnnotation(SheetName.class).value();
        sheetName = StringUtils.isNotBlank(sheetName) ? sheetName : "sheet1";
        EasyExcel.write(getOutputStream(fileName, response, excelTypeEnum), list.get(0).getClass()).sheet(sheetName).doWrite(list);
    }

    /**
     * 导出 Excel ：一个 sheet，带表头
     * 自定义WriterHandler 可以定制行列数据进行灵活化操作
     *
     * @param response HttpServletResponse
     * @param fileName 导出的文件名
     */
    public static void writeExcel(HttpServletResponse response, String fileName,
                                  ExcelTypeEnum excelTypeEnum, List... lists) throws Exception {
        ExcelWriter excelWriter = null;
        try {
            excelWriter = EasyExcel.write(getOutputStream(fileName, response, excelTypeEnum)).build();
            for (int count = 0; count < lists.length; count++) {
                if (CollectionUtils.isEmpty(lists[count])) {
                    continue;
                }
                String sheetName = lists[count].get(0).getClass().getAnnotation(SheetName.class).value();
                sheetName = StringUtils.isNotBlank(sheetName) ? sheetName : "sheet" + (count + 1);
                WriteSheet writeSheet = EasyExcel.writerSheet(count, sheetName)
                        .head(lists[count].get(0).getClass())
                        .build();
                excelWriter.write(lists[count], writeSheet);
            }
        } finally {
            if (excelWriter != null) {
                excelWriter.finish();
            }
        }

    }


    /**
     * 导出文件时为Writer生成OutputStream
     */
    private static OutputStream getOutputStream(String fileName, HttpServletResponse response, ExcelTypeEnum excelTypeEnum) throws Exception {
        //创建本地文件
        String filePath = fileName + excelTypeEnum.getValue();
        try {
            fileName = new String(filePath.getBytes(), StandardCharsets.UTF_8);
            response.setCharacterEncoding("UTF-8");
            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            return response.getOutputStream();
        } catch (IOException e) {
            throw new Exception("创建文件失败！");
        }
    }

    /**
     * 返回 ExcelReader
     *
     * @param excel 需要解析的 Excel 文件
     */
    public static ExcelReader getReader(MultipartFile excel) throws Exception {
        String fileName = excel.getOriginalFilename();
        if (fileName == null) {
            throw new Exception("文件格式错误！");
        }
        if (!fileName.toLowerCase().endsWith(ExcelTypeEnum.XLS.getValue()) && !fileName.toLowerCase().endsWith(ExcelTypeEnum.XLSX.getValue())) {
            throw new Exception("文件格式错误！");
        }
        InputStream inputStream;
        try {
            inputStream = excel.getInputStream();
            return EasyExcel.read(inputStream).build();
        } catch (IOException e) {
            //do something
        }
        return null;
    }

    /**
     * 利用BeanCopy转换list
     */
    public static <T> List<T> getExtendsBeanList(List<?> list, Class<T> typeClazz) {
        return MyBeanCopy.convert(list, typeClazz);
    }

}