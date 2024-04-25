package com.lin.util;

import com.lin.annotation.Excel;
import com.lin.enums.ExcelTypeEnum;
import com.lin.imports.CheckInterface;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 导入类
 *
 * @author: lin
 * @createTime: 2024/4/23:15:47
 */
public class ExcelImport<T extends CheckInterface> extends ExcelSuper<T> {

    /**
     * 字段描述行下标
     */
    public final static int FIELD_ROW_INDEX = 1;

    /**
     * 字段与注解的map
     */
    Map<Integer,Object[]> fieldMap;

    /**
     * 数据总行数
     */
    private Integer rowNum;

    /**
     * 组合索引map，每个唯一索引都有独立的map，用于查找对应的字段
     */
    Map<String,Map<Excel,String>> compositeIndexMaps = new HashMap<>();


    /**
     * 组合索引键缓存
     */
    Map<String,StringBuilder> compositeIndexKeyCache = new HashMap<>();

    /**
     * 组合索引值map
     */
    Map<String,Map<String,String>> compositeIndexVauleMap = new HashMap<>();

    /**
     * 值转换map
     */
    Map<String,Map<String,String>> valueConvertMap = new HashMap<>();

    /**
     * 值转换map（用于二次导入）
     */
    Map<String,Map<String,String>> reversalValueConvertMap = new HashMap<>();

    /**
     * 唯一索引值map
     */
    Map<Excel,Map<String,String>> uniqueValueMap = new HashMap<>();

    /**
     * 导入模板字段标题索引map
     */
    Map<String,Integer> headMap = new HashMap<>();

    public List<Object[]> getFieldInfoList() {
        return fieldInfoList;
    }

    /**
     * 字段信息list，下标为0的是Field对象，1是字段的Excel对象
     */
    List<Object[]> fieldInfoList = new ArrayList<>();

    public ExcelImport(Class<T> clazz) {
        super(clazz);
    }

    /**
     * 导入excel
     * @param inputStream 输入流
     * @param rowBeginIndex 读取数据的开始下标
     * @param sheetIndex sheet下标
     * @return 结果集
     */
    public List<T> importExcel(InputStream inputStream,int rowBeginIndex,int sheetIndex) throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        importExcelInit(inputStream,rowBeginIndex,sheetIndex);
        return getDataList(rowBeginIndex);
    }

    /**
     * 导入初始化
     *
     * @param inputStream 输入流
     * @param rowBeginIndex 数据开始下标
     * @param sheetIndex 工作表下标
     * @throws IOException 异常
     */
    public void importExcelInit(InputStream inputStream, int rowBeginIndex, int sheetIndex) throws IOException {
        excelFileInit(inputStream,rowBeginIndex,sheetIndex);
        fieldInfoInit();
    }

    /**
     * 字段信息初始化
     */
    public void fieldInfoInit(){
        Field[] fields=clazz.getDeclaredFields();
        fieldMap = new HashMap<>();
        Map<String, Excel> excelMap = new HashMap<>();
        for (Field field:fields) {
            Excel excel = field.getAnnotation(Excel.class);
            if(excel == null){
                continue;
            }
            fieldInfoList.add(new Object[]{field,excel});
            excelMap.put(excel.name(),excel);
            if(excel.excelType() == ExcelTypeEnum.IS_EXPORT){
                continue;
            }
            if(!"".equals(excel.valueConvert())){
                valueConvertMap.put(excel.name(),getKeyMap(excel));
            }
            if(excel.isUnique()){
                uniqueValueMap.put(excel,new HashMap<>());
            }
            if(excel.compositeIndex().length > 0){
                Map<Excel,String> compositeIndexMap = new HashMap<>();
                for (String name : excel.compositeIndex()) {
                    compositeIndexMap.put(excelMap.get(name),"");
                }
                compositeIndexMaps.put(Arrays.toString(excel.compositeIndex()),compositeIndexMap);
                compositeIndexKeyCache.put(Arrays.toString(excel.compositeIndex()),new StringBuilder());
                compositeIndexVauleMap.put(Arrays.toString(excel.compositeIndex()),new HashMap<>());
            }
            //设置私有属性可以访问
            field.setAccessible(true);
            if (!headMap.containsKey(excel.name())){
                continue;
            }
            fieldMap.put(headMap.get(excel.name()),new Object[]{field,excel});
        }
    }

    /**
     * 对excel文件的初始化
     * @param inputStream 输入流
     * @param rowBeginIndex 开始行
     * @param sheetIndex 工作表下标
     * @throws IOException 异常
     */
    public void excelFileInit(InputStream inputStream, int rowBeginIndex, int sheetIndex) throws IOException {
        wb = WorkbookFactory.create(inputStream);
        sheet = wb.getSheetAt(sheetIndex);
        rowNum = sheet.getPhysicalNumberOfRows();
        if(rowNum<=rowBeginIndex){
            throw new NullPointerException("数据不能为空");
        }
        //获取字段标题的一行数据
        Row headRow = sheet.getRow(FIELD_ROW_INDEX);
        int headCellNum = headRow.getPhysicalNumberOfCells();
        //循环单元格，并取出字段名
        for (int i=0;i<headCellNum;i++){
            Cell headCell=headRow.getCell(i);
            headMap.put(String.valueOf(headCell.getStringCellValue()),i);
        }
    }

    /**
     * 获取数据集
     * @param rowBeginIndex 数据开始下标
     * @return 数据
     * @throws NoSuchMethodException 异常
     * @throws InvocationTargetException 异常
     * @throws InstantiationException 异常
     * @throws IllegalAccessException 异常
     */
    public List<T> getDataList(int rowBeginIndex) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<T> res = new ArrayList<>();
        for (int i=rowBeginIndex;i<rowNum;i++){
            Row row = sheet.getRow(i);
            T t = clazz.getDeclaredConstructor().newInstance();
            StringBuilder sb = new StringBuilder();
            boolean isNullRow = true;
            for (Map.Entry<Integer,Object[]>entry:fieldMap.entrySet()){
                Cell cell=row.getCell(entry.getKey());
                if(isNullRow){
                    isNullRow = cell == null;
                }
                Field field = (Field) entry.getValue()[0];
                Excel excel = (Excel) entry.getValue()[1];
                //获取单元格值
                String cellValue = getCellValue(cell);
                for (Map.Entry<String,Map<Excel, String>> excelStringMap : compositeIndexMaps.entrySet()) {
                    if(excelStringMap.getValue().containsKey(excel)){
                        compositeIndexKeyCache.get(excelStringMap.getKey()).append(cellValue);
                    }
                }
                //检查单元格值
                cellValue = checkValue(cellValue,true,sb,excel,cell != null?String.valueOf(cell.getRowIndex() + 1):"未知");
                //设置字段值
                ReflectUtils.invokeSetter(t,field.getName(),cellValueConvert(field.getType(), excel, cellValue));
            }
            if(isNullRow){
                break;
            }
            if(sb.length() > 0){
                t.setMsg(sb.deleteCharAt(0).toString());
            }
            res.add(t);
            t.check();
        }
        return res;
    }

    /**
     * 获取单元格值
     *
     * @param cell 单元格对象
     * @return 单元格内容
     */
    public String getCellValue(Cell cell) {
        String res;
        if(cell == null){
            return null;
        }
        CellType cellType = cell.getCellType();
        if(cellType==CellType.NUMERIC){
            res = new BigDecimal(String.valueOf(cell.getNumericCellValue())).stripTrailingZeros().toPlainString();
        }else if(cellType==CellType.STRING){
            res = cell.getStringCellValue();
        }else if(cellType==CellType.FORMULA){
            res = String.valueOf(cell.getCellFormula());
        }else if(cellType==CellType.BLANK){
            res = null;
        }else if(cellType==CellType.BOOLEAN){
            res = String.valueOf(cell.getBooleanCellValue());
        }else if(cellType==CellType.ERROR){
            res = null;
        }else{
            res = null;
        }
        return res;
    }

    /**
     * 检查值
     *
     * @param value    单元格值
     * @param isCellValue 是否单元格值
     * @param msg     描述
     * @param excel    当前字段的注解信息
     * @param rowIndex 数据所在行下标
     * @return 单元格内容（去除两边空格）
     */
    public String checkValue(String value,boolean isCellValue, StringBuilder msg, Excel excel, String rowIndex) {
        String uniqueKey = null;
        String compositeIndex = null;
        Map<String,String> valueConvertMap = this.valueConvertMap.get(excel.name());
        Map<String, String> reversalValueConvertMap = this.reversalValueConvertMap.get(excel.name());
        Map<String,String> uniqueValueMap = this.uniqueValueMap.get(excel);
        Map<String,String> compositeIndexValueMap = this.compositeIndexVauleMap.get(Arrays.toString(excel.compositeIndex()));
        StringBuilder compositeIndexValueCache = this.compositeIndexKeyCache.get(Arrays.toString(excel.compositeIndex()));
        if(excel.isUnique()){
            uniqueKey = value;
        }
        if(excel.compositeIndex().length > 0){
            compositeIndex = compositeIndexValueCache.toString();
            compositeIndexValueCache.delete(0,compositeIndexValueCache.length());
        }
        if(uniqueKey != null){
            String dataIndex = uniqueValueMap.get(uniqueKey);
            if (dataIndex != null) {
                msg.append("、").append(excel.name()).append("在第").append(dataIndex).append("行已存在");
            }else{
                uniqueValueMap.put(uniqueKey,rowIndex);
            }
        }
        if(compositeIndex != null){
            String dataIndex = compositeIndexValueMap.get(compositeIndex);
            if(dataIndex != null){
                msg.append("、").append(ArrayUtils.toString(excel.compositeIndex())).append("在第").append(dataIndex).append("行已存在，").append("该校验为字段组合形成唯一值进行校验");
            }else{
                compositeIndexValueMap.put(compositeIndex,rowIndex);
            }

        }
        if(StringUtils.isBlank(value)){
            if(excel.isNotBlank()){
                msg.append("、");
                msg.append(excel.name()).append("不能为空");
            }
            return null;
        }
        value = value.trim();
        //正则校验
        if(!"".equals(excel.regularExpression())){
            Pattern pattern = Pattern.compile(excel.regularExpression());
            Matcher matcher = pattern.matcher(value);
            if(!matcher.find()){
                msg.append("、").append(excel.name()).append("不符合校验规则");
            }
        }
        //如果数值不在范围内
        if(CalculateUtil.greaterThan(excel.maxNumber(),excel.minNumber()) && !CalculateUtil.between(value,excel.minNumber(),excel.maxNumber())){
            msg.append("、");
            msg.append(excel.name()).append("超过取值范围：").append(excel.minNumber()).append("-").append(excel.maxNumber());
        }
        //如果长度不符合
        if(excel.length() > 0 && value.length() > excel.length()){
            msg.append("、");
            msg.append(excel.name()).append("长度不能超过").append(excel.length());
        }
        //小数位数不符合
        if(excel.scale() > 0 && new BigDecimal(value).scale() > excel.scale()){
            msg.append("、");
            msg.append(excel.name()).append("小数位数不能超过").append(excel.scale());
        }
        //判断是否枚举类型以及值是否合法
        if(!"".equals(excel.valueConvert())){
            if(isCellValue){
                if(valueConvertMap.containsKey(value)){
                    value = valueConvertMap.get(value);
                }else{
                    msg.append("、").append(excel.name()).append("没有").append(value).append("类型");
                }
            }else{
                if(!reversalValueConvertMap.containsKey(value)){
                    msg.append("、").append(excel.name()).append("没有").append(value).append("类型");
                }
            }
        }
        return value;
    }

    /**
     * 单元格值转换
     *
     * @param fieldType 字段类型
     * @param excel 注解信息
     * @param cellValue 单元格内容
     * @return 转换后的字段值
     */
    public Object cellValueConvert(Class<?> fieldType, Excel excel, String cellValue) {
        Object value;
        if(cellValue == null){
            return null;
        }
        if(String.class == fieldType){
            value = cellValue;
        }else if(Double.class==fieldType){
            value = Double.valueOf(cellValue);
        } else if (Float.class==fieldType) {
            value = Float.valueOf(cellValue);
        } else if (BigDecimal.class==fieldType) {
            value = new BigDecimal(cellValue);
        } else if(Integer.class==fieldType){
            value =  Integer.valueOf(cellValue);
        }else if(Long.class==fieldType){
            value =  Long.valueOf(cellValue);
        }else if(Short.class==fieldType){
            value =  Short.valueOf(cellValue);
        }else if(LocalDate.class==fieldType){
            value = DateUtil.strToLocalDate(cellValue,excel.pattern());
        }else if(LocalDateTime.class==fieldType){
            value = DateUtil.strToLocalDateTime(cellValue,excel.pattern());
        }else if(Date.class == fieldType){
            value = DateUtil.strToDate(cellValue,excel.pattern());
        }else{
            value = null;
        }
        return value;
    }

    /**
     * 获取值转换的键值Map，如：男，1
     * @param excel 注解信息
     * @return 值转换map
     */
    public Map<String,String> getKeyMap(Excel excel){
        String [] values=excel.valueConvert().split(",");
        String separate=":";
        Map<String,String> res = new HashMap<>();
        for (String value : values) {
            String [] valArr = value.split(separate);
            res.put(valArr[1],valArr[0]);
        }
        return res;
    }
}
