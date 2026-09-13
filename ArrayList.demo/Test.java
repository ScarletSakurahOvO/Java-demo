import java.util.ArrayList;
public class Test {
  public static void main(String[] args) {
    /*
      Arraylist集合
            空参构造：
            ArrayList()                     创建一个长度为0的集合
            常见方法：
            boolean add(E,e)                添加数据
            void add(int index, E e)        添加数据
            boolean remove(E e)             删除元素
            E remove(int index)             删除元素
            E set(int index,E e)            修改元素
            E get(int index)                获取元素
            int size()                      集合长度
       */

        //1.创建一个长度为0的ArrayList集合
        //  int[] arr = new int[3]; ... int
        //如果，没有进行限定。此时集合里面可以储存任意数据类型的数据
        //泛型：限定集合当中的数据类型  <数据类型>
        //ArrayList list = new ArrList();

        //用泛型去限定集合中能存储什么类型的数据
        //ArrayList<String> list = new ArrayList<String>();
        //list.add("aaa");

        //重复的内容：JDK7的时候，后面的泛型可以省略不写，但是见括号必须保留
        ArrayList<String> list = new ArrayList<>();
        list.add("Java");
        list.add("Python");
        System.out.println(list);
  }
}
