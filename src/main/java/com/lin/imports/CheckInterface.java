package com.lin.imports;

/**
 * 校验接口
 *
 * @author: lin
 * @createTime: 2023/12/21:9:58
 */
public interface CheckInterface {

    /**
     * 设置校验结果
     * @param checkResult
     */
    void setCheckResult(Boolean checkResult);

    /**
     * 设置数据不规范的提示
     * @param msg
     */
    CheckInterface setMsg(String msg);

    /**
     * 获取数据不规范的提示
     * @return
     */
    String getMsg();

    /**
     * 设置当前数据行下标
     * @param index
     */
    default void setIndex(Integer index){}

    /**
     * 检查，
     * @return
     */
    default boolean check(){
        boolean res = getMsg() == null;
        setCheckResult(res);
        return res;
    }
}
