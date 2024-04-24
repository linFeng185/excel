package com.lin.util;

import com.lin.annotation.Excel;
import com.lin.enums.FileNameEnum;
import com.lin.imports.CheckInterface;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * excel导入导出工具类
 *
 * @author: lin
 * @createTime: 2024/4/23:16:47
 */
public class ExcelUtils {



    /**
     * 默认的工作表下标
     */
    private final static int DEFAULT_SHEET_INDEX=0;

    /**
     * 数据导入时开始的行数
     */
    private final static int DATA_BEGIN_INDEX=2;

    /**
     * 导入excel
     * @param file 输入流
     * @return 结果集
     */
    public static<T extends CheckInterface> List<T> importExcel(MultipartFile file,Class<T> clazz){
        try {
            return importExcel(file.getInputStream(),clazz,DATA_BEGIN_INDEX,DEFAULT_SHEET_INDEX);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * 导入excel
     * @param inputStream 输入流
     * @param rowBeginIndex 读取数据的开始下标
     * @param sheetIndex sheet下标
     * @return 结果集
     */
    public static<T extends CheckInterface> List<T> importExcel(InputStream inputStream,Class<T> clazz, int rowBeginIndex, int sheetIndex){
        ExcelImport<T> excelImport = new ExcelImport<>(clazz);
        try {
            return excelImport.importExcel(inputStream,rowBeginIndex,sheetIndex);
        } catch (IOException | InvocationTargetException | NoSuchMethodException | InstantiationException |
                 IllegalAccessException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * 导出
     * @param clazz 类型
     * @param dataList 数据列表
     * @param fileEnum 文件名枚举
     * @param <T> 泛型
     */
    public static<T> void export(Class<T> clazz,List<T> dataList, FileNameEnum fileEnum){
        export(ServletUtil.getRequest(),ServletUtil.getResponse(),clazz,dataList,fileEnum);
    }

    /**
     * 导出excel
     * @param request 请求对象
     * @param response 响应对象
     * @param dataList 数据集
     * @param fileEnum 文件的枚举类
     */
    public static<T> void export(HttpServletRequest request, HttpServletResponse response,Class<T> clazz,List<T> dataList, FileNameEnum fileEnum) {
        ExcelExport<T> excelExport = new ExcelExport<>(clazz);
        try {
            excelExport.export(request,response,dataList,fileEnum);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 校验数据
     * @param data
     * @param clazz
     * @param <T>
     */
    public static<T extends CheckInterface> boolean checkData(List<T> data,Class<T> clazz){
        ExcelImport<T> excelImport = new ExcelImport<>(clazz);
        excelImport.fieldInfoInit();
        List<Object[]> fieldInfoList = excelImport.getFieldInfoList();
        boolean res = false;
        for (int i = 0; i < data.size(); i++) {
            T obj = data.get(i);
            StringBuilder sb = new StringBuilder();
            for (Object[] objArr : fieldInfoList) {
                Object val = ReflectUtils.invokeGetter(obj, ((Field) objArr[0]).getName());
                Excel excel = (Excel) objArr[1];
                excelImport.checkValue(val != null ? val.toString() : null,false,sb,excel,String.valueOf(i + 1));
            }
            if(sb.length() > 0){
                res = true;
                obj.setMsg(sb.deleteCharAt(0).toString());
            }
            obj.check();
        }
        return res;
    }
}
