package 数据结构实现;

public class MyLinkedList<E> implements MyList<E> {
  //1.链表的核心属性：车头，车尾，计数器
  private int size = 0;
  private Node<E> first;
  private Node<E> last;

  //2.内部节点类（双向链表的“车厢”）
  private static class Node<E> {
    E item;       //真正存放的数据
    Node<E> next; //指向下一个节点的“引用”
    Node<E> prev; //指向上一个节点的“引用”

    //节点的构造方法
    Node(Node<E> prev, E element, Node<E> next) {
      this.item = element;
      this.next = next;
      this.prev = prev;
    }
  }

  //辅助方法：查找指定索引位置的节点（利用双向的折半搜索提升效率）
  private Node<E> node(int index) {
    //判断index靠前还是靠后
    if (index < (size >> 1)) {
      //靠前：从头节点first向后遍历
      Node<E> x =first;
      for (int i =0; i < index; i++) {
        x = x.next;
      }
      return x;
    } else {
      //靠后：从尾节点last向前遍历
      Node<E> x = last;
      for (int i = size - 1; i > index; i--) {
        x = x.prev;
      }
      return x;
    }
  }

  //3.实现接口的方法（目前先搭个空壳不报错）
  @Override
  public int size() {
    return this.size;
  }

  @Override 
  public void add(E element) {
    //1.暂存当前链表的最后一个节点（旧尾巴）
    final Node<E> l = last;

    //2.创建新节点：prev 指向旧尾巴，item为传入的值，next为null
    final Node<E> newNode = new Node<>(l, element, null);

    //3.将链表的尾指针last更新指向这个新节点
    last = newNode;

    //4.判断原链表是否为空
    if (l == null) {
      //如果原链表为空（旧尾巴是null），说明这是加入的第一个节点，它同时也是头节点
      first = newNode;
    } else {
      //如果原链表不为空，让尾巴的next伸出手拉住新节点
      l.next = newNode;
    }

    //5.元素个数添加 1
    size++;
  }

  @Override 
  public void add(int index, E element) {
    //1.检查索引是否越界（注意：允许index == size,表示在末尾追加）
    if (index < 0 || index > size) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }

    //2.如果能插在末尾，直接复用 add(element)的尾插逻辑
    if (index == size) {
      add(element);
    } else {
      //3.如果插在头部或中间：先找到当前index 位置上的节点（目标节点）
      Node<E> target = node(index);
      Node<E> pred = target.prev; //目标节点原来的前驱节点

      //4.创建新节点，prev指向pred，next指向target
      Node<E> newNode = new Node<>(pred, element, target);

      //5.让目标节点的prev指向新节点
      target.prev = newNode;

      //6.处理前驱节点的执指向
      if (pred == null) {
        //如果pred为null，说明插在了最前面，更新头节点first
        first = newNode;
      } else {
        //否则，让原前驱节点的next指向新节点
        pred.next = newNode;
      }

      //7.元素加1
      size++;
    }
  }

  @Override 
  public E get(int index) {
    //1.检查索引是否越界（必须在 [0, size - 1] 范围内）
    if (index < 0 || index >= size) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }

    //2.调用刚才写好的node(index)找到对应节点，直接返回它的数据
    return node(index).item;
  }

  @Override 
  public E remove(int index) {
    //1.检查索引是否越界（必须在[0, size - 1] 范围内）
    if (index < 0 || index >= size) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }

    //2.借助node(index)找到要被删除的目标节点
    Node<E> target = node(index);
    E element = target.item;     //暂存节点里的数据，最后要返回
    Node<E> next = target.next;  //目标节点的后继节点
    Node<E> prev = target.prev;  //目标节点的前驱节点

    //3.处理前半部分的脱钩（打断target 和 prev 的联系）
    if (prev == null) {
      //如果prev是空，说明删除的是头节点，那么让first直接指向next
      first = next;
    } else {
      //否则，让上一个节点的next绕过target，直接连到下一个节点
      prev.next = next;
      target.prev = null; //切断target向前的引用，帮助垃圾回收
    }

    //4.处理后半部分的脱钩（打断target 和 next 的系统）
    if (next == null) {
      //如果next是空，说明删除的是尾节点，那么让last直接指向prev
      last = prev;
    } else {
      //否则，让下一个节点的prev绕过target，直接连到上一个节点
      next.prev = prev;
      target.next = null;//切断target向后的引用，帮助垃圾回收
    }

    //5.清空节点内部的数据，并使用元素总数减1
    target.item = null;//帮助垃圾回收器彻底回收这个节点
    size--;

    //6.返回刚才暂存的被删除元素
    return element;
  }

  @Override 
  public boolean isEmpty () {
    return size == 0;
  }
}
