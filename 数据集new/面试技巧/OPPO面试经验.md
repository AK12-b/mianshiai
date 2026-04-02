

# Java

# String 为什么是不可变的?

```java
public final class String implements java.io.Serializable, Comparable<String>, CharSequence { private final char value[]; //... }
```

String 真正不可变有下⾯⼏点原因：

1. 保存字符串的数组被 final 修饰且为私有的，并且 String 类没有提供/暴露修改这个字符串的⽅法。

2. String 类被 final 修饰导致其不能被继承，进⽽避免了⼦类破坏 String 不可变。

在 Java 9 之后， String 、 StringBuilder 与 StringBuffer 的实现改⽤ byte 数组存储字符串。

```java
public final class String implements java.io.Serializable,Comparable<String>, CharSequence { // @Stable 注解表示变量最多被修改一次，称为“稳定的”。 @Stable private final byte[] value; } abstract class AbstractStringBuilder implements Appendable, CharSequence { byte[] value; }
```

新版的 String 其实⽀持两个编码⽅案：Latin-1 和 UTF-16。如果字符串中包含的汉字没有超过 Latin-1 可表示范围内的字符，那就会使⽤ Latin-1 作为编码⽅案。Latin-1 编码⽅案下， byte 占⼀个字节(8 位)， char 占⽤ 2 个字节（16）， byte 相较 char 节省⼀半的内存空间。

JDK 官⽅就说了绝⼤部分字符串对象只包含 Latin-1 可表示的字符。

# Motivation

The current implementation of the String class stores characters in a char array,using two bytes (sixteen bits) for each character. Data gathered from manydifferent applications indicates that strinqs are a maior component of heap usageand,moreoverthat most String objects contain only Latin-1 characters.Suchcharacters require only one byte of storage,hence half of the space in the internalchar arrays of such String objects is going unused.

如果字符串中包含的汉字超过 Latin-1 可表示范围内的字符， byte 和 char 所占⽤的空间是⼀样的。

这是官⽅的介绍：https://openjdk.java.net/jeps/254 。

# 如何创建线程？

⼀般来说，创建线程有很多种⽅式，例如继承 Thread 类、实现 Runnable 接⼝、实现 Callable 接⼝、使⽤线程池、使⽤ CompletableFuture 类等等。

不过，这些⽅式其实并没有真正创建出线程。准确点来说，这些都属于是在 Java 代码中使⽤多线程的⽅法。

严格来说，Java就只有⼀种⽅式可以创建线程，那就是通过 new Thread().start() 创建。不管是哪种⽅式，最终还是依赖于 new Thread().start() 。

关于这个问题的详细分析可以查看这篇⽂章：⼤家都说Java有三种创建线程的⽅式！并发编程中的惊天骗局！。

# Java 线程的状态有哪⼏种?

Java 线程在运⾏的⽣命周期中的指定时刻只可能处于下⾯ 6 种不同状态的其中⼀个状态：

NEW: 初始状态，线程被创建出来但没有被调⽤ start() 。

RUNNABLE: 运⾏状态，线程被调⽤了 start() 等待运⾏的状态。

BLOCKED：阻塞状态，需要等待锁释放。

WAITING：等待状态，表示该线程需要等待其他线程做出⼀些特定动作（通知或中断）。

TIME_WAITING：超时等待状态，可以在指定的时间后⾃⾏返回⽽不是像 WAITING 那样⼀直等待。

TERMINATED：终⽌状态，表示该线程已经运⾏完毕。

线程在⽣命周期中并不是固定处于某⼀个状态⽽是随着代码的执⾏在不同状态之间切换。

Java 线程状态变迁图(图源：挑错 |《Java 并发编程的艺术》中关于线程状态的三处错误)：

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/ddb71a26-9c90-49cc-9e00-62502db24f2f/5110ab502dba3b970cfa30246656b29a93702e93bd7ea1999c62e9fd9daae642.jpg)


由上图可以看出：线程创建之后它将处于 NEW（新建） 状态，调⽤ start() ⽅法后开始运⾏，线程这时候处于READY（可运⾏） 状态。可运⾏状态的线程获得了 CPU 时间⽚（timeslice）后就处于 RUNNING（运⾏） 状态。

在操作系统层⾯，线程有 READY 和 RUNNING 状态；⽽在 JVM 层⾯，只能看到 RUNNABLE 状态（图源：HowToDoInJava：Java Thread Life Cycle and Thread States），所以 Java 系统⼀般将这两个状态统称为 RUNNABLE（运⾏中） 状态 。

为什么 JVM 没有区分这两种状态呢？ （摘⾃：Java 线程运⾏怎么有第六种状态？ - Dawell 的回答 ） 现在的时分（time-sharing）多任务（multi-task）操作系统架构通常都是⽤所谓的“时间分⽚（time quantum ortime slice）”⽅式进⾏抢占式（preemptive）轮转调度（round-robin 式）。这个时间分⽚通常是很⼩的，⼀个线程⼀次最多只能在 CPU 上运⾏⽐如 10-20ms 的时间（此时处于 running 状态），也即⼤概只有 0.01 秒这⼀量级，时间⽚⽤后就要被切换下来放⼊调度队列的末尾等待再次调度。（也即回到 ready 状态）。线程切换的如此之快，区分这两种状态就没什么意义了。

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/ddb71a26-9c90-49cc-9e00-62502db24f2f/b333d0ebceac658e8ecb1bd742fbb79e279aa75befffec90db57235b6361b4c3.jpg)


当线程执⾏ wait() ⽅法之后，线程进⼊ WAITING（等待） 状态。进⼊等待状态的线程需要依靠其他线程的通知才能够返回到运⾏状态。

TIMED_WAITING(超时等待) 状态相当于在等待状态的基础上增加了超时限制，⽐如通过 sleep（longmillis） ⽅法或 wait（long millis） ⽅法可以将线程置于 TIMED_WAITING 状态。当超时时间结束后，线程将会返回到 RUNNABLE 状态。

当线程进⼊ synchronized ⽅法/块或者调⽤ wait 后（被 notify ）重新进⼊ synchronized ⽅法/块，但是锁被其它线程占有，这个时候线程就会进⼊ BLOCKED（阻塞） 状态。

线程在执⾏完了 run() ⽅法之后将会进⼊到 TERMINATED（终⽌） 状态。

相关阅读：线程的⼏种状态你真的了解么？ 。

# 为什么要⽤线程池？项⽬中使⽤的线程池是使⽤内置的还是⾃⼰创建的？

线程池提供了⼀种限制和管理资源（包括执⾏⼀个任务）的⽅式。 每个线程池还维护⼀些基本统计信息，例如已完成任务的数量。

这⾥借⽤《Java 并发编程的艺术》提到的来说⼀下使⽤线程池的好处：

降低资源消耗。通过重复利⽤已创建的线程降低线程创建和销毁造成的消耗。

提⾼响应速度。当任务到达时，任务可以不需要等到线程创建就能⽴即执⾏。

提⾼线程的可管理性。线程是稀缺资源，如果⽆限制的创建，不仅会消耗系统资源，还会降低系统的稳定性，使⽤线程池可以进⾏统⼀的分配，调优和监控。

《阿⾥巴巴 Java 开发⼿册》中强制线程池不允许使⽤ Executors 去创建，⽽是通过 ThreadPoolExecutor 构造函数的⽅式，这样的处理⽅式让写的同学更加明确线程池的运⾏规则，规避资源耗尽的⻛险

Executors 返回线程池对象的弊端如下(后⽂会详细介绍到)：

FixedThreadPool 和 SingleThreadExecutor ：使⽤的是⽆界的 LinkedBlockingQueue ，任务队列最⼤⻓度为Integer.MAX_VALUE ,可能堆积⼤量的请求，从⽽导致 OOM。

CachedThreadPool ：使⽤的是同步队列 SynchronousQueue , 允许创建的线程数量为 Integer.MAX_VALUE如果任务数量过多且执⾏速度较慢，可能会创建⼤量的线程，从⽽导致 OOM。

ScheduledThreadPool 和 SingleThreadScheduledExecutor : 使⽤的⽆界的延迟阻塞队列 DelayedWorkQueue ，任务队列最⼤⻓度为 Integer.MAX_VALUE ,可能堆积⼤量的请求，从⽽导致 OOM。



# 数据库

# 如何找到慢 SQL？

MySQL 慢查询⽇志是⽤来记录 MySQL 在执⾏命令中，响应时间超过预设阈值的 SQL 语句。因此，通过分析慢查询⽇志我们就可以找出执⾏速度⽐较慢的 SQL 语句。

出于性能层⾯的考虑，慢查询⽇志功能默认是关闭的，你可以通过以下命令开启：

```txt
# 开启慢查询日志功能  
SET GLOBAL slow_query_log = 'ON';  
# 慢查询日志存放位置  
SET GLOBAL slow_query_log_file = '/var/lib/mysql/ranking-list-slow.log';  
# 无论是否超时，未被索引的记录也会记录下来。  
SET GLOBAL log Queries.not-usingIndices = 'ON';  
# 慢查询阈值（秒），SQL执行超过这个阈值将被记录在日志中。  
SET SESSION long_query_time = 1;  
# 慢查询仅记录扫描行数大于此参数的SQL  
SET SESSION min_examined_row_limit = 100;
```

设置成功之后，使⽤ show variables like 'slow%'; 命令进⾏查看。

```txt
Variable_name Value   
slow_launch_time 2   
slow_query_log ON   
slow_query_log_file /var/lib/mysql/ranking-list-slow.log   
3 rows in set (0.01 sec)
```

我们故意在百万数据量的表(未使⽤索引)中执⾏⼀条排序的语句：

```sql
SELECT `score`, `name` FROM `cus_order` ORDER BY `score` DESC;
```

确保⾃⼰有对应⽬录的访问权限：

```txt
chmod 755 /var/lib/mysql/
```

查看对应的慢查询⽇志：

```txt
cat /var/lib/mysql/ranking-list-slow.log
```

我们刚刚故意执⾏的 SQL 语句已经被慢查询⽇志记录了下来：

```sql
Time: 2022-10-09T08:55:37.486797Z  
# User@Host: root[root] @ [172.17.0.1] Id: 14  
# Query_time: 0.978054 Lock_time: 0.000164 Rows_sent: 999999 Rows_examined: 1999998  
SET timestamp=1665305736;  
SELECT `score`, `name` FROM `cus_order` ORDER BY `score` DESC;
```

这⾥对⽇志中的⼀些信息进⾏说明：

Time ：被⽇志记录的代码在服务器上的运⾏时间。

User@Host ：谁执⾏的这段代码。

Query_time ：这段代码运⾏时⻓。

Lock_time ：执⾏这段代码时，锁定了多久。

Rows_sent ：慢查询返回的记录。

Rows_examined ：慢查询扫描过的⾏数。

实际项⽬中，慢查询⽇志通常会⽐较复杂，我们需要借助⼀些⼯具对其进⾏分析。像 MySQL 内置的mysqldumpslow ⼯具就可以把相同的 SQL 归为⼀类，并统计出归类项的执⾏次数和每次执⾏的耗时等⼀系列对应的情况。

# 如何分析 SQL 性能

我们可以使⽤ EXPLAIN 命令来分析 SQL 的 执⾏计划 。执⾏计划是指⼀条 SQL 语句在经过 MySQL 查询优化器的优化会后，具体的执⾏⽅式。

EXPLAIN 并不会真的去执⾏相关的语句，⽽是通过 查询优化器 对语句进⾏分析，找出最优的查询⽅案，并显示对应的信息。

EXPLAIN 适⽤于 SELECT , DELETE , INSERT , REPLACE , 和 UPDATE 语句，我们⼀般分析 SELECT 查询较多。

我们这⾥简单来演示⼀下 EXPLAIN 的使⽤。

EXPLAIN 的输出格式如下：

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/ddb71a26-9c90-49cc-9e00-62502db24f2f/fdb3a6488c05309e0f17d50c9024d3c3b947bec5f1a060369d60510f0a1afe0d.jpg)


各个字段的含义如下：

<table><tr><td>列名</td><td>含义</td></tr><tr><td>id</td><td>SELECT查询的序列标识符</td></tr><tr><td>select_type</td><td>SELECT关键字对应的查询类型</td></tr><tr><td>table</td><td>用到的表名</td></tr><tr><td>partitions</td><td>匹配的分区，对于未分区的表，值为NULL</td></tr><tr><td>type</td><td>表的访问方法</td></tr><tr><td>possible_keys</td><td>可能用到的索引</td></tr><tr><td>key</td><td>实际用到的索引</td></tr><tr><td>key_len</td><td>所选索引的长度</td></tr><tr><td>ref</td><td>当使用索引等值查询时，与索引作比较的列或常量</td></tr><tr><td>rows</td><td>预计要读取的行数</td></tr><tr><td>filtered</td><td>按表条件过滤后，留存的记录数的百分比</td></tr><tr><td>Extra</td><td>附加信息</td></tr></table>


篇幅问题，我这⾥只是简单介绍了⼀下 MySQL 执⾏计划，详细介绍请看：MySQL执⾏计划分析这篇⽂章。


# 项⽬中是怎么使⽤索引的？联合索引了解吗？

索引是⼀种⽤于快速查询和检索数据的数据结构，其本质可以看成是⼀种排序好的数据结构。

索引的作⽤就相当于书的⽬录。打个⽐⽅: 我们在查字典的时候，如果没有⽬录，那我们就只能⼀⻚⼀⻚的去找我们需要查的那个字，速度很慢。如果有⽬录了，我们只需要先去⽬录⾥查找字的位置，然后直接翻到那⼀⻚就⾏了。

虽然索引能带来查询上的效率，但是维护索引的成本也是不⼩的。 如果⼀个字段不被经常查询，反⽽被经常修改，那么就更不应该在这种字段上建⽴索引了。

要选择选择合适的字段创建索引：

不为 NULL 的字段：索引字段的数据应该尽量不为 NULL，因为对于数据为 NULL 的字段，数据库较难优化。如果字段频繁被查询，但⼜避免不了为 NULL，建议使⽤ 0,1,true,false 这样语义较为清晰的短值或短字符作为替代。

被频繁查询的字段：我们创建索引的字段应该是查询操作⾮常频繁的字段。

被作为条件查询的字段：被作为 WHERE 条件查询的字段，应该被考虑建⽴索引。

频繁需要排序的字段：索引已经排序，这样查询可以利⽤索引的排序，加快排序查询时间。

被经常频繁⽤于连接的字段：经常⽤于连接的字段可能是⼀些外键列，对于外键列并不⼀定要建⽴外键，只是说该列涉及到表与表的关系。对于频繁被连接查询的字段，可以考虑建⽴索引，提⾼多表连接查询的效率。

使⽤表中的多个字段创建索引，就是 联合索引，也叫 组合索引 或 复合索引。

以 score 和 name 两个字段建⽴联合索引：

```sql
ALTER TABLE `cus_order` ADD INDEX id_score_name(score, name);
```

我们应该尽可能的考虑建⽴联合索引⽽不是单列索引。因为索引是需要占⽤磁盘空间的，可以简单理解为每个索引都对应着⼀颗 $^ { \mathsf { B } + }$ 树。如果⼀个表的字段过多，索引过多，那么当这个表的数据达到⼀个体量后，索引占⽤的空间也是很多的，且修改索引时，耗费的时间也是较多的。如果是联合索引，多个字段在⼀个索引上，那么将会节约很⼤磁盘空间，且修改数据的操作效率也会提升。

# 缓存

# Redis 提供的数据类型有哪些？

Redis 中⽐较常⻅的数据类型有下⾯这些：

5 种基础数据类型：String（字符串）、List（列表）、Set（集合）、Hash（散列）、Zset（有序集合）。

3 种特殊数据类型：HyperLogLog（基数统计）、Bitmap （位图）、Geospatial (地理位置)。

除了上⾯提到的之外，还有⼀些其他的⽐如 Bloom filter（布隆过滤器）、Bitfield（位域）

# String 的应⽤场景有哪些？底层实现是什么？

String 是 Redis 中最简单同时也是最常⽤的⼀个数据类型。它是⼀种⼆进制安全的数据类型，可以⽤来存储任何类型的数据⽐如字符串、整数、浮点数、图⽚（图⽚的 base64 编码或者解码或者图⽚的路径）、序列化后的对象。

String 的常⻅应⽤场景如下：

常规数据（⽐如 Session、Token、序列化后的对象、图⽚的路径）的缓存；

计数⽐如⽤户单位时间的请求数（简单限流可以⽤到）、⻚⾯单位时间的访问数；

分布式锁(利⽤ SETNX key value 命令可以实现⼀个最简易的分布式锁)；

Redis 是基于 C 语⾔编写的，但 Redis 的 String 类型的底层实现并不是 C 语⾔中的字符串（即以空字符 \0 结尾的字符数组），⽽是⾃⼰编写了 SDS（Simple Dynamic String，简单动态字符串） 来作为底层实现。

SDS 最早是 Redis 作者为⽇常 C 语⾔开发⽽设计的 C 字符串，后来被应⽤到了 Redis 上，并经过了⼤量的修改完善以适合⾼性能操作。

Redis7.0 的 SDS 的部分源码如下（https://github.com/redis/redis/blob/7.0/src/sds.h）:

```c
/\*Note: sdshdr5 is never used, we just access the flags byte directly. \* However is here to document the layout of type 5 SDS strings. \*/ struct _attribute_ ((packed)) sdshdr5 { unsigned char flags; /* 3 lsb of type, and 5 msb of string length */ char buf[]; } struct _attribute_ ((packed)) sdshdr8 { uint8_t len; /* used */ uint8_t alloc; /* excluding the header and null terminator */ unsigned char flags; /* 3 lsb of type, 5 unused bits */ char buf[]; } struct _attribute_ ((packed)) sdshdr16 { uint16_t len; /* used */ uint16_t alloc; /* excluding the header and null terminator */ unsigned char flags; /* 3 lsb of type, 5 unused bits */ char buf[]; } struct _attribute_ ((packed)) sdshdr32 { uint32_t len; /* used */ uint32_t alloc; /* excluding the header and null terminator */ unsigned char flags; /* 3 lsb of type, 5 unused bits */ char buf[]; } struct _attribute_ ((packed)) sdshdr64 { uint64_t len; /* used */ uint64_t alloc; /* excluding the header and null terminator */ unsigned char flags; /* 3 lsb of type, 5 unused bits */ char buf[]; }
```

通过源码可以看出，SDS 共有五种实现⽅式 SDS_TYPE_5（并未⽤到）、SDS_TYPE_8、SDS_TYPE_16、SDS_TYPE_32、SDS_TYPE_64，其中只有后四种实际⽤到。Redis 会根据初始化的⻓度决定使⽤哪种类型，从⽽减少内存的使⽤。

<table><tr><td>类型</td><td>字节</td><td>位</td></tr><tr><td>sdshdr5</td><td>&lt;1</td><td>&lt;8</td></tr><tr><td>sdshdr8</td><td>1</td><td>8</td></tr><tr><td>sdshdr16</td><td>2</td><td>16</td></tr><tr><td>sdshdr32</td><td>4</td><td>32</td></tr><tr><td>sdshdr64</td><td>8</td><td>64</td></tr></table>

对于后四种实现都包含了下⾯这 4 个属性：

len ：字符串的⻓度也就是已经使⽤的字节数

alloc ：总共可⽤的字符空间⼤⼩，alloc-len 就是 SDS 剩余的空间⼤⼩

buf[] ：实际存储字符串的数组

flags ：低三位保存类型标志

SDS 相⽐于 C 语⾔中的字符串有如下提升：

1. 可以避免缓冲区溢出：C 语⾔中的字符串被修改（⽐如拼接）时，⼀旦没有分配⾜够⻓度的内存空间，就会造成缓冲区溢出。SDS 被修改时，会先根据 len 属性检查空间⼤⼩是否满⾜要求，如果不满⾜，则先扩展⾄所需⼤⼩再进⾏修改操作。

2. 获取字符串⻓度的复杂度较低：C 语⾔中的字符串的⻓度通常是经过遍历计数来实现的，时间复杂度为O(n)。SDS 的⻓度获取直接读取 len 属性即可，时间复杂度为 O(1)。

3. 减少内存分配次数：为了避免修改（增加/减少）字符串时，每次都需要重新分配内存（C 语⾔的字符串是这样的），SDS 实现了空间预分配和惰性空间释放两种优化策略。当 SDS 需要增加字符串时，Redis 会为SDS 分配好内存，并且根据特定的算法分配多余的内存，这样可以减少连续执⾏字符串增⻓操作所需的内存重分配次数。当 SDS 需要减少字符串时，这部分内存不会⽴即被回收，会被记录下来，等待后续使⽤（⽀持⼿动释放，有对应的 API）。

4. ⼆进制安全：C 语⾔中的字符串以空字符 \0 作为字符串结束的标识，这存在⼀些问题，像⼀些⼆进制⽂件（⽐如图⽚、视频、⾳频）就可能包括空字符，C 字符串⽆法正确保存。SDS 使⽤ len 属性判断字符串是否结束，不存在这个问题。


很多⽂章⾥ SDS 的定义是下⾯这样的：

```txt
struct sdshdr{ unsigned int len; unsigned int free; charbuf[];   
}；
```

这个也没错，Redis 3.2 之前就是这样定义的。后来，由于这种⽅式的定义存在问题， len 和 free 的定义⽤了 4个字节，造成了浪费。Redis 3.2 之后，Redis 改进了 SDS 的定义，将其划分为了现在的 5 种类型。

# String 还是 Hash 存储对象数据更好呢？

String 存储的是序列化后的对象数据，存放的是整个对象。Hash 是对对象的每个字段单独存储，可以获取部分字段的信息，也可以修改或者添加部分字段，节省⽹络流量。如果对象中某些字段需要经常变动或者经常需要单独查询对象中的个别字段信息，Hash 就⾮常适合。

String 存储相对来说更加节省内存，缓存相同数量的对象数据，String 消耗的内存约是 Hash 的⼀半。并且，存储具有多层嵌套的对象时也⽅便很多。如果系统对性能和资源消耗⾮常敏感的话，String 就⾮常适合。

在绝⼤部分情况，我们建议使⽤ String 来存储对象数据即可！

# 多级缓存的是怎么做的？为什么还要再多加⼀层本地缓存呢？

这个问题的答案摘⾃《Java⾯试指北》（⾼质量原创 Java ⾯试⼩册）

我们这⾥只来简单聊聊 本地缓存 $^ +$ 分布式缓存 的多级缓存⽅案，这也是最常⽤的多级缓存实现⽅式。

这个时候估计有很多⼩伙伴就会问了：既然⽤了分布式缓存，为什么还要⽤本地缓存呢？

本地缓存和分布式缓存虽然都属于缓存，但本地缓存的访问速度要远⼤于分布式缓存，这是因为访问本地缓存不存在额外的⽹络开销，我们在上⾯也提到了。

不过，⼀般情况下，我们也是不建议使⽤多级缓存的，这会增加维护负担（⽐如你需要保证⼀级缓存和⼆级缓存的数据⼀致性）。⽽且，其实际带来的提升效果对于绝⼤部分业务场景来说其实并不是很⼤。

这⾥简单总结⼀下适合多级缓存的两种业务场景：

缓存的数据不会频繁修改，⽐较稳定；

数据访问量特别⼤⽐如秒杀场景。

多级缓存⽅案中，第⼀级缓存（L1）使⽤本地内存（⽐如 Caffeine)），第⼆级缓存（L2）使⽤分布式缓存（⽐如Redis）。

![image](https://cdn-mineru.openxlab.org.cn/result/2026-03-20/ddb71a26-9c90-49cc-9e00-62502db24f2f/03697a4a192a47506207a6b595498cd6ef7cfe93228c74880c8ca780d688eb1e.jpg)


如果 L2 也没有此数据的话，再去数据库查询，数据查询成功后再将数据写⼊到 L1 和 L2 中。

J2Cache 就是⼀个基于本地内存和分布式缓存的两级 Java 缓存框架，感兴趣的同学可以研究⼀下。

# Redis 缓存穿透、缓存击穿、缓存雪崩区别和解决⽅案

内容较多，单独写了⼀篇⽂章详细介绍：Redis 缓存穿透、缓存击穿、缓存雪崩区别和解决⽅案

# 如何保证缓存和数据库数据的⼀致性？

细说的话可以扯很多，但是我觉得其实没太⼤必要（⼩声 BB：很多解决⽅案我也没太弄明⽩）。我个⼈觉得引⼊缓存之后，如果为了短时间的不⼀致性问题，选择让系统设计变得更加复杂的话，完全没必要。

下⾯单独对 Cache Aside Pattern（旁路缓存模式） 来聊聊。

Cache Aside Pattern 中遇到写请求是这样的：更新 DB，然后直接删除 cache 。

如果更新数据库成功，⽽删除缓存这⼀步失败的情况的话，简单说两个解决⽅案：

1. 缓存失效时间变短（不推荐，治标不治本）：我们让缓存数据的过期时间变短，这样的话缓存就会从数据库中加载数据。另外，这种解决办法对于先操作缓存后操作数据库的场景不适⽤。

2. 增加 cache 更新重试机制（常⽤）：如果 cache 服务当前不可⽤导致缓存删除失败的话，我们就隔⼀段时间进⾏重试，重试次数可以⾃⼰定。如果多次重试还是失败的话，我们可以把当前更新失败的 key 存⼊队列中，等缓存服务可⽤之后，再将缓存中对应的 key 删除即可。
