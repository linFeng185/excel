package com.lin.enums;

/**
 * @Author: lin
 * @Date: 2021/2/18 15:29
 */
public enum  FileName {

    /**
     * 文件名和标题的枚举类
     */
    TEST("test","测试标题","data")
    ,ERROR("错误数据","错误数据，请修改后再作提交","data")
    ;

    private final String fileName;

    private final String titleValue;

    private final String sheetName;

    FileName(String fileName,String titleValue,String sheetName){
        this.fileName=fileName;
        this.titleValue=titleValue;
        this.sheetName=sheetName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getTitleValue() {
        return titleValue;
    }

    public String getSheetName() {
        return sheetName;
    }}
