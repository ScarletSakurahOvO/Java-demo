public class 数据结构实现 {
  public interface MyList<E> {
    //在列表末尾添加元素
    void add(E element);

    //在指定索引位置插入元素
    void add(int index, E element);

    //获取指定索引位置的元素
    E get(int index);

    //删除指定索引位置的元素并返回被删除的元素
    E remove(int index);

    //返回当前列表中的元素个数
    int size();

    //判断列表是否为空
    boolean isEmpty();
  
    
  }
}
