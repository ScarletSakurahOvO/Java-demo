import java.util.ArrayList;
public class Test2 {
  public static void main(String[] args) {
      /*
      Arraylist集合
            空参构造：
            ArrayList()                     创建一个长度为0的集合
            常见方法：
            boolean add(E,e)                将数据添加到末尾
            void add(int index, E e)        将数据添加到指定位置
            boolean remove(E e)             根据元素删除
            E remove(int index)             根据索引删除
            E set(int index,E e)            将指定位置的数据，修改为新元素
            E get(int index)                获取待定索引的数据
            int size()                      获取集合长度
       */

      //1.创建一个ArrayList集合的对象
      ArrayList<String> list = new ArrayList<>();

      //2.添加数据
      //细节1：ArrayList的的add的方法不管添加什么都会添加成功，忽略返回值即可
      //true: 添加成功  false:添加失败
      //此时add的方法任意情况下，都会添加成功，永远不会失败
      //因为在Java当中，有很多很多的集合 HashSet（元素要唯一）  aaa（true） aaa（false）
      //设计：跟其他的集合保持统一（面向对象的思想）

      //细节2：在集合当中无法直接添加基本数据类型的（byte short int long float double char boolean）
      //      只能添加引用数据类型（对象）
      //      如果在集合里面一定要添加基本数据类型，那么可以转成其对应的包装类（下一个视频）
      list.add("aaa");
      list.add("bbb");
      list.add("ccc");

      //把qqq添加到0索引的位置
      //细节： 
      //    如果集合长度为3，那么下面方法要添加的索引范围只能是0 ~ 3
      //    0 ~ 2 已经存在的索引
      //    3:把当前元素添加到集合的末尾，等同于一个参数的add方法
      //    如果要添加的索引超出了这个范围，程序就会直接报错
      list.add(3,"qqq");


      // boolean remove(E e)             根据元素删除
      //细节：
      //    根据元素的内容进行删除的
      //    存在：删除成功 true
      //    不存在：删除失败 false
      /* boolean res = list.remove("qqq");
      System.out.println(res);*/
      
      System.out.println(list);
      // E remove(int index)             根据索引删除
      //细节：
      //    根据索引进行的删除的
      //    会把被删除的元素进行返回
      //注意：
      //    如果当前的索引不存在，代码会报错
      String res = list.remove(0);
      System.out.println(res);


     

      System.out.println(list);
  }
}
