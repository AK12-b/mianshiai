①请说明强缓存和协商缓存的区别与原理

在 Web
前端性能优化中，浏览器缓存机制是减少网络请求、提升加载速度的核心手段。缓存主要分为**强缓存**和**协商缓存**，两者的主要区别在于**是否需要向服务器发送请求来确认缓存有效性**。

### **一、 强缓存 (Strong Caching)**

强缓存是指浏览器在访问资源时，直接根据本地缓存的过期时间来判断资源是否有效。如果缓存命中，浏览器直接从本地读取资源，**不向服务器发送任何请求**。

#### **1. 工作原理**

1.  浏览器发起请求，先在本地缓存中查找该资源。

2.  检查缓存标识中的过期时间。

3.  如果资源未过期（命中缓存），浏览器直接从内存或磁盘读取资源，状态码显示为 200
    (from memory cache) 或 200 (from disk cache)。

#### **2. 核心 HTTP 响应头**

- **Expires
  (HTTP/1.0)：** 指定资源过期的绝对时间（服务器时间）。缺点是如果客户端与服务器时间不一致，会导致缓存偏差。

- **Cache-Control (HTTP/1.1)：** 优先级高于
  Expires。常用取值为 max-age=3600（代表资源在 3600
  秒内有效），属于相对时间，解决了时间不一致的问题。

### **二、 协商缓存 (Negotiated Caching)**

协商缓存是指强缓存失效后，浏览器携带缓存标识向服务器发起请求，由服务器根据标识判断本地缓存是否依然可用。

#### **1. 工作原理**

1.  强缓存失效或配置为不直接使用强缓存（如 Cache-Control: no-cache）。

2.  浏览器携带缓存标识（如 ETag 或 Last-Modified）发送请求给服务器。

3.  服务器比对标识：

    - **资源未变化：** 返回 304 Not
      Modified，不返回资源内容，浏览器继续使用本地缓存。

    <!-- -->

    - **资源已更新：** 返回 200 OK，并下发最新的资源和缓存标识。

#### **2. 核心 HTTP 请求/响应头对**

- **Last-Modified / If-Modified-Since (基于时间)：**

  - 服务器响应时通过 Last-Modified 告知资源最后修改时间。

  <!-- -->

  - 再次请求时，客户端通过 If-Modified-Since 携带该时间。

  - **缺点：** 只能精确到秒；如果文件内容没变但修改时间变了，会导致缓存失效。

- **ETag / If-None-Match (基于内容指纹)：**

  - 服务器根据文件内容生成唯一哈希值，通过 ETag 返回。

  <!-- -->

  - 再次请求时，客户端通过 If-None-Match 携带该值。

  - **优点：** 优先级高于 Last-Modified，更精确（内容不变指纹就不变）。

### **三、 强缓存与协商缓存的区别对比**

  ---------------------------------------------------------------------------------
  **特性**           **强缓存**                **协商缓存**
  ------------------ ------------------------- ------------------------------------
  **是否发送请求**   不发送，直接从本地读取    发送请求，由服务器告知是否使用缓存

  **状态码**         200 (from cache)          304 (Not Modified) 或 200

  **性能**           最优，无网络延迟          次之，有一次网络往返开销

  **触发时机**       缓存时间未到期            强缓存失效或手动刷新

  **主要字段**       Cache-Control, Expires    ETag, Last-Modified
  ---------------------------------------------------------------------------------

### **四、 整体流程总结**

浏览器加载资源的完整链路如下：

1.  **先检查强缓存**：如果 Cache-Control 命中且未过期，直接使用，结束。

2.  **触发协商缓存**：如果强缓存失效，发起 HTTP
    请求，携带 If-None-Match (ETag)
    和 If-Modified-Since (Last-Modified)。

3.  **服务器判断**：

    - 若 ETag/时间 匹配，返回 **304**，浏览器读取本地缓存。

    - 若不匹配，返回 **200** 和新资源，浏览器更新本地缓存。

②请阐述对跨域的理解，包括跨域的定义、产生原因及解决方法

在Web前端开发中，跨域是一个绕不开的核心安全机制与技术挑战。以下是从定义、原理到解决方案的深度解析：

一、 什么是跨域？

跨域是指当一个资源（通常是浏览器中的脚本）尝试请求另一个与当前页面协议、域名或端口三者中任意一个不同的资源时，就会触发跨域限制。

只要以下三个要素中有一个不同，即被视为跨域：

协议不同：如 http:// 与 https://

域名不同：如 example.com 与 api.example.com（主域相同但子域不同也算）

端口不同：如 localhost:8080 与 localhost:3000

二、 为什么会产生跨域？

跨域产生的根源是浏览器的同源策略（Same-Origin Policy）。这是由 Netscape
公司在1995年引入的浏览器最核心、最基本的安全功能。

其核心目的在于防御攻击，保护用户信息安全：

防止CSRF（跨站请求伪造）：如果没有同源策略，黑客可以在自己的网页上通过脚本读取你在银行网站的
Cookie，并冒充你发送转账请求。

保护数据隐私：防止恶意网站通过 iframe 或 AJAX 读取其他网站的 DOM
结构或私密数据。

注意：跨域限制是浏览器的行为。实际上，请求可能已经发到了服务器，服务器也正常返回了响应，但浏览器在接收到响应后，发现跨域且未获得授权，于是拦截了数据，并向开发者抛出错误。

三、 如何解决跨域问题？

在实际开发中，我们通常采用以下几种主流方案：

1\. CORS (跨源资源共享) - 最标准、最常用

这是目前最推荐的方案，由 W3C 制定。它需要后端在响应头中添加特定的字段：

Access-Control-Allow-Origin: 指定允许访问的域名（如 \* 或特定的域名）。

Access-Control-Allow-Methods: 允许的请求方法（GET, POST, PUT等）。

Access-Control-Allow-Headers: 允许携带的自定义请求头。

机制：对于"非简单请求"（如 PUT, DELETE 或 Content-Type 为
application/json），浏览器会先发送一个 OPTIONS
预检请求，确认服务器允许跨域后再发送真实请求。

2\. Nginx 反向代理 - 最适合生产环境

通过配置 Nginx 服务器，将前端请求转发到真实的后端服务器。

原理：同源策略仅存在于浏览器端，服务器与服务器之间的通信不受同源策略限制。

操作：前端请求自己的 Nginx 路径（如 /api），Nginx 在后台通过 proxy_pass
转发给目标服务器，再将结果返回给前端。

3\. Node.js 中间件代理 (Webpack/Vite) - 仅限开发阶段

在开发环境中，我们可以利用 Webpack-dev-server 或 Vite 的 proxy 配置。

原理：本地启动一个 Node.js 服务作为中转站，接收前端请求并转发给后端
API，解决开发过程中的跨域阻塞。

4\. JSONP (JSON with Padding) - 历史遗留方案

原理：利用 \<script\>
标签不受同源策略限制的特性。前端定义一个回调函数，后端返回一段调用该函数的
JS 代码，并将数据作为参数传入。

局限性：仅支持 GET 请求，且存在安全风险（容易受 XSS
攻击），现代开发中已基本被 CORS 取代。

5\. postMessage - 用于跨窗口通信

当需要实现两个不同源的 iframe
之间，或者页面与弹出窗口之间的通信时，可以使用 HTML5 的
window.postMessage API。

6\. WebSocket

WebSocket 协议本身在握手阶段虽然使用
HTTP，但它不受同源策略的限制，建立连接后可实现全双工跨域通信。

总结

开发环境：常用 Webpack/Vite 代理。

生产环境：首选 CORS（规范且安全）或 Nginx 反向代理（灵活且透明）。

特殊场景：跨页面通讯用 postMessage，实时交互用 WebSocket。

③在浏览器中输入URL地址到显示主页的过程是什么？若输入的是HTTPS的URL，该过程有何差异？

这是一个经典的面试题目，考察的是面试者对网络协议、浏览器渲染机制以及安全传输的综合理解。整个过程可以分为以下几个核心阶段：

1\. URL 解析与 HSTS 检查

URL 解析：浏览器首先会解析输入的 URL，提取出协议、域名、端口和路径。

HSTS 检查：如果是 HTTPS URL，浏览器会检查 HSTS（HTTP Strict Transport
Security）列表。如果该域名在列表中，浏览器会强制使用 HTTPS
与服务器建立连接。

2\. DNS 解析（寻找 IP 地址）

浏览器需要将域名转换为服务器的 IP 地址：

浏览器缓存：检查浏览器自身是否有该域名的 DNS 缓存。

操作系统缓存：检查本地 hosts 文件和系统 DNS 缓存。

路由器/中继缓存：检查路由器的 DNS 缓存。

递归查询：如果以上都没有，则请求本地 DNS 服务器（ISP 提供）。本地 DNS
会依次向根域名服务器、顶级域名服务器（.com）、权威域名服务器发起请求，直到获取最终
IP。

3\. TCP 连接（三次握手）

获取 IP 后，客户端与服务器通过 TCP 协议建立可靠连接：

第一次握手：客户端发送 SYN 包，进入 SYN_SENT 状态。

第二次握手：服务器收到 SYN 包，回应 SYN+ACK 包，进入 SYN_RCVD 状态。

第三次握手：客户端收到包后，发送 ACK 包。连接建立，双方进入 ESTABLISHED
状态。

4\. TLS/SSL 握手（仅针对 HTTPS）

如果输入的是 HTTPS 的 URL，在 TCP 连接建立后，需要进行 TLS
握手以建立加密通道：

Client Hello：客户端发送支持的 TLS 版本、加密组件列表（Cipher
Suites）和一个随机数（Client Random）。

Server Hello：服务器确认 TLS
版本、选择加密算法，并发送一个随机数（Server Random）和 数字证书。

证书校验：客户端验证证书的有效性（颁发机构、有效期、域名是否匹配等）。

密钥交换：

客户端生成一个随机数（Pre-master Secret）。

使用证书中的公钥对该随机数加密并发送给服务器。

服务器用自己的私钥解密，得到 Pre-master Secret。

生成对话密钥：双方根据 Client Random、Server Random 和 Pre-master Secret
计算出相同的"对称密钥"。

加密测试：双方互发加密消息，确认握手成功。后续所有 HTTP
数据都通过该对称密钥进行加密传输。

5\. 发送 HTTP 请求

连接建立后，浏览器发送 HTTP 请求报文。包含：

请求行：方法（GET/POST）、URL、协议版本。

请求头：Cookie、User-Agent、Accept 等。

请求体：POST 请求时的提交数据。

6\. 服务器处理与响应

服务器处理请求（可能涉及后端业务逻辑、数据库查询等），并返回 HTTP
响应报文：

状态码：如 200 OK、301 永久重定向、404 Not Found 等。

响应头：Content-Type、Cache-Control、Set-Cookie 等。

响应体：HTML 源码、JSON 数据或二进制文件等。

7\. 浏览器渲染页面

这是前端开发最核心的部分，浏览器收到 HTML 后执行以下步骤：

构建 DOM 树：解析 HTML 标签，构建文档对象模型。

构建 CSSOM 树：解析 CSS 文件或样式代码。

生成 Render Tree（渲染树）：将 DOM 和 CSSOM 合并，剔除 display: none
等不可见元素。

Layout（布局/回流）：计算每个节点在屏幕上的确切位置和大小。

Painting（绘制）：将像素绘制到屏幕上。

Composite（层叠配合）：如果页面有复杂的层级（如 transform
动画），浏览器会将不同层合成为最终图像。

注意：在这个过程中，如果遇到 JS 脚本，除非有 defer 或 async
属性，否则会阻塞 DOM 的构建。

8\. 连接结束（四次挥手）

数据传输完成后，通过四次挥手断开 TCP 连接（但在 HTTP/1.1 中，由于
Keep-Alive 头部，连接通常会保持一段时间以备复用）。

④简述 sessionStorage 和 localStorage 的区别?

sessionStorage 和 localStorage 都是 HTML5 提供的本地存储方案，统称为 Web
Storage。它们的主要区别在于数据的生命周期和共享范围。

生命周期（主要区别）：

localStorage：永久存储。除非用户手动清理浏览器缓存或通过代码主动删除（removeItem/clear），否则数据一直存在。

sessionStorage：会话存储。数据仅在当前会话（窗口或标签页）有效，当该窗口或标签页被关闭时，数据即被销毁。

作用域（数据共享）：

localStorage：遵循同源策略（协议、域名、端口一致）。在同一个浏览器中，只要源相同，不同窗口和标签页之间可以共享数据。

sessionStorage：不仅遵循同源策略，还具有"窗口隔离"特性。不同标签页即使指向同一个
URL，它们的 sessionStorage 也是相互独立的（除非是通过 window.open
在当前页面打开的新页面，可能会复制初始状态，但之后互不影响）。

存储容量：

两者通常都在 5MB - 10MB 之间（具体取决于浏览器），远大于 Cookie 的 4KB。

接口与特性：

接口相同：都提供 setItem, getItem, removeItem, clear 等 API。

数据类型：两者都只能存储字符串。存储对象时需通过 JSON.stringify 转换。

网络交互：数据保存在本地，不随 HTTP 请求发送到服务器（与 Cookie
的本质区别）。

⑤请比较Vue和React的状态管理方案及其优缺点

在Web前端开发中，Vue和React的状态管理方案反映了两种框架底层哲学（响应式追踪
vs 不可变性）的差异。以下是对两者的详细对比及优缺点分析：

一、 核心设计思想对比

Vue：基于响应式原理的"自动挡"
Vue的状态管理（如Pinia、Vuex）深度集成其自身的响应式系统（Proxy或Object.defineProperty）。

更新机制：
状态是可变的（Mutable）。当你修改状态时，Vue能够精确追踪哪些组件依赖了该数据，并触发最小颗粒度的更新。

开发体验： 写法直观，类似于直接操作普通JavaScript对象。

React：基于不可变性的"手动挡"
React本身不包含复杂的状态管理工具，仅提供Context API。其生态（Redux,
Zustand等）强调不可变性（Immutability）。

更新机制：
状态是不可变的。每次更新必须返回一个全新的对象，通过diffing算法对比新旧状态来决定重绘范围。

开发体验： 函数式编程风格强，更新逻辑通常更加纯粹（Pure
Function），但手动优化性能（如避免不必要的重渲染）的成本较高。

二、 主要方案及工具

1\. Vue 的方案：Pinia (主流) / Vuex (旧版)

Pinia： 抛弃了复杂的Mutation，支持组合式API（Setup
Store），类型推断极其友好。

优点：

极简： 几乎没有样板代码，上手快。

自动拆分： 天然支持Code Splitting，每个Store都是独立的。

性能： 借助响应式系统，仅更新受影响的组件。

缺点：

过于灵活：
由于可以直接修改状态，如果不遵循规范，在大型项目中可能导致状态流向难以追踪。

2\. React 的方案：Redux / Zustand / Context API

Redux (Toolkit)： 业界标准，遵循严格的单向数据流。

Zustand：
目前最受欢迎的轻量级方案，结合了类似Vue的简单写法与React的Hooks风格。

Context API： 内置方案，适用于低频更新的全剧配置（如主题、语言）。

优点：

可预测性高：
严格的Reducer模式让每一个动作（Action）都有迹可循，配合Redux
DevTools可实现"时光旅行"。

生态繁荣： 中间件（Middleware）极度丰富，适合处理极其复杂的异步逻辑。

缺点：

样板代码多： 即使是Redux Toolkit，配置逻辑依然比Pinia复杂。

性能挑战： Context
API或未优化的Redux容易导致大规模的组件重渲染（Rerender），需要频繁使用memo或选择器（Selectors）进行优化。

三、 综合优缺点总结

维度 Vue (Pinia/Vuex) React (Redux/Zustand)

上手难度 低。符合直觉，学习曲线平缓。
高。需理解函数式编程、不可变性、副作用管理。

代码量 少。直接修改状态，无冗余动作。
多。需要定义Action、Reducer、Dispatch等。

数据流 单向数据流，但支持双向绑定/直接修改。 严格单向数据流。

性能表现 细粒度更新，开发者负担小。
粗粒度更新，需依赖Memo和选择器手动优化。

调试体验 良好。官方工具集成度高。 优秀。Redux生态的调试工具是行业标杆。

四、 选型建议

选择 Vue 方案的情景：
追求开发效率，项目周期短，或者团队希望通过响应式系统减少性能优化的心智负担。Pinia
目前是绝大多数 Vue 3 项目的首选。

选择 React 方案的情景：

如果是超大型复杂业务，逻辑纠缠不清，Redux
的严格模式能极大增强系统的稳定性。

如果是中小型项目，推荐使用 Zustand，它在保留 React
特性的基础上，提供了接近 Pinia 的简洁体验。

如果是简单的跨组件传参，直接使用原生的 Context API 即可。

⑥简述页面从URL请求到渲染的完整流程及DOM树的构建过程

页面的完整渲染流程可以分为 网络请求 和 浏览器渲染 两个大阶段：

1\. 网络请求阶段

DNS 解析：将域名解析为 IP 地址。

建立连接：TCP 三次握手，若是 HTTPS 则还需进行 TLS 握手。

发送请求：浏览器发送 HTTP 请求，服务器处理并返回 HTML 响应。

2\. 浏览器渲染阶段

构建 DOM 树：解析 HTML 字节流，将其转换为 DOM 节点对象并构成树状结构。

构建 CSSOM 树：解析 CSS，构建样式的层叠模型。

生成渲染树 (Render Tree)：将 DOM 与 CSSOM 合并，剔除不可见节点（如
display: none）。

布局 (Layout)：计算每个可见节点在视口内的几何位置。

分层与绘制
(Paint)：根据层级生成绘制指令，最终通过合成线程（Compositor）交给 GPU
渲染到屏幕。

3\. DOM 树构建细节

DOM 的构建是一个渐进的过程：

解码：将字节流转换为字符。

令牌化
(Tokenization)：通过词法分析将字符序列转换为起始标签、结束标签、文本等
Token。

构建节点：根据 Token 创建对应的 Element 对象。

生成树：利用栈结构维护标签的父子关系，最终生成 DOM 树。

⑦请谈谈你使用 Webpack 进行过的性能优化实践

Webpack 的性能优化可以从构建速度优化和输出产物优化两个维度进行实践：

1\. 构建速度优化 (Build Speed)

多进程并行构建：使用 thread-loader 开启多线程编译，显著提升大型项目在
CPU 密集型任务（如 babel-loader）下的效率。

缓存策略：配置 cache-loader 或开启 Webpack 5 的 filesystem
cache，将编译结果持久化到磁盘，二次构建速度提升 50% 以上。

减少查找范围：通过 resolve.extensions 减少后缀尝试，利用 include/exclude
缩小 loader 的处理目录。

DLL 插件（Webpack 4 及以前）：将三方库预先打包，避免重复编译。但在
Webpack 5 中推荐使用 Module Federation 或更好的缓存机制。

2\. 输出产物优化 (Output Quality)

代码分割 (Code Splitting)：利用 optimization.splitChunks
将公共模块、三方库、异步组件抽离为独立的 bundle，实现按需加载。

Tree Shaking：配合 ES Modules，删除无用代码，减小最终包体积。

压缩与混淆：使用 TerserWebpackPlugin 压缩 JS，CssMinimizerPlugin 压缩
CSS。

图片资源优化：利用 image-minimizer-webpack-plugin
压缩图片，或将小图转化为 base64 (Data URI)。

持久化缓存 (Hashing)：使用 \[contenthash\]
确保文件内容不变时文件名不变，利用浏览器缓存。

⑧简述MVC与MVVM的区别

1\. 模式定义

MVC (Model-View-Controller)：

Model：处理数据逻辑和状态。

View：展示数据。

Controller：核心中转站。接收用户输入，更新 Model，并通知 View 刷新。

MVVM (Model-View-ViewModel)：

Model：原始数据或 API 接口。

View：UI 界面（通常是声明式的模板）。

ViewModel：View 和 Model 的纽带。通过数据绑定（Data Binding）和 DOM
监听，实现 View 和 Model 的自动双向同步。

2\. 核心区别

数据流向：

MVC：数据流通常是单向的。View 往往需要了解 Model 的结构才能渲染。

MVVM：数据流是双向的。View 不直接引用 Model，而是绑定到 ViewModel
的属性上。

同步方式：

MVC：需要手动编写大量代码（如 DOM 操作或回调函数）来同步 View 和 Model
的状态。

MVVM：通过框架底层实现的"数据劫持"或"发布-订阅"模式，实现自动更新（数据驱动），极大减少了
DOM 操作。

耦合度：

MVC：View 与 Model 存在间接耦合。

MVVM：彻底解耦。View 和 Model 互相完全不可见。

⑨请介绍懒加载，并说明图片懒加载与路由懒加载的区别

懒加载（Lazy Loading）
是一种网页性能优化技术，它将资源的加载推迟到真正需要（通常是进入浏览器可视区域）时才进行。这种策略可以显著减少页面初始加载时间，节省用户流量，并减轻服务器压力。

常用场景及举例

图片懒加载：
这是最常见的场景。长页面中位于底部的图片在初始加载时不请求，只有当用户滚动到图片位置时才加载。

例子：电商平台的商品列表页、新闻资讯流。

路由懒加载 (组件懒加载)：
在单页面应用（SPA）中，将不同路由对应的组件分割成不同的代码块，只有在跳转到特定路由时才下载该组件的代码。

例子：Vue 或 React 项目中通过 import() 动态导入路由组件。

视频/动画懒加载： 对于非自动播放的视频或复杂的 Lottie
动画，可以在用户点击播放或滚动到视图内时再初始化。

数据/列表无限滚动：
分页加载数据，当用户滚动到页面底部时再请求下一页数据。

⑩请阐述虚拟列表的核心原理，以及列表项在定高和不定高两种场景下的实现方式

虚拟列表（Virtual List）深度解析

虚拟列表是一种针对长列表性能优化的方案。其核心思想是：只渲染用户当前可见区域（Viewport）内的
DOM
节点，而不是渲染整个列表。通过监听滚动事件，动态计算并替换可见区域内的内容，从而保持
DOM 节点数恒定在极低的水平（如 20-30
个），解决海量数据渲染导致的页面卡顿和内存溢出问题。

一、 核心组成部分

实现一个虚拟列表通常需要三个关键层级：

可视区容器（Container）：设置 overflow-y:
auto，限制高度，作为滚动监听对象。

撑高占位层（Phantom/Placeholder）：高度为 总数据量 \*
预估高度。其作用是撑开滚动条，让浏览器感知到列表的实际总长度。

列表渲染层（Content List）：绝对定位或使用
transform。它始终跟随滚动位置移动，确保渲染的节点正好出现在用户视野内。

二、 定高列表的实现

当每个列表项高度（itemHeight）固定时，计算逻辑最为简单：

计算索引：

startIndex = Math.floor(scrollTop / itemHeight)

endIndex = startIndex + Math.ceil(containerHeight / itemHeight)

设置缓冲区（Buffer）：

为了防止快速滚动时白屏，通常会在首尾多渲染几个节点（如 startIndex - 5,
endIndex + 5）。

计算偏移量（Offset）：

渲染层需要通过 transform: translateY(startIndex \* itemHeight)
偏移，使其回到可视区。如果不偏移，渲染层会随着滚动条滑出视野。

三、 不定高列表的实现

当列表项高度由内容撑开（如社交动态、评论区）时，无法直接通过 scrollTop /
itemHeight 获取索引。通常采用以下策略：

预估高度与位置存储：

初始化一个数组 positions，记录每一项的 height（初始设为预估值）、top 和
bottom。

动态计算索引：

通过二分查找法在 positions 数组中寻找第一个 bottom \> scrollTop
的项，作为 startIndex。

渲染后校准（关键点）：

当 DOM 渲染完成后，在 updated 或 useLayoutEffect 生命周期中，获取真实的
DOM 高度（getBoundingClientRect().height）。

更新 positions 数组中对应索引的高度。

联动更新：该项之后的所有项的 top 和 bottom
都需要根据高度差（diff）进行累加偏移。

滚动平滑处理：

由于高度是动态更新的，滚动条可能会出现"跳动"。需要通过重新计算占位层总高度并利用
ResizeObserver 监听 DOM 变化来持续修正。

四、 性能优化要点

节流/防抖：滚动事件触发频率极高，建议使用 requestAnimationFrame
进行频率限制，确保渲染与屏幕刷新率同步。

离屏渲染（Buffer）：增加上下缓冲区域，避免出现肉眼可见的白屏闪烁。

节点复用：在 React/Vue 中，确保每个列表项有唯一的 key，但要注意不要让
index 作为 key，否则在列表项滑动切换时无法充分复用 DOM 节点。

CSS 属性优化：使用 will-change: transform 开启 GPU 加速，减少重排。
