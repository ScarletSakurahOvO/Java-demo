package 数据结构实现;

public class MyArrayList<E> implements MyList<E> {

  //1.定义默认初始容量（通常设为10）
  private static final int DEFAUL_CAPACITY =10;

  //2.底层用来存储数据的数组
  //注意：java不支持直接 new E[10] 创建泛型数组，所以只能用Objesct[]兜底
  private Object[] elementData;

  //3.记录当前数组里实际装了多少个元素，而不是数组的总长度
  private int size;

  //4.构造方法：当你 new MyArrayList() 的时候，会执行这里
  public MyArrayList() {
    //一上来先分配一个长度为10的空数组
    this.elementData = new Object[DEFAUL_CAPACITY];
    this.size = 0;
  }

  //5.grow扩容办法
  private void grow() {
    int oldCapacity = elementData.length;
    //新容量扩容为原来的1.5倍（位运算右移一位相当于除以2）
    int newCapacity = oldCapacity + (oldCapacity >> 1);

    //创建新数组，并将旧数组的内容完整拷贝过去
    Object[] newArray = new Object[newCapacity];
    System.arraycopy(elementData, 0, newArray, 0,oldCapacity);

    //让elementData指向新数组
    elementData = newArray;
  }

  //6.调用checkIndex,辅助方法：检查索引是否越界
  private void checkIndex(int index) {
    if(index < 0 || index >= size) {
      throw new IndexOutOfBoundsException("Index:" + index + ", Size" + size);
    }
  }

  //以下是必须实现的接口方法

  @Override 
  public int size() {
      return this.size; //直接返回实际元素个数
  }

  @Override
  public boolean isEmpty() {
    return this.size == 0; //个数为0就是空
  }

  //留空的几个核心方法，接下来一步步填满他们
  @Override 
  public void add(E element) {
    //1.容量检查：如果实现元素个数达到了数组总长度，触发扩容
    if (size == elementData.length) {
      grow();
    }

    //2.将元素放入当前size的位置，随后size自增1
    elementData[size] = element;
    size++;
  }

  @Override 
  public void add(int index, E element) {
    //1.严格的边界检查
    //注意：这里允许 index == size，因为这代表的最末尾追加一个元素
    if (index < 0 || index > size) {
      throw new IndexOutOfBoundsException("Index:" + index + ", Size" + size);
    }

    //2.空间检查：如果房子满了，先触发扩容
    if(size == elementData.length) {
      grow();
    }

    //3.数据整体向右平移
    //参数含义：（源数组，从哪个位置开始复制，目标数组，复制到哪个新位置，一共复制多少个元素）
    System.arraycopy(elementData,index,elementData,index + 1,size - index);

    //4.将新元素填入腾出来的坑位，并将总数个数size加1
    elementData[index] = element;
    size++;
  }

  @Override 
  public E get(int index) {
    //1.检查索引是否越界，确保index在[0, size-1]范围内
    checkIndex(index);

    //2.从Object[]数组中取出元素，并安全地强制转换为泛型类型 E
    @SuppressWarnings("unchecked")
    E element = (E) elementData[index];
    return element;

  }

  @Override 
  public E remove(int index) {
    //1.严格的边界检查（确保索引在合法境内）
    checkIndex(index);

    //2.暂存被删除的元素，以便方法结束时将其返回
    @SuppressWarnings("unchecked")
    E removedElement = (E) elementData[index];

    //3.计算需要向左平移的元素个数
    int numMoved = size - index - 1;

    //如果被删除的不是最后一个元素，才需要执行数组搬运
    if (numMoved > 0) {
      System.arraycopy(elementData, index + 1, elementData, index, numMoved);
    }

    //4.将原数组的最后一位置为 null（防止内存泄漏），并将size减1
    elementData[--size] = null;

    //5.返回被删除的元素
    return removedElement;
  }
}
