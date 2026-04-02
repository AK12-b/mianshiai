# JavaGuide面试突击

Java集合篇



# 基础概念

# 简单介绍⼀下 Java 集合

Java 集合，也叫作容器，主要是由两⼤接⼝派⽣⽽来：⼀个是 Collection 接⼝，主要⽤于存放单⼀元素；另⼀个是 Map 接⼝，主要⽤于存放键值对。对于 Collection 接⼝，下⾯⼜有三个主要的⼦接⼝： List 、 Set 、 Queue 。

Java 集合框架如下图所示：

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/74e63b86-c4d9-4310-a67f-de1260f2f1d2/46222720a92fb5b0a4aec4c4cdde75eba339fdf7e67204780715b8e93b4eaae0.jpg)


![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/74e63b86-c4d9-4310-a67f-de1260f2f1d2/e5334759275687d21b3a47ed2292909de75ebac46f671c1203bba1f70ad75099.jpg)



注：图中只列举了主要的继承派⽣关系，并没有列举所有关系。⽐⽅省略了 AbstractList ,NavigableSet 等抽象类以及其他的⼀些辅助类，如想深⼊了解，可⾃⾏查看源码。


# 说说 List, Set, Queue, Map 四者的区别？

List (对付顺序的好帮⼿): 存储的元素是有序的、可重复的。

Set (注重独⼀⽆⼆的性质): 存储的元素不可重复的。

Queue (实现排队功能的叫号机): 按特定的排队规则来确定先后顺序，存储的元素是有序的、可重复的。

. Map (⽤ key 来搜索的专家): 使⽤键值对（key-value）存储，类似于数学上的函数 $\scriptstyle { \mathsf { y } } = { \mathsf { f } } ( { \mathsf { x } } )$ ，"x"代表 key，"y" 代表 value，key 是⽆序的、不可重复的，value 是⽆序的、可重复的，每个键最多映射到⼀个值。

# List




# ArrayList 和 Array（数组）的区别？

ArrayList 内部基于动态数组实现，⽐ Array （静态数组） 使⽤起来更加灵活：

ArrayList 会根据实际存储的元素动态地扩容或缩容，⽽ Array 被创建之后就不能改变它的⻓度了。

ArrayList 允许你使⽤泛型来确保类型安全， Array 则不可以。

ArrayList 中只能存储对象。对于基本类型数据，需要使⽤其对应的包装类（如 Integer、Double 等）。 Array 可以直接存储基本类型数据，也可以存储对象。

ArrayList ⽀持插⼊、删除、遍历等常⻅操作，并且提供了丰富的 API 操作⽅法，⽐如add() 、 remove() 等。 Array 只是⼀个固定⻓度的数组，只能按照下标访问其中的元素，不具备动态添加、删除元素的能⼒。

ArrayList 创建时不需要指定⼤⼩，⽽ Array 创建时必须指定⼤⼩。

下⾯是⼆者使⽤的简单对⽐：

Array

```txt
// 初始化一个 String 类型的数组
String[] stringArr = new String[]{"hello", "world", !)];
// 修改数组元素的值
stringArr[0] = "goodbye";
System.out.println(Arrs.toString(stringArr)); // [goodbye, world,!] // 删除数组中的元素，需要手动移动后面的元素
for (int i = 0; i < stringArr.length - 1; i++) {
    stringArr[i] = stringArr[i + 1];
}
stringArr[stringArr.length - 1] = null;
System.out.println(Arrs.toString(stringArr)); // [world,!, null]
```

ArrayList

```txt
// 初始化一个 String 类型的 ArrayList
ArrayList<String> stringList = new ArrayList<> (Arrays.asList("hello", "world",
"!")); // 添加元素到 ArrayList 中
stringList.add("goodbye");
System.out.println(stringList); // [hello, world,!, goodbye]
// 修改 ArrayList 中的元素
stringList.set(0, "hi");
System.out.println(stringList); // [hi, world,!, goodbye]
// 删除 ArrayList 中的元素
stringList.remove(0);
System.out.println(stringList); // [world,!, goodbye]
```

# ArrayList 可以添加 null 值吗？

ArrayList 中可以存储任何类型的对象，包括 null 值。不过，不建议向 ArrayList 中添加 null值， null 值⽆意义，会让代码难以维护⽐如忘记做判空处理就会导致空指针异常。

示例代码：

```txt
ArrayList<String> listOfStrings = new ArrayList<>();  
listOfStrings.add(null);  
listOfStrings.add("java");  
System.out.println(listOfStrings);
```

输出：

```json
[null,java]
```

# ArrayList 插⼊和删除元素的时间复杂度？

对于插⼊：

头部插⼊：由于需要将所有元素都依次向后移动⼀个位置，因此时间复杂度是 O(n)。

尾部插⼊：当 ArrayList 的容量未达到极限时，往列表末尾插⼊元素的时间复杂度是 O(1)，因为它只需要在数组末尾添加⼀个元素即可；当容量已达到极限并且需要扩容时，则需要执⾏⼀次 O(n) 的操作将原数组复制到新的更⼤的数组中，然后再执⾏ O(1) 的操作添加元素。

指定位置插⼊：需要将⽬标位置之后的所有元素都向后移动⼀个位置，然后再把新元素放⼊指定位置。这个过程需要移动平均 n/2 个元素，因此时间复杂度为 O(n)。

对于删除：

头部删除：由于需要将所有元素依次向前移动⼀个位置，因此时间复杂度是 O(n)。

尾部删除：当删除的元素位于列表末尾时，时间复杂度为 O(1)。

指定位置删除：需要将⽬标元素之后的所有元素向前移动⼀个位置以填补被删除的空⽩位置，因此需要移动平均 n/2 个元素，时间复杂度为 O(n)。

这⾥简单列举⼀个例⼦：

// ArrayList的底层数组⼤⼩为10，此时存储了7个元素

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/74e63b86-c4d9-4310-a67f-de1260f2f1d2/c27ab37fe0dc0691e6d4240f33a082fd8005912e4fe7b09be80ebb94f8ae1dfa.jpg)


// 在索引为1的位置插⼊⼀个元素8，该元素后⾯的所有元素都要向右移动⼀位

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/74e63b86-c4d9-4310-a67f-de1260f2f1d2/e064a724ea8988be3ce1377467cc9a8da92ffe142adc447d080019fdb3bdf7c1.jpg)


// 删除索引为1的位置的元素，该元素后⾯的所有元素都要向左移动⼀位

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/74e63b86-c4d9-4310-a67f-de1260f2f1d2/2e24274570ac5c7f9393d2ed93dca5c76cd35c497a8d1ae7f0d2709e6911510a.jpg)



# LinkedList 插⼊和删除元素的时间复杂度？

头部插⼊/删除：只需要修改头结点的指针即可完成插⼊/删除操作，因此时间复杂度为 O(1)。

尾部插⼊/删除：只需要修改尾结点的指针即可完成插⼊/删除操作，因此时间复杂度为 O(1)。

指定位置插⼊/删除：需要先移动到指定位置，再修改指定节点的指针完成插⼊/删除，不过由于有头尾指针，可以从较近的指针出发，因此需要遍历平均 $\mathsf { n } / 4$ 个元素，时间复杂度为 O(n)。

这⾥简单列举⼀个例⼦：假如我们要删除节点 9 的话，需要先遍历链表找到该节点。然后，再执⾏相应节点指针指向的更改，具体的源码可以参考：LinkedList 源码分析 。

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/74e63b86-c4d9-4310-a67f-de1260f2f1d2/a0716d03b4086471d0848dcb6b3c6c97b67479fc36a0a60685f9c243dcd004ce.jpg)


LinkedList 为什么不能实现 RandomAccess 接⼝？

RandomAccess 是⼀个标记接⼝，⽤来表明实现该接⼝的类⽀持随机访问（即可以通过索引快速访问元素）。由于 LinkedList 底层数据结构是链表，内存地址不连续，只能通过指针来定位，不⽀持随机快速访问，所以不能实现 RandomAccess 接⼝。

# ArrayList 与 LinkedList 区别?

是否保证线程安全： ArrayList 和 LinkedList 都是不同步的，也就是不保证线程安全；

底层数据结构： ArrayList 底层使⽤的是 Object 数组； LinkedList 底层使⽤的是 双向链表数据结构（JDK1.6 之前为循环链表，JDK1.7 取消了循环。注意双向链表和双向循环链表的区别，下⾯有介绍到！）

插⼊和删除是否受元素位置的影响：

ArrayList 采⽤数组存储，所以插⼊和删除元素的时间复杂度受元素位置的影响。 ⽐如：执⾏ add(E e) ⽅法的时候， ArrayList 会默认在将指定的元素追加到此列表的末尾，这种情况时间复杂度就是 O(1)。但是如果要在指定位置 i 插⼊和删除元素的话（ add(int index, Eelement) ），时间复杂度就为 O(n)。因为在进⾏上述操作的时候集合中第 i 和第 i 个元素之后的(n-i)个元素都要执⾏向后位/向前移⼀位的操作。

。 LinkedList 采⽤链表存储，所以在头尾插⼊或者删除元素不受元素位置的影响（ add(Ee) addFirst(E e) 、 addLast(E e) 、 removeFirst() removeLast() ），时间复杂度为O(1)，如果是要在指定位置 i 插⼊和删除元素的话（ add(int index, E element)remove(Object o) , remove(int index) ）， 时间复杂度为 O(n) ，因为需要先移动到指定位置再插⼊和删除。

是否⽀持快速随机访问： LinkedList 不⽀持⾼效的随机元素访问，⽽ ArrayList （实现了RandomAccess 接⼝） ⽀持。快速随机访问就是通过元素的序号快速获取元素对象(对应于get(int index) ⽅法)。

内存空间占⽤： ArrayList 的空间浪费主要体现在在 list 列表的结尾会预留⼀定的容量空间，⽽ LinkedList 的空间花费则体现在它的每⼀个元素都需要消耗⽐ ArrayList 更多的空间（因为要存放直接后继和直接前驱以及数据）。

我们在项⽬中⼀般是不会使⽤到 LinkedList 的，需要⽤到 LinkedList 的场景⼏乎都可以使⽤ArrayList 来代替，并且，性能通常会更好！就连 LinkedList 的作者约书亚 · 布洛克（JoshBloch）⾃⼰都说从来不会使⽤ LinkedList

另外，不要下意识地认为 LinkedList 作为链表就最适合元素增删的场景。我在上⾯也说了，LinkedList 仅仅在头尾插⼊或者删除元素的时候时间复杂度近似 O(1)，其他情况增删元素的平均时间复杂度都是 O(n) 。

# 补充内容: 双向链表和双向循环链表

双向链表： 包含两个指针，⼀个 prev 指向前⼀个节点，⼀个 next 指向后⼀个节点。


双向链表


![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/74e63b86-c4d9-4310-a67f-de1260f2f1d2/3cde1fa57ac8dd8746c10fd6149dced7c63905aec2f7b8261fd6a9629987d883.jpg)


双向循环链表： 最后⼀个节点的 next 指向 head，⽽ head 的 prev 指向最后⼀个节点，构成⼀个环。


双向循环链表


![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/74e63b86-c4d9-4310-a67f-de1260f2f1d2/d354a611d5acb943faf49d7e7d57343a406e82209feb63d604990902b0bb1d7c.jpg)



补充内容:RandomAccess 接⼝


```txt
public interface RandomAccess { }
```

查看源码我们发现实际上 RandomAccess 接⼝中什么都没有定义。所以，在我看来 RandomAccess接⼝不过是⼀个标识罢了。标识什么？ 标识实现这个接⼝的类具有随机访问功能。

在 binarySearch() ⽅法中，它要判断传⼊的 list 是否 RandomAccess 的实例，如果是，调⽤indexedBinarySearch() ⽅法，如果不是，那么调⽤ iteratorBinarySearch() ⽅法

```java
public static <T> int binarySearch(List<? extends Comparable<? super T>> list, T key) { if (list instanceof RandomAccess || list.size()<BINARYSEARCH_threshold) return CollectionsindexedBinarySearch(list, key); else return Collections.iteratorBinarySearch(list, key); }
```

ArrayList 实现了 RandomAccess 接⼝， ⽽ LinkedList 没有实现。为什么呢？我觉得还是和底层数据结构有关！ ArrayList 底层是数组，⽽ LinkedList 底层是链表。数组天然⽀持随机访问，时间复杂度为 O(1)，所以称为快速随机访问。链表需要遍历到特定位置才能访问特定位置的元素，时间复杂度为 O(n)，所以不⽀持快速随机访问。 ArrayList 实现了 RandomAccess 接⼝，就表明了他具有快速随机访问功能。 RandomAccess 接⼝只是标识，并不是说 ArrayList 实现 RandomAccess 接⼝才具有快速随机访问功能的！

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/74e63b86-c4d9-4310-a67f-de1260f2f1d2/1355c74f13cf027b23c6f063bb27759348037f2f22a6f78f37b620b16ce69837.jpg)


# 说⼀说 ArrayList 的扩容机制吧

详⻅笔主的这篇⽂章: ArrayList 扩容机制分析。

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/74e63b86-c4d9-4310-a67f-de1260f2f1d2/941a214746c20536149dd2d4bda18b606f206ac34f3fa28d4aae2e67d0bcfc08.jpg)


# 集合中的 fail-fast 和 fail-safe 是什么

关于 fail-fast 引⽤ medium 中⼀篇⽂章关于 fail-fast 和 fail-safe 的说法：

Fail-fast systems are designed to immediately stop functioning upon encountering anunexpected condition. This immediate failure helps to catch errors early, making debuggingmore straightforward.

快速失败的思想即针对可能发⽣的异常进⾏提前表明故障并停⽌运⾏，通过尽早的发现和停⽌错误，降低故障系统级联的⻛险。

在 java.util 包下的⼤部分集合是不⽀持线程安全的，为了能够提前发现并发操作导致线程安全⻛险，提出通过维护⼀个 modCount 记录修改的次数，迭代期间通过⽐对预期修改次数expectedModCount 和 modCount 是否⼀致来判断是否存在并发操作，从⽽实现快速失败，由此保证在避免在异常时执⾏⾮必要的复杂代码。

对应的我们给出下⾯这样⼀段在示例，我们⾸先插⼊ 100 个操作元素，⼀个线程迭代元素，⼀个线程删除元素，最终输出结果如愿抛出 ConcurrentModificationException ：

```txt
// 使用线程安全的 CopyOnWriteArrayList 避免 ConcurrentModificationException  
List<Integer> list = new CopyOnWriteArrayList<>();  
CountDownLatch countDownLatch = new CountDownLatch(2);
```

```txt
// 添加元素
```

```javascript
for (int i = 0; i < 100; i++) {  
    list.add(i);
```

}   
Thread t1  $=$  new Thread()  $\rightarrow$  { //迭代元素（注意：Integer是不可变的，这里的  $\mathrm{i + + }$  不会修改list中的值） for（Integeri：list）{ i++;//这行代码实际上没有修改list中的元素 } countDownLatch.countDown();   
}）;   
Threadt2  $=$  new Thread()  $\rightarrow$  { System.out.println("删除元素1"); list.remove(IntegervalueOf(1)); //使用Integer.valueOf(1）删除指定值的对象 countDownLatch.countDown();   
}）;   
t1.start();   
t2.start();   
countDownLatch await();

我们在初始化时插⼊了 100 个元素，此时对应的修改 modCount 次数为 100 ，随后线程 2 在线程 1迭代期间进⾏元素删除操作，此时对应的 modCount 就变为 101 。 线程 1 在随后 foreach 第 2 轮循环发现 modCount 为 101 ，与预期的 expectedModCount(值为100因为初始化插⼊了元素100个) 不等，判定为并发操作异常，于是便快速失败，抛出 ConcurrentModificationException ：

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/74e63b86-c4d9-4310-a67f-de1260f2f1d2/6655625acfb038fc86ebb860fe6b84e52dedf2663bd24f76c78df57a66e30184.jpg)


对此我们也给出 for 循环底层迭代器获取下⼀个元素时的 next ⽅法，可以看到其内部的checkForComodification 具有针对修改次数⽐对的逻辑：

```txt
public E next(){ //检查是否存在并发修改 checkForCommodification(); //...... //返回下一个元素 return (E) elementData[lastRet = i]; } final void checkForCommodification(){ //当前循环遍历次数和预期修改次数不一致时，就会抛出 ConcurrentModificationException if (modCount != expectedModCount) throw new ConcurrentModificationException(); }
```

⽽ fail-safe 也就是安全失败的含义，它旨在即使⾯对意外情况也能恢复并继续运⾏，这使得它特别适⽤于不确定或者不稳定的环境：

Fail-safe systems take a different approach, aiming to recover and continue even in the faceof unexpected conditions. This makes them particularly suited for uncertain or volatileenvironments.

该思想常运⽤于并发容器，最经典的实现就是 CopyOnWriteArrayList 的实现，通过写时复制的思想保证在进⾏修改操作时复制出⼀份快照，基于这份快照完成添加或者删除操作后，将

CopyOnWriteArrayList 底层的数组引⽤指向这个新的数组空间，由此避免迭代时被并发修改所⼲扰所导致并发操作安全问题，当然这种做法也存在缺点，即进⾏遍历操作时⽆法获得实时结果：

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/74e63b86-c4d9-4310-a67f-de1260f2f1d2/d3991354b1f2a95c3de323070d73e1b510aaf15d979ee8acd50944fba754c725.jpg)


对应我们也给出 CopyOnWriteArrayList 实现 fail-safe 的核⼼代码，可以看到它的实现就是通过getArray 获取数组引⽤然后通过 Arrays.copyOf 得到⼀个数组的快照，基于这个快照完成添加操作后，修改底层 array 变量指向的引⽤地址由此完成写时复制：

public boolean add(E e) { final ReentrantLock lock  $=$  this.lock; lock.lock(); try { //获取原有数组 Object[]elements  $\equiv$  getArray(); int len  $\equiv$  elements.length;

```txt
//基于原有数组复制出一份内存快照
Object[] newElements = Arrays.Ofelements, len + 1);
//进行添加操作
newElements[len] = e;
//array指向新的数组
setArray(newElements);
return true;
} finally {
lock.unlock();
}
}
```

# Set

# Comparable 和 Comparator 的区别

Comparable 接⼝和 Comparator 接⼝都是 Java 中⽤于排序的接⼝，它们在实现类对象之间⽐较⼤⼩、排序等⽅⾯发挥了重要作⽤：

. Comparable 接⼝实际上是出⾃ java.lang 包 它有⼀个 compareTo(Object obj) ⽅法⽤来排序

. Comparator 接⼝实际上是出⾃ java.util 包它有⼀个 compare(Object obj1, Object obj2) ⽅法⽤来排序

⼀般我们需要对⼀个集合使⽤⾃定义排序时，我们就要重写 compareTo() ⽅法或 compare() ⽅法，当我们需要对某⼀个集合实现两种排序⽅式，⽐如⼀个 song 对象中的歌名和歌⼿名分别采⽤⼀种排序⽅法的话，我们可以重写 compareTo() ⽅法和使⽤⾃制的 Comparator ⽅法或者以两个 Comparator来实现歌名排序和歌星名排序，第⼆种代表我们只能使⽤两个参数版的 Collections.sort() .

# Comparator 定制排序

```txt
ArrayList<Integer> ArrayList = new ArrayList<Integer>();   
ArrayList.add(-1);   
ArrayList.add(3);   
ArrayList.add(3);   
ArrayList.add(-5);   
ArrayList.add(7);
```

```java
arrayList.add(4);   
arrayList.add(-9);   
arrayList.add(-7);   
System.out.println("原始数组：");   
System.out.println(arrayList);   
//void reverse(List list):反转   
Collections.reverse(arrayList);   
System.out.println("Collections.reverse(arrayList):");   
System.out.println(arrayList);   
//void sort(List list),按自然排序的升序排序   
Collections.sort(arrayList);   
System.out.println("Collections.sort(arrayList):");   
System.out.println(arrayList);   
//定制排序的用法   
Collections.sort(arrayList, new Comparator<Integer>(   ) { @Override public int compare(Integer o1, Integer o2) \{ return o2 compareTo(o1); }   
}）;   
System.out.println("定制排序后：");   
System.out.println(arrayList);
```

Output:

原始数组:

```txt
[-1，3，3，-5，7，4，-9，-7] Collections.reverse(arrayList): [-7，-9，4，7，-5，3，3，-1] Collections.sort(arrayList): [-9，-7，-5，-1，3，3，4，7] 定制排序后： [7，4，3，3，-1，-5，-7，-9]
```

重写 compareTo ⽅法实现按年龄来排序

person对象没有实现Comparable接⼝，所以必须实现，这样才不会出错，才可以使treemap中的数据按顺序排列

前⾯⼀个例⼦的String类已经默认实现了Comparable接⼝，详细可以查看String类的API⽂档，另外其他

// 像Integer类等都已经实现了Comparable接⼝，所以不需要另外实现了

```txt
public class Person implements Comparable<Person> { private String name; private int age;
```

```java
public Person(String name, int age) { super(); this.name = name; this.age = age; }
```

```java
public String getName() { return name; }
```

public void setName(String name) { this.name  $=$  name; }

```txt
public int getAge(){ return age;
```

```txt
public void setAge(int age) { this.age = age; }
```

```txt
***  
* T重写compareTo方法实现按年龄来排序  
*/
```

@override   
public int compareTo(Person o) { if (this.age  $>$  o.getAge()) { return 1;

```java
} if (this.age < o.getAge()) { return -1; } return 0; } public static void main(String[] args) { TreeMap<Person, String> pdata = new TreeMap<Person, String>(); pdata.put(new Person("张三", 30), "zhangsan"); pdata.put(new Person("李四", 20), "lisi"); pdata.put(new Person("王五", 10), "wangwu"); pdata.put(new Person("小红", 5), "xiaohong"); // 得到key的值的同时得到key所对应的值 Set<Person> keys = pdata.keySet(); for (Person key : keys) { System.out.println(key.getAge() + "-" + key.getName()); } }
```

Output：

5-⼩红

10-王五

20-李四

30-张三

# ⽐较 HashSet、LinkedHashSet 和 TreeSet 三者的异同

. HashSet 、 LinkedHashSet 和 TreeSet 都是 Set 接⼝的实现类，都能保证元素唯⼀，并且都不是线程安全的。

. HashSet 、 LinkedHashSet 和 TreeSet 的主要区别在于底层数据结构不同。 HashSet 的底层数据结构是哈希表（基于 HashMap 实现）。 LinkedHashSet 的底层数据结构是链表和哈希表，元素的插⼊和取出顺序满⾜ FIFO。 TreeSet 底层数据结构是红⿊树，元素是有序的，排序的⽅式有⾃然排序和定制排序。

底层数据结构不同⼜导致这三者的应⽤场景不同。 HashSet ⽤于不需要保证元素插⼊和取出顺序的场景， LinkedHashSet ⽤于保证元素的插⼊和取出顺序满⾜ FIFO 的场景， TreeSet ⽤于⽀持对元素⾃定义排序规则的场景。

# Queue

# Queue 与 Deque 的区别

Queue 是单端队列，只能从⼀端插⼊元素，另⼀端删除元素，实现上⼀般遵循 先进先出（FIFO）规则。

Queue 扩展了 Collection 的接⼝，根据 因为容量问题⽽导致操作失败后处理⽅式的不同 可以分为两类⽅法: ⼀种在操作失败后会抛出异常，另⼀种则会返回特殊值。

<table><tr><td>Queue 接口</td><td>抛出异常</td><td>返回特殊值</td></tr><tr><td>插入队尾</td><td>add(E e)</td><td>offer(E e)</td></tr><tr><td>删除队首</td><td>remove()</td><td>poll()</td></tr><tr><td>查询队首元素</td><td>element()</td><td>peek()</td></tr></table>

Deque 是双端队列，在队列的两端均可以插⼊或删除元素。

Deque 扩展了 Queue 的接⼝, 增加了在队⾸和队尾进⾏插⼊和删除的⽅法，同样根据失败后处理⽅式的不同分为两类：

<table><tr><td>Deque 接口</td><td>抛出异常</td><td>返回特殊值</td></tr><tr><td>插入队首</td><td>addFirst(E e)</td><td>offerFirst(E e)</td></tr><tr><td>插入队尾</td><td>addLast(E e)</td><td>offerLast(E e)</td></tr><tr><td>删除队首</td><td>removeFirst()</td><td>pollFirst()</td></tr><tr><td>删除队尾</td><td>removeLast()</td><td>pollLast()</td></tr><tr><td>查询队首元素</td><td>getFirst()</td><td>peekFirst()</td></tr><tr><td>查询队尾元素</td><td>getLast()</td><td>peekLast()</td></tr></table>

事实上， Deque 还提供有 push() 和 pop() 等其他⽅法，可⽤于模拟栈。

# ArrayDeque 与 LinkedList 的区别

ArrayDeque 和 LinkedList 都实现了 Deque 接⼝，两者都具有队列的功能，但两者有什么区别呢？

ArrayDeque 是基于可变⻓的数组和双指针来实现，⽽ LinkedList 则通过链表来实现。

ArrayDeque 不⽀持存储 NULL 数据，但 LinkedList ⽀持。

ArrayDeque 是在 JDK1.6 才被引⼊的，⽽ LinkedList 早在 JDK1.2 时就已经存在。

ArrayDeque 插⼊时可能存在扩容过程, 不过均摊后的插⼊操作依然为 O(1)。虽然 LinkedList不需要扩容，但是每次插⼊数据时均需要申请新的堆空间，均摊性能相⽐更慢。

从性能的⻆度上，选⽤ ArrayDeque 来实现队列要⽐ LinkedList 更好。此外， ArrayDeque 也可以⽤于实现栈。

# 说⼀说 PriorityQueue

PriorityQueue 是在 JDK1.5 中被引⼊的, 其与 Queue 的区别在于元素出队顺序是与优先级相关的，即总是优先级最⾼的元素先出队。

这⾥列举其相关的⼀些要点：

. PriorityQueue 利⽤了⼆叉堆的数据结构来实现的，底层使⽤可变⻓的数组来存储数据

. PriorityQueue 通过堆元素的上浮和下沉，实现了在 O(logn) 的时间复杂度内插⼊元素和删除堆顶元素。

PriorityQueue 是⾮线程安全的，且不⽀持存储 NULL 和 non-comparable 的对象。

. PriorityQueue 默认是⼩顶堆，但可以接收⼀个 Comparator 作为构造参数，从⽽来⾃定义元素优先级的先后。

PriorityQueue 在⾯试中可能更多的会出现在⼿撕算法的时候，典型例题包括堆排序、求第 K ⼤的数、带权图的遍历等，所以需要会熟练使⽤才⾏。

# 什么是 BlockingQueue？

BlockingQueue （阻塞队列）是⼀个接⼝，继承⾃ Queue 。 BlockingQueue 阻塞的原因是其⽀持当队列没有元素时⼀直阻塞，直到有元素；还⽀持如果队列已满，⼀直等到队列可以放⼊新元素时再放⼊。

```typescript
public interface BlockingQueue<E> extends Queue<E> { // ... }
```

BlockingQueue 常⽤于⽣产者-消费者模型中，⽣产者线程会向队列中添加数据，⽽消费者线程会从队列中取出数据进⾏处理。

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/74e63b86-c4d9-4310-a67f-de1260f2f1d2/27bd1bcf7e6a5d54abd88b08bb8afaf009ddbb0e184f0d94202f5fc9d42437c1.jpg)


# BlockingQueue 的实现类有哪些？

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/74e63b86-c4d9-4310-a67f-de1260f2f1d2/10b3d7ed1a3068349c065fe80b7bc0fbbd8a4dbf47d961644dc54c0f06d7addd.jpg)


Java 中常⽤的阻塞队列实现类有以下⼏种：

1. ArrayBlockingQueue ：使⽤数组实现的有界阻塞队列。在创建时需要指定容量⼤⼩，并⽀持公平和⾮公平两种⽅式的锁访问机制。

2. LinkedBlockingQueue ：使⽤单向链表实现的可选有界阻塞队列。在创建时可以指定容量⼤⼩，如果不指定则默认为 Integer.MAX_VALUE 。和 ArrayBlockingQueue 不同的是， 它仅⽀持⾮公平的锁访问机制。

3. PriorityBlockingQueue ：⽀持优先级排序的⽆界阻塞队列。元素必须实现 Comparable 接⼝或者在构造函数中传⼊ Comparator 对象，并且不能插⼊ null 元素。

4. SynchronousQueue ：同步队列，是⼀种不存储元素的阻塞队列。每个插⼊操作都必须等待对应的删除操作，反之删除操作也必须等待插⼊操作。因此， SynchronousQueue 通常⽤于线程之间的直接传递数据。

5. DelayQueue ：延迟队列，其中的元素只有到了其指定的延迟时间，才能够从队列中出队。

6.

⽇常开发中，这些队列使⽤的其实都不多，了解即可。

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/74e63b86-c4d9-4310-a67f-de1260f2f1d2/8338537902ef095ec7d602ede06c65c6c5b4901954041a9c25cad02a55ef6fbd.jpg)


# ArrayBlockingQueue 和 LinkedBlockingQueue 有什么区别？

ArrayBlockingQueue 和 LinkedBlockingQueue 是 Java 并发包中常⽤的两种阻塞队列实现，它们都是线程安全的。不过，不过它们之间也存在下⾯这些区别：

底层实现： ArrayBlockingQueue 基于数组实现，⽽ LinkedBlockingQueue 基于链表实现。

是否有界： ArrayBlockingQueue 是有界队列，必须在创建时指定容量⼤⼩。LinkedBlockingQueue 创建时可以不指定容量⼤⼩，默认是 Integer.MAX_VALUE ，也就是⽆界的。但也可以指定队列⼤⼩，从⽽成为有界的。

锁是否分离： ArrayBlockingQueue 中的锁是没有分离的，即⽣产和消费⽤的是同⼀个锁；LinkedBlockingQueue 中的锁是分离的，即⽣产⽤的是 putLock ，消费是 takeLock ，这样可以防⽌⽣产者和消费者线程之间的锁争夺。

内存占⽤： ArrayBlockingQueue 需要提前分配数组内存，⽽ LinkedBlockingQueue 则是动态分配链表节点内存。这意味着， ArrayBlockingQueue 在创建时就会占⽤⼀定的内存空间，且往往申请的内存⽐实际所⽤的内存更⼤，⽽ LinkedBlockingQueue 则是根据元素的增加⽽逐渐占⽤内存空间。

# Map （重要）

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/74e63b86-c4d9-4310-a67f-de1260f2f1d2/32cad80b40147d368da171138a3301ca42796235be7f8beaddd92a3125f7857e.jpg)


# HashMap 和 Hashtable 的区别

线程是否安全： HashMap 是⾮线程安全的， Hashtable 是线程安全的,因为 Hashtable 内部的⽅法基本都经过 synchronized 修饰。（如果你要保证线程安全的话就使⽤ ConcurrentHashMap吧！）；

效率： 因为线程安全的问题， HashMap 要⽐ Hashtable 效率⾼⼀点。另外， Hashtable 基本被淘汰，不要在代码中使⽤它；

对 Null key 和 Null value 的⽀持： HashMap 可以存储 null 的 key 和 value，但 null 作为键只能有⼀个，null 作为值可以有多个；Hashtable 不允许有 null 键和 null 值，否则会抛出NullPointerException 。

初始容量⼤⼩和每次扩充容量⼤⼩的不同： $\textcircled{1}$ 创建时如果不指定容量初始值， Hashtable 默认的初始⼤⼩为 11，之后每次扩充，容量变为原来的 $_ { 2 \mathsf { n } + 1 }$ 。 HashMap 默认的初始化⼤⼩为16。之后每次扩充，容量变为原来的 2 倍。 $\textcircled{2}$ 创建时如果给定了容量初始值，那么 Hashtable会直接使⽤你给定的⼤⼩，⽽ HashMap 会将其扩充为 2 的幂次⽅⼤⼩（ HashMap 中的tableSizeFor() ⽅法保证，下⾯给出了源代码）。也就是说 HashMap 总是使⽤ 2 的幂作为哈希表的⼤⼩,后⾯会介绍到为什么是 2 的幂次⽅。

底层数据结构： JDK1.8 以后的 HashMap 在解决哈希冲突时有了较⼤的变化，当链表⻓度⼤于阈值（默认为 8）时，将链表转化为红⿊树（将链表转换成红⿊树前会判断，如果当前数组的⻓度⼩于 64，那么会选择先进⾏数组扩容，⽽不是转换为红⿊树），以减少搜索时间（后⽂中我会结合源码对这⼀过程进⾏分析）。 Hashtable 没有这样的机制。

哈希函数的实现： HashMap 对哈希值进⾏了⾼位和低位的混合扰动处理以减少冲突，⽽Hashtable 直接使⽤键的 hashCode() 值。

# HashMap 中带有初始容量的构造函数：

```txt
public HashMap(int initialCapacity, float loadFactor) {
    if (initialCapacity < 0)
        throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
    if (initialCapacity > MAXIMUM_CAPACITY)
        initialCapacity = MAXIMUM_CAPACITY;
    if (loadFactor <= 0 || Float.isNaN(loadFactor))
        throw new IllegalArgumentException("Illegal load factor: " + loadFactor);
    this.loadFactor = loadFactor;
    this.threshold = tableSizeFor(initialCapacity);
```

```java
} public HashMap(int initialCapacity) { this(initialCapacity，DEFAULT_LOAD_FACTOR); }
```

下⾯这个⽅法保证了 HashMap 总是使⽤ 2 的幂作为哈希表的⼤⼩。

```c
/**
* Returns a power of two size for the given target capacity.
*/
static final int tableSizeFor(int cap) {
    int n = cap - 1;
    n |= n >>> 1;
    n |= n >>> 2;
    n |= n >>> 4;
    n |= n >>> 8;
    n |= n >>> 16;
    return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
}
```

# HashMap 和 HashSet 区别

如果你看过 HashSet 源码的话就应该知道： HashSet 底层就是基于 HashMap 实现的。

（ HashSet 的源码⾮常⾮常少，因为除了 clone() 、 writeObject() 、 readObject() 是 HashSet ⾃⼰不得不实现之外，其他⽅法都是直接调⽤ HashMap 中的⽅法。

<table><tr><td colspan="2">HashMap</td><td colspan="2">HashSet</td></tr><tr><td colspan="2">实现了 Map 接口</td><td colspan="2">实现 Set 接口</td></tr><tr><td colspan="2">存储键值对</td><td colspan="2">仅存储对象</td></tr><tr><td colspan="2">调用 put() 向 map 中添加元素</td><td colspan="2">调用 add() 方法向 Set 中添加元素</td></tr><tr><td rowspan="2" colspan="2">HashMap 使用键(Key) 计算hashcode</td><td colspan="2">HashSet 使用成员对象来计算 hashcode 值，对于两个对象来说</td></tr><tr><td colspan="2">hashcode 可能相同，所以 equals() 方法用来判断对象的相等性</td></tr></table>

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/74e63b86-c4d9-4310-a67f-de1260f2f1d2/87909581cfd5947f7b8f7801e797fbcc0fa4f1d559e84dbda908d3ac885ff009.jpg)


# HashMap 和 TreeMap 区别

TreeMap 和 HashMap 都继承⾃ AbstractMap ，但是需要注意的是 TreeMap 它还实现了NavigableMap 接⼝和 SortedMap 接⼝。

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/74e63b86-c4d9-4310-a67f-de1260f2f1d2/a927a77ae6873912c4832aabc1857583b2935a49e8ebe4f8196d0c393e0e1545.jpg)


实现 NavigableMap 接⼝让 TreeMap 有了对集合内元素的搜索的能⼒。

NavigableMap 接⼝提供了丰富的⽅法来探索和操作键值对:

1. 定向搜索: ceilingEntry() , floorEntry() , higherEntry() 和 lowerEntry() 等⽅法可以⽤于定位⼤于等于、⼩于等于、严格⼤于、严格⼩于给定键的最接近的键值对。

2. ⼦集操作: subMap() , headMap() 和 tailMap() ⽅法可以⾼效地创建原集合的⼦集视图，⽽⽆需复制整个集合。

3. 逆序视图: descendingMap() ⽅法返回⼀个逆序的 NavigableMap 视图，使得可以反向迭代整个TreeMap 。

4. 边界操作: firstEntry() , lastEntry() , pollFirstEntry() 和 pollLastEntry() 等⽅法可以⽅便地访问和移除元素。

这些⽅法都是基于红⿊树数据结构的属性实现的，红⿊树保持平衡状态，从⽽保证了搜索操作的时间复杂度为 O(log n)，这让 TreeMap 成为了处理有序集合搜索问题的强⼤⼯具。

实现 SortedMap 接⼝让 TreeMap 有了对集合中的元素根据键排序的能⼒。默认是按 key 的升序排序，不过我们也可以指定排序的⽐较器。示例代码如下：

```java
/** 
* @author shuang.kou 
* @createTime 2020年06月15日 17:02:00 
*/ 
public class Person {
    private Integer age;
    public Person(Integer age) {
        this.age = age;
    }
    public Integer getAge() {
        return age;
    }
    public static void main(String[] args) {
        TreeMap<Person, String> treeMap = new TreeMap<> (new Comparator<Person> () {
            @Override
            public int compare(Person person1, Person person2) {
                int num = person1.getAge() - person2.getAge();
                return Integer.compare(num, 0);
            }
        });
        treeMap.put(new Person(3), "person1");
        treeMap.put(new Person(18), "person2");
```

```txt
treeMap.put(new Person(35), "person3");
treeMap.put(new Person(16), "person4");
treeMap entrySet().stream().forEach(personStringEntry -> {
    System.out.println(personStringEntry.getValue());
});
});
```

输出:

```txt
person1   
person4   
person2   
person3
```

可以看出， TreeMap 中的元素已经是按照 Person 的 age 字段的升序来排列了。

上⾯，我们是通过传⼊匿名内部类的⽅式实现的，你可以将代码替换成 Lambda 表达式实现的⽅式：

```java
TreeMap<person, String> treeMap = new TreeMap<>((person1, person2) -> {
    int num = person1.getAge() - person2.getAge();
    return Integer.compare(num, 0);
});
```

综上，相⽐于 HashMap 来说， TreeMap 主要多了对集合中的元素根据键排序的能⼒以及对集合内元素的搜索的能⼒。

# HashSet 如何检查重复?

以下内容摘⾃我的 Java 启蒙书《Head first java》第⼆版：

当你把对象加⼊ HashSet 时， HashSet 会先计算对象的 hashcode 值来判断对象加⼊的位置，同时也会与其他加⼊的对象的 hashcode 值作⽐较，如果没有相符的 hashcode ， HashSet 会假设对象没有重复出现。但是如果发现有相同 hashcode 值的对象，这时会调⽤ equals() ⽅法来检查 hashcode 相等的对象是否真的相同。如果两者相同， HashSet 就不会让加⼊操作成功。

在 JDK1.8 中， HashSet 的 add() ⽅法只是简单的调⽤了 HashMap 的 put() ⽅法，并且判断了⼀下返回值以确保是否有重复元素。直接看⼀下 HashSet 中的源码：

```java
// Returns: true if this set did not already contain the specified element  
// 返回值：当 set 中没有包含 add 的元素时返回真  
public boolean add(E e) {  
    return map.put(e, PRESENT) == null;  
}
```

⽽在 HashMap 的 putVal() ⽅法中也能看到如下说明：

```txt
// Returns : previous value, or null if none  
// 返回值：如果插入位置没有元素返回null，否则返回上一个元素  
final V putVal(int hash, K key, V value, boolean onlyIfAbsent, boolean evict) {  
    ...  
}
```

也就是说，在 JDK1.8 中，实际上⽆论 HashSet 中是否已经存在了某元素， HashSet 都会直接插⼊，只是会在 add() ⽅法的返回值处告诉我们插⼊前是否存在相同元素。

# HashMap 的底层实现

JDK1.8 之前

JDK1.8 之前 HashMap 底层是 数组和链表 结合在⼀起使⽤也就是 链表散列。HashMap 通过 key的 hashcode 经过扰动函数处理过后得到 hash 值，然后通过 (n - 1) & hash 判断当前元素存放的位置（这⾥的 n 指的是数组的⻓度），如果当前位置存在元素的话，就判断该元素与要存⼊的元素的hash 值以及 key 是否相同，如果相同的话，直接覆盖，不相同就通过拉链法解决冲突。

HashMap 中的扰动函数（ hash ⽅法）是⽤来优化哈希值的分布。通过对原始的 hashCode() 进⾏额外处理，扰动函数可以减⼩由于糟糕的 hashCode() 实现导致的碰撞，从⽽提⾼数据的分布均匀性。

# JDK 1.8 HashMap 的 hash ⽅法源码:

JDK 1.8 的 hash ⽅法 相⽐于 JDK 1.7 hash ⽅法更加简化，但是原理不变。

```txt
static final int hash(Object key) {  
    int h;  
    // key.hashCode(): 返回散列值也就是hashCode  
    // ^: 按位异或  
    // >>>: 无符号右移，忽略符号位，空位都以0补齐  
    return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);  
}
```

对⽐⼀下 JDK1.7 的 HashMap 的 hash ⽅法源码.

static int hash(int h) { // This function ensures that memcmp codes that differ only by // constant multiples at each bit position have a bounded // number of collisions (approximately 8 at default load factor).  $\mathrm{h}^{\wedge} = (\mathrm{h} > > > 20)^{\wedge}(\mathrm{h} > > > 12);$  return h ^ (h >>> 7) ^ (h >>> 4); }

相⽐于 JDK1.8 的 hash ⽅法 ，JDK 1.7 的 hash ⽅法的性能会稍差⼀点点，因为毕竟扰动了 4 次。

所谓 “拉链法” 就是：将链表和数组相结合。也就是说创建⼀个链表数组，数组中每⼀格就是⼀个链表。若遇到哈希冲突，则将冲突的值加到链表中即可。

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/74e63b86-c4d9-4310-a67f-de1260f2f1d2/086d792b6ab1a1960c21bbe34fbf3b868f51511c3fc6488cd81126ca86b48ab1.jpg)


# JDK1.8 之后

相⽐于之前的版本， JDK1.8 之后在解决哈希冲突时有了较⼤的变化，当链表⻓度⼤于阈值（默认为 8）（将链表转换成红⿊树前会判断，如果当前数组的⻓度⼩于 64，那么会选择先进⾏数组扩容，⽽不是转换为红⿊树）时，将链表转化为红⿊树。

这样做的⽬的是减少搜索时间：链表的查询效率为 O(n)（n 是链表的⻓度），红⿊树是⼀种⾃平衡⼆叉搜索树，其查询效率为 O(log n)。当链表较短时，O(n) 和 O(log n) 的性能差异不明显。但当链表变⻓时，查询性能会显著下降。

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/74e63b86-c4d9-4310-a67f-de1260f2f1d2/9b151e010d2482a443085c33cdf835873ebb79ef245a2fce49fc9bdf471f102b.jpg)


为什么优先扩容⽽⾮直接转为红⿊树？

数组扩容能减少哈希冲突的发⽣概率（即将元素重新分散到新的、更⼤的数组中），这在多数情况下⽐直接转换为红⿊树更⾼效。

红⿊树需要保持⾃平衡，维护成本较⾼。并且，过早引⼊红⿊树反⽽会增加复杂度。

# 为什么选择阈值 8 和 64？

1. 泊松分布表明，链表⻓度达到 8 的概率极低（⼩于千万分之⼀）。在绝⼤多数情况下，链表⻓度都不会超过 8。阈值设置为 8，可以保证性能和空间效率的平衡。

2. 数组⻓度阈值 64 同样是经过实践验证的经验值。在⼩数组中扩容成本低，优先扩容可以避免过早引⼊红⿊树。数组⼤⼩达到 64 时，冲突概率较⾼，此时红⿊树的性能优势开始显现。

TreeMap、TreeSet 以及 JDK1.8 之后的 HashMap 底层都⽤到了红⿊树。红⿊树就是为了解决⼆叉查找树的缺陷，因为⼆叉查找树在某些情况下会退化成⼀个线性结构。

我们来结合源码分析⼀下 HashMap 链表到红⿊树的转换。

# 1、 putVal ⽅法中执⾏链表转红⿊树的判断逻辑。

链表的⻓度⼤于 8 的时候，就执⾏ treeifyBin （转换红⿊树）的逻辑。

```javascript
//遍历链表  
for (int binCount = 0; ; ++binCount) {  
//遍历到链表最后一个节点  
if ((e = p.next) == null) {  
p.next = newNodeHash, key, value, null);  
//如果链表元素个数大于TREETHY_THRESHOLD（8）  
if (binCount >= TREETHYreshold - 1) // -1 for lst  
//红黑树转换（并不会直接转换成红黑树）  
treeifyBin_tab, hash);  
break;  
}  
if (e.hash == hash && ((k = e.key) == key || (key != null && key.equals(k))))  
break;  
p = e;
```

# 2、 treeifyBin ⽅法中判断是否真的转换为红⿊树。

```txt
final void treeifyBin(Node<K,V>[ ] tab, int hash) {
    int n, index; Node<K,V> e;
    // 判断当前数组的长度是否小于 64
    if (tab == null || (n = tab.length) < MIN_treeIFY_CAPACITY)
        // 如果当前数组的长度小于 64，那么会选择先进行数组扩容
        resize();
    else if ((e = tab[index == (n - 1) & hash]) != null) {
        // 否则才将列表转换为红黑树
        TreeNode<K,V> hd = null, tl = null;
        do {
            TreeNode<K,V> p = replacementTreeNode(e, null);
            if (tl == null)
                hd = p;
            else {
                pprev = tl;
                tl.next = p;
            }
            t1 = p;
        } while ((e = e.next) != null);
        if ((tab[index] = hd) != null)
            hd.treeify_tab);
        }
    }
}
```

将链表转换成红⿊树前会判断，如果当前数组的⻓度⼩于 64，那么会选择先进⾏数组扩容，⽽不是转换为红⿊树。

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/74e63b86-c4d9-4310-a67f-de1260f2f1d2/a54bdf039bd89862b32ea4fbff05277955b5bb045f7e0a957a5b39fc07657bcc.jpg)


# HashMap 的⻓度为什么是 2 的幂次⽅

为了让 HashMap 存取⾼效并减少碰撞，我们需要确保数据尽量均匀分布。哈希值在 Java 中通常使⽤ int 表示，其范围是 $- 2 1 4 7 4 8 3 6 4 8 \sim 2 1 4 7 4 8 3 6 4 7$ 前后加起来⼤概 40 亿的映射空间，只要哈希函数映射得⽐较均匀松散，⼀般应⽤是很难出现碰撞的。但是，问题是⼀个 40 亿⻓度的数组，内存是放不下的。所以，这个散列值是不能直接拿来⽤的。⽤之前还要先做对数组的⻓度取模运算，

得到的余数才能⽤来要存放的位置也就是对应的数组下标。

# 这个算法应该如何设计呢？

我们⾸先可能会想到采⽤ $\%$ 取余的操作来实现。但是，重点来了：“取余 $( \% )$ 操作中如果除数是 2 的幂次则等价于与其除数减⼀的与(&)操作（也就是说 hash%length $| = =$ hash&(length-1) 的前提是 length是 2 的 n 次⽅）。” 并且，采⽤⼆进制位操作 & 相对于 $\%$ 能够提⾼运算效率。

除了上⾯所说的位运算⽐取余效率⾼之外，我觉得更重要的⼀个原因是：⻓度是 2 的幂次⽅，可以让 HashMap 在扩容的时候更均匀。例如:

length $= 8$ 时，length - $1 = 7$ 的⼆进制位 0111

length $= 1 6$ 时，length - $1 = 1 5$ 的⼆进制位 1111

这时候原本存在 HashMap 中的元素计算新的数组位置时 hash&(length-1) ，取决 hash 的第四个⼆进制位（从右数），会出现两种情况：

1. 第四个⼆进制位为 0，数组位置不变，也就是说当前元素在新数组和旧数组的位置相同。

2. 第四个⼆进制位为 1，数组位置在新数组扩容之后的那⼀部分。

这⾥列举⼀个例⼦：

假设有⼀个元素的哈希值为 10101100

旧数组元素位置计算：

hash = 10101100

length - 1 = 00000111

&

index = 00000100 (4)

新数组元素位置计算：

hash = 10101100

length - 1 = 00001111

&

index = 00001100 (12)

看第四位（从右数）：

1.⾼位为 0：位置不变。

注意：这⾥列举的场景看的是第四个⼆进制位，更准确点来说看的是⾼位（从右数），例如length $= 3 2$ 时， length - $1 = 3 1$ ，⼆进制为 11111 ，这⾥看的就是第五个⼆进制位。

也就是说扩容之后，在旧数组元素 hash 值⽐较均匀（⾄于 hash 值均不均匀，取决于前⾯讲的对象的 hashcode() ⽅法和扰动函数）的情况下，新数组元素也会被分配的⽐较均匀，最好的情况是会有⼀半在新数组的前半部分，⼀半在新数组后半部分。

这样也使得扩容机制变得简单和⾼效，扩容后只需检查哈希值⾼位的变化来决定元素的新位置，要么位置不变（⾼位为 0），要么就是移动到新位置（⾼位为 1，原索引位置 $^ +$ 原容量）。

最后，简单总结⼀下 HashMap 的⻓度是 2 的幂次⽅的原因：

1. 位运算效率更⾼：位运算(&)⽐取余运算 $( \% )$ 更⾼效。当⻓度为 2 的幂次⽅时， hash $\%$ length 等价于 hash & (length - 1) 。

2. 可以更好地保证哈希值的均匀分布：扩容之后，在旧数组元素 hash 值⽐较均匀的情况下，新数组元素也会被分配的⽐较均匀，最好的情况是会有⼀半在新数组的前半部分，⼀半在新数组后半部分。

3. 扩容机制变得简单和⾼效：扩容后只需检查哈希值⾼位的变化来决定元素的新位置，要么位置不变（⾼位为 0），要么就是移动到新位置（⾼位为 1，原索引位置 $^ +$ 原容量）。

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/74e63b86-c4d9-4310-a67f-de1260f2f1d2/44e34b42cf73ffe527188638228ad747a404818cfa44a8623fbbb13829802ec6.jpg)


# HashMap 多线程操作导致死循环问题

JDK1.7 及之前版本的 HashMap 在多线程环境下扩容操作可能存在死循环问题，这是由于当⼀个桶位中有多个元素需要进⾏扩容时，多个线程同时对链表进⾏操作，头插法可能会导致链表中的节点指向错误的位置，从⽽形成⼀个环形链表，进⽽使得查询元素的操作陷⼊死循环⽆法结束。

为了解决这个问题，JDK1.8 版本的 HashMap 采⽤了尾插法⽽不是头插法来避免链表倒置，使得插⼊的节点永远都是放在链表的末尾，避免了链表中的环形结构。但是还是不建议在多线程下使⽤HashMap ，因为多线程下使⽤ HashMap 还是会存在数据覆盖的问题。并发环境下，推荐使⽤ConcurrentHashMap 。

般⾯试中这样介绍就差不多，不需要记各种细节，个⼈觉得也没必要记。如果想要详细了解HashMap 扩容导致死循环问题，可以看看耗⼦叔的这篇⽂章：Java HashMap 的死循环。

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/74e63b86-c4d9-4310-a67f-de1260f2f1d2/587bc8fa80cac61c31985ea952f5604587c82a4cb14f7a74df0c556211d47cf5.jpg)


# HashMap 为什么线程不安全？

JDK1.7 及之前版本，在多线程环境下， HashMap 扩容时会造成死循环和数据丢失的问题。

数据丢失这个在 JDK1.7 和 JDK 1.8 中都存在，这⾥以 JDK 1.8 为例进⾏介绍。

JDK 1.8 后，在 HashMap 中，多个键值对可能会被分配到同⼀个桶（bucket），并以链表或红⿊树的形式存储。多个线程对 HashMap 的 put 操作会导致线程不安全，具体来说会有数据覆盖的⻛险。

# 举个例⼦：

两个线程 1,2 同时进⾏ put 操作，并且发⽣了哈希冲突（hash 函数计算出的插⼊下标是相同的）。

不同的线程可能在不同的时间⽚获得 CPU 执⾏的机会，当前线程 1 执⾏完哈希冲突判断后，由于时间⽚耗尽挂起。线程 2 先完成了插⼊操作。

随后，线程 1 获得时间⽚，由于之前已经进⾏过 hash 碰撞的判断，所有此时会直接进⾏插⼊，这就导致线程 2 插⼊的数据被线程 1 覆盖了。

```txt
public V put(K key, V value) {
    return putchar(key), key, value, false, true);
}  
final V putchar(int hash, K key, V value, boolean onlyIfAbsent, boolean evict) {
    // ...
    // 判断是否出现hash碰撞
    // (n - 1) & hash 确定元素存放在哪个桶中，桶为空，新生成结点放入桶中(此时，这个结点是放在数组中)
    if ((p = tab[i = (n - 1) & hash] == null)
        tab[i] = newNodehashCode, key, value, null);
    // 桶中已经存在元素（处理hash冲突）
    else {
        // ...
    }
}
```

还有⼀种情况是这两个线程同时 put 操作导致 size 的值不正确，进⽽导致数据覆盖的问题：

1. 线程 1 执⾏ if(++size > threshold) 判断时，假设获得 size 的值为 10，由于时间⽚耗尽挂起。

2. 线程 2 也执⾏ if(++size $>$ threshold) 判断，获得 size 的值也为 10，并将元素插⼊到该桶位中，并将 size 的值更新为 11。

3. 随后，线程 1 获得时间⽚，它也将元素放⼊桶位中，并将 size 的值更新为 11。

4. 线程 1、2 都执⾏了⼀次 put 操作，但是 size 的值只增加了 1，也就导致实际上只有⼀个元素被添加到了 HashMap 中。

```txt
public V put(K key, V value) {
    return putchar(key), key, value, false, true);
}  
final V putchar(int hash, K key, V value, boolean onlyIfAbsent, boolean evict) {
    // ...
    // 实际大小大于阈值则扩容
    if (++size > threshold)
        resize();
    // 插入后回调
    afterNodeInsertion(evict);
    return null;
}
```

# HashMap 常⻅的遍历⽅式?

HashMap 的 7 种遍历⽅式与性能分析！

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/74e63b86-c4d9-4310-a67f-de1260f2f1d2/1baaacc3627760996bab2da964fbabdd6cc8824c15927c34ced07499b041052d.jpg)


修正（参⻅：issue#1411）：

这篇⽂章对于 parallelStream 遍历⽅式的性能分析有误，先说结论：存在阻塞时 parallelStream 性能最⾼, ⾮阻塞时 parallelStream 性能最低 。

当遍历不存在阻塞时, parallelStream 的性能是最低的：

```txt
Benchmark Mode Cnt Score Error Units Test entrySet avgt 5 288.651 ± 10.536 ns/op Test.keySet avgt 5 584.594 ± 21.431 ns/op Test.lambda avgt 5 221.791 ± 10.198 ns/op Test(parallelStream avgt 5 6919.163 ± 1116.139 ns/op
```

加⼊阻塞代码 Thread.sleep(10) 后, parallelStream 的性能才是最⾼的:

```txt
Benchmark Mode Cnt Score Error Units  
Test.entrySet avgt 5 1554828440.000 ± 23657748.653 ns/op  
Test.keySet avgt 5 1550612500.000 ± 6474562.858 ns/op  
Test Lambda avgt 5 1551065180.000 ± 19164407.426 ns/op  
Test(parallelStream avgt 5 186345456.667 ± 3210435.590 ns/op
```

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/74e63b86-c4d9-4310-a67f-de1260f2f1d2/2e1a3fd25d09f13d6434cb2336f87d7dc512d1bc479121acf93a04357cf0c539.jpg)


# ConcurrentHashMap 和 Hashtable 的区别

ConcurrentHashMap 和 Hashtable 的区别主要体现在实现线程安全的⽅式上不同。

底层数据结构： JDK1.7 的 ConcurrentHashMap 底层采⽤ 分段的数组 $^ +$ 链表 实现，在 JDK1.8中采⽤的数据结构跟 HashMap 的结构⼀样，数组 $^ +$ 链表/红⿊⼆叉树。 Hashtable 和 JDK1.8之前的 HashMap 的底层数据结构类似都是采⽤ 数组+链表 的形式，数组是 HashMap 的主体，链表则是主要为了解决哈希冲突⽽存在的；

# 实现线程安全的⽅式（重要）：

在 JDK1.7 的时候， ConcurrentHashMap 对整个桶数组进⾏了分割分段( Segment ，分段锁)，每⼀把锁只锁容器其中⼀部分数据（下⾯有示意图），多线程访问容器⾥不同数据段的数据，就不会存在锁竞争，提⾼并发访问率。

到了 JDK1.8 的时候， ConcurrentHashMap 已经摒弃了 Segment 的概念，⽽是直接⽤Node 数组 $^ +$ 链表 $^ +$ 红⿊树的数据结构来实现，并发控制使⽤ synchronized 和 CAS 来操作。（JDK1.6 以后 synchronized 锁做了很多优化） 整个看起来就像是优化过且线程安全的 HashMap ，虽然在 JDK1.8 中还能看到 Segment 的数据结构，但是已经简化了属性，只是为了兼容旧版本；

Hashtable (同⼀把锁) :使⽤ synchronized 来保证线程安全，效率⾮常低下。当⼀个线程访问同步⽅法时，其他线程也访问同步⽅法，可能会进⼊阻塞或轮询状态，如使⽤ put 添加元素，另⼀个线程不能使⽤ put 添加元素，也不能使⽤ get，竞争会越来越激烈效率越低。

下⾯，我们再来看看两者底层数据结构的对⽐图。

# Hashtable :

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/74e63b86-c4d9-4310-a67f-de1260f2f1d2/93d8fb1a2cd100d21b8b288ffccdec8339de48c5fa7bdd2b75c901380ea25334.jpg)


https://www.cnblogs.com/chengxiao/p/6842045.html>

# JDK1.7 的 ConcurrentHashMap：

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/74e63b86-c4d9-4310-a67f-de1260f2f1d2/2f0fba0103ea7ce99e066ae8cb40e29978b14e2b00d05e5d167d747481f1e075.jpg)


ConcurrentHashMap 是由 Segment 数组结构和 HashEntry 数组结构组成。

# JDK1.8 的 ConcurrentHashMap：

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/74e63b86-c4d9-4310-a67f-de1260f2f1d2/01c996675722cf6398ef33d256531bb058e5689b30a1cbb9e34289dcb5c538b4.jpg)


JDK1.8 的 ConcurrentHashMap 不再是 Segment 数组 $^ +$ HashEntry 数组 $^ +$ 链表，⽽是 Node 数组$^ +$ 链表 / 红⿊树。不过，Node 只能⽤于链表的情况，红⿊树的情况需要使⽤ TreeNode 。当冲突链表达到⼀定⻓度时，链表会转换成红⿊树。

TreeNode 是存储红⿊树节点，被 TreeBin 包装。 TreeBin 通过 root 属性维护红⿊树的根结点，因为红⿊树在旋转的时候，根结点可能会被它原来的⼦节点替换掉，在这个时间点，如果有其他线程要写这棵红⿊树就会发⽣线程不安全问题，所以在 ConcurrentHashMap 中 TreeBin 通过 waiter 属性维护当前使⽤这棵红⿊树的线程，来防⽌其他线程的进⼊。

```txt
static final class TreeBin<K,V> extends Node<K,V> {
   TreeNode<K,V> root;
    volatileTreeNode<K,V> first;
    volatile Thread waiter;
    volatile int lockState;
    // values for lockState
    static final int WRITER = 1; // set while holding write lock
    static final int WAITER = 2; // set when waiting for write lock
    static final int READER = 4; // increment value for setting read lock
}
```

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/74e63b86-c4d9-4310-a67f-de1260f2f1d2/030f35bcdc74986594d9454264aa29aad91c6b16e8087815eb628a1eb4cf4784.jpg)


# ConcurrentHashMap 线程安全的具体实现⽅式/底层具体实现

# JDK1.8 之前

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/74e63b86-c4d9-4310-a67f-de1260f2f1d2/411ae5b58216e007b6113faac4e98ded032d96aa369162125ec1b4846ac6546a.jpg)


⾸先将数据分为⼀段⼀段（这个“段”就是 Segment ）的存储，然后给每⼀段数据配⼀把锁，当⼀个线程占⽤锁访问其中⼀个段数据时，其他段的数据也能被其他线程访问。

ConcurrentHashMap 是由 Segment 数组结构和 HashEntry 数组结构组成。

Segment 继承了 ReentrantLock ,所以 Segment 是⼀种可重⼊锁，扮演锁的⻆⾊。 HashEntry ⽤于存储键值对数据。

```txt
static class Segment<K,V> extends ReentrantLock implementsSerializable { }
```

个 ConcurrentHashMap ⾥包含⼀个 Segment 数组， Segment 的个数⼀旦初始化就不能改变。Segment 数组的⼤⼩默认是 16，也就是说默认可以同时⽀持 16 个线程并发写。

Segment 的结构和 HashMap 类似，是⼀种数组和链表结构，⼀个 Segment 包含⼀个 HashEntry数组，每个 HashEntry 是⼀个链表结构的元素，每个 Segment 守护着⼀个 HashEntry 数组⾥的元素，当对 HashEntry 数组的数据进⾏修改时，必须⾸先获得对应的 Segment 的锁。也就是说，对同⼀ Segment 的并发写⼊会被阻塞，不同 Segment 的写⼊是可以并发执⾏的。


JDK1.8 之后


![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/74e63b86-c4d9-4310-a67f-de1260f2f1d2/78be449f9dd25b3878fe04a62e6d2f67a5dce06ac30a95b840f0d0eb51aa32cc.jpg)


Java 8 ⼏乎完全重写了 ConcurrentHashMap ，代码量从原来 Java 7 中的 1000 多⾏，变成了现在的6000 多⾏。

ConcurrentHashMap 取消了 Segment 分段锁，采⽤ Node + CAS + synchronized 来保证并发安全。数据结构跟 HashMap 1.8 的结构类似，数组 $^ +$ 链表/红⿊⼆叉树。Java 8 在链表⻓度超过⼀定阈值（8）时将链表（寻址时间复杂度为 O(N)）转换为红⿊树（寻址时间复杂度为 O(log(N))）

Java 8 中，锁粒度更细， synchronized 只锁定当前链表或红⿊⼆叉树的⾸节点，这样只要 hash 不冲突，就不会产⽣并发，就不会影响其他 Node 的读写，效率⼤幅提升。

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/74e63b86-c4d9-4310-a67f-de1260f2f1d2/719b95e143b977c7efe321930ddbbeefc4b7ea9903f544f97d4010bd7e68b20f.jpg)


# JDK 1.7 和 JDK 1.8 的 ConcurrentHashMap 实现有什么不同？

线程安全实现⽅式：JDK 1.7 采⽤ Segment 分段锁来保证安全， Segment 是继承⾃ReentrantLock 。JDK1.8 放弃了 Segment 分段锁的设计，采⽤ Node + CAS + synchronized 保证线程安全，锁粒度更细， synchronized 只锁定当前链表或红⿊⼆叉树的⾸节点。

Hash 碰撞解决⽅法 : JDK 1.7 采⽤拉链法，JDK1.8 采⽤拉链法结合红⿊树（链表⻓度超过⼀定阈值时，将链表转换为红⿊树）。

并发度：JDK 1.7 最⼤并发度是 Segment 的个数，默认是 16。JDK 1.8 最⼤并发度是 Node数组的⼤⼩，并发度更⼤。

# ConcurrentHashMap 为什么 key 和 value 不能为 null？

ConcurrentHashMap 的 key 和 value 不能为 null 主要是为了避免⼆义性。null 是⼀个特殊的值，表示没有对象或没有引⽤。如果你⽤ null 作为键，那么你就⽆法区分这个键是否存在于ConcurrentHashMap 中，还是根本没有这个键。同样，如果你⽤ null 作为值，那么你就⽆法区分这个值是否是真正存储在 ConcurrentHashMap 中的，还是因为找不到对应的键⽽返回的。

拿 get ⽅法取值来说，返回的结果为 null 存在两种情况：

值没有在集合中 ；

值本身就是 null。

这也就是⼆义性的由来。

具体可以参考 ConcurrentHashMap 源码分析 。

多线程环境下，存在⼀个线程操作该 ConcurrentHashMap 时，其他的线程将该 ConcurrentHashMap修改的情况，所以⽆法通过 containsKey(key) 来判断否存在这个键值对，也就没办法解决⼆义性问题了。

与此形成对⽐的是， HashMap 可以存储 null 的 key 和 value，但 null 作为键只能有⼀个，null 作为值可以有多个。如果传⼊ null 作为参数，就会返回 hash 值为 0 的位置的值。单线程环境下，不存在⼀个线程操作该 HashMap 时，其他的线程将该 HashMap 修改的情况，所以可以通过contains(key) 来做判断是否存在这个键值对，从⽽做相应的处理，也就不存在⼆义性问题。

也就是说，多线程下⽆法正确判定键值对是否存在（存在其他线程修改的情况），单线程是可以的（不存在其他线程修改的情况）。

如果你确实需要在 ConcurrentHashMap 中使⽤ null 的话，可以使⽤⼀个特殊的静态空对象来代替null。

```java
public static final Object NULL = new Object();
```

最后，再分享⼀下 ConcurrentHashMap 作者本⼈ (Doug Lea)对于这个问题的回答：

The main reason that nulls aren't allowed in ConcurrentMaps (ConcurrentHashMaps,ConcurrentSkipListMaps) is that ambiguities that may be just barely tolerable in non-concurrent maps can't be accommodated. The main one is that if map.get(key) returns nullyou can't detect whether the key explicitly maps to null vs the key isn't mapped. In a non-concurrent map, you can check this via map.contains(key) , but in a concurrent one, the mapmight have changed between calls.

翻译过来之后的，⼤致意思还是单线程下可以容忍歧义，⽽多线程下⽆法容忍。

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/74e63b86-c4d9-4310-a67f-de1260f2f1d2/f100111a5ec409fea4c92c1a5bd24f90e9bbc3e61a2fe41f762ca8ab5712be23.jpg)


# ConcurrentHashMap 能保证复合操作的原⼦性吗？

ConcurrentHashMap 是线程安全的，意味着它可以保证多个线程同时对它进⾏读写操作时，不会出现数据不⼀致的情况，也不会导致 JDK1.7 及之前版本的 HashMap 多线程操作导致死循环问题。但是，这并不意味着它可以保证所有的复合操作都是原⼦性的，⼀定不要搞混了！

复合操作是指由多个基本操作(如 put 、 get remove containsKey 等)组成的操作，例如先判断某个键是否存在 containsKey(key) ，然后根据结果进⾏插⼊或更新 put(key, value) 。这种操作在执⾏过程中可能会被其他线程打断，导致结果不符合预期。

例如，有两个线程 A 和 B 同时对 ConcurrentHashMap 进⾏复合操作，如下：

```javascript
// 线程 A  
if (!map.containsKey(key)) {  
map.put(key, value);  
}  
// 线程 B  
if (!map.containsKey(key)) {  
map.put(key, anotherValue);  
}
```

如果线程 A 和 B 的执⾏顺序是这样：

1. 线程 A 判断 map 中不存在 key

2. 线程 B 判断 map 中不存在 key

3. 线程 B 将 (key, anotherValue) 插⼊ map

4. 线程 A 将 (key, value) 插⼊ map

那么最终的结果是 (key, value)，⽽不是预期的 (key, anotherValue)。这就是复合操作的⾮原⼦性导致的问题。

# 那如何保证 ConcurrentHashMap 复合操作的原⼦性呢？

ConcurrentHashMap 提供了⼀些原⼦性的复合操作，如 putIfAbsent 、 compute computeIfAbsentcomputeIfPresent 、 merge 等。这些⽅法都可以接受⼀个函数作为参数，根据给定的 key 和 value来计算⼀个新的 value，并且将其更新到 map 中。

上⾯的代码可以改写为：

```javascript
// 线程 A  
map.putIfAbsent(key, value);  
// 线程 B  
map.putIfAbsent(key, anotherValue);
```

或者：

```javascript
// 线程 A  
map.computeIfAbsent(key, k -> value);
```

```txt
// 线程 B  
map.computeIfAbsent(key, k -> anotherValue);
```

很多同学可能会说了，这种情况也能加锁同步呀！确实可以，但不建议使⽤加锁的同步机制，违背了使⽤ ConcurrentHashMap 的初衷。在使⽤ ConcurrentHashMap 的时候，尽量使⽤这些原⼦性的复合操作⽅法来保证原⼦性。

