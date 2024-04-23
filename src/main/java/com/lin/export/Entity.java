package com.lin.export;

import com.lin.annotation.Excel;
import com.lin.imports.CheckInterface;
import com.lin.util.DateUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 实体类
 * @Author: lin
 * @Date: 2021/1/29 17:14
 */
public class Entity implements CheckInterface {

    /**
     * 名称
     */
    @Excel(name = "名字")
    private String name;

    /**
     * 年龄
     */
    @Excel(name = "年龄")
    private Integer age;

    /**
     * 性别
     */
    @Excel(name = "性别",valueConvert = "0:女,1:男,2:未知")
    private Integer sex;

    /**
     * 体重
     */
    @Excel(name = "体重")
    private Double bodyWeight;

    /**
     * 生日
     */
    @Excel(name = "生日", pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    /**
     * 时间
     */
    @Excel(name = "时间",isOptional = true,judgeStr = "isTime", pattern = DateUtil.YYYY_MM_DD_HH_MM)
    private LocalDateTime time;

    /**
     * 检查结果
     */
    private Boolean checkResult;

    /**
     * 检查提示
     */
    private String msg;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Double getBodyWeight() {
        return bodyWeight;
    }

    public void setBodyWeight(Double bodyWeight) {
        this.bodyWeight = bodyWeight;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Boolean getCheckResult() {
        return checkResult;
    }

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
