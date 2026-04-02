

# 1、Long 的⻓度和范围，为什么要减 1 ？

先来复习⼀下 Java 中的 8 种基本数据类型：

6 种数字类型：

4 种整数型： byte 、 short 、 int long

2 种浮点型： float 、 double

1 种字符类型： char

1 种布尔型： boolean 。

这 8 种基本数据类型的默认值以及所占空间的⼤⼩如下：

<table><tr><td>基本类型</td><td>位数</td><td>字节</td><td>默认值</td><td>取值范围</td></tr><tr><td>byte</td><td>8</td><td>1</td><td>0</td><td>-128 ~ 127</td></tr><tr><td>short</td><td>16</td><td>2</td><td>0</td><td>-32768 (-2^15) ~ 32767 (2^15 - 1)</td></tr><tr><td>int</td><td>32</td><td>4</td><td>0</td><td>-2147483648 ~ 2147483647</td></tr><tr><td>long</td><td>64</td><td>8</td><td>0L</td><td>-9223372036854775808 (-2^63) ~ 9223372036854775807 (2^63 - 1)</td></tr><tr><td>char</td><td>16</td><td>2</td><td>&#x27;u0000&#x27;</td><td>0 ~ 65535 (2^16 - 1)</td></tr><tr><td>float</td><td>32</td><td>4</td><td>0f</td><td>1.4E-45 ~ 3.4028235E38</td></tr><tr><td>double</td><td>64</td><td>8</td><td>0d</td><td>4.9E-324 ~ 1.7976931348623157E308</td></tr><tr><td>boolean</td><td>1</td><td></td><td>false</td><td>true、false</td></tr></table>

可以看到，像 byte 、 short 、 int 、 long 能表示的最⼤正数都减 1 了。这是为什么呢？这是因为在⼆进制补码表示法中，最⾼位是⽤来表示符号的（0 表示正数，1 表示负数），其余位表示数值部分。所以，如果我们要表示最⼤的正数，我们需要把除了最⾼位之外的所有位都设为 1。如果我们再加 1，就会导致溢出，变成⼀个负数。

对于 boolean ，官⽅⽂档未明确定义，它依赖于 JVM ⼚商的具体实现。逻辑上理解是占⽤ 1 位，但是实际中会考虑计算机⾼效存储因素。

另外，Java 的每种基本类型所占存储空间的⼤⼩不会像其他⼤多数语⾔那样随机器硬件架构的变化⽽变化。这种所占存储空间⼤⼩的不变性是 Java 程序⽐⽤其他⼤多数语⾔编写的程序更具可移植性的原因之⼀（《Java 编程思想》2.2 节有提到）。

# 2、JAVA 异常的层次结构

Java 异常类层次结构图概览：

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/d2f51c72-bcb8-49f2-bfb3-f3d95b830e1c/4bed7a79caf8a98d31b8de5bf36bed65b1da14e5aa9ee8973c759b334b6b57c2.jpg)


在 Java 中，所有的异常都有⼀个共同的祖先 java.lang 包中的 Throwable 类。 Throwable 类有两个重要的⼦类:

Exception :程序本身可以处理的异常，可以通过 catch 来进⾏捕获。 Exception ⼜可以分为 CheckedException (受检查异常，必须处理) 和 Unchecked Exception (不受检查异常，可以不处理)。

Error Error 属于程序⽆法处理的错误 ，我们没办法通过 catch 来进⾏捕获不建议通过 catch 捕获 。例如 Java 虚拟机运⾏错误（ Virtual MachineError ）、虚拟机内存不够错误( OutOfMemoryError )、类定义错误（ NoClassDefFoundError ）等 。这些异常发⽣时，Java 虚拟机（JVM）⼀般会选择线程终⽌。

# 3、JAVA 的集合类有了解么？

Java 集合， 也叫作容器，主要是由两⼤接⼝派⽣⽽来：⼀个是 Collection 接⼝，主要⽤于存放单⼀元素；另⼀个是 Map 接⼝，主要⽤于存放键值对。对于 Collection 接⼝，下⾯⼜有三个主要的⼦接⼝： List 、 Set 和Queue 。

Java 集合框架如下图所示：

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/d2f51c72-bcb8-49f2-bfb3-f3d95b830e1c/ec97d0ea4898b36ee03f23692421d006f8a6839e852ffdd569ba3613a75ee396.jpg)


![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/d2f51c72-bcb8-49f2-bfb3-f3d95b830e1c/4739e324c65ad6bfcee519c0ef229dabdf734b39b56ad97d9cdb5a4abd9ffc26.jpg)


注：图中只列举了主要的继承派⽣关系，并没有列举所有关系。⽐⽅省略了 AbstractList , NavigableSet 等抽象类以及其他的⼀些辅助类，如想深⼊了解，可⾃⾏查看源码。

List (对付顺序的好帮⼿): 存储的元素是有序的、可重复的。

Set (注重独⼀⽆⼆的性质): 存储的元素不可重复的。

Queue (实现排队功能的叫号机): 按特定的排队规则来确定先后顺序，存储的元素是有序的、可重复的。

Map (⽤ key 来搜索的专家): 使⽤键值对（key-value）存储，类似于数学上的函数 $\scriptstyle { \mathsf { y } } = { \mathsf { f } } ( { \mathsf { x } } )$ ，"x" 代表 key，"y"代表 value，key 是⽆序的、不可重复的，value 是⽆序的、可重复的，每个键最多映射到⼀个值。

# 4、ArrayList 和 LinkedList 区别

是否保证线程安全： ArrayList 和 LinkedList 都是不同步的，也就是不保证线程安全；

底层数据结构： ArrayList 底层使⽤的是 Object 数组； LinkedList 底层使⽤的是 双向链表 数据结构（JDK1.6 之前为循环链表，JDK1.7 取消了循环。注意双向链表和双向循环链表的区别，下⾯有介绍到！）

插⼊和删除是否受元素位置的影响：

。 ArrayList 采⽤数组存储，所以插⼊和删除元素的时间复杂度受元素位置的影响。 ⽐如：执⾏ add(E e)⽅法的时候， ArrayList 会默认在将指定的元素追加到此列表的末尾，这种情况时间复杂度就是 O(1)。但是如果要在指定位置 i 插⼊和删除元素的话（ add(int index, E element) ），时间复杂度就为 O(n)。因为在进⾏上述操作的时候集合中第 i 和第 i 个元素之后的(n-i)个元素都要执⾏向后位/向前移⼀位的操作。

。 LinkedList 采⽤链表存储，所以在头尾插⼊或者删除元素不受元素位置的影响（ add(E e) 、 addFirst(Ee) addLast(E e) 、 removeFirst() 、 removeLast() ），时间复杂度为 O(1)，如果是要在指定位置 i 插⼊和删除元素的话（ add(int index, E element) ， remove(Object o) , remove(int index) ）， 时间复杂度为O(n) ，因为需要先移动到指定位置再插⼊和删除。

是否⽀持快速随机访问： LinkedList 不⽀持⾼效的随机元素访问，⽽ ArrayList （实现了 RandomAccess接⼝） ⽀持。快速随机访问就是通过元素的序号快速获取元素对象(对应于 get(int index) ⽅法)。

内存空间占⽤： ArrayList 的空间浪费主要体现在在 list 列表的结尾会预留⼀定的容量空间，⽽ LinkedList的空间花费则体现在它的每⼀个元素都需要消耗⽐ ArrayList 更多的空间（因为要存放直接后继和直接前驱以及数据）。

我们在项⽬中⼀般是不会使⽤到 LinkedList 的，需要⽤到 LinkedList 的场景⼏乎都可以使⽤ ArrayList 来代替，并且，性能通常会更好！就连 LinkedList 的作者约书亚 · 布洛克（Josh Bloch）⾃⼰都说从来不会使⽤LinkedList 。


另外，不要下意识地认为 LinkedList 作为链表就最适合元素增删的场景。我在上⾯也说了， LinkedList 仅仅在头尾插⼊或者删除元素的时候时间复杂度近似 O(1)，其他情况增删元素的平均时间复杂度都是 O(n) 。

补充内容: 双向链表和双向循环链表

双向链表： 包含两个指针，⼀个 prev 指向前⼀个节点，⼀个 next 指向后⼀个节点。


双向链表


![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/d2f51c72-bcb8-49f2-bfb3-f3d95b830e1c/299b911243dce2bc2c61ab2cb28b1472ecef9fd5cf817702ccd2c52b832f4aaf.jpg)


双向循环链表： 最后⼀个节点的 next 指向 head，⽽ head 的 prev 指向最后⼀个节点，构成⼀个环。


双向循环链表


![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/d2f51c72-bcb8-49f2-bfb3-f3d95b830e1c/b2273e215faf27c7ba7d4636db75d6c5f1edd7fbec9ed48380933678db803bb0.jpg)


补充内容:RandomAccess 接⼝

```txt
public interface RandomAccess { }
```

查看源码我们发现实际上 RandomAccess 接⼝中什么都没有定义。所以，在我看来 RandomAccess 接⼝不过是⼀个标识罢了。标识什么？ 标识实现这个接⼝的类具有随机访问功能。

在 binarySearch（) ⽅法中，它要判断传⼊的 list 是否 RandomAccess 的实例，如果是，调⽤ indexedBinarySearch() ⽅法，如果不是，那么调⽤ iteratorBinarySearch() ⽅法

```java
public static <T> int binarySearch(List<? extends Comparable<? super T>> list, T key) { if (list instanceof RandomAccess || list.size()<BINARYSEARCH_threshold) return CollectionsindexedBinarySearch(list, key); else return Collections.iteratorBinarySearch(list, key); }
```

ArrayList 实现了 RandomAccess 接⼝， ⽽ LinkedList 没有实现。为什么呢？我觉得还是和底层数据结构有关！ ArrayList 底层是数组，⽽ LinkedList 底层是链表。数组天然⽀持随机访问，时间复杂度为 O(1)，所以称为快速随机访问。链表需要遍历到特定位置才能访问特定位置的元素，时间复杂度为 O(n)，所以不⽀持快速随机访问。 ArrayList 实现了 RandomAccess 接⼝，就表明了他具有快速随机访问功能。 RandomAccess 接⼝只是标识，并不是说 ArrayList 实现 RandomAccess 接⼝才具有快速随机访问功能的！

# 5、HashMap 有了解么，它的底层实现，为什么线程不安全？

HashMap 主要⽤来存放键值对，它基于哈希表的 Map 接⼝实现，是常⽤的 Java 集合之⼀，是⾮线程安全的。

HashMap 可以存储 null 的 key 和 value，但 null 作为键只能有⼀个，null 作为值可以有多个

JDK1.8 之前 HashMap 由 数组 $^ +$ 链表 组成的，数组是 HashMap 的主体，链表则是主要为了解决哈希冲突⽽存在的（“拉链法”解决冲突）。 JDK1.8 以后的 HashMap 在解决哈希冲突时有了较⼤的变化，当链表⻓度⼤于等于阈值（默认为 8）（将链表转换成红⿊树前会判断，如果当前数组的⻓度⼩于 64，那么会选择先进⾏数组扩容，⽽不是转换为红⿊树）时，将链表转化为红⿊树，以减少搜索时间。

HashMap 默认的初始化⼤⼩为 16。之后每次扩充，容量变为原来的 2 倍。并且， HashMap 总是使⽤ 2 的幂作为哈希表的⼤⼩。

JDK1.7 及之前版本，在多线程环境下， HashMap 扩容时会造成死循环和数据丢失的问题。

数据丢失这个在 JDK1.7 和 JDK 1.8 中都存在，这⾥以 JDK 1.8 为例进⾏介绍。

JDK 1.8 后，在 HashMap 中，多个键值对可能会被分配到同⼀个桶（bucket），并以链表或红⿊树的形式存储。多个线程对 HashMap 的 put 操作会导致线程不安全，具体来说会有数据覆盖的⻛险。

# 举个例⼦：

两个线程 1,2 同时进⾏ put 操作，并且发⽣了哈希冲突（hash 函数计算出的插⼊下标是相同的）。

不同的线程可能在不同的时间⽚获得 CPU 执⾏的机会，当前线程 1 执⾏完哈希冲突判断后，由于时间⽚耗尽挂起。线程 2 先完成了插⼊操作。

随后，线程 1 获得时间⽚，由于之前已经进⾏过 hash 碰撞的判断，所有此时会直接进⾏插⼊，这就导致线程 2 插⼊的数据被线程 1 覆盖了。

```txt
public V put(K key, V value) {
    return putVal(key), key, value, false, true);
}
final V putVal(int hash, K key, V value, boolean onlyIfAbsent, boolean evict) {
    // ...
    // 判断是否出现 hash 碰撞
    // (n - 1) & hash 确定元素存放在哪个桶中，桶为空，新生成结点放入桶中（此时，这个结点是放在数组中）
    if ((p = tab[i = (n - 1) & hash]) == null)
        tab[i] = newNode(key, key, value, null);
    // 桶中已经存在元素（处理hash冲突）
    else {
        // ...
    }
}
```

还有⼀种情况是这两个线程同时 put 操作导致 size 的值不正确，进⽽导致数据覆盖的问题：

1. 线程 1 执⾏ if(++size $>$ threshold) 判断时，假设获得 size 的值为 10，由于时间⽚耗尽挂起。

2. 线程 2 也执⾏ if(++size $>$ threshold) 判断，获得 size 的值也为 10，并将元素插⼊到该桶位中，并将 size的值更新为 11。

3. 随后，线程 1 获得时间⽚，它也将元素放⼊桶位中，并将 size 的值更新为 11。

4. 线程 1、2 都执⾏了⼀次 put 操作，但是 size 的值只增加了 1，也就导致实际上只有⼀个元素被添加到了HashMap 中。

```cpp
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

# 6、CoucurHashMap 和 HashTable

ConcurrentHashMap 和 Hashtable 的区别主要体现在实现线程安全的⽅式上不同。

底层数据结构： JDK1.7 的 ConcurrentHashMap 底层采⽤ 分段的数组+链表 实现，JDK1.8 采⽤的数据结构跟 HashMap1.8 的结构⼀样，数组 $^ +$ 链表/红⿊⼆叉树。 Hashtable 和 JDK1.8 之前的 HashMap 的底层数据结构类似都是采⽤ 数组 $^ +$ 链表 的形式，数组是 HashMap 的主体，链表则是主要为了解决哈希冲突⽽存在的；

实现线程安全的⽅式（重要）：

在 JDK1.7 的时候， ConcurrentHashMap 对整个桶数组进⾏了分割分段( Segment ，分段锁)，每⼀把锁只锁容器其中⼀部分数据（下⾯有示意图），多线程访问容器⾥不同数据段的数据，就不会存在锁竞争，提⾼并发访问率。

到了 JDK1.8 的时候， ConcurrentHashMap 已经摒弃了 Segment 的概念，⽽是直接⽤ Node 数组 $^ +$ 链表 $^ +$ 红⿊树的数据结构来实现，并发控制使⽤ synchronized 和 CAS 来操作。（JDK1.6 以后synchronized 锁做了很多优化） 整个看起来就像是优化过且线程安全的 HashMap ，虽然在 JDK1.8 中还能看到 Segment 的数据结构，但是已经简化了属性，只是为了兼容旧版本；

Hashtable (同⼀把锁) :使⽤ synchronized 来保证线程安全，效率⾮常低下。当⼀个线程访问同步⽅法时，其他线程也访问同步⽅法，可能会进⼊阻塞或轮询状态，如使⽤ put 添加元素，另⼀个线程不能使⽤ put 添加元素，也不能使⽤ get，竞争会越来越激烈效率越低。

下⾯，我们再来看看两者底层数据结构的对⽐图。

# Hashtable :

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/d2f51c72-bcb8-49f2-bfb3-f3d95b830e1c/0226c922b96cae9065d3a86ccca37f7d81e44079d3ede6fa95bd5f549c22b837.jpg)


https://www.cnblogs.com/chengxiao/p/6842045.html>

# JDK1.7 的 ConcurrentHashMap：

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/d2f51c72-bcb8-49f2-bfb3-f3d95b830e1c/1fa1a2bfe10adccddb2164e79c61451e4172758cb2e11aee7ca4ba6507ac72e6.jpg)


ConcurrentHashMap 是由 Segment 数组结构和 HashEntry 数组结构组成。

Segment 数组中的每个元素包含⼀个 HashEntry 数组，每个 HashEntry 数组属于链表结构。

# JDK1.8 的 ConcurrentHashMap：

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/d2f51c72-bcb8-49f2-bfb3-f3d95b830e1c/c68944a2aa41a46f535c0155cfadff49aef1f0b35c097abc5907dd064ed38144.jpg)


JDK1.8 的 ConcurrentHashMap 不再是 Segment 数组 $^ +$ HashEntry 数组 $^ +$ 链表，⽽是 Node 数组 $^ +$ 链表 / 红⿊树。不过，Node 只能⽤于链表的情况，红⿊树的情况需要使⽤ TreeNode 。当冲突链表达到⼀定⻓度时，链表会转换成红⿊树。

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
```

# 7、线程池有了解么，讲⼀下

顾名思义，线程池就是管理⼀系列线程的资源池，其提供了⼀种限制和管理线程资源的⽅式。每个线程池还维护⼀些基本统计信息，例如已完成任务的数量。

这⾥借⽤《Java 并发编程的艺术》书中的部分内容来总结⼀下使⽤线程池的好处：

降低资源消耗。通过重复利⽤已创建的线程降低线程创建和销毁造成的消耗。

提⾼响应速度。当任务到达时，任务可以不需要等到线程创建就能⽴即执⾏。

提⾼线程的可管理性。线程是稀缺资源，如果⽆限制的创建，不仅会消耗系统资源，还会降低系统的稳定性，使⽤线程池可以进⾏统⼀的分配，调优和监控。

线程池⼀般⽤于执⾏多个不相关联的耗时任务，没有多线程的情况下，任务顺序执⾏，使⽤了线程池的话可让多个不相关联的任务同时执⾏。

# 8、线程池配置⽆界队列了之后，拒绝策略怎么搞，什么时候⽤到⽆界对列？

线程池配置⽆界队列了之后，拒绝策略其实就失去了意义，因为⽆论有多少任务提交到线程池，都会被放⼊队列中等待执⾏，不会触发拒绝策略。不过，这样可能堆积⼤量的请求，从⽽导致 OOM。因此，⼀般不推荐使⽤误解队列。

假设不是⽆界队列的话，如果当前同时运⾏的线程数量达到最⼤线程数量并且队列也已经被放满了任务时， ThreadPoolTaskExecutor 定义⼀些拒绝策略:

ThreadPoolExecutor.AbortPolicy ：抛出 RejectedExecutionException 来拒绝新任务的处理。

ThreadPoolExecutor.CallerRunsPolicy ：调⽤执⾏⾃⼰的线程运⾏任务，也就是直接在调⽤ execute ⽅法的线程中运⾏( run )被拒绝的任务，如果执⾏程序已关闭，则会丢弃该任务。因此这种策略会降低对于新任务提交速度，影响程序的整体性能。如果您的应⽤程序可以承受此延迟并且你要求任何⼀个任务请求都要被执⾏的话，你可以选择这个策略。

ThreadPoolExecutor.DiscardPolicy ：不处理新任务，直接丢弃掉。

ThreadPoolExecutor.DiscardOldestPolicy ：此策略将丢弃最早的未处理的任务请求。

举个例⼦：

Spring 通过 ThreadPoolTaskExecutor 或者我们直接通过 ThreadPoolExecutor 的构造函数创建线程池的时候，当我们不指定 RejectedExecutionHandler 饱和策略的话来配置线程池的时候默认使⽤的是

ThreadPoolExecutor.AbortPolicy 。在默认情况下， ThreadPoolExecutor 将抛出 RejectedExecutionException 来拒绝新来的任务 ，这代表你将丢失对这个任务的处理。 对于可伸缩的应⽤程序，建议使⽤

ThreadPoolExecutor.CallerRunsPolicy 。当最⼤池被填满时，此策略为我们提供可伸缩队列（这个直接查看ThreadPoolExecutor 的构造函数源码就可以看出，⽐较简单的原因，这⾥就不贴代码了）。

# 9、MVCC 讲⼀下

MVCC 是多版本并发控制⽅法，即对⼀份数据会存储多个版本，通过事务的可⻅性来保证事务能看到⾃⼰应该看到的版本。通常会有⼀个全局的版本分配器来为每⼀⾏数据设置版本号，版本号是唯⼀的。

MVCC 在 MySQL 中实现所依赖的⼿段主要是: 隐藏字段、read view、undo log。

undo log : undo log ⽤于记录某⾏数据的多个版本的数据。

read view 和 隐藏字段 : ⽤来判断当前版本数据的可⻅性。

关于 InnoDB 对 MVCC 的具体实现可以看这篇⽂章：InnoDB 存储引擎对 MVCC 的实现 。

# 10、事务特性、隔离级别

关系型数据库（例如： MySQL 、 SQL Server 、 Oracle 等）事务都有 ACID 特性：

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/d2f51c72-bcb8-49f2-bfb3-f3d95b830e1c/98521da18c5e737fe71bc2f95468b688cc7b30d6ff9d244c289190ef9bdf95a7.jpg)


1. 原⼦性（ Atomicity ）：事务是最⼩的执⾏单位，不允许分割。事务的原⼦性确保动作要么全部完成，要么完全不起作⽤；

2. ⼀致性（ Consistency ）：执⾏事务前后，数据保持⼀致，例如转账业务中，⽆论事务是否成功，转账者和收款⼈的总额应该是不变的；

3. 隔离性（ Isolation ）：并发访问数据库时，⼀个⽤户的事务不被其他事务所⼲扰，各并发事务之间数据库是独⽴的；

4. 持久性（ Durability ）：⼀个事务被提交之后。它对数据库中数据的改变是持久的，即使数据库发⽣故障也不应该对其有任何影响。

这⾥要额外补充⼀点：只有保证了事务的持久性、原⼦性、隔离性之后，⼀致性才能得到保障。也就是说 A、I、D 是⼿段，C 是⽬的！ 想必⼤家也和我⼀样，被 ACID 这个概念被误导了很久! 我也是看周志明⽼师的公开课《周志明的软件架构课》才搞清楚的（多看好书！！！）。

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/d2f51c72-bcb8-49f2-bfb3-f3d95b830e1c/7e1127c5c4969a4d98073471e7e45609f349a2f10e751ea5d8d7d3ced07b1a76.jpg)


另外，DDIA 也就是 《Designing Data-Intensive Application（数据密集型应⽤系统设计）》 的作者在他的这本书中如是说：

Atomicity, isolation, and durability are properties of the database, whereas consis‐tency (in the ACID sense) is a property of the application. The application may relyon the database’s atomicity and isolation properties in order to achieve consistency,but it’s not up to the database alone.

翻译过来的意思是：原⼦性，隔离性和持久性是数据库的属性，⽽⼀致性（在 ACID 意义上）是应⽤程序的属性。应⽤可能依赖数据库的原⼦性和隔离属性来实现⼀致性，但这并不仅取决于数据库。因此，字⺟ C 不属于 ACID 。

# SQL 标准定义了四个隔离级别：

READ-UNCOMMITTED(读取未提交)：最低的隔离级别，允许读取尚未提交的数据变更，可能会导致脏读、幻读或不可重复读。

READ-COMMITTED(读取已提交)：允许读取并发事务已经提交的数据，可以阻⽌脏读，但是幻读或不可重复读仍有可能发⽣。

REPEATABLE-READ(可重复读)：对同⼀字段的多次读取结果都是⼀致的，除⾮数据是被本身事务⾃⼰所修改，可以阻⽌脏读和不可重复读，但幻读仍有可能发⽣。

SERIALIZABLE(可串⾏化)：最⾼的隔离级别，完全服从 ACID 的隔离级别。所有的事务依次逐个执⾏，这样事务之间就完全不可能产⽣⼲扰，也就是说，该级别可以防⽌脏读、不可重复读以及幻读。

<table><tr><td>隔离级别</td><td>脏读</td><td>不可重复读</td><td>幻读</td></tr><tr><td>READ-UNCOMMITTED</td><td>✓</td><td>✓</td><td>✓</td></tr><tr><td>READ-COMMITTED</td><td>×</td><td>✓</td><td>✓</td></tr><tr><td>REPEATABLE-READ</td><td>×</td><td>×</td><td>✓</td></tr><tr><td>SERIALIZABLE</td><td>×</td><td>×</td><td>×</td></tr></table>

MySQL 的隔离级别基于锁和 MVCC 机制共同实现的。

SERIALIZABLE 隔离级别是通过锁来实现的，READ-COMMITTED 和 REPEATABLE-READ 隔离级别是基于MVCC 实现的。不过， SERIALIZABLE 之外的其他隔离级别可能也需要⽤到锁机制，就⽐如 REPEATABLE-READ 在当前读情况下需要使⽤加锁读来保证不会出现幻读。

MySQL InnoDB 存储引擎的默认⽀持的隔离级别是 REPEATABLE-READ（可重读）。我们可以通过 SELECT@@tx_isolation; 命令来查看，MySQL 8.0 该命令改为 SELECT @@transaction_isolation;

```txt
mysql> SELECT @@tx_isolation;  
+  
| @@tx_isolation |  
+  
| REPEATABLE-READ |
```


# 11、Redis 有了解过么?zset 的底层数据结构？

Redis 是⼀个基于 C 语⾔开发的开源数据库（BSD 许可），与传统数据库不同的是 Redis 的数据是存在内存中的（内存数据库），读写速度⾮常快，被⼴泛应⽤于缓存⽅向。并且，Redis 存储的是 KV 键值对数据。

为了满⾜不同的业务场景，Redis 内置了多种数据类型实现（⽐如 String、Hash、Sorted Set、Bitmap、HyperLogLog、GEO）。并且，Redis 还⽀持事务、持久化、Lua 脚本、多种开箱即⽤的集群⽅案（RedisSentinel、Redis Cluster）。

Redis 没有外部依赖，Linux 和 OS X 是 Redis 开发和测试最多的两个操作系统，官⽅推荐⽣产环境使⽤ Linux 部署 Redis。

Redis 内部做了⾮常多的性能优化，⽐较重要的有下⾯ 3 点：

1. Redis 基于内存，内存的访问速度是磁盘的上千倍；

2. Redis 基于 Reactor 模式设计开发了⼀套⾼效的事件处理模型，主要是单线程事件循环和 IO 多路复⽤（Redis 线程模式后⾯会详细介绍到）；

3. Redis 内置了多种优化过后的数据结构实现，性能⾮常⾼。

# Redis 除了做缓存，还能做什么？

分布式锁：通过 Redis 来做分布式锁是⼀种⽐较常⻅的⽅式。通常情况下，我们都是基于 Redisson 来实现分布式锁。关于 Redis 实现分布式锁的详细介绍，可以看我写的这篇⽂章：分布式锁详解 。

限流：⼀般是通过 Redis $^ +$ Lua 脚本的⽅式来实现限流。相关阅读：《我司⽤了 6 年的 Redis 分布式限流器，可以说是⾮常厉害了！》。

消息队列：Redis ⾃带的 list 数据结构可以作为⼀个简单的队列使⽤。Redis 5.0 中增加的 stream 类型的数据结构更加适合⽤来做消息队列。它⽐较类似于 Kafka，有主题和消费组的概念，⽀持消息持久化以及 ACK 机制。

延时队列：Redisson 内置了延时队列（基于 sorted set 实现的） 。

分布式 Session ：利⽤ string 或者 hash 保存 Session 数据，所有的服务器都可以访问。

复杂业务场景：通过 Redis 以及 Redis 扩展（⽐如 Redisson）提供的数据结构，我们可以很⽅便地完成很多复杂的业务场景⽐如通过 bitmap 统计活跃⽤户、通过 sorted set 维护排⾏榜。

Redis 共有 5 种基本数据结构：String（字符串）、List（列表）、Set（集合）、Hash（散列）、Zset（有序集合）。

这 5 种数据结构是直接提供给⽤户使⽤的，是数据的保存形式，其底层实现主要依赖这 8 种数据结构：简单动态字符串（SDS）、LinkedList（双向链表）、Dict（哈希表/字典）、SkipList（跳跃表）、Intset（整数集合）、ZipList（压缩列表）、QuickList（快速列表）。

Redis 基本数据结构的底层数据结构实现如下：

<table><tr><td>String</td><td>List</td><td>Hash</td><td>Set</td><td>Zset</td></tr><tr><td>SDS</td><td>LinkedList/ZipList/QuickList</td><td>Dict、ZipList</td><td>Dict、Intset</td><td>ZipList、SkipList</td></tr></table>

Redis 3.2 之前，List 底层实现是 LinkedList 或者 ZipList。 Redis 3.2 之后，引⼊了 LinkedList 和 ZipList 的结合QuickList，List 的底层实现变为 QuickList。从 Redis 7.0 开始， ZipList 被 ListPack 取代。

