package com.lin.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Stack;

/**
 * @author lin
 * @date 2021/8/27 11:15
 **/
public class CalculateUtil {
    /** 默认小数点位数 */
    public static final int SCALE=2;

    /** 0 */
    public static final BigDecimal ZERO = new BigDecimal("0");

    /** 100 */
    public static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    /** 1 */
    public static final BigDecimal ONE = new BigDecimal("1");

    /**
     * 加
     */
    public static final char ADD = '+';

    public static final Calculate ADD_CALCULATE =  CalculateUtil::add;

    /**
     * 减
     */
    public static final char SUBTRACT = '-';

    public static final Calculate SUBTRACT_CALCULATE =  CalculateUtil::subtract;

    /**
     * 乘
     */
    public static final char MULTIPLY = '*';

    public static final Calculate MULTIPLY_CALCULATE =  (b1, b2) -> CalculateUtil.multiply(b1,b2,10);

    /**
     * 除
     */
    public static final char DIVIDE = '/';

    public static final Calculate DIVIDE_CALCULATE =  (b1, b2) -> CalculateUtil.divide(b1,b2,10);

    /**
     * 左圆括号
     */
    public static final char LEFT_ROUND_BRACKETS = '(';

    /**
     * 右圆括号
     */
    public static final char RIGHT_ROUND_BRACKETS = ')';

    /**
     * 左中括号
     */
    public static final char LEFT_SQUARE_BRACKETS = '[';

    /**
     * 右中括号
     */
    public static final char RIGHT_SQUARE_BRACKETS = ']';

    /**
     * 设置小数位数
     * @param number 数值
     * @param scale 小数位
     * @param roundingMode 取舍模式
     * @return
     */
    public static BigDecimal scale(BigDecimal number,int scale,RoundingMode roundingMode){
        return number.setScale(scale,roundingMode);
    }

    /**
     * 设置小数位数，默认四舍五入
     * @param number 数值
     * @param scale 小数位
     * @return
     */
    public static BigDecimal scale(BigDecimal number,int scale){
        return number.setScale(scale,RoundingMode.HALF_UP);
    }

    /**
     * 类型装换，把Object类型强制转换为目标类型
     * @param obj 要转换的值
     * @return 转换后的类型
     */
    public static BigDecimal transition(Object obj){
        if(obj==null){
            return ZERO;
        }
        if(obj instanceof Integer){
            return new BigDecimal((Integer) obj);
        }
        if(obj instanceof String){
            return new BigDecimal((String) obj);
        }
        if(obj instanceof Double){
            return BigDecimal.valueOf((Double) obj);
        }
        if(obj instanceof Float){
            return BigDecimal.valueOf((Float) obj);
        }
        if(obj instanceof Long){
            return new BigDecimal((Long) obj);
        }
        if(obj instanceof BigDecimal){
            return (BigDecimal) obj;
        }
        return ZERO;
    }

    /**
     * 加法运算
     * @param i1 被加数
     * @param i2 加数
     * @return 和
     */
    public static BigDecimal add(Object i1,Object i2){
        return transition(i1).add(transition(i2));
    }

    /**
     * 减法运算
     * @param i1 被减数
     * @param i2 减数
     * @return 差
     */
    public static BigDecimal subtract(Object i1,Object i2){
        return transition(i1).subtract(transition(i2));
    }

    /**
     * 乘法运算
     * @param i1 被乘数
     * @param i2 乘数
     * @return 积
     */
    public static BigDecimal multiply(Object i1,Object i2){
        return transition(i1).multiply(transition(i2));
    }

    /**
     * 乘法运算
     * @param i1 被乘数
     * @param i2 乘数
     * @param scale 保留小数位数
     * @return 积
     */
    public static BigDecimal multiply(Object i1,Object i2,int scale){
        return multiply(i1,i2,scale,RoundingMode.HALF_UP);
    }

    /**
     * 乘法运算
     * @param i1 被乘数
     * @param i2 乘数
     * @param scale 保留小数位数
     * @param roundingMode 取舍模式
     * @return 积
     */
    public static BigDecimal multiply(Object i1,Object i2,int scale,RoundingMode roundingMode){
        return transition(i1).multiply(transition(i2)).setScale(scale,roundingMode);
    }

    /**
     * 除法运算
     * @param i1 被除数
     * @param i2 除数
     * @param scale 保留小数位数
     * @param roundingMode 取舍方法
     * @return 商
     */
    public static BigDecimal divide(Object i1,Object i2,int scale,RoundingMode roundingMode){
        BigDecimal b2=transition(i2);
        if (ZERO.compareTo(b2) == 0){
            return ZERO;
        }
        return transition(i1).divide(transition(i2),scale,roundingMode);
    }
    /**
     * 除法运算，默认为保留两位小数，且四舍五入
     * @param i1 被除数
     * @param i2 除数
     * @return 商
     */
    public static BigDecimal divide(Object i1,Object i2){
        return divide(i1,i2,SCALE,RoundingMode.HALF_UP);
    }

    /**
     * 除法运算，默认四舍五入
     * @param i1 被除数
     * @param i2 除数
     * @param scale 保留小数位数
     * @return 商
     */
    public static BigDecimal divide(Object i1,Object i2,int scale){
        return divide(i1,i2,scale,RoundingMode.HALF_UP);
    }

    /**
     * 获取百分比（小数）
     * @param b1 占比数
     * @param b2 总数
     * @return
     */
    public static BigDecimal getPercentageByDecimalNumber(Object b1, Object b2){
        return isNull(b1,b2)?ZERO:divide(b1,b2,4);
    }

    /**
     * 获取百分比（乘以100）
     * @param b1 占比数
     * @param b2 总数
     * @return
     */
    public static BigDecimal getPercentageByFullNumber(Object b1, Object b2){
        return isNull(b1,b2)?ZERO:multiply(getPercentageByDecimalNumber(b1, b2),"100",2);
    }

    /**
     * 获取增速（小数）
     * @param nowValue 当前数据
     * @param lastValue 过去数据
     * @return
     */
    public static BigDecimal getGrowthRatioByDecimalNumber(Object nowValue,Object lastValue){
        return isNull(nowValue,lastValue)?ZERO:getPercentageByDecimalNumber(subtract(nowValue,lastValue),lastValue);
    }

    /**
     * 获取增速
     * @param nowValue 当前数据
     * @param lastValue 过去数据
     * @return
     */
    public static BigDecimal getGrowthRatioByFullNumber(Object nowValue, Object lastValue){
        return isNull(nowValue,lastValue)?ZERO:getPercentageByFullNumber(subtract(nowValue,lastValue),lastValue);
    }

    /**
     * 通过当前值与增速获取上期数据
     * @param nowValue
     * @param growth 未乘100的增速
     * @return
     */
    public static BigDecimal getBeforeValueByNowValueAndGrowthRateByDecimalNumber(Object nowValue, Object growth){
        return divide(nowValue,add("1",growth));
    }

    /**
     * 通过当前值与增速获取上期数据
     * @param nowValue
     * @param growth 未乘100的增速
     * @return
     */
    public static BigDecimal getBeforeValueByNowValueAndGrowthRateByFullNumber(Object nowValue, Object growth){
        return divide(nowValue,add("1",divide(growth,ONE_HUNDRED,10)));
    }

    /**
     * 获取百分比字符
     * @param b1 被除数
     * @param b2 除数
     * @return
     */
    public static String getPercentageStr(Object b1,Object b2){
        return getPercentageByFullNumber(b1,b2)+"%";
    }

    /**
     * 大于
     * @param b1
     * @param b2
     * @return
     */
    public static boolean greaterThan(Object b1,Object b2){
        return transition(b1).compareTo(transition(b2)) > 0;
    }

    /**
     * 大于等于
     * @param b1
     * @param b2
     * @return
     */
    public static boolean beEqualOrGreaterThan(Object b1,Object b2){
        return transition(b1).compareTo(transition(b2)) >= 0;
    }

    /**
     * 小于
     * @param b1
     * @param b2
     * @return
     */
    public static boolean lessThan(Object b1,Object b2){
        return transition(b1).compareTo(transition(b2))<0;
    }

    /**
     * 小于等于
     * @param b1
     * @param b2
     * @return
     */
    public static boolean lessThanOrEqualTo(Object b1,Object b2){
        return transition(b1).compareTo(transition(b2))<=0;
    }

    /**
     * 等于
     * @param b1
     * @param b2
     * @return
     */
    public static boolean equal(Object b1,Object b2){
        return b1 != null && b2 != null && transition(b1).compareTo(transition(b2))==0;
    }

    /**
     * 不等于
     * @param b1
     * @param b2
     * @return
     */
    public static boolean unequal(Object b1,Object b2){
        return !equal(b1,b2);
    }

    /**
     * 大于0
     * @param b1
     * @return
     */
    public static boolean greaterThanZero(Object b1){
        return greaterThan(b1,ZERO);
    }

    /**
     * 小于0
     * @param b1
     * @return
     */
    public static boolean lessThanZero(Object b1){
        return lessThan(b1,ZERO);
    }

    /**
     * 小于等于0
     * @param b1
     * @return
     */
    public static boolean lessThanOrEqualZero(Object b1){
        return equal(b1,ZERO) || lessThanZero(b1);
    }

    /**
     * 判断b1是否在b2和b3的范围内（包含等于）
     * @param b1 判断数
     * @param b2 起始数
     * @param b3 结束数
     * @return
     */
    public static boolean between(Object b1,Object b2,Object b3){
        return beEqualOrGreaterThan(b1,b2) && lessThanOrEqualTo(b1,b3);
    }

    /**
     * 判断b1是否在b2和b3的范围内（大于等于b2，小于b3）
     * @param b1 判断数
     * @param b2 起始数
     * @param b3 结束数
     * @return
     */
    public static boolean betweenAndLessThanEnd(Object b1,Object b2,Object b3){
        return beEqualOrGreaterThan(b1,b2) && lessThan(b1,b3);
    }

    /**
     * 判断b1是否在b2和b3的范围内（大于b2，小于等于b3）
     * @param b1 判断数
     * @param b2 起始数
     * @param b3 结束数
     * @return
     */
    public static boolean betweenAndGreaterThanBegin(Object b1,Object b2,Object b3){
        return greaterThan(b1,b2) && lessThanOrEqualTo(b1,b3);
    }

    /**
     * 判断两个范围值是否交叉，也不允许相等
     * @param b1
     * @param b2
     * @param b3
     * @param b4
     * @return
     */
    public static boolean isOverlapOfNotEqual(Object b1, Object b2, Object b3, Object b4){
        return !(lessThan(b4,b1) || greaterThan(b3,b2));
    }

    /**
     * 判断两个范围值是否交叉，b3可以和b2相等
     * @param b1
     * @param b2
     * @param b3
     * @param b4
     * @return
     */
    public static boolean isOverlapOfBeginEqual(Object b1, Object b2, Object b3, Object b4){
        return !(lessThan(b4,b1) || beEqualOrGreaterThan(b3,b2));
    }

    /**
     * 判断两个范围值是否交叉，允许相等
     * @param b1
     * @param b2
     * @param b3
     * @param b4
     * @return
     */
    public static boolean isOverlap(Object b1, Object b2, Object b3, Object b4){
        return !(lessThanOrEqualTo(b4,b1) || beEqualOrGreaterThan(b3,b2));
    }

    /**
     * 获取总和
     * @param numbers
     * @return
     */
    public static BigDecimal getSum(Object... numbers) {
        BigDecimal sum = ZERO;
        for (Object number : numbers){
            sum = add(sum,number);
        }
        return sum;
    }

    /**
     * 获取平均值数组
     * @param total
     * @param avg
     * @return
     */
    public static BigDecimal [] getAvgArray(Object total,int avg){
        BigDecimal avgNumber = divide(total,avg);
        BigDecimal monthActualAmendment = avgNumber;
        //判断是否需要修正数据
        if(!equal(total,multiply(avgNumber,avg))){
            monthActualAmendment = add(monthActualAmendment,subtract(total,multiply(avgNumber,avg)));
        }
        BigDecimal [] res = new BigDecimal[avg];
        for (int i = 0; i < avg; i++){
            if(i+1 == avg){
                res[i] = monthActualAmendment;
                continue;
            }
            res[i] = avgNumber;
        }
        return res;
    }

    /**
     * 判空
     * @param number
     * @return
     */
    public static boolean isNull(Object... number){
        for (Object obj:number) {
            if(obj == null){
                return true;
            }
        }
        return false;
    }

    /**
     * 根据字符串计算，返回结果默认为十位小数，并四舍五入
     * 在遍历到第一个运算符时，会将它入栈。随后继续遍历，将第二个数值入栈。遇到第二个运算符时会根据运算符判断：
     *
     * <p>
     *     假设字符串为 1+2-3。
     *     如果第二个运算符是'+'或者'-'：如果运算符栈不等于空并且运算符栈的栈顶（当前栈顶为第一个运算符'+'）不等于'(''['时，
     *     则先将数值栈内的值[1,2]根据栈顶的运算符（'+'）计算完并将计算结果压入数值栈。
     *     此时数值栈内只有一个[3]。随后再将当前运算符入栈。此时数值栈为[3]运算符栈为['-']。
     *     遍历完字符串后，数值栈为[3,3]运算符栈为[-]
     * </p>
     * <p>
     *     假设字符串为 1+2/3。
     *     如果第二个运算符是'*'或者'/'：如果运算符栈不为空，并且栈顶的运算符等于'*'或'/'时，则先计算栈内的值（这步处理了优先级的问题）。随后将当前运算符入栈。
     *     当当前计算式遍历到'/'时，并不会执行计算，而是会将'/'入栈，此时数值栈为[1,2]运算符栈为['+','/']，遍历完字符串后数值栈为[1,2,3]。
     * </p>
     * 随后方法将会遍历运算符栈，并调用{@link #calc(Stack,Stack)}方法计算结果，计算完成后数值栈内只会有一个结果值，方法会将结果值出栈并返回。
     * @param expression 要计算的字符串
     * @return 计算结果
     */
    public static BigDecimal calculateByStr(String expression) {
        //数值栈
        Stack<BigDecimal> numStack = new Stack<>();
        //运算符栈
        Stack<Character> opStack = new Stack<>();
        //存储运算符前的数值的字符串
        StringBuilder numBuffer = new StringBuilder();
        //运算符前的数值
        BigDecimal num;
        char[] chars = expression.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            //如果是数字，则往numBuffer中拼接字符
            if (c >= '0' && c <= '9' || c == '.') {
                numBuffer.append(c);
                if (i == chars.length - 1) {
                    num = new BigDecimal(numBuffer.toString());
                    numStack.push(num);
                }
            } else {
                if (numBuffer.length() > 0) {
                    num = new BigDecimal(numBuffer.toString());
                    numStack.push(num);
                    numBuffer.setLength(0);
                }
                //如果当前字符等于左圆括号或左中括号则往运算符栈中压入当前字符
                if (c == LEFT_ROUND_BRACKETS || c == LEFT_SQUARE_BRACKETS) {
                    opStack.push(c);
                    //如果当前字符等于右圆括号，则开始计算，直到当前栈顶为左圆括号时停止，并将左圆括号出栈
                } else if (c == RIGHT_ROUND_BRACKETS) {
                    while (opStack.peek() != LEFT_ROUND_BRACKETS) {
                        calc(numStack, opStack);
                    }
                    opStack.pop();
                    //右中括号同理
                } else if (c == RIGHT_SQUARE_BRACKETS) {
                    while (opStack.peek() != LEFT_SQUARE_BRACKETS) {
                        calc(numStack, opStack);
                    }
                    opStack.pop();
                } else if (c == ADD || c == SUBTRACT) {
                    //如果运算符栈不等于空并且运算符栈的栈顶不等于([，则先将栈内的值计算完，并将计算结果入栈。随后再将当前运算符入栈
                    while (!opStack.isEmpty() && opStack.peek() != LEFT_ROUND_BRACKETS && opStack.peek() != LEFT_SQUARE_BRACKETS) {
                        calc(numStack, opStack);
                    }
                    opStack.push(c);
                } else if (c == MULTIPLY || c == DIVIDE) {
                    //如果运算符栈不为空，并且栈顶的运算符等于*或/时，则先计算栈内的值（这步处理了优先级的问题）。随后将当前运算符入栈。
                    while (!opStack.isEmpty() && (opStack.peek() == MULTIPLY || opStack.peek() == DIVIDE)) {
                        calc(numStack, opStack);
                    }
                    opStack.push(c);
                }
            }
        }
        //再进行最后的计算，直到运算符栈为空
        while (!opStack.isEmpty()) {
            calc(numStack, opStack);
        }
        return numStack.pop();
    }

    /**
     * 计算栈中的两个数的结果，将结果入栈并且将栈顶的运算符出栈
     * @param numStack 存储数值的栈
     * @param opStack 存储运算符的栈
     */
    private static void calc(Stack<BigDecimal> numStack, Stack<Character> opStack) {
        BigDecimal num2 = numStack.pop();
        BigDecimal num1 = numStack.pop();
        char op = opStack.pop();
        BigDecimal result = null;
        switch (op) {
            case ADD:
                result = ADD_CALCULATE.calculate(num1,num2);
                break;
            case SUBTRACT:
                result = SUBTRACT_CALCULATE.calculate(num1,num2);
                break;
            case MULTIPLY:
                result = MULTIPLY_CALCULATE.calculate(num1,num2);
                break;
            case DIVIDE:
                result = DIVIDE_CALCULATE.calculate(num1,num2);
                break;
            default:
                break;
        }
        numStack.push(result);
    }

    /**
     * 计算接口，具体计算方式由子类实现
     * @author lin
     * @date 2023/5/10 10:39
     */
    interface Calculate{

        /**
         * 计算方法，由子类自行实现
         * @param b1
         * @param b2
         * @return
         */
        BigDecimal calculate(BigDecimal b1,BigDecimal b2);

        /**
         * 计算方法，调用子类的计算后再设置小数位数，默认为四舍五入
         * @param b1
         * @param b2
         * @param scale
         * @return
         */
        default BigDecimal calculate(BigDecimal b1,BigDecimal b2,int scale){
            return calculate(b1,b2,scale,RoundingMode.HALF_UP);
        }

        /**
         * 计算方法，调用子类的计算后再设置小数位数与取舍方式
         * @param b1
         * @param b2
         * @param scale
         * @param roundingMode
         * @return
         */
        default BigDecimal calculate(BigDecimal b1,BigDecimal b2,int scale,RoundingMode roundingMode){
            return calculate(b1,b2).setScale(scale,roundingMode);
        }
    }

    public static void main(String [] args){
//        Calculator2 s = new Calculator2("4/2");
//        System.out.println(s.calculate(new BigDecimal("8"),new BigDecimal("8")));
        System.out.println(getBeforeValueByNowValueAndGrowthRateByFullNumber(70,-30));
        int i1=1;
        int i2=2;
//        System.out.println(CalculateUtil.getPercentageByDecimalNumber(6,5));
//        System.out.println(CalculateUtil.multiply(10,CalculateUtil.getPercentageByDecimalNumber(5,11),2));
    }
}
