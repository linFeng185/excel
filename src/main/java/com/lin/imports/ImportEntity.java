package com.lin.imports;

import com.lin.annotation.Excel;
import com.lin.util.DateUtil;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @Author: lin
 * @Date: 2021/2/5 9:51
 */
@Data
public class ImportEntity implements CheckInterface {
    /**
     * 名称
     */
    @Excel(name = "名称", isNotBlank = true,isUnique = true, length = 5)
    private String name;

    /**
     * 年龄
     */
    @Excel(name = "年龄", isNotBlank = true,isUnique = true,compositeIndex = {"名称","年龄"})
    private Long age;

    /**
     * 性别
     */
    @Excel(name = "性别", isNotBlank = true,valueConvert = "0:女,1:男,2:未知")
    private Integer sex;

    /**
     * 体重
     */
    @Excel(name = "体重", isNotBlank = true,length = 11,scale = 2)
    private BigDecimal bodyWeight;

    /**
     * 生日
     */
    @Excel(name = "备注",pattern = DateUtil.YYYY_MM_DD)
    private LocalDate birthday;

    /**
     * 时间
     */
    @Excel(name = "时间",isNotBlank = true,isOptional = true,judgeStr = "isTime", pattern = DateUtil.YYYY_MM_DD_HH_MM, length = 16)
    private LocalDateTime time;

    /**
     * 检查结果
     */
    private Boolean checkResult;

    /**
     * 检查提示
     */
    private String msg;

    /**
     * 设置校验结果
     *
     * @param checkResult
     */
    @Override
    public void setCheckResult(Boolean checkResult) {
        this.checkResult = checkResult;
    }

    /**
     * 设置数据不规范的提示
     *
     * @param msg
     */
    @Override
    public CheckInterface setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    /**
     * 获取数据不规范的提示
     *
     * @return
     */
    @Override
    public String getMsg() {
        return this.msg;
    }
}
