

# Java 后端学习路线概览

我画了⼀张图，先简单带⼤家看看 Java 后端学习路线的全貌以及我所推荐的学习顺序。

下图中涉及到的每⼀个知识点都会在下⽂中详细介绍（附带学习资源推荐）。

![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/dbe4a45b0dd9694e9bb8736c42c797668332a15024c77fed8601e9e611daa85f.jpg)


![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/1e27cfd4f6a9de2731a63b506aa3d4bf27f6a7590be6f9766f0f1e66b53a3cdc.jpg)



# 已经淘汰的 Java 技术

# JSP

原因：JSP 已经过时，⽆法满⾜现代 Web 开发需求；前后端分离成为主流。

替代⽅案：模板引擎（如 Thymeleaf、Freemarker）在传统全栈开发中更流⾏；⽽在前后端分离架构中，React、Vue、Angular 等现代前端框架已取代 JSP 的⻆⾊。

注意：⼀些国企和央企的⽼项⽬可能仍然在使⽤ JSP，但这种情况越来越少⻅。

# Struts（尤其是 1.x）

原因：配置繁琐、开发效率低，且存在严重的安全漏洞（如世界著名的 Apache Struts 2 漏洞）。此外，社区维护不⾜，⽣态逐渐萎缩。

替代⽅案：Spring MVC 和 Spring WebFlux 提供了更简洁的开发体验、更强⼤的功能以及完善的社区⽀持，完全取代了 Struts。

# EJB (Enterprise JavaBeans)

原因：EJB 过于复杂，开发成本⾼，学习曲线陡峭，在实际项⽬中逐步被更轻量化的框架取代。

替代⽅案：Spring/Spring Boot 提供了更加简洁且功能强⼤的企业级开发解决⽅案，⼏乎已经成为 Java 企业开发的事实标准。此外，国产的 Solon 和云原⽣友好的 Quarkus 等框架也⾮常不错。

# Java Applets

原因：现代浏览器（如 Chrome、Firefox、Edge）早已全⾯移除对 Java Applets 的⽀持，同时Applets 存在严重的安全性问题。

替代⽅案：HTML5、WebAssembly 以及现代 JavaScript 框架（如 React、Vue）可以实现更加安全、⾼效的交互体验，⽆需插件⽀持。

# SOAP / JAX-WS

原因：SOAP 和 JAX-WS 过于复杂，数据格式冗⻓（XML），对开发效率和性能不友好。

替代⽅案：RESTful API 和 RPC 更轻量、⾼效，是现代微服务架构的⾸选。

# RMI（Remote Method Invocation）

原因：RMI 是⼀种早期的 Java 远程调⽤技术，但兼容性差、配置繁琐，且性能较差。

替代⽅案：RESTful API 和 PRC 提供了更简单、⾼效的远程调⽤解决⽅案，完全取代了RMI。

# Swing / JavaFX

原因：桌⾯应⽤在开发领域的份额⼤幅减少，Web 和移动端成为主流。Swing 和 JavaFX 的⽣态不如现代跨平台框架丰富。

替代⽅案：跨平台桌⾯开发框架（如 Flutter Desktop、Electron）更具现代化体验。

注意：⼀些国企和央企的⽼项⽬可能仍然在使⽤ Swing / JavaFX，但这种情况越来越少⻅。

# Ant

原因：Ant 是⼀种基于 XML 配置的构建⼯具，缺乏易⽤性，配置繁琐。

替代⽅案：Maven 和 Gradle 提供了更⾼效的项⽬依赖管理和构建功能，成为现代构建⼯具的⾸选。

# ⾯试题⾃测


# 这份资源可以帮你：

⾃我检测： 系统性地检验⾃⼰对各个知识点的掌握情况。

查漏补缺： 及时发现⾃⼰的薄弱环节，进⾏针对性巩固。

模拟⾯试： 提前熟悉⾯试节奏和⾼频考点。

强烈推荐⼤家通过⾃测的⽅式，把学习推向更深的层次。

# Java 核⼼

# Java 基础

如果你之前没有学习过编程的话，我建议你可以看看视频教程。像尚硅⾕的 《Java 基础教程系列》和韩顺平⽼师的《零基础 30 天学会 Java》就很不错。

![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/a319538d76940e216c2257f81a2dc0a415132c7afdf3b89c04362e65771dad6e.jpg)


![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/900b53218cc93a0981da031b78e72ce43c10511647ff2c070680e19f3d42e6a2.jpg)


我整理了尚硅⾕最新的 Java 后端学习系列完整的视频教程&资料，喜欢看视频的朋友可以点此链接下载： 【最新整理】尚硅⾕ Java 后端全套教程 & 实战项⽬（推荐）。

全部文件》尚硅谷Java学科全套教程（总207.77G..

看视频的同时，配套⼀本好书也是⾮常有作⽤的。

优秀的 Java 基础书籍⾮常多，我这⾥只推荐 3 本。

# 1、《Head First Java》

![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/b720b49a4a7e8ef4718148ad902e3f4f8f6d4d1e5986387dbf6f10f3507346b1.jpg)


作者: Kathy Sierra,Bert Bates 著/杨尊一 编译 张然等 改编

出版社：中国电力出版社

译者:杨尊一

出版年:2007-2

页数：600

定价：79.00元

装帧：16开

丛书:O'Reilly Head First系列 (中译本)

ISBN: 9787508344980 

8.7 



《Head First Java》这本书的内容很轻松有趣，可以说是我学习编程初期最喜欢的⼏本书之⼀了。同时，这本书也是我的 Java 启蒙书籍。我在学习 Java 的初期多亏了这本书的帮助，⾃⼰才算是跨进 Java 语⾔的⼤⻔。我在 Java 这块能够坚持下来，这本书有很⼤的功劳。我身边的的很多朋友学习 Java 初期都是看的这本书。

有很多⼩伙伴就会问了：这本书适不适合编程新⼿阅读呢？

我个⼈觉得这本书还是挺适合编程新⼿阅读的，毕竟是 “Head First” 系列。

# 2、《Java 核⼼技术卷 1+卷 2》

![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/d5997dd1dd9af0bd32c72b3c23380744726c1efce6add0abb639b38d13c7ff08.jpg)


作者:[美]凯 S.霍斯特曼 (Cay S.Horstmann)

出版社:机械工业出版社

副标题：基础知识

原作名:Core Java Volume I-Fundamentls (ElevenEdition)

译者：林琪/苏钰涵

出版年:2019-11-25

页数：636

定价：149元

装帧：平装

丛书:Java核心技术系列

ISBN:9787111636663 

9.4 



《Java 核⼼技术卷 $1 +$ 卷 2》这两本书的内容很多，全看的话⽐较费时间，⽐较适合当⼯作书。我当时在⼤学的时候就买了两本放在寝室，没事的时候就翻翻。个⼈建议有点 Java 基础之后再读这两本，介绍的还是⽐较深⼊和全⾯的。

# 3、《Java 编程的逻辑》

《Java 编程的逻辑》是⼀本⾮常低调的好书，相⽐于⼊⻔书来说，内容更有深度。适合初学者，同时也适合⼤家拿来复习 Java 基础知识。这篇⽂章中有这本书的阅读建议：⼋股⽂骚套路之 Java 基础 。

![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/43a34078916377a8985932d91295a5b73b40ea49c89531f4a514e0d6ed21d7d6.jpg)


作者：马俊昌

出版社:机械工业出版社

出版年:2018-1-1

页数：675

定价：99

装帧：平装

丛书:Java核心技术系列

ISBN: 9787111587729 

9.2 


学完 Java 基础之后，你可以⽤⾃⼰学的东⻄实现⼀个简单的 Java 程序，也可以尝试⽤ Java 解决⼀些编程问题，以此来将⾃⼰学到的东⻄付诸于实践。

不太建议学习 Java 基础的之后通过做游戏来巩固。为什么培训班喜欢通过这种⽅式呢？说⽩点就是为了找到你的 G 点。新⼿学习完 Java 基础后做游戏⼀般是不太现实的，还不如找⼀些简单的程序问题解决⼀下⽐如简单的算法题。

记得多总结！打好基础！把⾃⼰重要的东⻄都记录下来。 API ⽂档放在⾃⼰可以看到的地⽅，以备⾃⼰可以随时查阅。为了能让⾃⼰写出更优秀的代码，《Effective Java》、《重构》 这两本书没事也可以看。

学完这部分内容之后，务必确保⾃⼰掌握下⾯知识点：

基本语法、基本数据类型

对象、类、接⼝

继承、泛型

⽅法

异常、断⾔

集合



学习的过程中，强烈建议配合上我总结的常⻅问题和重要知识点（顺便还能准备⼀下⾯试常⻅问题）：

# . Java 基础：

Java 基础常⻅⾯试题总结(上)（Java 语⾔的基本概念、语法、数据类型、变量、⽅法等）

Java 基础常⻅⾯试题总结(中)（⾯向对象基础、字符串、对象的⽐较与拷⻉等）

Java 基础常⻅⾯试题总结（下） （异常、泛型、反射、SPI、序列化、注解等）

# Java 集合：

Java 集合常⻅⾯试题总结（上） （Java 集合基础、 ArrayList 、 LinkedList 、 HashSet 、ArrayDeque 、 PriorityQueue BlockingQueue 等）

Java 集合常⻅⾯试题总结（下）（ HashMap 、 ConcurrentHashMap 等）

# Java 并发（进阶）

并发或者说多线程这部分内容稍微会⽐较难以理解和实践。如果你刚学完 Java 基础的话，我建议你学习并发这部分内容的时候，可以先简单地了解⼀下基础知识⽐如线程和进程的对⽐。到了后⾯，你对于 Java 了解的更深了之后，再回来仔细看看这部分的内容。

Java 并发书籍的话，挺多写的还不错的，⽐如《实战 Java ⾼并发程序设计》、《Java 并发编程之美》、《Java 并发实现原理：JDK 源码剖析》。

![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/3eb27d1608edfed2f5e5e3170630c5cc8bb5c82b8433b2eb54d5a82a21a69653.jpg)


作者:葛一鸣

出版社：电子工业出版社

出品方:博文视点

出版年:2018-10

页数:416

定价：89

装帧：平装

ISBN: 9787121350030 



![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/9e2d35245edff4fed7c7cfc9822cb15bafab166e39fd054dec6ccca28aa07747.jpg)


作者：翟陆续/薛宾田

出版社:电子工业出版社

出品方:博文视点

出版年:2018-10

页数：356

定价：89

ISBN: 9787121349478 

7.7 




101人评价

# Java并发实现原理：JDK源码剖析

![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/3783751e2048b0de1e19854a7c508afaa153be69fd1876f3d281498b90ee9de6.jpg)


作者：余春龙

出版社：电子工业出版社

出版年：2020-4

页数：256

定价：89元

装帧：平装-胶订

ISBN:9787121379727 

9.0 




想要系统学习的话，还是找从⾥⾯找⼀本认真阅读⼀下。当然，你也可以多选⼏本结合起来⼀起看，遇到不懂的知识点再去看看别的书籍的讲解或者找对应的博客讲解。

视频的话，还是推荐尚硅⾕周阳⽼师讲的：https://www.bilibili.com/video/BV1ar4y1x727/ 。



# JVM（进阶）

JVM 属于是⽐并发更⾼阶⼀些的内容，学习顺序可以适当延后，⽐如你可以在框架知识学完之后再回过头来看 JVM。并且，JVM 相关的知识点，⼀般是⼤⼚（例如美团、阿⾥）和⼀些不错的中⼚（例如携程、顺丰、招银⽹络）⾯试才会问到，⾯试国企、差⼀点的中⼚和⼩⼚就没必要准备了。

不过，我个⼈建议如果你学有余⼒的话，还是抽时间学习⼀下，还是有⽤的。正所谓只有搞懂了JVM 才有可能真正把 Java 语⾔“吃透”。

实际⼯作中，中⼩⼚⼀般不会做 JVM 调优，但万⼀遇到类似 OOM 的问题，你如果知道如何去排查和解决，岂不是更好？

学习 JVM 这部分的内容，⼀定要注意要实战和理论结合。

书籍的话，《深⼊理解 Java 虚拟机》 这本书是⾸先要推荐的。

# 深入理解Java虚拟机 (第3版)

![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/7c374e704d592006dbf07f2e3b5f6d46abf26f7e00c21b13903a6426e703d5be.jpg)


作者：周志明

出版社：机械工业出版社

副标题：JVM高级特性与最佳实践

出版年:2019-12

页数：540

定价：129.00元

装帧：平装

丛书：华章原创精品

ISBN:9787111641247 

9.5 



这本书就⼀句话形容：国产书籍中的战⽃机，实实在在的优秀！ （真⼼希望国内能有更多这样的优质书籍出现！加油！ ）

这本书的第三版已经出来挺久了，新增了很多不错的内容⽐如 ZGC 等新⼀代 GC 的原理剖析。

不论是你⾯试还是你想要在 Java 领域学习的更深，你都离不开这本书籍。这本书不光要看，你还要多看⼏遍，⾥⾯都是⼲货。这本书⾥⾯还有⼀些需要⾃⼰实践的东⻄，我建议你也跟着实践⼀下。

类似的书籍还有 《实战 Java 虚拟机》、《虚拟机设计与实现:以 JVM 为例》，这两本都是⾮常不错的！

# 实战Java虚拟机

![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/5207bfcbdec9b53e02f749bdf026c980600a4e9e6bbc102fe02d0fc36ecd8ae3.jpg)


作者：葛一鸣

出版社：电子工业出版社

出品方：博文视点

副标题：JVM故障诊断与性能优化

出版年：2015-3

页数：452

定价：79.00元

装帧：平装

ISBN: 9787121256127 

# 虚拟机设计与实现

![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/f580130b8cdaf0a45c1dc2944056dd77694ffb8caad4b90fb635f2783c71989b.jpg)


作者：李晓峰

出版社：人民邮电出版社

副标题：以JVM为例

译者：单业

出版年：2020-1

页数：380

定价：129元

装帧：平装

丛书：图灵程序设计丛书·Java系列

ISBN:9787115527288 



如果你对实战⽐较感兴趣，想要⾃⼰动⼿写⼀个简易的 JVM 的话，可以看看 《⾃⼰动⼿写 Java 虚拟机》 这本书。

![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/05aa5fed076d323478a256e45fbd943d368561870ed85964bbdfaccd9c131794.jpg)


![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/c40af323b6a639d2d6a373825705a39876eaef583c15d3771db87fe4a5c20d10.jpg)


作者：张秀宏

出版社：机械工业出版社

出版年：2016-6-1

页数：267

定价：69.00元

装帧：平装

丛书：自己动手系列

ISBN:9787111534136 

8.2 



书中的代码是基于 Go 语⾔实现的，搞懂了原理之后，你可以使⽤ Java 语⾔模仿着写⼀个，也算是练练⼿！ 如果你当前没有能⼒独⽴使⽤ Java 语⾔模仿着写⼀个的话，你也可以在⽹上找到很多基于 Java 语⾔版本的实现，⽐如《zachaxy 的⼿写 JVM 系列》 。

另外，R ⼤在⾖瓣发的《从表到⾥学习 JVM 实现》这篇⽂章中也推荐了很多不错的 JVM 相关的书籍，推荐⼩伙伴们去看看。

视频的话，尚硅⾕的宋红康⽼师讲的《JVM 全套教程》内容⾮常硬，⼀共有接近 400 ⼩节（对应的浓缩精华版：《尚硅⾕ JVM 精讲与 GC 调优教程》）。

课程的内容分为 3 部分：

1. 《内存与垃圾回收篇》

2. 《字节码与类的加载篇》

3. 《性能监控与调优篇》

![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/99f5d5c7f4ae528862694b7bbb61588dda73fd30ae2d98f83e21d907aaa4e348.jpg)



学习的过程中，强烈建议配合上我总结的常⻅问题和重要知识点：

Java 内存区域详解（重点）

JVM 垃圾回收详解（重点）

类⽂件结构详解

类加载过程详解

类加载器详解（重点）

# 数据库

# 基础（可选）


书籍的话，强烈推荐《数据库系统概念》，这本书涵盖了数据库系统的全套概念，知识体系清晰，是学习数据库系统⾮常经典的教材！不是参考书！

# 数据库系统概念

![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/3eb097493f28387c425feed23c17799fd6080774fc1b6edf40cb2c55ca0a0035.jpg)


出版社：机械工业出版社

副标题：（原书第6版）

原作名:Database System Concepts, 6E

译者：杨冬青/李红燕/唐世渭

出版年：2012-3

页数：805

定价：99.00元

装帧：平装

丛书：计算机科学丛书

ISBN:9787111375296 

8.4 




如果你觉得书籍⽐较枯燥，⾃⼰坚持不下来的话，我推荐你可以先看看⼀些不错的视频。就⽐如北京师范⼤学的《数据库系统原理》这个就很不错。

这个课程的⽼师讲的⾮常详细，⽽且每⼀⼩节的作业设计的也与所讲知识很贴合，后⾯还有很多配套实验。

![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/fed415bd5f6ac98758bbe43dfdce2938c3db223d1c7534c349ee4c6d1e1210cb.jpg)


如果你⽐较喜欢动⼿，对于理论知识⽐较抵触的话，我推荐你看看《如何开发⼀个简单的数据库》，这个 project 会⼿把⼿教你编写⼀个简单的数据库。

# Let's Build a Simple Database

Writing a sqlite clone from scratch in C 

# How Does a Database Work?

• What format is data saved in? (in memory and on disk) 

·When does it move from memory to disk? 

· Why can there only be one primary key per table? 

·How does rolling back a transaction work? 

·How are indexes formatted? 

· When and how does a fulltable scan happen? 

· What format is a prepared statement saved in? 

In short, how does a database work? 

I'm building a clone of sqlite from scratch in C in order to understand, and 

I'm going to document my proces 

# Table of Contents

“What I cannot create, I do not understand."- Richard Feynman 

![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/ec76242318abab283059787ea6e6cb21c55a9577a464907af60ee64dbe97674a.jpg)



sqlite architecture (https://www.sqlite.org/arch.html)


This project is maintained by cstack 

纸上学来终觉浅 绝知此事要躬⾏！强烈推荐 CS 专业的⼩伙伴⼀定要多多实践！！！

# MySQL

对于 Java 开发来说，虽然 PostgreSQL 也挺⽕，但 MySQL 是主流，国内的绝⼤部分企业还是⽤的MySQL。

MySQL ⼊⻔可以找⼀些视频看看，⽐如⿊⻢的《MySQL 数据库⼊⻔到精通》。看视频的过程中，可以配套⼀本 MySQL ⼊⻔类的书籍⽐如《MySQL 必知必会》。

初期不需要学太深了，搞清楚下⾯这些知识点即可：

1. MySQL 常⽤命令 ：

安全：登录、增加/删除⽤户、备份数据和还原数据

数据库操作： 建库建表/删库删表、⽤户权限分配


2. MySQL 中常⽤的数据类型、字符集编码

3. MySQL 简单查询、条件查询、模糊查询、多表查询以及如何对查询结果排序、过滤、分组…

4. MySQL 中使⽤索引、视图、存储过程、游标、触发器

更进⼀步的话，可以找⼀些优秀的书籍来学习底层原理和性能优化，⽐如《⾼性能 MySQL》和《MySQL 技术内幕》。

![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/fe6a0299fbc753bd98beb957b632c1cb92468cdebbd27c3ddc7ec3b5bde50dec.jpg)


![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/9474a0819ad6805371ae925f0da32061778c1997d931c1aed5d281e1c51a46cc.jpg)


![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/974ad38421b1ce2ab0578ef77bced5420d4ba84be645b35ebe944711f0e1cb21.jpg)


另外，强推⼀波 《MySQL 是怎样运⾏的》 这本书，内容很适合拿来准备⾯试。讲的很细节，但⼜不枯燥，内容⾮常良⼼！

# MySQL是怎样运行的

![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/b1d4a71fa14b5b07bbf2095b43916ab87da966879b37e09ce9f76be11c987465.jpg)


作者：小孩子4919

出版社：人民邮电出版社

出品方：异步图书

副标题：从根儿上理解MySQL

原作名：小孩子4919

出版年:2020-11-1

页数：456

定价：99.00元

装帧：平装

ISBN: 9787115547057 

9.5 


如果你想让⾃⼰更加了解 MySQL ，同时也是为了准备⾯试的话，下⾯这些知识点要格外注意：

1. 索引：索引优缺点、B 树和 $\mathsf { B } +$ 树、聚集索引与⾮聚集索引、覆盖索引

2. 事务：事务、数据库事务、ACID、并发事务、事务隔离级别

3. 存储引擎（MyISAM 和 InnoDB）

4. 锁机制与 InnoDB 锁算法

学习的过程中，强烈建议配合上我总结的常⻅问题和重要知识点：

MySQL 常⻅⾯试题总结（MySQL 基础、存储引擎、事务、索引、锁、性能优化等）

MySQL 索引详解

MySQL 三⼤⽇志(binlog、redo log 和 undo log)详解

MySQL 事务隔离级别详解

InnoDB 存储引擎对 MVCC 的实现

SQL 语句在 MySQL 中的执⾏过程

# PostgreSQL（可选）

和 MySQL ⼀样，PostgreSQL 也是开源免费且功能强⼤的关系型数据库。PostgreSQL 的 Slogan是“世界上最先进的开源关系型数据库” 。

https://www.postgresql.org 

# PostgreSQL: The world's most advanced open source database

PostgreSQL is a powerful,open source object-relational database system with over 30 years of active development that has earned it a strong reputation for ... 

客观来说，PostgreSQL 确实⽐ MySQL 优秀。不过，⽬前国内 MySQL 还是主流，PostgreSQL 是可选择性学习的。

PostgreSQL 中⽂⽂档建议看看：http://www.postgres.cn/docs/14/index.html 。另外， PostgreSQL书籍的话，看这⾥的推荐即可：https://javaguide.cn/books/database.html#postgresql 。

# Redis

后端项⽬如果⽤到分布式缓存的话，⼀般⽤的都是 Redis。不过，Redis 不仅仅能做缓存，还能⽤作分布式锁、延时队列、消息队列等等。

免费的视频教程的话，推荐 GeekHour 的 ⼀⼩时Redis教程（⾮常推荐，通俗易懂，简单介绍了Redis 中涉及到的绝⼤部分知识点） 和尚硅⾕的 《Redis 7 系列最新视频》（阳哥出品，内容更全⾯，Redis 版本更新，强烈推荐）。

书籍的话，强烈推荐 《Redis 设计与实现》和 《Redis 核⼼原理与实践》 这两本书。《Redis 核⼼原理与实践》这本书出版⽇期相对近⼀些，主要是结合源码来分析 Redis 的重要知识点⽐如各种数据结构和⾼级特性。

# Redis设计与实现

![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/ab6e28aeac019ad682d9e97a5a8e662bda45eade49eb1c2354f23d8e8e4fe254.jpg)


作者：黄健宏

出版社：机械工业出版社

出版年：2014-6

页数：388

定价：79.00

装帧：平装

丛书：数据库技术丛书

ISBN: 9787111464747 

8.7 


# Redis核心原理与实践

![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/56d3cfa1196fdde5c71d4f7c1fef1147288ea9c6643a01de4005ad7c7be327b4.jpg)


作者：梁国斌

出版社：电子工业出版社

出版年:2021-8-18

页数：468

定价：138.00元

装帧：平装

ISBN: 9787121415487 

8.0 


# MongoDB（可选）

MongoDB 作为 Java 后端开发来说，是可选择性学习的，⽤的不多，⾯试⼀般也不会问，除⾮你的项⽬⽤到了 MongoDB 。

这⾥就不推荐视频或者书籍了，推荐两篇我写的⽂章：


# 常⽤开发⼯具

⾮常重要！⾮常重要！特别是 Git 和 Docker。

除了下⾯这些⼯具之外，我强烈建议你⼀定要搞懂 Github 的使⽤。⼀些使⽤ Github 的⼩技巧，你可以看Github ⼩技巧这篇⽂章。

# IDEA

俗话说：“⼯欲善其事，必先利其器 !”。选择⼀款好的开发⼯具对于我们⾼效率编码⾮常有帮助！

常⽤的 Java 开发⼯具就 Eclipse 和 IDEA。就我个⼈⽽⾔ IDEA 是最适合 Java 开发者的 IDE ，没有之⼀（勿杠，你喜欢的就是最好的）。

除了 IDEA ⾃身对编码优秀的⽀持（⽐如智能上下⽂提示）之外，IDEA 中还有丰富的插件来帮助我们⾼效开发。

近⼏年，像 Cursor 这样的 AI 编程 IDE 兴起，确实对 IDEA 有了⼀定冲击。但整体来看，IDEA 依然难以被取代。⽆论是开发体验还是代码重构能⼒，IDEA 都有着⽆与伦⽐的优势。当然，在 AI 辅助编程这⼀块，IDEA 的表现的确有些落后。要知道，过去代码智能提示可是它的拿⼿好戏。

IntelliJ IDEA 官⽅中⽂⽂档今年正式上线了，强烈推荐以这个为第⼀⼿资料。

IDEA 官⽅中⽂⽂档⼊⼝： https://www.jetbrains.com/zh-cn/help/idea/getting-started.html

另外， 「IDEA ⾼效使⽤指南」是我创建的⼀个⽹站，上⾯包含了下⾯这些内容：

IDEA 使⽤技巧

IDEA 必备插件

. IDEA 插件开发⼊⻔

使⽤ IDEA 进⾏重构的⼩技巧

使⽤ IDEA 进⾏源码阅读的技巧

![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/9e1793fce2761f4f523631449f43582a70503a070f649918c8002cfcbfbc10ea.jpg)


# Maven

Maven 其实使⽤起来挺简单的，⼀两天时间就能⼊⻔基本使⽤了。不过，想要⽤好还是挺难的，初期的时候会基本使⽤就好了。

多提⼀句：学习常⽤框架之前可以提前花时间学习⼀下 Maven 的使⽤，千万不要 到处找 Jar 包，下载 Jar 包（如果你做的项⽬没⽤上包管理⼯具，那请你尽快换⼀个新点的教程看）。

Maven 这⾥不⽤推荐什么视频或者书籍了，直接看下⾯这篇⽂章即可：

Maven 核⼼概念总结

Maven 最佳实践

四⼗五图，⼀万五千字！⼀⽂让你⾛出迷雾玩转 Maven！

学完之后，务必要搞懂下⾯这些问题（初学者搞懂前两个问题即可）：

1. Maven 项⽬如何创建？如何引⼊依赖？

2. Maven 依赖冲突如何解决？

3. Maven 多模块项⽬的构建、运⾏、打包如何做？

4. Maven 私服如何搭建？

# Git

Git 技能对于程序员来说也是必备的！试着在学习的过程中将⾃⼰的代码托管在 Github 上，有⼀个漂亮的 Github 主⻚在求职⾯试中是⼗分加分的。并且，现在的企业都是基于 Git 在 GitHub 或GitLab 平台上做版本控制。

学习 Git 的话，强烈推荐给⼤家⼀个可以交互式学习 Git 的⽹站 Learn Git Branching。效果真的⾮常⾮常棒，通过游戏的⽅式让你学习 Git 的常⻅操作。

整个教程分为很多关，每⼀关都有⾮常详细的指导，还会有详细的动图展示结果。并且，你做错了之后还可以使⽤ reset 命令从头开始。

![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/3e77c1ae6d664bcc51d31631f0a7e9e783fb2b59eccbd2e6e8e10aea4c3c4199.jpg)


如果你是在不知道答案的话，还可以使⽤ show solution 命令查看答案。

![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/6d00e39c5dcacc9ad55c84ecb5e1e5e837e07764149895c176da398602fdb37b.jpg)


![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/479c5fd633908e1ae3ccd92d12dc9f8d0fb73f21a8880632a15d80bbd09f2b8c.jpg)


这种即时反馈的学习让过程变得有趣！真⼼感谢这个⽹站的作者，太爱了！

另外，你可以看看这篇 Git 极简⼊⻔ ，像版本控制和 Git 的相关概念、Git 常⻅操作这篇⽂章都有介绍到。

如果想要详细了解 Git 的话，可以看看《Pro Git》这本书，介绍的⾮常全⾯，免费，⽀持阅读，并且有中⽂版！

# Pro Git

![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/e64b0c3628957e48ddcf7a897f9288bae3b7defea7948708241f04c3ba841b95.jpg)


作者：Scott Chacon

出版社:Apress

出版年：2009-8-27

页数：288

定价：USD 34.99

装帧：Paperback

ISBN:9781430218333 


总结

欢迎来到Pro Git第二版。第一版出版到现在已经过去了四年。到今天，Git 虽然出现了许多改变，但是还有很多重要的事情一如昨日。因为Git核心团队对保持向后兼容性异常固执，所以直到今天大多数核心命令与概念依然有效，但是围绕Git的社区还是有一些重大的增加与改变。本书的第二版就是为了更新书籍并讲解那些改动以使其对新用户更有帮助。

当我写第一版时，Git对于超级黑客来说还是一个相对难用，只能勉强接受的工具。它开始在特定的社区中快速发展，但是还没有达到像今天一样无处不在的地步。自那时起，几乎每一个开源社区都采用了它。Git在Windows上取得了难以置信的进步，包括所有平台的图形用户界面对它的支持、IDE的支持，以及商业使用的爆炸式发展。四年前的Pro Git对此一无所知。新版本的主要目标之一就是涉及Git社区中那些所有新的前沿领域。

使用 Git的开源社区也呈现出爆炸式的发展。大概在五年前吧，我坐下来写这本书时（写完第一个版本花了我不少时间），我开始在一个知名度极小的开发Git托管网站的公司工作，这家公司就是GitHub。本书出版时大概有几千人在使用 GitHub 网站，而为其工作的只有我们四个人。在我写这篇介绍时，GitHub 宣布我们托管了1000万个项目、拥有大概 500万注册开发者账户与大概230 名员工。爱它也好，恨它也罢，当我坐下来写第一版时，GitHub 以一种意想不到的方式猛烈地改变了一大批开源社区。

我在 Pro Git 的原始版本中写了一节我并不是很满意的内容，是作为和提供Git托管服务相关的例子的 GitHub。我在书里写的东西本质上都是和社区有关的，但是又不得不讨论到我的公司，这点我不喜欢。同时我还不喜欢那个兴趣的冲突，GitHub 在 Git社区中的重要性是无法避免的。我已经决定将本书的那部分转变为深度介绍GitHub 是什么以及如何高效地使用它，而不再是作为一个Git托管的例子。如果你正学习如何使用Git，那么了解如何使用 GitHub将会帮助你加入到一个巨大的社区中。不论你决定为自己的代码使用哪一个Git托管服务，这都很有价值。

自从上次出版以来另一个重大变革是Git网络传输HTTP协议的开发与崛起。书中的大多数例子都已经从 SSH切换到 HTTP，因为它更简单。

在过去这几年看到Git从一个相对无名的版本管理系统成长为商业与开源版本管理的事实标准是令人吃惊的。我很高兴Pro Git 做得很好并已经成为市场上几本既成功又完全开源的技术书籍之一。

我希望你能享受这个升级版的Pro Git。

本书的第一版就是将我与Git结下不解之缘的原因。书中采用的是我引进的做软件的风格，这种风格比我之前看到的任何事情都要自然。那时我已经做了好几年开发者了，但是这本书将我指引到一条更加精彩的道路上。

几年之后的现在，我是Git的一个主要实现的贡献者，我在最大的Git托管公司工作，我已经环游世界教人们使用Git。当 Scott问我是否有兴趣在第二版上工作时，我甚至连想都没想就答应了。

能在这本书上工作是一份巨大的快乐与荣耀。我希望它能像帮助我一样帮助你。

致我的妻子，Becky，没有她的话这段冒险不会开始。—Ben

谨以此书献给我的家人。给这些年一直支持着我的妻子Jesica 和女儿Josephine，还有那些在我风烛残年之时还能支持我的人。

这是这本书的另外⼀个在线阅读地址：https://git-scm.com/book/zh/v2。

如果你⽐较喜欢看视频教程的话，可以看看极客时间的《玩转 Git 三剑客》，课程的作者是携程代码平台负责⼈苏玲，讲的挺不错的！

# Docker

传统的开发流程中，我们的项⽬通常需要使⽤ MySQL、Redis、FastDFS 等等环境，这些环境都是需要我们⼿动去进⾏下载并配置的，安装配置流程极其复杂，⽽且不同系统下的操作也不⼀样。

Docker 的出现完美地解决了这⼀问题，我们可以在容器中安装 MySQL、Redis 等软件环境，使得应⽤和环境架构分开，它的优势在于：

1. ⼀致的运⾏环境，能够更轻松地迁移

2. 对进程进⾏封装隔离，容器与容器之间互不影响，更⾼效地利⽤系统资源

3. 可以通过镜像复制多个⼀致的容器

Docker 常⻅概念解读，可以看这篇 JavaGuide 的这篇Docker 基本概念解读 ，从零到上⼿实战可以看Docker 从⼊⻔到上⼿⼲事这篇⽂章，内容⾮常详细！

另外，再给⼤家推荐⼀本质量⾮常⾼的开源书籍 《Docker 从⼊⻔到实践》 ，这本书的内容⾮常新，毕竟书籍的内容是开源的，可以随时改进。

![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/9894a1d106139d4600e8c7d028d9beacdfa5a19cf3df3b4ba034cf32d6aac214.jpg)


![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/fc3a505da4bcfbbf1174981d6f45786752540be8435a140648d6433a2f074a4c.jpg)


如果想看视频的话，推荐这个：https://www.bilibili.com/video/BV11L411g7U1/ ，没啥废话，⼲货挺多。⽽且，课件也是直接免费分享出来的：https://docker.easydoc.net/doc/81170005/cCewZWoN/lTKfePfP 。

■Docker 快速安装软件

制作自己的镜像

目录挂载

多容器通信

★ Docker-Compose 

发布和部署

备份和迁移数据

![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/2175821b01ed19f64f21f97b17955450fa93622d124cf2c46cf4025f729bf9ab.jpg)


# Docker简介和安装

# Docker是什么

Docker是一个应用打包、分发、部署的工具

你也可以把它理解为一个轻量的虚拟机，它只虚拟你软件需要的运行环境，多余的一点都不要，而普通虚拟机则是一个完整而庞大的系统，包含各种不管你要不要的软件。


跟普通虚拟机的对比


<table><tr><td>特性</td><td>普通虚拟机</td><td>Docker</td></tr><tr><td>跨平台</td><td>通常只能在桌面级系统运行，例如 Windows/Mac, 无法在不带图形界面的服务器上运行</td><td>支持的系统非常多，各类 windows 和 Linux 都支持</td></tr><tr><td>性能</td><td>性能损耗大，内存占用高，因为是把整个完整系统都虚拟出来了</td><td>性能好，只虚拟软件所需运行环境，最大化减少没用的配置</td></tr><tr><td>自动化</td><td>需要手动安装所有东西</td><td>一个命令就可以自动部署好所需环境</td></tr><tr><td>稳定性</td><td>稳定性不高，不同系统差异大</td><td>稳定性好，不同系统都一样部署方式</td></tr></table>

跟普通虚拟机的对比

打包、分发、部署

Docker部署的优势

Docker通常用来做什么

重要概念：镜像、容器

安装

启动报错解决

镜像加速源

最后，在学习完 Docker 的常⻅操作之后，建议⼤家以⼀个前后端分离的项⽬为例，去实践部署⼀下。⽐如，你可以选择部署⾃⼰的简历项⽬，这样的话，项⽬经历部分贴上在线体验地址，也算是⼀个加分项了！

# 设计模式

软件开发中有⼀个概念叫做“软件复⽤”。简单来说，软件复⽤就是我们在构建⼀个新的软件的时候，不需要从零开始，通过复⽤已有的⼀些轮⼦（框架、第三⽅库等）、设计模式、设计原则等等现成的物料，我们可以更快地构建出⼀个满⾜要求的软件。

软件复⽤需要设计模式的帮助。因为，在软件开发中，设计模式可以通过封装变化来提⾼代码的可扩展性和可维护性！

在我们平时⼯作的业务开发中，如果你不会设计模式，你或许也可以完成项⽬的功能需求。但是！单纯 CRUD 多没意思啊！我们要思考如何写出质量更⾼的业务代码。另外，各种框架⽐如Spring、MyBatis 中都⼤量使⽤了设计模式。如果，你想要搞懂他们的原理，设计模式也是你的必备利器。

设计模式不光需要我们在学习，最重要的还是要不断去实践体会。但是！设计模式不是银弹，不要为了⽤设计模式⽽⽤设计模式。

想要看书学习设计模式的话，⾸推 《重学 Java 设计模式》 。有趣的例⼦，配合形象的图⽚，通过实战案例讲解设计模式的⽅式秒极了！⽂中的每⼀个细节⽆不透露着作者的⽤⼼！每⼀种设计模式实际都不难理解，⼤部分读者最需要的还是设计模式的实战经验。如果你能细⼼思考实践《重学Java 设计模式》 中的每⼀个案例，我相信，你对设计模式的理解⼀定会更上⼀层楼！

# 重学Java设计模式 (全彩)

![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/ff5d772b8e1e7d2237c73a3c5f134e5488aa67835db3458131d26d2ef187e936.jpg)


作者：付政委

出版社：电子工业出版社

原作名：付政委

出版年:2021-5-1

页数：367

定价：128

ISBN:9787121409387 


想要看视频学习的话，⾸推 《尚硅⾕ Java 设计模式（图解+框架源码剖析）》 这个视频。


尚硅谷Java设计模式 (图解+框架源码剖析)


![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/5751ed9bbc10563324fabf9a1c529124c80e9db606ca1736ae62eeb01555bc38.jpg)



![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/fad38255dd0bf462f42609ab2cc0aed5cd723cdb147220edf09fd5ae86c7af8c.jpg)


【2021版】马士兵重讲23种设计模式，居然能这么.

![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/1d0879891f43aead6ed8f3d2978d88b280fc0c6b70eaccc993b90054967f03a3.jpg)


狂神说JaTA


这个视频通过图解 $^ +$ 框架源码分析的⽅式全⾯地讲解了设计模式相关的内容，包括设计模式七⼤原则、UML 类图-类的六⼤关系、23 种设计模式及其分类等知识点。

# Linux

对于 Java 程序员来说， 我们需要掌握 Linux 基本的使⽤，尤其是是各种常⽤的命令⽐如：⽬录切换命令、⽬录操作命令、⽂件的操作命令、压缩或者解压⽂件的命令等等。像 Linux 内核架构、底层原理这些底层内容，不是必需的，可以根据⾃身情况来决定是否学习。

对于想要快速⼊⻔ Linux 的同学来说，建议阅读我写的 Linux 基础知识总结这篇⽂章，⾥⾯介绍了Java 程序员必知的 Linux 的⼀些概念以及常⻅命令。

视频的话，我推荐 GeekHour 的 30 分钟 Linux ⼊⻔教程，通俗易懂，实战讲解！不过，相对偏基础⼀些，适合想要快速⼊⻔的同学。

对于想要系统学习的同学来说，还是建议看书籍，像《⻦哥的 Linux 私房菜》系列就挺不错的。不过，内容有点太多了，个⼈还是更建议作为⼯具书参考或者选择⾃⼰感兴趣的内容章节进⾏学习。

# 鸟哥的Linux私房菜基础学习篇第四版

![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/30474a7906289320bdd1d48d7cccc2004655cdf68a01677c20d8aa7b207223b3.jpg)


作者：鸟哥

出版社：人民邮电出版社

出品方:异步图书

出版年:2018-10

页数：796

定价：118.00元

装帧：平装

丛书:鸟哥的Linux私房菜

ISBN: 9787115472588 



不要忘记学习⼀下 Shell 编程了，这个也是必须要掌握的，快速⼊⻔可以阅读我写的 Shell 编程基础知识总结这篇⽂章，总结了 Shell 变量、基本运算符、流程控制、函数这些重要的知识点。

# 前端基础

笔者主要从事 Java 后端开发的，对于前端的了解属于⽪⽑，刚刚⼊⻔的状态（当过⼀年全栈），这⾥只是简单聊聊⾃⼰的看法。

前端框架更新换代的很快，⽬前⽐较流⾏的是 Vue 和 React 。对于国内的同学，Vue 更适合投⼊精⼒学习，因为国内⽤ Vue 的公司更多⼀些。不过，前端框架并不是必学的，可以根据⾃身情况来决定是否学习。

不过，不管前端这些技术怎么变，前端三剑客（HTML、CSS、JavaScript ）是不会变的，也是必学的。

HTML 和 CSS 相对 JS 来说就⽐较简单了。你可以在 W3school 上学习⼀些关于 HTML、CSS、JS的基础知识。然后，通过⼀个简单的前端项⽬来实战⼀下。⽐如你可以做⼀个个⼈简历或者模仿某某官⽹写个类似的⽹⻚。

JavaScript 的⽔更深，也是前端⾯试中的重⼼。

学习 JS 的话，MDN 上的 JS 相关的内容是必须要看的！上⾯的内容很全⾯，质量⾮常⾼！

除此之外，开源的 JS 教程《The Modern JavaScript Tutorial》⾮常赞！⽬前的话，这个系列的教程还被翻译成了多国的语⾔。

![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/6f1789a4b66360b385acf8c59e7a77a85ef1f3577ff6a23bff6901b115a92240.jpg)


# 这个教程的内容分为 3 部分

1. JavaScript 编程语⾔ ： JavaScript ⼊⻔，还会介绍 OOP 等相关⾼级概念。

2. 浏览器（⽂档，事件，接⼝） ： 学习如何管理浏览器⻚⾯

3. 其他⽂章 ： 按需学习其他 JavaScript ⾼级知识。

另外，除了⼀些⽼项⽬之外，现在⼀般都是前后端分离开发，也就是前端和后端可以独⽴开发、测试和部署，两者之间通过 API 进⾏通信。因此，后端程序员还需要掌握：

HTTP 协议（计算机⽹络部分的内容，这⾥再多提⼀下）

. RESTful API 的设计和使⽤

# J2EE 基础

# Servlet

Servlet 属于⽐较古⽼的技术了，现在你⼏乎不会直接使⽤到 Servlet 相关的 API。不过，学习Servlet 有助于我们搞清各种封装的⽐较好的 Web 框架的原理，⽐如 Spring MVC 不过就是对Servlet 的封装，它的底层还是依赖于 Servlet 。

在 Java Web 程序中， Servlet 主要负责接收⽤户请求 HttpServletRequest ,在 doGet() , doPost() 中做相应的处理，并将回应 HttpServletResponse 反馈给⽤户。

你可以通过书籍《Head First Servlets & JSP（中⽂版）》或者《Servlet 和 JSP 学习指南》来学习Servlet 基础知识。

注意：JSP 就不要学了，过时的技术，已经被淘汰了！

# Web 服务器

Tomcat 是 Apache 基⾦会下的⼀个项⽬，主要⽤作 Web 服务器。

如果你直接学习 Spring Boot 的话，不学习 Tomcat 也没什么影响（建议还是学⼀学）。因为Spring Boot （ spring-boot-starter-web ）使⽤ Tomcat 作为默认的嵌⼊式 Servlet 容器, 你使⽤起来是⽆感知的。

简单来说，Tomcat 主要实现了 2 个核⼼功能：

1. 处理 Socket 连接，负责⽹络字节流与 Request 和 Response 对象的转化。

2. 加载和管理 Servlet ，以及具体处理 Request 请求。

如果你要深⼊研究 Tomcat 的话，⾸选极客时间的 《深⼊拆解 Tomcat & Jetty》 这个专栏。这是我看过讲解 Tomcat 底层原理最好的资料，强烈推荐！

这个专栏不光可以加深⾃⼰对于 Tomcat 的理解，还能提⾼⾃⼰对于系统架构、性能优化等领域的思考。



 你将获得

·通过Tomcat&Jetty深入理解Java进阶技术;

·掌握了Tomcat&Jetty中间件的设计思想;

·解读Servlet最新技术及实际应用；

·让Web程序飞起来的性能调优策略。

除了 Tomcat 之外，Nginx 也是必须要学习的！

Nginx 是⼀个⾼性能的 HTTP 和反向代理服务服务器，经常被拿来做反向代理和负载均衡。

如果你要学习 Nginx 的话，可以看看《Nginx 核⼼知识 150 讲》 。内容很全⾯，从概念、代码再到实战，从 HTTP 到 OpenResty 。

# 常⽤框架

实际⾯试中，框架类知识问的不多，学习常⽤框架更多地是为了满⾜项⽬开发需要以及⼯作要求。

# Spring/SpringBoot

没有学习 Spring 可以直接上⼿学习 SpringBoot 吗？

明确的说，必须可以！⽬前绝⼤部分企业都是⽤的 SpringBoot ，Spring 也并不是学习 Spring Boot的前置基础，相⽐于 Spring 来说，Spring Boot 要更容易上⼿⼀些！如果你只是想使⽤ Spring Boot来做项⽬的话，直接学 Spring Boot 就可以了。

不过，个⼈还是建议提前搞懂 Spring AOP 和 IoC 这俩⽐较重要的概念之后再去学习 SpringBoot。除此之外，准备⾯试的话，Spring 中 bean 的作⽤域与⽣命周期、SpringMVC ⼯作原理详解等等知识点都是⾮常重要的，⼀定要搞懂。推荐阅读这篇⽂章：Spring 常⻅⾯试题总结。

学习 Spring Boot 的话，还是建议可以多看看 《Spring Boot 的官⽅⽂档》，写的很详细。

像 SpringBoot 和⼀些常⻅技术的整合你也要知道怎么做，⽐如 SpringBoot 整合 MyBatis、ElasticSearch、SpringSecurity、Redis 等等。尽量还是实践⼀下，写⼀些 Demo。到了后期，甚⾄可以独⽴做⼀些⼩项⽬把这些知识都应⽤上。

书籍的话，个⼈其实并没有什么特别好的推荐，毕竟是框架类知识，更新换代的⽐较快，很多书籍的内容都已经过时了。

考虑到很多同学⽐较喜欢阅读书籍，我这⾥还是简单推荐⼏本吧！

对于想要实战的同学，我强烈不推荐看书，直接看尚硅⾕的实战项⽬即可。这篇⽂章可以获取到最新的视频且对尚硅⾕的实战项⽬做了介绍：【最新整理】尚硅⾕ Java 后端全套教程 & 实战项⽬（推荐）。

对于专研 Spring Boot 底层原理同学，可以看看 《Spring Boot 编程思想（核⼼篇）》 。

![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/01f38ab67e9702ac9d66ce01d4d586a61ac936f65ec1a31ea2e352b4ea740bdb.jpg)


作者：小马哥

出版社：电子工业出版社

出品方：博文视点

出版年:2019-4

页数：628

定价：118.00元

装帧：平装

ISBN:9787121360398 

6.5 



如果你⽐较喜欢看视频的话，推荐尚硅⾕雷神的《2023 版 Spring Boot3 零基础⼊⻔》 。这可能是全⽹质量最⾼并且免费的 Spring Boot 教程了，好评爆炸！

另外，Spring Boot 这块还有很多优质的开源教程，我已经整理好放到 Java 优质开源技术教程 中了。

# ■技术教程

实战项目

系统设计

工具类库

$\pmb { \mathscr { p } }$ 开发工具

机器学习

大数据

# SpringBoot

·springboot-guide：SpringBoot 核心知识点总结。基于 Spring Boot $^ { 2 . 1 9 + }$ 

·SpringAll：循序渐进，学习 Spring Boot、Spring Boot & Shiro、Spring Cloud、Spring Security &Spring Security OAuth2，博客 Spring 系列源码。

·Springboot-Notebook :一系列以 Spring Boot 为基础开发框架，整合 Redis、 Rabbitmq、ES、MongoDB、Spring Cloud、Kafka、Skywalking 等互联网主流技术，实现各种常见功能点的综合性案例。

·springboot-learning-example：Spring Boot 实践学习案例，是 Spring Boot 初学者及核心技术巩固的最佳实践。

·spring-boot-demo：spring boot demo 是一个用来深度学习并实战 spring boot的项目，目前总共包含 63个集成 demo，已经完成 52个。

·SpringBoot-Labs：Spring Boot 系列教程。

# MyBatis

MyBatis 是国内使⽤最多的 ORM 框架。在学习 Spring/Spring Boot 的时候，你就要顺带去学习MyBatis，这个我在上⾯也提到过。

另外，建议你还要掌握⾄少⼀个 MyBatis 增强框架，这⾥推荐两个国产的：

1. MyBatis-Plus：简称 MP，在 MyBatis 的基础上只做增强不做改变，为简化开发、提⾼效率⽽⽣。

2. MyBatis-Flex：⾮常轻量、同时拥有极⾼的性能与灵活性的 MyBatis 增强框架。

对于做项⽬的同学，也可以直接选择学习使⽤ MyBatis 增强框架。

# 单元测试

对于单测来说，⽬前常⽤的单测框架有：JUnit、Mockito、Spock、PowerMock、JMockit、TestableMock 等等。

JUnit ⼏乎是默认选择，但是其不⽀持 Mock，因此我们还需要选择⼀个 Mock ⼯具。Mockito 和Spock 是最主流的两款 Mock ⼯具，⼀般都是在这两者中选择。

究竟是选择 Mockito 还是 Spock 呢？我这⾥做了⼀些简单的对⽐分析：

1. Spock 没办法 Mock 静态⽅法和私有⽅法 ，Mockito 3.4.0 以后，⽀持静态⽅法的 Mock，具体可以看这个 issue：https://github.com/mockito/mockito/issues/1013，具体教程可以看这篇⽂章：https://www.baeldung.com/mockito-mock-static-methods。

2. Spock 基于 Groovy，写出来的测试代码更清晰易读，⽐较规范(⾃带 given-when-then 的常⽤测试结构规范)。Mockito 没有具体的结构规范，需要项⽬组⾃⼰约定⼀个或者遵守⽐较好的测试代码实践。通常来说，同样的测试⽤例，Spock 的代码要更简洁。

3. Mockito 使⽤的⼈群更⼴泛，稳定可靠。并且，Mockito 是 SpringBoot Test 默认集成的 Mock⼯具。

Mockito 和 Spock 都是⾮常不错的 Mock ⼯具，相对来说，Mockito 的适⽤性更强⼀些。

这⾥顺带推荐⼀些测试相关的学习资料：

1. 阿⾥内部单元测试培训教程

2. 单元测试到底是什么？应该怎么做？

3. Integration Testing in Spring 

4. Testing the Web Layer 

5. 可能是全⽹最好的 Spock 单测⼊⻔⽂章:

6. 单元测试框架 Mockito 落地实践分享

7. 如何写出有效的单元测试

# Netty（可选）

Netty 是 Java ⽹络编程最热⻔的框架，⼤家可以根据个⼈需要决定是否进⾏学习，实际企业开发中⽤的不多。

不过，个⼈建议学有余⼒的同学还是抽时间认真学习⼀下，对个⼈开发能⼒的提升还是很有帮助的。

1. Netty 基于 NIO （NIO 是⼀种同步⾮阻塞的 I/O 模型，在 Java 1.4 中引⼊了 NIO ）。使⽤Netty 可以极⼤地简化并简化了 TCP 和 UDP 套接字服务器等⽹络编程,并且性能以及安全性等很多⽅⾯都⾮常优秀。

2. 我们平常经常接触的 Dubbo、RocketMQ、Elasticsearch、gRPC、Spark、Elasticsearch 等等热⻔开源项⽬都⽤到了 Netty。

3. ⼤部分微服务框架底层涉及到⽹络通信的部分都是基于 Netty 来做的，⽐如说 Spring Cloud ⽣态系统中的⽹关 Spring Cloud Gateway 。

下⾯是⼀些⽐较推荐的书籍/专栏。

# 《Netty 实战》

![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/1465b3f07f8bb0932fd608bb81463df25b35b293d55e2bc0235e06ce47f27da5.jpg)


作者：诺曼·毛瑞尔(NormanMaurer）/马文·艾伦·沃尔夫

出版社：人民邮电出版社

出品方：异步图书

副标题：Netty INACTION

原作名：Netty IN ACTION

译者：何品

出版年：2017-5-1

页数：276

定价：69.00

装帧：平装

ISBN: 9787115453686 

7.5 


这本书可以⽤来⼊⻔ Netty ，内容从 BIO 聊到了 NIO、之后才详细介绍为什么有 Netty 、Netty 为什么好⽤以及 Netty 重要的知识点讲解。

这本书基本把 Netty ⼀些重要的知识点都介绍到了，⽽且基本都是通过实战的形式讲解。

《Netty 进阶之路：跟着案例学 Netty》

![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/080592009201f407ed824eee7d74aac9be18e291b570255bb3972a3fdf88ee23.jpg)


作者：李林锋

出版社：电子工业出版社

出版年：2018-11-1

页数：340

定价：79.00元

装帧：平装

ISBN: 9787121352621 

7.5 



内容都是关于使⽤ Netty 的实践案例⽐如内存泄露这些东⻄。如果你觉得你的 Netty 已经完全⼊⻔了，并且你想要对 Netty 掌握的更深的话，推荐你看⼀下这本书。

《跟闪电侠学 Netty：Netty 即时聊天实战与底层原理》

# 跟闪电侠学Netty：Netty即时聊天实战与底层原理

![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/079b1580761244e15c1cd06ffb386dd31f12b62464d82e1eebe783eb5dff405a.jpg)


作者：俞超

出版社：电子工业出版社

出版年:2022-3

页数：344

定价：108

ISBN: 9787121426797 



这本书分为上下两篇，上篇通过⼀个即时聊天系统的实战案例带你⼊⻔ Netty，下篇通过 Netty 源码分析带你搞清 Netty ⽐较重要的底层原理。

视频的话，⿊⻢的 ⿊⻢程序员 Netty 全套教程 就挺不错的，从 Netty 的基础知识 NIO 讲起，⽐较容易接受。

![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/b98d5c852528123e996b60e557f487dfa7fa7c0125586e0ac2204526c350bc22.jpg)




![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/a8cb01cadd0be08c6d4b2328754961552003d725ec83c389447b0d921902f2cc.jpg)


# Java基础+进阶

![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/e69ff645ab2d9276cbfc9be4dc3f7965d59a2ca53b048b57dd5e20986458c6cd.jpg)


黑马程序员全面深入学习Java并发编程，JUC并发.

# ⼯作流（可选）

国内⽤的⽐较多的开源⼯作流引擎是 Flowable 和 Activiti 这两个，参考资料也蛮多的。Camunda也不错，更轻量，功能也很完善，性能和稳定性也很不错。关于开源流程引擎的选择，可以参考这篇⽂章：https://zhuanlan.zhihu.com/p/369761832 。

ps：Flowable 和 Camunda 都是 Activiti5 的⼀个分⽀发展⽽来， 三者的理念有所差别。

国内⽐较⽕的⼯作流引擎 LiteFlow（ https://liteflow.cc/ ） 只做基于逻辑的流转，⽽不做基于⻆⾊任务的流转。如果你想做基于⻆⾊任务的流转，推荐使⽤ Flowable 和 Activiti 这两个框架。也就是说，像审批流（A 审批完应该是 B 审批，然后再流转到 C ⻆⾊）这种 LiteFlow 就不适合了。LiteFlow 适⽤于拥有复杂逻辑的业务，⽐如说价格引擎，下单流程等，这些业务往往都拥有很多步骤，这些步骤完全可以按照业务粒度拆分成⼀个个独⽴的组件，进⾏装配复⽤变更。

这⾥就不推荐学习资料了，感兴趣的同学可以⾃⼰去找⼀下。

# 搜索引擎

搜索引擎⽤于提⾼搜索效率，功能和浏览器搜索引擎类似。⽐较常⻅的搜索引擎是 Elasticsearch（推荐） 和 Solr。

如果你要学习 Elasticsearch 的话，Elastic 中⽂社区 以及 Elastic 官⽅博客 都是⾮常不错的资源，上⾯会分享很多具体的实践案例。

视频教程可以看看尚硅⾕的 《ElasticSearch ⼊⻔到精通》，前⾯基于 ElasticSearch 7.x 讲解，后⾯加更了 Elasticsearch8.x 新特性。

书籍可以看看《⼀本书讲透Elasticsearch：原理、进阶与⼯程实践》。这本书基于 8.x 版本编写，⽬前全⽹最新的 Elasticsearch 讲解书籍。内容覆盖 Elastic 官⽅认证的核⼼知识点，源⾃真实项⽬案例和企业级问题解答。

# 一本书讲透Elasticsearch：原理、进阶与工程实践杨昌玉著

![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/d81e81101f000cf74c2fd4e88a87a1469ebaa6a462847713bb329da327144ed0.jpg)


作者：杨昌玉著

出版社:机械工业出版社

副标题：杨昌玉

出版年:2023-12

页数:504

ISBN: 9787111740353 



最后，再推荐⼀些 ElasticSearch 相关的优秀⽂章和专辑来帮助你学习和更好的使⽤ElasticSearch：

Elasticsearch 常⻅⾯试题总结 - JavaGuide

Elasticsearch 基础⼊⻔详⽂ - 腾讯技术⼯程

在⼯作中 ElasticSearch 的⼀些使⽤规范

《滴滴技术的 ES 系列》

《死磕 Elasticsearch 系列》 （上百篇 ES 的理论+实战⽂章，全⽹最全⾯的 ES 教程。部分内容对应的视频教程：https://space.bilibili.com/471049389 ）

# 分布式&微服务（进阶）

这部⻔内容涉及到的知识点⽐较多，我这⾥只列举⽐较重要的部分⽐如分布式算法和协议、配置中⼼、分布式事务。

学习分布式知识，个⼈⽐较建议阅读书籍和博客。当然了，如果⽐较喜欢看视频的话，也可以找⼀些不错的教程视频或者公开课来看，⽤适合⾃⼰的学习⽅式去学习即可！

书籍推荐（理论向）

《深⼊理解分布式系统》这本书⾮常不错。这本书的作者⽤了⼤量篇幅来介绍分布式领域中⾮常重要的共识算法，并且还会基于 Go 语⾔带着你从零实现了⼀个共识算法的⿐祖 Paxos 算法。

# 深入理解分布式系统

![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/27afdf88403840b3cd8a3d216e63e2779424da3596e1013fad492a6f3145d22b.jpg)


作者：唐伟志

出版年：2022-3

页数：316

定价：108

ISBN:9787121428111 

8.7 



《从零开始学架构》这本书的内容⽐较全⾯，分布式、微服务、⾼并发、⾼可⽤这些都有涉及到。这本书对应的是极客时间的专栏—《从零开始学架构》，⾥⾯的很多内容都是这个专栏⾥⾯的，两者选⼀个阅读就⾏了。

![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/22ef2163ffb5f7896172ca5f16f4db1023b2fbc7234e4c3f94cce46cb161a960.jpg)


余⽼师的 《软件架构设计：⼤型⽹站技术架构与业务架构融合之道》这本书类似于《从零开始学架构》，内容同样⽐较全⾯，也很不错。

![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/890d742f4b6df6eb95fabf76300d1ce1868b225d9ff673a61e2635985c52ad01.jpg)


# 公开课推荐（理论向）：

MIT6.824: Distributed System 这⻔公开课挺经典的。这⻔课每节课都会精读⼀篇分布式系统领域的经典论⽂，并由此传授分布式系统设计与实现的重要原则和关键技术。


# 视频推荐（实战向）：

视频可以直接学习尚硅⾕的 2024 最新版 Spring Cloud 教程，这⻔课程介绍了 SpringCloud 和SpringCloud Alibaba 中⽬前最主流的组件。学完了这⻔课程之后，就可以直接上⼿为微服务项⽬的开发实战了。


尚硅谷2024最新SpringCloud教程，springcloud从入门到大牛


![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/0dc7b6a73c524f39d0f3cc007a50fcd311debe361dcfa714cc5c12f73cfb70ba.jpg)


![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/f0f0950c394c7a540ce280aa768a0a5dd00dea230b5e7b5f2fc0bafc4d7d60eb.jpg)





# 理论&算法&协议

⽐较重要的分布式理论&算法&协议有：CAP 理论、BASE 理论、Paxos 算法、Gossip 协议、Raft算法等等。

# ⽂章推荐：

. CAP & BASE 理论详解

Paxos 算法详解

Raft 算法详解

Gossip 协议详解

# 远程调⽤

不同服务之间的调⽤⼀般有两种⽅法：

RPC：RPC（Remote Procedure Call） 即远程过程调⽤，通过 RPC 可以帮助我们调⽤远程计算机上某个服务的⽅法，这个过程就像调⽤本地⽅法⼀样简单。Dubbo 是⼀款国产的 RPC框架，由阿⾥开源，国内⽤的最多。

HTTP 客户端 ：通过 HTTP 协议调⽤其他服务的 RESTful API。Feign 和 OpenFeign（SpringCloud 官⽅基于 Feign 开发的，⽤于替代已经进⼊停更维护状态的 Feign） 是⽬前最常⽤的HTTP 客户端。

OpenFeign 和 Dubbo 都是⽬前⼴泛应⽤于微服务架构的远程调⽤框架，但两者实现⽅式不同（OpenFeign 基于 HTTP 协议，Dubbo ⽀持多种协议，还可以⾃定义协议），适合的场景也略有区别。Spring Cloud 微服务项⽬现在⽤的⽐较多的是基于 Rest ⻛格的调⽤⽅式的 OpenFeign，个⼈⽐较建议学习这个。

不过，如果你跟着教程做的项⽬⽤的是 Dubbo 或者⼯作需要⽤到 Dubbo 的话，那你可以主要学习Dubbo。推荐⼀下我写的总结：

RPC 基础知识总结

Dubbo 常⻅问题总结

另外，Dubbo 官⽅⽂档是⼀定要看的，地址：https://cn.dubbo.apache.org/zh-cn/overview/home/。

# 服务注册与发现

Eureka、Zookeeper、Consul、Nacos 都可以提供服务注册与发现的功能。

个⼈⽐较建议学习 Nacos，国内⽤的⽐较多，功能也更强⼤！除了提供服务注册与发现⼯功能之外，还可以作为配置中⼼使⽤。

学习 Nacos 的话，官⽅⽂档是⼀定要看的：https://nacos.io/zh-cn/docs/v2/quickstart/quick-start.html 。

另外，再推荐⼀些我觉得还不错的学习资料：

Nacos 架构&原理 - 阿⾥藏经阁（推荐，像 Nacos 内核设计、底层原理、最佳实践）

55 张图吃透 Nacos - 不才陈某

图⽂解析 Nacos 配置中⼼的实现 - 掘⾦（没有过多代码粘贴，原理讲的很清楚）

Nacos 帮我们解决什么问题？ $--$ 配置管理篇 - 阿⾥巴巴中间件

# API ⽹关

⽹关可以为我们提供请求转发、安全认证（身份/权限认证）、流量控制、负载均衡、降级熔断、⽇志、监控、参数校验、协议转换等功能。

关于 API ⽹关的基础知识和技术选型推荐阅读我写的 API ⽹关基础知识总结这篇⽂章。

Spring Cloud 微服务项⽬⽐较推荐实⽤ SpringCloud Gateway 作为 API ⽹关，这是 Spring Cloud的⼀个全新项⽬，为了取代 Netflix Zuul。为了提升⽹关的性能，SpringCloud Gateway 是基于WebFlux 实现。Spring Cloud Gateway 的⽬标是不仅提供统⼀的路由⽅式，并且基于 Filter 链的⽅式提供了⽹关基本的功能，例如：安全，监控/指标，和限流。

下⾯这些是我觉得还不错的学习资料：

Spring Cloud Gateway 常⻅问题总结 - JavaGuide

6000 字 | 16 图 | 深⼊理解 Spring Cloud Gateway 的原理 - 悟空聊架构

Spring Cloud Gateway 夺命连环 10 问？ - 不才陈某

Spring Cloud Gateway 整合阿⾥ Sentinel ⽹关限流实战！ - 不才陈某

实战 Spring Cloud Gateway 之限流篇 - aneasystone（对于常⻅的限流算法和组件都有介绍到）

# 配置中⼼

微服务下，业务的发展⼀般会导致服务数量的增加，进⽽导致程序配置（服务地址、数据库参数等等）增多。

传统的配置⽂件的⽅式已经⽆法满⾜当前需求，主要有两点原因：⼀是安全性得不到保障（配置放在代码库中容易泄露）；⼆是时效性不⾏ （修改配置需要重启服务才能⽣效）。

Spring Cloud Config、Nacos 、Apollo、K8s ConfigMap 都可以⽤来做配置中⼼。

Apollo 和 Nacos 我个⼈更喜欢。Nacos 使⽤起来更加顺⼿，还能顺便作为注册中⼼使⽤，Apollo在配置管理⽅⾯做的更加全⾯。

个⼈还是更建议学习 Nacos ,学习资料在上⾯的服务注册与发现已经推荐过了。

# 分布式 ID

ID 是数据的唯⼀标识，分布式 ID 是分布式系统下的 ID。

分布式 ID 的解决⽅案有很多，⽐如 ：

算法 ：UUID、Snowflake(雪花算法)

开源框架 ： UidGenerator(百度)、Leaf(美团)、Tinyid(滴滴)、IdGenerator(个⼈)

这块内容⽐较简单，推荐阅读下⾯这两篇⽂章进⾏学习：

C 分布式 ID 介绍&实现⽅案总结

分布式 ID 设计指南

# 分布式事务

微服务架构下，⼀个系统被拆分为多个⼩的微服务。

每个微服务都可能存在不同的机器上，并且每个微服务可能都有⼀个单独的数据库供⾃⼰使⽤。这种情况下，⼀组操作可能会涉及到多个微服务以及多个数据库。

举个例⼦：电商系统中，你创建⼀个订单往往会涉及到订单服务（订单数加⼀）、库存服务（库存减⼀）等等服务，这些服务会有供⾃⼰单独使⽤的数据库。

![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/823e44ef85a89e3a59c81fda595f62808802e915d313428f5c6b0f304e4d3a05.jpg)


那么如何保证这⼀组操作要么都执⾏成功，要么都执⾏失败呢？

这个时候单单依靠数据库事务就不⾏了！我们就需要引⼊ 分布式事务 这个概念了！

常⽤分布式事务解决⽅案有 Seata 和 Hmily。

1. Seata :Seata 是⼀款开源的分布式事务解决⽅案，致⼒于在微服务架构下提供⾼性能和简单易⽤的分布式事务服务。

2. Hmily : ⾦融级分布式事务解决⽅案。

⽬前国内⽤的⽐较多的是 Seata，建议学习这个。

# 分布式链路追踪

不同于单体架构，在分布式架构下，请求需要在多个服务之间调⽤，排查问题会⾮常麻烦。我们需要分布式链路追踪系统来解决这个痛点。

⽬前分布式链路追踪系统基本都是根据⾕歌的《Dapper ⼤规模分布式系统的跟踪系统》这篇论⽂发展⽽来，主流的有 Pinpoint，Skywalking ，CAT（当然也有其他的例如 Zipkin，Jaeger 等产品，不过总体来说不如前⾯选取的 3 个完成度⾼）等。

Zipkin 是 Twitter 公司开源的⼀个分布式链路追踪⼯具，Spring Cloud Sleuth 实际是基于 Zipkin的。

SkyWalking 是国⼈吴晟（华为）开源的⼀款分布式追踪，分析，告警的⼯具，现在是 Apache 旗下开源项⽬。

⽬前国内⽤的⽐较多的是 SkyWalking，建议学习这个。

# ⾼性能（进阶）

# CDN（掌握概念和原理即可）

CDN 就是将静态资源分发到多个不同的地⽅以实现就近访问，进⽽加快静态资源的访问速度，减轻服务器以及带宽的负担。

我们只需要掌握 CDN 的基本概念和原理以及会⽤云⼚商提供的现成 CDN 服务即可，花费不了太多时间。推荐阅读我写的CDN 常⻅问题总结这篇⽂章。

# 消息队列

消息队列在分布式系统中主要是为了异步、解耦和削峰。

常⽤的消息队列如下：

1. RocketMQ ：阿⾥巴巴开源的⼀款⾼性能、⾼吞吐量的分布式消息中间件。

2. Kafka: Kafka 是⼀种分布式的，基于发布 / 订阅的消息系统。

3. RabbitMQ :基于 erlang 开发的基于 AMQP（Advanced Message Queue ⾼级消息队列协议）协议实现的消息队列。

4. Pulsar：下⼀代云原⽣分布式消息流平台。

建议选择 RocketMQ 和 Kafka 其中的⼀个进⾏深⼊学习，其他消息队列了解即可。

关于消息队列基础概念、技术选型⽅⾯的介绍，建议阅读我写的消息队列基础知识总结这篇⽂章。

Kafka、RocketMQ、RabbitMQ 学习资源推荐请看知识星球的这篇帖⼦：https://t.zsxq.com/0bEDFwgon 。

# 读写分离&分库分表（掌握概念和原理即可）

读写分离主要是为了将数据库的读和写操作分不到不同的数据库节点上。主服务器负责写，从服务器负责读。另外，⼀主⼀从或者⼀主多从都可以。

读写分离可以⼤幅提⾼读性能，⼩幅提⾼写的性能。因此，读写分离更适合单机并发读请求⽐较多的场景。

![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/91666b5468ebb892ccfa2486bf8fa30b63cb920e1b5a94b58b6a3eb7e414463a.jpg)


分库分表是为了解决由于库、表数据量过⼤，⽽导致数据库性能持续下降的问题。

常⻅的分库分表⼯具有：sharding-jdbc（当当）、TSharding（蘑菇街）、MyCAT（基于Cobar）、Cobar（阿⾥巴巴）...。 推荐使⽤ sharding-jdbc。 因为，sharding-jdbc 是⼀款轻量级Java 框架，以 jar 包形式提供服务，不要我们做额外的运维⼯作，并且兼容性也很好。

![image](https://cdn-mineru.openxlab.org.cn/result/2026-04-02/e308f97a-b2a5-40e7-b015-a9ea8b6d1075/1f64801b74a0660464ac93e73b29df9375c1aa695af5512580575f72031328fa.jpg)


现在很多公司都是⽤的类似于 TiDB 这种分布式关系型数据库，不需要我们⼿动进⾏分库分表，因此我们只需要掌握读写分离&分库分表的常⻅概念和原理即可，不需要花费太多时间去实践，推荐阅读我写的 读写分离&分库分表常⻅问题总结这篇⽂章。

# 负载均衡

负载均衡系统通常⽤于将任务⽐如⽤户请求处理分配到多个服务器处理以提⾼⽹站、应⽤或者数据库的性能和可靠性。

开发过程中，我们接触到的负载均衡可以简单分为 服务端负载均衡 和 客户端负载均衡 这两种。服务端负载均衡可以通过硬件（⽐如 F5、A10、Array ）或者软件（⽐如 LVS、Nginx、HAproxy ）实现。Java 领域主流的微服务框架 Dubbo、Spring Cloud 等都内置了开箱即⽤的客户端负载均衡实现。Dubbo 属于是默认⾃带了负载均衡功能，Spring Cloud 是通过组件的形式实现的负载均衡，属于可选项，⽐较常⽤的是 Spring Cloud Load Balancer（官⽅，推荐） 和 Ribbon（Netflix，已被启⽤）。

个⼈建议学习⼀下 Nginx 和 Spring Cloud Load Balancer。

负载均衡的常⻅概念、算法和技术⽅案可以看看这篇⽂章：负载均衡常⻅问题总结。

# ⾼可⽤（进阶）

⾼可⽤描述的是⼀个系统在⼤部分时间都是可⽤的，可以为我们提供服务的。⾼可⽤代表系统即使在发⽣硬件故障或者系统升级的时候，服务仍然是可⽤的 。

# 限流&降级&熔断

限流是从⽤户访问压⼒的⻆度来考虑如何应对系统故障。限流为了对服务端的接⼝接受请求的频率进⾏限制，防⽌服务挂掉。⽐如某⼀接⼝的请求限制为 100 个每秒, 对超过限制的请求放弃处理或者放到队列中等待处理。限流可以有效应对突发请求过多。

关于服务限流的介绍推荐阅读我写的服务限流详解这篇⽂章，⾥⾯有介绍常⻅的限流算法以及单机限流和分布式限流的技术⽅案。

降级是从系统功能优先级的⻆度考虑如何应对系统故障。服务降级指的是当服务器压⼒剧增的情况下，根据当前业务情况及流量对⼀些服务和⻚⾯有策略的降级，以此释放服务器资源以保证核⼼任务的正常运⾏。

熔断和降级是两个⽐较容易混淆的概念，两者的含义并不相同。降级的⽬的在于应对系统⾃身的故障，⽽熔断的⽬的在于应对当前系统依赖的外部系统或者第三⽅系统的故障。

Netflix 开源的Hystrix 和阿⾥开源的 Sentinel 都能实现限流、降级、熔断。不过，Hystrix 已经停⽌维护了，更建议使⽤功能更为强⼤的 Sentinel。另外，Sentinel 的 Wiki 中对⽐了常⽤限流降级组件，感兴趣的可以看看，传送⻔：常⽤限流降级组件对⽐。

Sentinel 的 wiki 中已经详细描述了其与 Hystrix 的区别，你可以看看。

学习 Sentinel 的话，官⽅⽂档是⼀定要看的：https://sentinelguard.io/zh-cn/docs/introduction.html。

另外，再推荐⼀些我觉得还不错的学习资料：

阿⾥限流神器 Sentinel 夺命连环 17 问？ - 不才陈某

Sentinel 为什么这么强，我扒了扒背后的实现原理 - 三友的 java ⽇志

Sentinel 流控滑动窗⼝算法设计 - ⽼周聊架构

# 排队

另类的⼀种限流，类⽐于现实世界的排队。玩过英雄联盟的⼩伙伴应该有体会，每次⼀有活动，就要经历⼀波排队才能进⼊游戏。

实现排队的⽅法有很多种，⽐如我们可以借助消息队列、JDK 中的各种阻塞队列。

# 集群

相同的服务部署多份，避免单点故障。

# 超时和重试机制

⼀旦⽤户的请求超过某个时间得不到响应就结束此次请求并抛出异常。 如果不进⾏超时设置可能会导致请求响应速度慢，甚⾄导致请求堆积进⽽让系统⽆法在处理请求。

另外，重试的次数⼀般设为 3 次，再多次的重试没有好处，反⽽会加重服务器压⼒（部分场景使⽤失败重试机制会不太适合）。

# 云原⽣ （可选）

提示：云原⽣开发对能⼒要求很⾼，Java 后端岗位通常也不会要求云原⽣开发技能。因此，这部分内容不推荐对云原⽣开发不感兴趣或者不了解的同学学习，可以选择跳过。

云原⽣就是在云中构建、运⾏应⽤程序的⼀套完整的技术体系和⽅法论。这⾥的技术体系和⽅法论就⽬前来说指的是 微服务 $^ +$ DevOps+持续交付 $^ +$ 容器化。

越来越多的编程语⾔、框架开始拥抱云原⽣，例如 Spring 推出了⾯向云原⽣的技术 SpringNative、RedHat 开源了 Java 云原⽣服务框架 Quarkus。

如果你对云原⽣领域⽐较感兴趣的话，建议你重点关注下⾯这些技术：

1. 微服务：SpringCloud 或者 SpringCloud Alibaba 其实是不⽤学习的，云原⽣下⼀般基于后⾯提到的 Kubernetes 来构建微服务。

2. ⽹关：⽹关是整个微服务架构的流量⼊⼝，负责认证授权、请求分发、认证授权、限流、API管理、负载均衡等⼯作，是微服务架构中⾮常重要的⼀个组件。因此，我这⾥专⻔单独将⽹关拿出来提⼀嘴。

3. ⽇志和监控告警：Metrics（借助它我们可以在 Grafana 中绘制出各种直观的⾯板，可以更加全⾯的了解我们系统的运⾏状态）、Trace（借助它我们可以构建出系统调⽤的全貌）、Logs（⼀些必要的⽇志记录）。

4. 容器：容器技术是云原⽣发展的基⽯，以 Docker 为⾸的容器⼯具提出了“⼀次构建，到处运⾏”的⼝号。

5. Kubernetes：K8s 被称为云原⽣时代的操作系统，云原⽣应⽤的优势与其提供的功能息息相关。

6. DevOps：DevOps 关注的是如何实现应⽤程序的全⽣命周期（开发，测试，运维）⾃动化管理，从⽽实现更快速、更⾼质量、更频繁、更稳定的软件交付。DevOps 团队通常会使⽤微服务架构来构建应⽤程序，借助于持续集成和持续部署（CI/CD）来实施 DevOps。

7. ServiceMesh：你可以将 Service Mesh 看作是为了简化开发⼯作专⻔抽象出来的⼀层，通常作为透明的⼀层接⼊到现有的分布式应⽤程序⾥。

8. 

其中，⽐较重要的是 Kubernetes。如果你做项⽬的话，建议优先考虑 Kubernetes 相关的项⽬。

我之前写过⼀篇⽂章来介绍云原⽣，可以看看： 云原⽣时代，程序员应该掌握哪些能⼒？

另外，还推荐看看这篇：2024年的云原⽣架构需要哪些技术栈。

# AI（拓展补充）

我把 Java 程序员需要重点掌握的 AI 相关技能，分为两⼤⽅向：AI 赋能开发（提升个⼈效率） 和AI 应⽤集成（创造业务价值）。

# AI 赋能开发

AI 编程助⼿：GitHub Copilot、Tabnine 等 AI 编程插件以及Augment Code、Cursor、Windsurf 等 AI 编程 IDE，还有像 Claude Code 这种命令⾏编程助⼿。需要熟练使⽤⾄少⼀款AI 编程助⼿，将其融⼊⽇常编码流程。

通⽤⼤语⾔模型 ：如 ChatGPT (OpenAI)、Google Gemini 、Claude。 需要掌握提问的艺术（Prompt Engineering），能够通过清晰、具体的指令，让 AI ⾼效地为你解决编程和学习中遇到的问题。

# AI 应⽤集成

AI 应⽤开发框架：Spring AI、LangChain4j。强烈推荐 Spring AI。作为 Spring 官⽅推出的 AI框架，它不仅能与庞⼤的 Spring ⽣态（如 Spring Boot, Spring Data）⽆缝对接，⽽且未来的维护更新和社区⽀持也更有保障，是 Java 开发者进⼊ AI 应⽤领域的⾸选。

向量数据库：对于⼤多数 Java 项⽬，我个⼈推荐直接使⽤ PostgreSQL 的 pgvector 插件来实现向量数据库功能。这种⽅式的优势在于能够复⽤现有的 PostgreSQL 基础设施，极⼤地降低了技术栈的复杂度和运维成本，⾮常适合项⽬起步和中⼩型应⽤。当然，对于⼤规模、⾼性能的向量搜索场景，了解并使⽤专业的向量数据库（如 Milvus, Pinecone）也是必要的。

在 Java 中运⾏本地模型 ： 出于数据隐私、成本控制或需要离线运⾏等需求，将 AI 模型直接部署在本地服务器并通过 Java 应⽤调⽤。 Ollama 极⼤地简化了在本地环境运⾏开源 LLM（如 Qwen3、Llama 3, Mistral）的流程。Spring AI 和 LangChain4j 都提供了与 Ollama 的原⽣对接⽀持，使得在 Java 中调⽤本地模型变得异常简单。

更详细的 Java 向 AI ⼤模型应⽤开发学习路线可以参考这篇帖⼦：https://t.zsxq.com/reesk（JavaGuide 知识星球专属），介绍了⼤模型应⽤开发基础、Java ⽣态的 AI 开发框架、AI 实战项⽬推荐以及学习建议，包含具体的学习资料。


