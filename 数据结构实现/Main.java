package 数据结构实现;

public class Main {
  public static void main(String[] args) {
    // 1. 创建对象并进行基础测试
        MyList<String> arrayList = new MyArrayList<>();
        arrayList.add("Java");
        arrayList.add("Python");
        System.out.println("ArrayList 元素个数: " + arrayList.size());
        System.out.println("ArrayList 索引0处的元素: " + arrayList.get(0));

        System.out.println("--------------------");

        MyList<String> linkedList = new MyLinkedList<>();
        linkedList.add("数据结构");
        linkedList.add("算法");
        System.out.println("LinkedList 元素个数: " + linkedList.size());
        System.out.println("LinkedList 索引1处的元素: " + linkedList.get(1));

        // 2. 测试删除与中间插入
        System.out.println("\n====== 测试删除与中间插入 ======");

        // 测试 ArrayList 
        arrayList.add(1, "C++"); // [Java, C++, Python]
        System.out.println("ArrayList 插入后索引1的元素: " + arrayList.get(1)); 
        
        String removedArrayItem = arrayList.remove(0); // 删除 Java
        System.out.println("ArrayList 被删除的元素: " + removedArrayItem);
        System.out.println("ArrayList 删除后索引0的元素: " + arrayList.get(0)); 

        System.out.println("--------------------");

        // 测试 LinkedList
        linkedList.add(1, "计算机网络"); // [数据结构, 计算机网络, 算法]
        System.out.println("LinkedList 插入后索引1的元素: " + linkedList.get(1)); 
        
        String removedLinkedItem = linkedList.remove(2); // 删除 算法
        System.out.println("LinkedList 被删除的元素: " + removedLinkedItem);
        System.out.println("LinkedList 当前元素个数: " + linkedList.size());   
 }
}
