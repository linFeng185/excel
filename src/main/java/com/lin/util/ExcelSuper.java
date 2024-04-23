package com.lin.util;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * excel处理超类
 *
 * @author: lin
 * @createTime: 2024/4/23:16:30
 */
public class ExcelSuper <T> {

    /**
     * 工作蒲对象
     */
    protected Workbook wb;

    /**
     * 工作表对象
     */
    protected Sheet sheet;

    /**
     * 实体类型
     */
    protected final Class<T> clazz;

    public ExcelSuper(Class<T> clazz) {
        this.clazz = clazz;
    }
}
