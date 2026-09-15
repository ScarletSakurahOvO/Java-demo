package 数据结构实现;
//列表接口，定义了列表应具备的基本功能规范
//<E>表示泛型，代表这个列表可以装任何引用类型的数据
public interface MyList<E>{

  //在列表都追加一个元素
  void add(E element);

  //在指定索引位置插入一个元素
  void add(int index, E element);

  //获取指定索引位置的元素
  E get(int index);

  //删除指定索引位置的元素，并返回被删除的元素
  E remove(int index);

  //获取当前列表中元素的个数
  int size();

  //判断列表是否为空
  boolean isEmpty();
}
