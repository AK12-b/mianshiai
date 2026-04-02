# JavaGuide面试突击

JVM篇


1. Java 内存区域详解

2. JVM 垃圾回收详解

3. 类⽂件结构详解

4. 类加载过程详解

5. 类加载器详解

# Java 内存区域



# Java 内存区域（运⾏时数据区）的组成

Java 虚拟机在执⾏ Java 程序的过程中会把它管理的内存划分成若⼲个不同的数据区域。

JDK 1.8 和之前的版本略有不同，我们这⾥以 JDK 1.7 和 JDK 1.8 这两个版本为例介绍。

JDK 1.7：

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/c4eb0d4e-b27e-4621-902c-d1513fa8f90b/9961ad01026cb76689d1a8360b7652688eea1c6ce070e8bdb98124d79ab50662.jpg)


![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/c4eb0d4e-b27e-4621-902c-d1513fa8f90b/3ee24dbebe1eca1289a4a59eb26bab3d56b71c91449b349c493c591c773fe94e.jpg)


# 线程私有的：

程序计数器

. 虚拟机栈

本地⽅法栈

# 线程共享的：

堆

. ⽅法区

直接内存 (⾮运⾏时数据区的⼀部分)

Java 虚拟机规范对于运⾏时数据区域的规定是相当宽松的。以堆为例：堆可以是连续空间，也可以不连续。堆的⼤⼩可以固定，也可以在运⾏时按需扩展 。虚拟机实现者可以使⽤任何垃圾回收算法管理堆，甚⾄完全不进⾏垃圾收集也是可以的。

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/c4eb0d4e-b27e-4621-902c-d1513fa8f90b/361e51be8f36232b40a6bbbb558bc2368537e276707f6b890daa8bb9c4a17e9f.jpg)


# 哪个区域不会出现 OutOfMemoryError？

程序计数器是唯⼀⼀个不会出现 OutOfMemoryError 的内存区域，它的⽣命周期随着线程的创建⽽创建，随着线程的结束⽽死亡。

程序计数器是⼀块较⼩的内存空间，可以看作是当前线程所执⾏的字节码的⾏号指示器。字节码解释器⼯作时通过改变这个计数器的值来选取下⼀条需要执⾏的字节码指令，分⽀、循环、跳转、异常处理、线程恢复等功能都需要依赖这个计数器来完成。

另外，为了线程切换后能恢复到正确的执⾏位置，每条线程都需要有⼀个独⽴的程序计数器，各线程之间计数器互不影响，独⽴存储，我们称这类内存区域为“线程私有”的内存。

从上⾯的介绍中我们知道了程序计数器主要有两个作⽤：

字节码解释器通过改变程序计数器来依次读取指令，从⽽实现代码的流程控制，如：顺序执⾏、选择、循环、异常处理。

在多线程的情况下，程序计数器⽤于记录当前线程执⾏的位置，从⽽当线程被切换回来的时候能够知道该线程上次运⾏到哪⼉了。

# 程序运⾏中栈可能会出现什么错误？

StackOverFlowError ： 如果栈的内存⼤⼩不允许动态扩展，那么当线程请求栈的深度超过当前Java 虚拟机栈的最⼤深度的时候，就抛出 StackOverFlowError 错误。

OutOfMemoryError ： 如果栈的内存⼤⼩可以动态扩展， 那么当虚拟机在动态扩展栈时⽆法申请到⾜够的内存空间，则抛出 OutOfMemoryError 异常。

在《Java虚拟机规范》中，对这个内存区域规定了两类异常状况：如果线程请求的栈深度大于虚拟机所允许的深度，将抛出StackOverflowError异常；如果Java虚拟机栈容量可以动态扩展[2]，当栈扩展时无法申请到足够的内存会抛出OutOfMemoryError异常。

[1]栈帧是方法运行期很重要的基础数据结构，在本书的第8章中还会对帧进行详细讲解。

[2] HotSpot虚拟机的栈容量是不可以动态扩展的，以前的Classic虚拟机倒是可以。所以在HotSpot虚拟机上是不会由于虚拟机栈无法扩展而导致OutOfMemoryError异常——只要线程申请栈空间成功了就不会有OOM，但是如果申请时就失败，仍然是会出现OOM异常的，后面的实战中笔者也演示了这种情况。本书第2版时这里的描述是有误的，请阅读过第2版的读者特别注意。

# 堆内存的作⽤和组成

Java 虚拟机所管理的内存中最⼤的⼀块，Java 堆是所有线程共享的⼀块内存区域，在虚拟机启动时创建。此内存区域的唯⼀⽬的就是存放对象实例，⼏乎所有的对象实例以及数组都在这⾥分配内存。

Java 世界中“⼏乎”所有的对象都在堆中分配，但是，随着 JIT 编译器的发展与逃逸分析技术逐渐成熟，栈上分配、标量替换优化技术将会导致⼀些微妙的变化，所有的对象都分配到堆上也渐渐变得不那么“绝对”了。从 JDK 1.7 开始已经默认开启逃逸分析，如果某些⽅法中的对象引⽤没有被返回或者未被外⾯使⽤（也就是未逃逸出去），那么对象可以直接在栈上分配内存。

Java 堆是垃圾收集器管理的主要区域，因此也被称作 GC 堆（Garbage Collected Heap）。从垃圾回收的⻆度，由于现在收集器基本都采⽤分代垃圾收集算法，所以 Java 堆还可以细分为：新⽣代和⽼年代；再细致⼀点有：Eden、Survivor、Old 等空间。进⼀步划分的⽬的是更好地回收内存，或者更快地分配内存。

在 JDK 7 版本及 JDK 7 版本之前，堆内存被通常分为下⾯三部分：

1. 新⽣代内存(Young Generation)

2. ⽼⽣代(Old Generation)

3. 永久代(Permanent Generation)

下图所示的 Eden 区、两个 Survivor 区 S0 和 S1 都属于新⽣代，中间⼀层属于⽼年代，最下⾯⼀层属于永久代。

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/c4eb0d4e-b27e-4621-902c-d1513fa8f90b/43d54f4bec46161b017023b87fd1186e925f7ed50eed79277a25bdb8da8e5905.jpg)


![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/c4eb0d4e-b27e-4621-902c-d1513fa8f90b/16abac858a27aa04ad1429eef732ea85b6ee66c2b59de911f19a61d51ed92500.jpg)


![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/c4eb0d4e-b27e-4621-902c-d1513fa8f90b/f033b29331ae649b130b043c8172e049557efa147156dbd2c47bd91c59b1f51f.jpg)


![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/c4eb0d4e-b27e-4621-902c-d1513fa8f90b/354f1122db89bae29a1aa8add52179d971fa87528d096746e955f650b7c9b22c.jpg)


![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/c4eb0d4e-b27e-4621-902c-d1513fa8f90b/5f10bd3cfc55cf1edb68b8b8dbef1a0843ec473fcaa7a6d1dfbea6ba2ca2d01e.jpg)


![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/c4eb0d4e-b27e-4621-902c-d1513fa8f90b/21de464098f0a85cf65f93a356a648385bb57db2c4e42715a1e0dbdea1c2a678.jpg)


JDK 8 版本之后 PermGen(永久代) 已被 Metaspace(元空间) 取代，元空间使⽤的是本地内存。（我会在⽅法区这部分内容详细介绍到）。

⼤部分情况，对象都会⾸先在 Eden 区域分配，在⼀次新⽣代垃圾回收后，如果对象还存活，则会进⼊ S0 或者 S1，并且对象的年龄还会加 1(Eden 区- $\Bumpeq$ Survivor 区后对象的初始年龄变为 1)，当它的年龄增加到⼀定程度（默认为 15 岁），就会被晋升到⽼年代中。对象晋升到⽼年代的年龄阈值，可以通过参数 -XX:MaxTenuringThreshold 来设置。不过，设置的值应该在 0-15，否则会爆出以下错误：

MaxTenuringThreshold of 20 is invalid; must be between 0 and 15

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/c4eb0d4e-b27e-4621-902c-d1513fa8f90b/56a8dd5263296cfa11e2d5d782c8e13b5ae0f375694f7aac74e13968bfc66b32.jpg)


# 程序运⾏中堆可能会出现什么错误？

堆这⾥最容易出现的就是 OutOfMemoryError 错误，并且出现这种错误之后的表现形式还会有⼏种，⽐如：

1 java.lang.OutOfMemoryError: GC Overhead Limit Exceeded ：当 JVM 花太多时间执⾏垃圾回收并且只能回收很少的堆空间时，就会发⽣此错误。

2. java.lang.OutOfMemoryError: Java heap space :假如在创建新的对象时, 堆内存中的空间不⾜以存放新创建的对象, 就会引发此错误。(和配置的最⼤堆内存有关，且受制于物理内存⼤⼩。最⼤堆内存可通过 -Xmx 参数配置，若没有特别配置，将会使⽤默认值，详⻅：Default Java 8max heap size)

3.



# 为什么要将永久代 (PermGen) 替换为元空间 (MetaSpace) 呢?

下图来⾃《深⼊理解 Java 虚拟机》第 3 版 2.2.5

虚拟机时，但因为两者对方法区实现的差异而面临诸多困难。考虑到HotSpot未来的发展，在JDK6的时候HotSpot开发团队就有放弃永久代，逐步改为采用本地内存（NativeMemory）来实现方法区的计划了[1]，到了JDK7的HotSpot，已经把原本放在永久代的字符串常量池、静态变量等移出，而到了JDK8，终于完全废弃了永久代的概念，改用与JRockit、J9一样在本地内存中实现的元空间（Meta-space）来代替，把JDK7中永久代还剩余的内容（主要是类型信息）全部移到元空间中。

1、整个永久代有⼀个 JVM 本身设置的固定⼤⼩上限，⽆法进⾏调整（也就是受到 JVM 内存的限制），⽽元空间使⽤的是本地内存，受本机可⽤内存的限制，虽然元空间仍旧可能溢出，但是⽐原来出现的⼏率会更⼩。

当元空间溢出时会得到如下错误： java.lang.OutOfMemoryError: MetaSpace

你可以使⽤ -XX：MaxMetaspaceSize 标志设置最⼤元空间⼤⼩，默认值为 unlimited，这意味着它只受系统内存的限制。 -XX：MetaspaceSize 调整标志定义元空间的初始⼤⼩如果未指定此标志，则Metaspace 将根据运⾏时的应⽤程序需求动态地重新调整⼤⼩。

2、元空间⾥⾯存放的是类的元数据，这样加载多少类的元数据就不由 MaxPermSize 控制了, ⽽由系统的实际可⽤空间来控制，这样能加载的类就更多了。

3、在 JDK8，合并 HotSpot 和 JRockit 的代码时, JRockit 从来没有⼀个叫永久代的东⻄, 合并之后就没有必要额外的设置这么⼀个永久代的地⽅了。

4、永久代会为 GC 带来不必要的复杂度，并且回收效率偏低。

# ⽅法区常⽤参数有哪些？

JDK 1.8 之前永久代还没被彻底移除的时候通常通过下⾯这些参数来调节⽅法区⼤⼩。

```txt
-XX:PermSize=N //方法区（永久代）初始大小  
-XX:MaxPermSize=N //方法区（永久代）最大大小，超过这个值将会抛出 OutOfMemoryError 异常：java.lang.OfMemoryError:PermGen
```

相对⽽⾔，垃圾收集⾏为在这个区域是⽐较少出现的，但并⾮数据进⼊⽅法区后就“永久存在”了。

JDK 1.8 的时候，⽅法区（HotSpot 的永久代）被彻底移除了（JDK1.7 就已经开始了），取⽽代之是元空间，元空间使⽤的是本地内存。下⾯是⼀些常⽤参数：

```txt
- XX:Metaspacesize=N //设置 Metaspace 的初始（和最小大小）
- XX:MaxMetaspacesize=N //设置 Metaspace 的最大大小
```

与永久代很⼤的不同就是，如果不指定⼤⼩的话，随着更多类的创建，虚拟机会耗尽所有可⽤的系统内存。

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/c4eb0d4e-b27e-4621-902c-d1513fa8f90b/cd475e6c54ee93380d9a71d06c20aaadd13365eceb78e8c49461dab2956fff5a.jpg)


# 字符串常量池的作⽤是？

字符串常量池 是 JVM 为了提升性能和减少内存消耗针对字符串（String 类）专⻔开辟的⼀块区域，主要⽬的是为了避免字符串的重复创建。

```txt
// 在字符串常量池中创建字符串对象 "ab"  
// 将字符串对象 "ab" 的引用赋值给 aa  
String aa = "ab";  
// 直接返回字符串常量池中字符串对象 "ab", 赋值给引用 bb  
String bb = "ab";  
System.out.println(aa == bb); // true
```

HotSpot 虚拟机中字符串常量池的实现是 src/hotspot/share/classfile/stringTable.cpp , StringTable 可以简单理解为⼀个固定⼤⼩的 HashTable ，容量为 StringTableSize （可以通过 -XX:StringTableSize 参数来设置），保存的是字符串（key）和 字符串对象的引⽤（value）的映射关系，字符串对象的引⽤指向堆中的字符串对象。

JDK1.7 之前，字符串常量池存放在永久代。JDK1.7 字符串常量池和静态变量从永久代移动到了Java 堆中。

# JDK1.6

方法区 (PermGen)

类型信息

字段信息

运行时常量池

方法表

JIT代码缓存

静态变量

字符串常量池

堆

# JDK1.7

方法区 (PermGen)

类型信息

运行时常量池

字段信息

方法表

JIT代码缓存

堆

静态变量

字符串常量池

# JDK 1.7 为什么要将字符串常量池移动到堆中？

主要是因为永久代（⽅法区实现）的 GC 回收效率太低，只有在整堆收集 (Full GC)的时候才会被执⾏ GC。Java 程序中通常会有⼤量的被创建的字符串等待回收，将字符串常量池放到堆中，能够更⾼效及时地回收字符串内存。

# 直接内存的作⽤是？

直接内存是⼀种特殊的内存缓冲区，并不在 Java 堆或⽅法区中分配的，⽽是通过 JNI 的⽅式在本地内存上分配的。

直接内存并不是虚拟机运⾏时数据区的⼀部分，也不是虚拟机规范中定义的内存区域，但是这部分内存也被频繁地使⽤。⽽且也可能导致 OutOfMemoryError 错误出现。

JDK1.4 中新加⼊的 NIO（Non-Blocking I/O，也被称为 New I/O），引⼊了⼀种基于通道（Channel）与缓存区（Buffer）的 I/O ⽅式，它可以直接使⽤ Native 函数库直接分配堆外内存，然后通过⼀个存储在 Java 堆中的 DirectByteBuffer 对象作为这块内存的引⽤进⾏操作。这样就能在⼀些场景中显著提⾼性能，因为避免了在 Java 堆和 Native 堆之间来回复制数据。

直接内存的分配不会受到 Java 堆的限制，但是，既然是内存就会受到本机总内存⼤⼩以及处理器寻址空间的限制。

类似的概念还有 堆外内存 。在⼀些⽂章中将直接内存等价于堆外内存，个⼈觉得不是特别准确。

堆外内存就是把内存对象分配在堆外的内存，这些内存直接受操作系统管理（⽽不是虚拟机），这样做的结果就是能够在⼀定程度上减少垃圾回收对应⽤程序造成的影响。

# Java 对象的创建过程

JVM（HotSpot 虚拟机）中对象的创建过程主要分为以下五步：

1. 类加载检查：虚拟机执⾏ new 指令时，先检查常量池中对应类的符号引⽤是否已加载、解析和初始化，未完成则先执⾏类加载过程。

2. 分配内存：类加载通过后，根据类加载确定的对象⼤⼩从 Java 堆划分内存，分配⽅式有 “指针碰撞”（适⽤于堆内存规整，如 Serial/ParNew 收集器）和 “空闲列表”（适⽤于堆内存不规整，如 CMS 收集器）；为保证线程安全，采⽤ CAS $^ +$ 失败重试或 TLAB（线程本地分配缓冲）机制。

3. 初始化零值：将分配的内存空间（除对象头外）初始化为零值，确保 Java 代码中未赋初始值的实例字段可直接使⽤对应类型的零值。

4. 设置对象头：在对象头中记录类元数据信息、哈希码、GC 分代年龄、锁状态等必要信息，具体设置依虚拟机运⾏状态（如是否启⽤偏向锁）⽽定。

5. 执⾏ init ⽅法：虚拟机视⻆下对象已创建，但需执⾏ <init> ⽅法按程序员定义完成初始化，最终⽣成可⽤对象。

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/c4eb0d4e-b27e-4621-902c-d1513fa8f90b/b45a196f9f40699f6b7b89735bc46f561cb2352c8e2dde952209d2e78e05a3ec.jpg)


# 对象访问定位的⽅式有哪些？

建⽴对象就是为了使⽤对象，我们的 Java 程序通过栈上的 reference 数据来操作堆上的具体对象。对象的访问⽅式由虚拟机实现⽽定，⽬前主流的访问⽅式有：使⽤句柄、直接指针。

# 句柄

如果使⽤句柄的话，那么 Java 堆中将会划分出⼀块内存来作为句柄池，reference 中存储的就是对象的句柄地址，⽽句柄中包含了对象实例数据与对象类型数据各⾃的具体地址信息。


局部变量表


![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/c4eb0d4e-b27e-4621-902c-d1513fa8f90b/dab83a201f3b20c7e539697a411d06ae8c3fadbe13f9b1efb416546badda94a3.jpg)


# 直接指针

如果使⽤直接指针访问，reference 中存储的直接就是对象的地址。


局部变量表


![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/c4eb0d4e-b27e-4621-902c-d1513fa8f90b/992d74753a79a03dc0e6431b44e1d058db6f500f55bc4fc369891bad79f47c26.jpg)


这两种对象访问⽅式各有优势。使⽤句柄来访问的最⼤好处是 reference 中存储的是稳定的句柄地址，在对象被移动时只会改变句柄中的实例数据指针，⽽ reference 本身不需要修改。使⽤直接指针访问⽅式最⼤的好处就是速度快，它节省了⼀次指针定位的时间开销。

HotSpot 虚拟机主要使⽤的就是这种⽅式来进⾏对象访问。

# JVM 垃圾回收

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/c4eb0d4e-b27e-4621-902c-d1513fa8f90b/68412671723cc811a96cd6e4f45f10d9c32a3d507ddf4a83b4e692a3e58f58a0.jpg)


# 如何判断对象是否死亡

堆中⼏乎放着所有的对象实例，对堆垃圾回收前的第⼀步就是要判断哪些对象已经死亡（即不能再被任何途径使⽤的对象）。

# 引⽤计数法

给对象中添加⼀个引⽤计数器：

每当有⼀个地⽅引⽤它，计数器就加 1；

当引⽤失效，计数器就减 1；

任何时候计数器为 0 的对象就是不可能再被使⽤的。

这个⽅法实现简单，效率⾼，但是⽬前主流的虚拟机中并没有选择这个算法来管理内存，其最主要的原因是它很难解决对象之间循环引⽤的问题。

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/c4eb0d4e-b27e-4621-902c-d1513fa8f90b/f5d4eac4e7c47199ba64920574f6dca2a221f0d7f48efdc4803141496977a412.jpg)


所谓对象之间的相互引⽤问题，如下⾯代码所示：除了对象 objA 和 objB 相互引⽤着对⽅之外，这两个对象之间再⽆任何引⽤。但是他们因为互相引⽤对⽅，导致它们的引⽤计数器都不为0，于是引⽤计数算法⽆法通知 GC 回收器回收他们。

```java
public class ReferenceCountingGc {
    Object instance = null;
    public static void main(String[] args) {
        ReferenceCountingGc objA = new ReferenceCountingGc();
        ReferenceCountingGc objB = new ReferenceCountingGc();
        objA.instance = objB;
        objB.instance = objA;
        objA = null;
        objB = null;
    }
}
```

# 可达性分析算法

这个算法的基本思想就是通过⼀系列的称为 “GC Roots” 的对象作为起点，从这些节点开始向下搜索，节点所⾛过的路径称为引⽤链，当⼀个对象到 GC Roots 没有任何引⽤链相连的话，则证明此对象是不可⽤的，需要被回收。

下图中的 Object $6 \sim$ Object 10 之间虽有引⽤关系，但它们到 GC Roots 不可达，因此为需要被回收的对象。

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/c4eb0d4e-b27e-4621-902c-d1513fa8f90b/d5980475ee92a2ff12e97e1af37918f802e888267cbbdf3a5c67aaebd8eff099.jpg)


# 哪些对象可以作为 GC Roots 呢？

虚拟机栈(栈帧中的局部变量表)中引⽤的对象

本地⽅法栈(Native ⽅法)中引⽤的对象

⽅法区中类静态属性引⽤的对象

⽅法区中常量引⽤的对象

所有被同步锁持有的对象

JNI（Java Native Interface）引⽤的对象

# 对象可以被回收，就代表⼀定会被回收吗？

即使在可达性分析法中不可达的对象，也并⾮是“⾮死不可”的，这时候它们暂时处于“缓刑阶段”，要真正宣告⼀个对象死亡，⾄少要经历两次标记过程；可达性分析法中不可达的对象被第⼀次标记并且进⾏⼀次筛选，筛选的条件是此对象是否有必要执⾏ finalize ⽅法。当对象没有覆盖 finalize ⽅法，或 finalize ⽅法已经被虚拟机调⽤过时，虚拟机将这两种情况视为没有必要执⾏。

被判定为需要执⾏的对象将会被放在⼀个队列中进⾏第⼆次标记，除⾮这个对象与引⽤链上的任何⼀个对象建⽴关联，否则就会被真的回收。

Object 类中的 finalize ⽅法⼀直被认为是⼀个糟糕的设计，成为了 Java 语⾔的负担，影响了 Java 语⾔的安全和 GC 的性能。JDK9 版本及后续版本中各个类中的 finalize ⽅法会被逐渐弃⽤移除。忘掉它的存在吧！

参考：

JEP 421: Deprecate Finalization for Removal

是时候忘掉 finalize ⽅法了

# 常⻅的引⽤类型有哪些？

⽆论是通过引⽤计数法判断对象引⽤数量，还是通过可达性分析法判断对象的引⽤链是否可达，判定对象的存活都与“引⽤”有关。

JDK1.2 之前，Java 中引⽤的定义很传统：如果 reference 类型的数据存储的数值代表的是另⼀块内存的起始地址，就称这块内存代表⼀个引⽤。

JDK1.2 以后，Java 对引⽤的概念进⾏了扩充，将引⽤分为强引⽤、软引⽤、弱引⽤、虚引⽤四种（引⽤强度逐渐减弱），强引⽤就是 Java 中普通的对象，⽽软引⽤、弱引⽤、虚引⽤在 JDK 中定义的类分别是 SoftReference 、 WeakReference 、 PhantomReference 。

# 引用类型总结

强引用

(StrongReference)

虚引用

(PhantomReference)

软引用

(SoftReference)

弱引用

(WeakReference)

# 1．强引⽤（StrongReference）

强引⽤实际上就是程序代码中普遍存在的引⽤赋值，这是使⽤最普遍的引⽤，其代码如下

```txt
String strongReference = new String("abc");
```

如果⼀个对象具有强引⽤，那就类似于必不可少的⽣活⽤品，垃圾回收器绝不会回收它。当内存空间不⾜，Java 虚拟机宁愿抛出 OutOfMemoryError 错误，使程序异常终⽌，也不会靠随意回收具有强引⽤的对象来解决内存不⾜问题。

# 2．软引⽤（SoftReference）

如果⼀个对象只具有软引⽤，那就类似于可有可⽆的⽣活⽤品。软引⽤代码如下

```dart
// 软引用  
String str = new String("abc");  
SoftReference<String> softReference = new SoftReference<String>(str);
```

如果内存空间⾜够，垃圾回收器就不会回收它，如果内存空间不⾜了，就会回收这些对象的内存。只要垃圾回收器没有回收它，该对象就可以被程序使⽤。软引⽤可⽤来实现内存敏感的⾼速缓存。

软引⽤可以和⼀个引⽤队列（ReferenceQueue）联合使⽤，如果软引⽤所引⽤的对象被垃圾回收，JAVA 虚拟机就会把这个软引⽤加⼊到与之关联的引⽤队列中。

# 3．弱引⽤（WeakReference）

如果⼀个对象只具有弱引⽤，那就类似于可有可⽆的⽣活⽤品。弱引⽤代码如下：

```txt
String str = new String("abc");  
WeakReference<String> weakReference = new WeakReference<> (str);  
str = null; //str变成软引用，可以被收集
```

弱引⽤与软引⽤的区别在于：只具有弱引⽤的对象拥有更短暂的⽣命周期。在垃圾回收器线程扫描它所管辖的内存区域的过程中，⼀旦发现了只具有弱引⽤的对象，不管当前内存空间⾜够与否，都会回收它的内存。不过，由于垃圾回收器是⼀个优先级很低的线程， 因此不⼀定会很快发现那些只具有弱引⽤的对象。

弱引⽤可以和⼀个引⽤队列（ReferenceQueue）联合使⽤，如果弱引⽤所引⽤的对象被垃圾回收，Java 虚拟机就会把这个弱引⽤加⼊到与之关联的引⽤队列中。

# 4．虚引⽤（PhantomReference）

"虚引⽤"顾名思义，就是形同虚设，与其他⼏种引⽤都不同，虚引⽤并不会决定对象的⽣命周期。如果⼀个对象仅持有虚引⽤，那么它就和没有任何引⽤⼀样，在任何时候都可能被垃圾回收。虚引⽤代码如下：

```txt
String str = new String("abc");  
ReferenceQueue queue = new ReferenceQueue();  
// 创建虚引用，要求必须与一个引用队列关联  
PhantomReference pr = new PhantomReference(str, queue);
```

虚引⽤主要⽤来跟踪对象被垃圾回收的活动。

虚引⽤与软引⽤和弱引⽤的⼀个区别在于： 虚引⽤必须和引⽤队列（ReferenceQueue）联合使⽤。当垃圾回收器准备回收⼀个对象时，如果发现它还有虚引⽤，就会在回收对象的内存之前，把这个虚引⽤加⼊到与之关联的引⽤队列中。程序可以通过判断引⽤队列中是否已经加⼊了虚引⽤，来了解被引⽤的对象是否将要被垃圾回收。程序如果发现某个虚引⽤已经被加⼊到引⽤队列，那么就可以在所引⽤的对象的内存被回收之前采取必要的⾏动。

特别注意，在程序设计中⼀般很少使⽤弱引⽤与虚引⽤，使⽤软引⽤的情况较多，这是因为软引⽤可以加速 JVM 对垃圾内存的回收速度，可以维护系统的运⾏安全，防⽌内存溢出（OutOfMemory）等问题的产⽣。

# 如何判断⼀个类是⽆⽤的类？

⽅法区主要回收的是⽆⽤的类，那么如何判断⼀个类是⽆⽤的类的呢？

判定⼀个常量是否是“废弃常量”⽐较简单，⽽要判定⼀个类是否是“⽆⽤的类”的条件则相对苛刻许多。类需要同时满⾜下⾯ 3 个条件才能算是 “⽆⽤的类”：

该类所有的实例都已经被回收，也就是 Java 堆中不存在该类的任何实例。

加载该类的 ClassLoader 已经被回收。

该类对应的 java.lang.Class 对象没有在任何地⽅被引⽤，⽆法在任何地⽅通过反射访问该类的⽅法。

虚拟机可以对满⾜上述 3 个条件的⽆⽤类进⾏回收，这⾥说的仅仅是“可以”，⽽并不是和对象⼀样不使⽤了就会必然被回收。

# 垃圾回收算法有哪些？

# 标记-清除算法

标记-清除（Mark-and-Sweep）算法分为“标记（Mark）”和“清除（Sweep）”阶段：⾸先标记出所有不需要回收的对象，在标记完成后统⼀回收掉所有没有被标记的对象。

它是最基础的收集算法，后续的算法都是对其不⾜进⾏改进得到。这种垃圾收集算法会带来两个明显的问题：

1. 效率问题：标记和清除两个过程效率都不⾼。

2. 空间问题：标记清除后会产⽣⼤量不连续的内存碎⽚。

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/c4eb0d4e-b27e-4621-902c-d1513fa8f90b/adeedffeab5573635daadd91e4f9be59220812a969e141d9895baccb81f09e75.jpg)


关于具体是标记可回收对象还是不可回收对象，众说纷纭，两种说法其实都没问题，我个⼈更倾向于是前者。

如果按照前者的理解，整个标记-清除过程⼤致是这样的：

1. 当⼀个对象被创建时，给⼀个标记位，假设为 0 (false)；

2. 在标记阶段，我们将所有可达对象（或⽤户可以引⽤的对象）的标记位设置为 1 (true)；

3. 扫描阶段清除的就是标记位为 0 (false)的对象。

# 复制算法

为了解决标记-清除算法的效率和内存碎⽚问题，复制（Copying）收集算法出现了。它可以将内存分为⼤⼩相同的两块，每次使⽤其中的⼀块。当这⼀块的内存使⽤完后，就将还存活的对象复制到另⼀块去，然后再把使⽤的空间⼀次清理掉。这样就使每次的内存回收都是对内存区间的⼀半进⾏回收。


内存回收前


![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/c4eb0d4e-b27e-4621-902c-d1513fa8f90b/01534e5480362cdc01ad7baf75e6e5030ec7cf6f34a4db3e0fe1ea3899e437b1.jpg)



内存回收后


![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/c4eb0d4e-b27e-4621-902c-d1513fa8f90b/66ecd8fe598e285720add0b13a9dd8cc4d666ea30a83a743b9c8d2c79ba00573.jpg)


存活对象

可回收内存

可用内存

保留内存

虽然改进了标记-清除算法，但依然存在下⾯这些问题：

可⽤内存变⼩：可⽤内存缩⼩为原来的⼀半。

不适合⽼年代：如果存活对象数量⽐较⼤，复制性能会变得很差。

# 标记-整理算法

标记-整理（Mark-and-Compact）算法是根据⽼年代的特点提出的⼀种标记算法，标记过程仍然与“标记-清除”算法⼀样，但后续步骤不是直接对可回收对象回收，⽽是让所有存活的对象向⼀端移动，然后直接清理掉端边界以外的内存。

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/c4eb0d4e-b27e-4621-902c-d1513fa8f90b/d0b26957e8feca485fb90e10a0fb1489e2c1dc18cf988e3ca686a6abb9ba62d7.jpg)


由于多了整理这⼀步，因此效率也不⾼，适合⽼年代这种垃圾回收频率不是很⾼的场景。

# 分代收集算法

当前虚拟机的垃圾收集都采⽤分代收集算法，这种算法没有什么新的思想，只是根据对象存活周期的不同将内存分为⼏块。⼀般将 Java 堆分为新⽣代和⽼年代，这样我们就可以根据各个年代的特点选择合适的垃圾收集算法。

⽐如在新⽣代中，每次收集都会有⼤量对象死去，所以可以选择”标记-复制“算法，只需要付出少量对象的复制成本就可以完成每次垃圾收集。⽽⽼年代的对象存活⼏率是⽐较⾼的，⽽且没有额外的空间对它进⾏分配担保，所以我们必须选择“标记-清除”或“标记-整理”算法进⾏垃圾收集。

延伸⾯试问题： HotSpot 为什么要分为新⽣代和⽼年代？

根据上⾯的对分代收集算法的介绍回答。

# JDK 1.8 的默认垃圾回收器是？JDK1.9 之后呢？

JDK 1.8 默认垃圾回收器：Parallel Scanvenge（新⽣代） $^ +$ Parallel Old（⽼年代）。 这个组合也被称为 Parallel GC 或 Throughput GC，侧重于吞吐量。

JDK 1.9 及以后默认垃圾回收器：G1 GC (Garbage-First Garbage Collector)。 G1 GC 是⼀个更现代化的垃圾回收器，旨在平衡吞吐量和停顿时间，尤其适⽤于堆内存较⼤的应⽤。

# G1 垃圾回收的过程

G1（Garbage-First）垃圾收集器在 JDK 7 中⾸次引⼊，作为⼀种试验性的垃圾收集器。到了 JDK8，G1 得到了进⼀步的完善和改进，功能基本已经完全实现，成为⼀个稳定、可⽤于⽣产环境的垃圾收集器。

G1 收集器的运作⼤致分为以下⼏个步骤：

初始标记： 短暂停顿（Stop-The-World，STW），标记从 GC Roots 可直接引⽤的对象，即标记所有直接可达的活跃对象

并发标记：与应⽤并发运⾏，标记所有可达对象。 这⼀阶段可能持续较⻓时间，取决于堆的⼤⼩和对象的数量。

最终标记： 短暂停顿（STW），处理并发标记阶段结束后残留的少量未处理的引⽤变更。

筛选回收：根据标记结果，选择回收价值⾼的区域，复制存活对象到新区域，回收旧区域内存。这⼀阶段包含⼀个或多个停顿（STW），具体取决于回收的复杂度。

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/c4eb0d4e-b27e-4621-902c-d1513fa8f90b/8b35f8f223a7cb65b581cfc46634e8231fbcc3e722cf1d7f42a0c89a6febe083.jpg)



G1收集器运行示意图


G1 收集器在后台维护了⼀个优先列表，每次根据允许的收集时间，优先选择回收价值最⼤的Region(这也就是它的名字 Garbage-First 的由来) 。这种使⽤ Region 划分内存空间以及有优先级的区域回收⽅式，保证了 G1 收集器在有限时间内可以尽可能⾼的收集效率（把内存化整为零）。

# ZGC 有哪些改进？

与 CMS、ParNew 和 G1 类似，ZGC 也采⽤标记-复制算法，不过 ZGC 对该算法做了重⼤改进。

ZGC 可以将暂停时间控制在⼏毫秒以内，且暂停时间不受堆内存⼤⼩的影响，出现 Stop TheWorld 的情况会更少，但代价是牺牲了⼀些吞吐量。ZGC 最⼤⽀持 16TB 的堆内存。

ZGC 在 Java11 中引⼊，处于试验阶段。经过多个版本的迭代，不断的完善和修复问题，ZGC 在Java15 已经可以正式使⽤了。

不过，默认的垃圾回收器依然是 G1。你可以通过下⾯的参数启⽤ ZGC：

```batch
java -XX:+UseZGC className
```

在 Java21 中，引⼊了分代 ZGC，暂停时间可以缩短到 1 毫秒以内。

你可以通过下⾯的参数启⽤分代 ZGC：

java -XX:  $^+$  UseZGC -XX:+ZGenerational className

关于 ZGC 收集器的详细介绍推荐看看这⼏篇⽂章：

从历代 GC 算法⻆度剖析 ZGC - 京东技术

新⼀代垃圾回收器 ZGC 的探索与实践 - 美团技术团队

极致⼋股⽂之 JVM 垃圾回收器 G1&ZGC 详解 - 阿⾥云开发者

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/c4eb0d4e-b27e-4621-902c-d1513fa8f90b/cb131426bb882371d70c0707b886aa429cd80db69208ec65d3c42964fd810c10.jpg)


# 双亲委派模型

# 双亲委派模型指的是？

类加载器有很多种，当我们想要加载⼀个类的时候，具体是哪个类加载器加载呢？这就需要提到双亲委派模型了。

根据官⽹介绍：

The ClassLoader class uses a delegation model to search for classes and resources. Eachinstance of ClassLoader has an associated parent class loader. When requested to find aclass or resource, a ClassLoader instance will delegate the search for the class or resourceto its parent class loader before attempting to find the class or resource itself. The virtualmachine's built-in class loader, called the "bootstrap class loader", does not itself have aparent but may serve as the parent of a ClassLoader instance.

# 翻译过来⼤概的意思是：

ClassLoader 类使⽤委托模型来搜索类和资源。每个 ClassLoader 实例都有⼀个相关的⽗类加载器。需要查找类或资源时， ClassLoader 实例会在试图亲⾃查找类或资源之前，将搜索类或资源的任务委托给其⽗类加载器。

虚拟机中被称为 "bootstrap class loader"的内置类加载器本身没有⽗类加载器，但是可以作为ClassLoader 实例的⽗类加载器。

# 从上⾯的介绍可以看出：

. ClassLoader 类使⽤委托模型来搜索类和资源。

双亲委派模型要求除了顶层的启动类加载器外，其余的类加载器都应有⾃⼰的⽗类加载器。

. ClassLoader 实例会在试图亲⾃查找类或资源之前，将搜索类或资源的任务委托给其⽗类加载器。

下图展示的各种类加载器之间的层次关系被称为类加载器的“双亲委派模型(Parents DelegationModel)”。

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/c4eb0d4e-b27e-4621-902c-d1513fa8f90b/6602e68cefa053b6c3642e536d16429696621aec05c30457a0566796a39b8d3e.jpg)



注意 ：双亲委派模型并不是⼀种强制性的约束，只是 JDK 官⽅推荐的⼀种⽅式。如果我们因为某些特殊需求想要打破双亲委派模型，也是可以的，后⽂会介绍具体的⽅法。


其实这个双亲翻译的容易让别⼈误解，我们⼀般理解的双亲都是⽗⺟，这⾥的双亲更多地表达的是“⽗⺟这⼀辈”的⼈⽽已，并不是说真的有⼀个 MotherClassLoader 和⼀个 FatherClassLoader 。个⼈觉得翻译成单亲委派模型更好⼀些，不过，国内既然翻译成了双亲委派模型并流传了，按照这个来也没问题，不要被误解了就好。

另外，类加载器之间的⽗⼦关系⼀般不是以继承的关系来实现的，⽽是通常使⽤组合关系来复⽤⽗加载器的代码。

```txt
public abstract class ClassLoader {   
// 组合   
private final ClassLoader parent;   
protected ClassLoader(ClassLoader parent) { this(checkCreateClassLoader(), parent);   
}   
}
```

在⾯向对象编程中，有⼀条⾮常经典的设计原则：组合优于继承，多⽤组合少⽤继承。

# 如何打破打破双亲委派模型？

定义加载器的话，需要继承 ClassLoader 。如果我们不想打破双亲委派模型，就重写 ClassLoader类中的 findClass() ⽅法即可，⽆法被⽗类加载器加载的类最终会通过这个⽅法被加载。但是，如果想打破双亲委派模型则需要重写 loadClass() ⽅法。

为什么是重写 loadClass() ⽅法打破双亲委派模型呢？双亲委派模型的执⾏流程已经解释了：

类加载器在进⾏类加载的时候，它⾸先不会⾃⼰去尝试加载这个类，⽽是把这个请求委派给⽗类加载器去完成（调⽤⽗加载器 loadClass() ⽅法来加载类）。

重写 loadClass() ⽅法之后，我们就可以改变传统双亲委派模型的执⾏流程。例如，⼦类加载器可以在委派给⽗类加载器之前，先⾃⼰尝试加载这个类，或者在⽗类加载器返回之后，再尝试从其他地⽅加载这个类。具体的规则由我们⾃⼰实现，根据项⽬需求定制化。

我们⽐较熟悉的 Tomcat 服务器为了能够优先加载 Web 应⽤⽬录下的类，然后再加载其他⽬录下的类，就⾃定义了类加载器 WebAppClassLoader 来打破双亲委托机制。这也是 Tomcat 下 Web 应⽤之间的类实现隔离的具体原理。

Tomcat 的类加载器的层次结构如下：

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/c4eb0d4e-b27e-4621-902c-d1513fa8f90b/cd003c7995c74aba70dde458aea8480e080bb8731d830d4e27dc4612e3133541.jpg)


Tomcat 这四个⾃定义的类加载器对应的⽬录如下：

. CommonClassLoader 对应 <Tomcat>/common/*

CatalinaClassLoader 对应 <Tomcat >/server/*

. SharedClassLoader 对应 <Tomcat >/shared/*

. WebAppClassloader 对应 <Tomcat >/webapps/<app>/WEB-INF/*

从图中的委派关系中可以看出：

CommonClassLoader 作为 CatalinaClassLoader 和 SharedClassLoader 的⽗加载器。CommonClassLoader 能加载的类都可以被 CatalinaClassLoader 和 SharedClassLoader 使⽤。因此， CommonClassLoader 是为了实现公共类库（可以被所有 Web 应⽤和 Tomcat 内部组件使⽤的类库）的共享和隔离。

CatalinaClassLoader 和 SharedClassLoader 能加载的类则与对⽅相互隔离。 CatalinaClassLoader⽤于加载 Tomcat ⾃身的类，为了隔离 Tomcat 本身的类和 Web 应⽤的类。 SharedClassLoader作为 WebAppClassLoader 的⽗加载器，专⻔来加载 Web 应⽤之间共享的类⽐如 Spring、Mybatis。

每个 Web 应⽤都会创建⼀个单独的 WebAppClassLoader ，并在启动 Web 应⽤的线程⾥设置线程线程上下⽂类加载器为 WebAppClassLoader 。各个 WebAppClassLoader 实例之间相互隔离，进⽽实现 Web 应⽤之间的类隔。

单纯依靠⾃定义类加载器没办法满⾜某些场景的要求，例如，有些情况下，⾼层的类加载器需要加载低层的加载器才能加载的类。

⽐如，SPI 中，SPI 的接⼝（如 java.sql.Driver ）是由 Java 核⼼库提供的，由 BootstrapClassLoader加载。⽽ SPI 的实现（如 com.mysql.cj.jdbc.Driver ）是由第三⽅供应商提供的，它们是由应⽤程序类加载器或者⾃定义类加载器来加载的。默认情况下，⼀个类及其依赖类由同⼀个类加载器加载。所以，加载 SPI 的接⼝的类加载器（ BootstrapClassLoader ）也会⽤来加载 SPI 的实现。按照双亲委派模型， BootstrapClassLoader 是⽆法找到 SPI 的实现类的，因为它⽆法委托给⼦类加载器去尝试加载。

再⽐如，假设我们的项⽬中有 Spring 的 jar 包，由于其是 Web 应⽤之间共享的，因此会由SharedClassLoader 加载（Web 服务器是 Tomcat）。我们项⽬中有⼀些⽤到了 Spring 的业务类，⽐如实现了 Spring 提供的接⼝、⽤到了 Spring 提供的注解。所以，加载 Spring 的类加载器（也就是 SharedClassLoader ）也会⽤来加载这些业务类。但是业务类在 Web 应⽤⽬录下，不在SharedClassLoader 的加载路径下，所以 SharedClassLoader ⽆法找到业务类，也就⽆法加载它们。

如何解决这个问题呢？ 这个时候就需要⽤到 线程上下⽂类加载器（ ThreadContextClassLoader ）了。

拿 Spring 这个例⼦来说，当 Spring 需要加载业务类的时候，它不是⽤⾃⼰的类加载器，⽽是⽤当前线程的上下⽂类加载器。还记得我上⾯说的吗？每个 Web 应⽤都会创建⼀个单独的

WebAppClassLoader ，并在启动 Web 应⽤的线程⾥设置线程线程上下⽂类加载器为

WebAppClassLoader 。这样就可以让⾼层的类加载器（ SharedClassLoader ）借助⼦类加载器（

WebAppClassLoader ）来加载业务类，破坏了 Java 的类加载委托机制，让应⽤逆向使⽤类加载器。

线程上下⽂类加载器的原理是将⼀个类加载器保存在线程私有数据⾥，跟线程绑定，然后在需要的时候取出来使⽤。这个类加载器通常是由应⽤程序或者容器（如 Tomcat）设置的。

Java.lang.Thread 中的 getContextClassLoader() 和 setContextClassLoader(ClassLoader cl) 分别⽤来获取和设置线程的上下⽂类加载器。如果没有通过 setContextClassLoader(ClassLoader cl) 进⾏设置的话，线程将继承其⽗线程的上下⽂类加载器。

Spring 获取线程线程上下⽂类加载器的代码如下：

```javascript
cl = Thread.currentThread().getContextClassLoader();
```

感兴趣的⼩伙伴可以⾃⾏深⼊研究⼀下 Tomcat 打破双亲委派模型的原理，推荐资料： 《深⼊拆解Tomcat & Jetty》。

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/c4eb0d4e-b27e-4621-902c-d1513fa8f90b/747939cedb502aa100706308b3634fee4b22ffac411257539ee5ea0ad1efdfc0.jpg)


# 问题排查

# 你知道哪些 Java 性能优化和问题排查⼯具？

JDK ⾃带的可视化分析⼯具：

JConsole ：基于 JMX 的可视化监视、管理⼯具，可以⽤于查看应⽤程序的运⾏概况、内存、线程、类、VM 概括、MBean 等信息。

VisualVM：基于 NetBeans 平台开发，具备了插件扩展功能的特性。利⽤它不仅能够监控服务的 CPU、内存、线程、类等信息，还可以捕获有关 JVM 软件实例的数据，并将该数据保存到本地系统，以供后期查看或与其他⽤户共享。根据《深⼊理解 Java 虚拟机》介绍：“VisualVM的性能分析功能甚⾄⽐起 JProfiler、YourKit 等专业且收费的 Profiling ⼯具都不会逊⾊多少，⽽且 VisualVM 还有⼀个很⼤的优点：不需要被监视的程序基于特殊 Agent 运⾏，因此他对应

⽤程序的实际性能的影响很⼩，使得他可以直接应⽤在⽣产环境中。这个优点是 JProfiler、YourKit 等⼯具⽆法与之媲美的”。

# JDK ⾃带的命令⾏⼯具：

. jps (JVM Process Status）: 类似 UNIX 的 ps 命令。⽤于查看所有 Java 进程的启动类、传⼊参数和 Java 虚拟机参数等信息；

. jstat （JVM Statistics Monitoring Tool）: ⽤于收集 HotSpot 虚拟机各⽅⾯的运⾏数据;

. jinfo (Configuration Info for Java) : Configuration Info for Java,显示虚拟机配置信息;

jmap (Memory Map for Java) : ⽣成堆转储快照;

. jhat (JVM Heap Dump Browser) : ⽤于分析 heapdump ⽂件，它会建⽴⼀个 HTTP/HTML 服务器，让⽤户可以在浏览器上查看分析结果。JDK9 移除了 jhat；

. jstack (Stack Trace for Java) : ⽣成虚拟机当前时刻的线程快照，线程快照就是当前虚拟机内每⼀条线程正在执⾏的⽅法堆栈的集合。

# 第三⽅⼯具：

MAT：⼀款功能强⼤的 Java 堆内存分析器，可以⽤于查找内存泄漏以及查看内存消耗情况，⽤户可以利⽤ VisualVM 或者是 jmap 命令⽣产堆⽂件，然后导⼊⼯具中进⾏分析。

GCeasy：⼀款在线的 GC ⽇志分析器，使⽤起来⾮常⽅便，⽤户可以通过它的 Web ⽹站导⼊GC ⽇志，实时进⾏内存泄漏检测、GC 暂停原因分析、JVM 配置建议优化等功能。⽹站地址：https://gceasy.io/ 。

GCViewer：⼀款⾮常强⼤的 GC ⽇志可视化分析⼯具，功能强⼤⽽且完全免费。

JProfiler：⼀款商⽤的性能分析利器，功能强⼤，但需要付费使⽤。 它提供更深⼊的性能分析功能，例如⽅法调⽤分析、内存分配分析等。

Arthas：阿⾥开源的⼀款线上监控诊断⼯具，可以查看应⽤负载、内存、gc、线程等信息。

# 如何查看服务器上运⾏的 Java 进程？

JDK ⾃带的 jps (JVM Process Status) 命令专⻔⽤于列出当前⽤户下所有正在运⾏的 JVM 实例。

jps 的基础⽤法和⼏个核⼼参数如下：

jps ：这是最基础的⽤法，它会列出 Java 进程的 LVMID（本地虚拟机唯⼀ ID，通常就是操作系统的进程号 PID）和主类名（或 Jar 包名）。

. jps -l ：这是我最常⽤的参数之⼀。它会输出主类的完整包名，或者如果应⽤是通过 Jar 包运⾏的，会输出 Jar 包的完整路径。这在同⼀台机器上部署了多个来⾃不同项⽬的 Java 应⽤时，能⾮常清晰地区分它们。

jps -v ：这个参数也⾮常实⽤，尤其是在排查配置问题时。它会显示传递给 JVM 的参数，例如 -Xmx 、 -Xms -XX: $^ +$ UseG1GC 等。通过它，我可以快速确认应⽤的内存配置、GC 策略等是否符合预期。

. jps -m ：这个参数⽤于查看传递给主函数 main() 的参数。当我们需要确认程序启动时传⼊的业务参数是否正确时，它⾮常有⽤。

在某些情况下， jps 命令可能⽆法满⾜需求，这时我会采⽤标准的操作系统命令：

1. 权限问题：jps 默认只能看到由当前⽤户启动的 Java 进程。如果需要查看服务器上所有⽤户（如 root 或其他业务⽤户）的 Java 进程，jps 就会受限。

2. 环境问题：在⼀些极简的⽣产环境或 Docker 容器中，可能只安装了 JRE ⽽没有完整的 JDK，此时 jps 命令可能不存在。

在这些情况下，我会使⽤ ps 命令来查找，例如：

```txt
列出所有进程，然后通过grep过滤出包含"java”关键字的进程ps -ef | grep java
```

# 堆内存相关的 JVM 参数有哪些？

# 堆内存⼤⼩控制：

1. -Xms ：设置 JVM 初始堆内存⼤⼩（如 -Xms512m 表示初始堆为 512MB） 。

2. -Xmx ：设置 JVM 最⼤堆内存⼤⼩（如 -Xmx1g 表示最⼤堆为 1GB） 。

在⽣产环境中，强烈建议将 -Xms 和 -Xmx 设置为相同的值。这样做可以避免 JVM 在运⾏时根据负载情况动态地收缩和扩展堆内存，这个过程会引发不必要的 Full GC 和性能抖动，从⽽提⾼服务的稳定性和响应速度。

# 新⽣代与⽼年代：

1. -Xmn ：这是最直接控制新⽣代⼤⼩的⽅式，优先级⾼于 - XX:NewRatio 。设置后，⽼年代的⼤⼩就是 -Xmx 减去 -Xmn 。当我们对应⽤的对象⽣命周期有明确的判断时（例如，有⼤量的短⽣命周期对象），可以直接给新⽣代⼀个合适的⼤⼩，以达到更好的 GC 性能。

2. -XX:NewRatio ：这是另⼀种调节新⽣代⼤⼩的⽅式，默认值为 2，表示⽼年代:新⽣代 $= 2 : 1$ 。因此，新⽣代默认占整个堆的 1/3。如果设置为 3，则新⽣代占堆的 1/4。通常在 -Xmn 和 -XX:NewRatio 中选择⼀个使⽤即可。

3. -XX:SurvivorRatio ：设置新⽣代中 Eden 区与单个 Survivor 区的⽐例。默认值为 8，表示Eden : From Survivor : To Survivor $= 8 { : } 1 { : } 1$ 。所以 Eden 区占整个新⽣代的 8/10。这个⽐例会影响对象能否在新⽣代中“存活”⾜够⻓的时间。如果 Survivor 区太⼩（即 -XX:SurvivorRatio 值过⼤），Minor GC 后存活的对象可能因为放不下⽽被迫提前进⼊⽼年代，增加 Full GC 的压⼒。

# 堆内存溢出相关参数：

-XX:+HeapDumpOnOutOfMemoryError ：当发⽣ OutOfMemoryError （OOM）时，⾃动⽣成堆转储⽂件（ .hprof ），记录堆内存对象状态。

2. -XX:HeapDumpPath ：指定 OOM 时堆转储⽂件的保存路径（如XX:HeapDumpPath=/logs/heapdump.hprof ），默认⽣成在程序运⾏⽬录。

最重要的 JVM 参数可以参考这篇⽂章：最重要的 JVM 参数总结。

# 如何检测死锁？

使⽤ jmap 、 jstack 等命令查看 JVM 线程栈和堆内存的情况。如果有死锁， jstack 的输出中通常会有 Found one Java-level deadlock: 的字样，后⾯会跟着死锁相关的线程信息。另外，实际项⽬中还可以搭配使⽤ top 、 df 、 free 等命令查看操作系统的基本情况，出现死锁可能会导致 CPU、内存等资源消耗过⾼。

采⽤ VisualVM、JConsole 等⼯具进⾏排查。

这⾥以 JConsole ⼯具为例进⾏演示。

⾸先，我们要找到 JDK 的 bin ⽬录，找到 jconsole 并双击打开。

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/c4eb0d4e-b27e-4621-902c-d1513fa8f90b/fe686e63d053848f7d308d99ebb6329cf7b09a88cab90caaa327f8eadcecf4e1.jpg)


对于 MAC ⽤户来说，可以通过 /usr/libexec/java_home -V 查看 JDK 安装⽬录，找到后通过 open . +⽂件夹地址 打开即可。例如，我本地的某个 JDK 的路径是：

```html
open . /Users/guide/Library/Java/JavaVirtualMachines/corretto1.8.0_252/Contents/Home
```

打开 jconsole 后，连接对应的程序，然后进⼊线程界⾯选择检测死锁即可！

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/c4eb0d4e-b27e-4621-902c-d1513fa8f90b/6b685befac1144748d685d8953094f44e85cac020891a04578891355c6a45730.jpg)


![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/c4eb0d4e-b27e-4621-902c-d1513fa8f90b/61ab669d1c7b33c344a1253ead4c274f22e44f6529b3429bc3502aaaadc6af4c.jpg)


详细介绍可以查看这篇⽂章的死锁部分内容：Java 并发常⻅⾯试题总结（上）

什么是 Heap Dump ⽂件？如何⽣成 Heap Dump ⽂件？

Heap Dump（堆转储⽂件）是 Java 虚拟机（JVM）在某个特定时间点，对整个 Java 堆内存的快照。它是⼀个⼆进制⽂件，包含了快照时刻堆中所有对象的信息，例如：

对象实例：每个对象的数据。

. 类信息：对象的类名、⽗类、静态字段等。

引⽤关系：对象之间复杂的引⽤链，即谁持有了谁。

线程信息：堆栈信息，特别是与 GC Roots 相关的线程栈。

简单来说，Heap Dump 就是 Java 进程在某⼀刻的“内存 X 光⽚”，是诊断内存问题的最核⼼、最权威的依据。

# ⾃动⽣成

在 JVM 启动参数中加⼊以下配置，这是⽣产环境排查 OOM 问题的⾸选⽅案。

```txt
当发生 OutOfMemoryError 时，自动生成 HeapDump 文件  
-XX: +HeapDumpOnOutOfMemoryError  
#指定 HeapDump 文件的生成路径，例如：/home/app/dumps/-XX:HeapDumpPath=<path-to-dump-dir>
```

# ⼿动⽣成

当应⽤出现内存疑似异常（如内存持续升⾼、GC 频繁）但未崩溃时，可以⼿动⽣成快照进⾏分析。

1. jmap ：JDK ⾃带的命令⾏⼯具，专⻔⽤于⽣成堆快照。使⽤示例： jmap -dump:format=b,file=heapdump.hprof <pid> 。在执⾏时会触发 STW ，导致 Java 进程短暂停顿，对⽣产环境有⼀定影响。在⾼版本 JDK 中已不推荐直接使⽤。

2. jcmd ：JDK 7 之后引⼊的多功能命令⾏⼯具，功能⽐ jmap 更强⼤⼀些，可⽤来替代 jmap，侵⼊性更⼩。使⽤示例： jcmd <pid> GC.heap_dump /path/to/heapdump.hprof 。

3. Arthas：阿⾥巴巴开源的 Java 诊断神器，对应⽤⽆侵⼊，功能强⼤，可在不重启服务的情况下动态分析。使⽤示例： heapdump /tmp/heapdump.hprof 。

4. 可视化⼯具：如 JVisualVM、JProfiler、YourKit 等，都提供了图形化界⾯，点击按钮即可⽣成Heap Dump ⽂件，并能直接进⾏分析，⾮常⽅便。

# 遇到 OutOfMemoryError 怎么排查解决？

我们可以通过 MAT、JVisualVM 等⼯具分析 Heap Dump 找到导致 OutOfMemoryError 的原因。

以 MAT 为例，其提供的泄漏嫌疑（Leak Suspects）报告是 MAT 最强⼤的功能之⼀。它会基于启发式算法⾃动分析整个堆，直接指出最可疑的内存泄漏点，并给出详细的报告，包括问题组件、累积点（Accumulation Point）和引⽤链的图示。

如果“泄漏嫌疑”报告不够明确，或者想要分析的是内存占⽤过⾼（⽽⾮泄漏）问题，可以切换到⽀配树（Dominator Tree）视图。这个视图将内存对象关系组织成⼀棵树，⽗节点“⽀配”⼦节点（即⽗节点被回收，⼦节点也必被回收）。

下⾯是⼀段模拟出现 OutOfMemoryError 的代码：

```java
import java.util.ArrayList;   
import java.util.List;   
public class SimpleLeak { //静态集合，生命周期与应用程序一样长 public static List byte[] > staticList = new ArrayList<>(); public void leakMethod() { //每次调用都向静态集合中添加一个1MB的字节数组 staticList.add(new byte[1024 * 1024]); //1MB } public static void main(String[] args) throws InterruptedException { SimpleLeak leak = new SimpleLeak(); System.out.println("Starting leak simulation..."); //循环添加对象，模拟内存泄漏过程 for (int i = 0; i < 200; i++) { leak.leakMethod(); System.out.println("Added " + (i + 1) + " MB to the list "); Thread.sleep(200); //稍微延时，方便观察 }
```

```txt
System.out.println("Leak simulation finished. Keeping process alive for HeapDump.");//保持进程存活，以便我们有时间生成HeapDump Thread.sleep(Long.MAX_VALUE); } 1
```

为了更快让程序出现 OutOfMemoryError 问题，我们可以故意设置⼀个较⼩的堆 -Xmx256m 。

IDEA 设置 VM 参数的⽅式如下图所示：

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/c4eb0d4e-b27e-4621-902c-d1513fa8f90b/1d43dcd4547a16ce4f752a388c4bea104dd81f59e9563637aa28a4cb74b5abc1.jpg)


具体设置的 VM 参数是： -Xmx128m -XX: $^ +$ HeapDumpOnOutOfMemoryError -

XX:HeapDumpPath ${ } = { }$ simple_leak.hprof ，其中：

. $- X \mathrm { m x } 1 2 8 \mathrm { m }$ ：设置 JVM 最⼤堆内存为 128MB。

. -XX:+HeapDumpOnOutOfMemoryError ：当 JVM 发⽣ OutOfMemoryError 时，⾃动⽣成堆转储⽂件（ .hprof ）。

. -XX:HeapDumpPath $1 =$ simple_leak.hprof ：指定 OOM 时⽣成的堆转储⽂件路径及⽂件名（这⾥是simple_leak.hprof ）。

运⾏程序之后，会出现 OutOfMemoryError 并⾃动⽣成了 Heap Dump ⽂件。

```txt
Starting leak simulation...  
Added 1 MB to the list.  
Added 2 MB to the list.  
Added 3 MB to the list.  
...  
Added 113 MB to the list.  
Added 114 MB to the list.  
Added 115 MB to the list.  
java.langOutOfMemoryError: Java heap space  
Dumping heap to simple_leak.hprof ...  
Heap dump file created [124217346 bytes in 0.121 secs]
```

我们将 .hprof ⽂件导⼊ MAT 后，它会⾸先进⾏解析和索引。完成后，可以查看它的 “泄漏嫌疑报告” (Leak Suspects Report)。

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/c4eb0d4e-b27e-4621-902c-d1513fa8f90b/9c4a18312f9ec62d1246cac7db81ac96be21ebc587428d74655a5f3098906bb2.jpg)



下图中的 Problem Suspect 1 就是可能出现内存泄露的问题分析：


![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/c4eb0d4e-b27e-4621-902c-d1513fa8f90b/a45b3bd148ecc105bcf1f727a3e2bd31c95549b752627bc86098e99aa338a648.jpg)


cn.javaguide.SimpleLeak 类由 sun.misc.Launcher$AppClassLoader 加载，占⽤ 120,589,040 字节（约 115MB，占堆 $9 8 . 8 0 \%$ ），是内存占⽤的核⼼。

内存主要被 java.lang.Object[] 数组 占⽤（120,588,752 字节），说明 SimpleLeak 中可能存在⼤量 Object 数组未释放，触发内存泄漏。

Problem Suspect 1 的可以看到有⼀个 Details，点进去即可看到内存泄漏的关键路径和对象占⽐：

# Shortest Paths To the Accumulation Point

<table><tr><td>Class Name</td><td>Shallow Heap</td><td>Retained Heap</td></tr><tr><td>java.lang.Object[163] @ 0x7bd19d430</td><td>672</td><td>120,588,752</td></tr><tr><td>elementData java.util.ArrayList @ 0x7bcc278f8</td><td>24</td><td>120,588,776</td></tr><tr><td>staticList class cn.javaguide SIMPLELeak @ 0x7bcc279e0</td><td>8</td><td>120,589,040</td></tr><tr><td>appClass class sun.launcher LauncherHelper @ 0x7bcc27920 System Class</td><td>80</td><td>912</td></tr><tr><td>&lt;class&gt; cn.javaguide SIMPLELeak @ 0x7bcc278e8 »</td><td>16</td><td>16</td></tr><tr><td>[3] java.lang.Object[10] @ 0x7bcd4acf8 »</td><td>56</td><td>608</td></tr><tr><td>Total: 3 entries</td><td></td><td></td></tr></table>

# Accumulated Objects in Dominator Tree

<table><tr><td>Class Name</td><td>Shallow Heap</td><td>Retained Heap</td><td>Percentage</td></tr><tr><td>class cn.javaguide SIMPLELeak @ 0x7bcc279e0</td><td>8</td><td>120,589,040</td><td>98.80%</td></tr><tr><td>java.util.List @ 0x7bcc278f8</td><td>24</td><td>120,588,776</td><td>98.80%</td></tr><tr><td>java.lang.Object[163] @ 0x7bd19d430</td><td>672</td><td>120,588,752</td><td>98.80%</td></tr><tr><td>byte[1048576] @ 0x7b8001730</td><td>1,048,592</td><td>1,048,592</td><td>0.86%</td></tr><tr><td>byte[1048576] @ 0x7b8101740</td><td>1,048,592</td><td>1,048,592</td><td>0.86%</td></tr><tr><td>byte[1048576] @ 0x7b8201750</td><td>1,048,592</td><td>1,048,592</td><td>0.86%</td></tr><tr><td>byte[1048576] @ 0x7b8322e08</td><td>1,048,592</td><td>1,048,592</td><td>0.86%</td></tr><tr><td>byte[1048576] @ 0x7b84345e8</td><td>1,048,592</td><td>1,048,592</td><td>0.86%</td></tr><tr><td>byte[1048576] @ 0x7b8544c80</td><td>1,048,592</td><td>1,048,592</td><td>0.86%</td></tr><tr><td>byte[1048576] @ 0x7b864a918</td><td>1,048,592</td><td>1,048,592</td><td>0.86%</td></tr><tr><td>byte[1048576] @ 0x7b874c900</td><td>1,048,592</td><td>1,048,592</td><td>0.86%</td></tr><tr><td>byte[1048576] @ 0x7b884e900</td><td>1,048,592</td><td>1,048,592</td><td>0.86%</td></tr><tr><td>byte[1048576] @ 0x7b89528c8</td><td>1,048,592</td><td>1,048,592</td><td>0.86%</td></tr><tr><td>byte[1048576] @ 0x7b8a58860</td><td>1,048,592</td><td>1,048,592</td><td>0.86%</td></tr><tr><td>byte[1048576] @ 0x7b8b833b0</td><td>1,048,592</td><td>1,048,592</td><td>0.86%</td></tr><tr><td>byte[1048576] @ 0x7b8cab580</td><td>1,048,592</td><td>1,048,592</td><td>0.86%</td></tr><tr><td>byte[1048576] @ 0x7b8daf548</td><td>1,048,592</td><td>1,048,592</td><td>0.86%</td></tr><tr><td>byte[1048576] @ 0x7b8edaec8</td><td>1,048,592</td><td>1,048,592</td><td>0.86%</td></tr></table>

可以看到： SimpleLeak 中的静态集合 staticList 是内存泄漏的 “根源”，因为静态变量⽣命周期与类⼀致，若持续向其中添加对象且不清理，会导致对象⽆法被 GC 回收。

