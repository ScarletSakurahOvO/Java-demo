//计算机（支持加减乘除）
//核心思路：接受输入 => 逻辑判断 => 执行计算 => 输出展示
//核心语法： Scanner类 => 基础数据类型 => switch语句 => System.out.prinln()

import java.util.Scanner; //引入 Scanner工具包

public class Calculator {
  public static void main(String[] args) {
    //1.召唤 Scanner 工具，准备接受控制台输入
    Scanner scanner = new Scanner(System.in);

    //2.引导用户输入数据
    System.out.print("请输入第一个数字：");
    double num1 = scanner.nextDouble();

    System.out.print("请输入运算符(+, -, *, /): ");
    char operator = scanner.next().charAt(0);//截取第一个字符串

    System.out.print("请输入第二个数字： ");
    double num2 = scanner.nextDouble();

    double result = 0;//声明一个变量，用来存放最终结果

    //3.核心逻辑判断
    switch (operator) {
      case '+':
        result = num1 + num2;
        break;
        //导师挑战：补全剩下的逻辑
      
      case '-':
        result = num1 - num2;
        break;

      case '*':
        result = num1 * num2;
        break;

      case '/':
        if (num2 == 0) {
          System.out.print("错误：除数不能为零！");
        } else {
          result = num1 / num2;
          //别忘了这里还可以加上输出结果或者 break
        }
        break;

      default:
        //如果用户输入了奇怪的符号
        System.out.println("错误：不认识的运算符！");
        scanner.close();
        return;//提前结束程序
    }
    //4.输出结果
    System.out.println("计算结果是：" + result);

    //好习惯，用完Scanner 后关闭它，释放内存
    scanner.close();
    return;
  }
}