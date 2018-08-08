# Netty 对 WebSocket的支持与简单实现

标签（空格分隔）： netty

---

## websockt 的由来

### http 协议的问题

http 协议无状态，为了保持状态，引出了 `session` & `cookies`等技术。

http 是基于 `请求` 和 `响应` 的，请求一定是客户端发出的。（1.1 还有 keepAlive, 持续连接，在一定时间可以进行连接的复用。）

导致的问题， 服务器无法推送数据。所以早期有客户端轮训技术。
会导致资源和网络带宽的浪费。因为 `Header` 数据每次都要构建。

### websocket 可以做到什么？

websocket 来自 html5; 所以是 http 协议的一个升级版本的协议可以建立浏览器和服务器之间的长连接。

- 可以实现服务端的push
- 只需要在一开始建立连接的时候构建 `Header`；其他时间都不需要再有 `Header` 信息

> 因为是基于 HTTP 的，所以建立连接的时候，发的请求是一个标准的 http  请求。只不过是在 `Header` 中添加了信息。

虽然是基于 `Http` 的，但是不仅仅在 `浏览器上使用`
也可以通过第三方的工具包在 `app` 端使用

## netty 对 webSocket 的简单实现

netty 功能众多，可以实现对 `HTTP` 的支持，可以实现高性能异步 `RPC` 的功能。同样，他也支持对 `WebSocket` 的支持。

虽然用过 `netty` 的人都说 `netty` 复杂，但是他无论简单和复杂的应用，写起来都是样的复杂，这样一来平均一下，只要你了解了规则，还是比较简单的2333

### netty 实现 websocket 服务端

三个步骤

- Server
- Initializer
- Handler

#### Server

因为你无论写什么 `Server` 都基本是这个套路：贴代码

```
/**
 * 这个示例主要用来阐述
 * netty 对于 WebSocket 连接的支持
 * 以及如何写一个简单的WebSocket demo
 */
public class WebSocketServer {

    public static void main(String[] args) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new WebSocketChannelInitializer());

            ChannelFuture channelFuture = serverBootstrap.bind(new InetSocketAddress(8899)).sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
```

> 这里简单解释下为什么有连个 group; 其实一个也没有问题。如果你才会用两个 group；那么 `bossGroup` 负责接收请求；而 `workerGroup` 负责处理请求

#### Initializer

`netty` 的各种复杂功能都是由各个 `handler` 实现的，实现 `WebSocket` 也是如此。没啥好说的，贴代码，看注释。

```java
public class WebSocketChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // 因为是基于HTTP 协议之上的，所以需要使用到 HTTP 的编解码技术
        pipeline.addLast(new HttpServerCodec());
        // 按照块来写数据 handler
        pipeline.addLast(new ChunkedWriteHandler());
        // http request 和 response 的一个聚合类
        // netty 会对http 请求做分段的处理；所以在第一个 http 的示例中，有些会调用多次
        pipeline.addLast(new HttpObjectAggregator(8192));

        // web socket netty 的特殊支持
        // 其中 "ws" 是指的是 websocket 协议路径
        // 通常形式   ws://localhost:8899/ws
        // 8899/ws 后面的 ws 就是我传入的 /ws 路径。
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
        // 插入自定义的 TextWebSocketFrameHandler
        pipeline.addLast(new TextWebSocketFrameHandler());
    }
}
```

#### TextWebSocketFrameHandler

`WebSocket` 协议的传输是是以 `Frame` 作为单位的。这里我们处理的是 `Text` 这种类型的 `Frame`。
传统，贴代码：

```Java
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        System.out.println("接受到消息：" + msg.text());
        // 这里我们接受客户端传来的消息，返回我们当前时间
        ctx.writeAndFlush(new TextWebSocketFrame("服务器时间：" + LocalTime.now()));
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("连接建立 : " + ctx.channel().id().asLongText());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("连接断开 : " + ctx.channel().id().asLongText());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("出异常");
        ctx.close();
        cause.printStackTrace();
    }
}
```

> `Frame` 一共有六种，我们可以从类的继承上看
![image_1bi10abjtij3k1fol91c5dkrq9.png-138.7kB][1]
六种分别有不同的用途，从字面上都比较容易理解
至于为什么是六种？ 那是因为 `WebSocket` 协议就是这么规定的。


### 简单的网页客户端

为了方便，我们就只用简单的 `JS` 来进行 `WebSocket` 的调用

```js
<script type="text/javascript">
    var socket;
    // 判断浏览器是不是支持
    if(window.WebSocket) {
        socket = new WebSocket("ws://localhost:8899/ws")
        socket.onmessage = function (event) {
            var ta = document.getElementById("responseText");
            ta.value = ta.value + "\n" + event.data
        }
        socket.onopen = function (event) {
            var ta = document.getElementById("responseText");
            ta.value = "连接开启"
        }
        socket.onclose = function (event) {
            var ta = document.getElementById("responseText");
            ta.value = "连接关闭"
        }
    } else {
        alert("浏览器不支持 WebSocket")
    }
    // 发送消息
    function send(message) {
        if(!window.WebSocket) {
            return;
        }
        if(socket.readyState == WebSocket.OPEN) {
            socket.send(message)
        } else {
            alert("连接尚未开启")
        }
    }
</script>


<form onsubmit="return false;">
    <textarea name="message" style="width: 400px; height: 200px"></textarea>
    <input type="button" value="发送数据" onclick="send(this.form.message.value)"/>
    <h3>服务端输出：</h3>
    <textarea id="responseText" style="width: 400px; height: 200px"></textarea>
    <input type="button" value="清空数据" onclick="javascript: document.getElementById('responseText').value=''"/>
</form>
```

### 测试

- 启动服务器：
![image_1bi10qil21u2ul2hhck541m1jm.png-89.1kB][2]

- 启动客户端

简单的做发你可以在 `IntelliJ` 中直接运行 `html` 他会帮你起一个服务。或者你可以用 `Python`

```shell
python -m SimpleHTTPServer 8080
```
我采用第一种：

![image_1bi1122017ar1ans1bm8fr71nm513.png-64.4kB][3]
客户端显示连接已经建立

此时，你可以在服务器端看到我们要打出的信息
![image_1bi11329c1gjgn4t1rnjvii1t7o1g.png-41.2kB][4]

然后你可以通过客户端给服务器发送消息：比如我发送

> 敏哥好帅
还用你所？

服务端会收到：
![image_1bi116pi91bq35iqpn4150u1t9h1t.png-41.9kB][5]

客户端也会收到服务端的时间：
![image_1bi117a54qu0k151sfo98a1q6t2a.png-17.4kB][6]

至此，我们可以的这个例子就成功运行了，你停止服务器会看到客户端输出 `连接关闭`； 你关闭客户端，会看见服务端输出 `连接断开`。

### 更近一步，基于 frame ? 基于 http ?

#### 基于 frame 的信息传递
刚刚说到，`WebSocket` 在建立长连接后，不需要在传递头信息。我们可以代开 `Chrome` 的控制台看一下：

![image_1bi11g7qkkrtt5ud4sop536d2n.png-149.2kB][7]

我们能看到的是 `ws` 的连接的信息里有个 `Frames` 的标签，你传输的信息都在这里。

#### WebSocket 连接是基于 HTTP 升级的？

我们可以刷新一下你的客户端的网页，观察下 network 的输出：

![image_1bi11n5ke12tr19kve631ot01p6a34.png-153kB][8]
注意看出了 `js` 之外的连个请求：第一个就是基本的 `http` 请求，状态的 `304`

![image_1bi11pgoo1kdfao6mi3r8e1i6p3h.png-135.4kB][9]

重点在看一下下面的 `ws` , 他是一个 `WebSocket` 请求：
![image_1bi11rlob15li1k8r1ars5pt1mb33u.png-166.7kB][10]

`request` 中还有一个
```
Upgrade:websocket
```
的内容，就是它将协议由 `http` 协议升级成为了 `websocket` 协议。
所以就是这么回事

所以，`websocket` 需要浏览器的支持。

## 环境

- `jdk8`
- `netty 4.1.10.Final`
- `gradle`



  [1]: http://static.zybuluo.com/zhumin1990719/vhfib8y17vai9qufzhdar6lk/image_1bi10abjtij3k1fol91c5dkrq9.png
  [2]: http://static.zybuluo.com/zhumin1990719/zx0v9dsjaum7lajszic0e65y/image_1bi10qil21u2ul2hhck541m1jm.png
  [3]: http://static.zybuluo.com/zhumin1990719/7n4gc92bqydxrl621vo7bsae/image_1bi1122017ar1ans1bm8fr71nm513.png
  [4]: http://static.zybuluo.com/zhumin1990719/gp12rirel4wu1dphfwbdn4e9/image_1bi11329c1gjgn4t1rnjvii1t7o1g.png
  [5]: http://static.zybuluo.com/zhumin1990719/7s4ne8auqwdfjwf5ao0f5hgf/image_1bi116pi91bq35iqpn4150u1t9h1t.png
  [6]: http://static.zybuluo.com/zhumin1990719/a0ftuajhha27h8d5npao2mz4/image_1bi117a54qu0k151sfo98a1q6t2a.png
  [7]: http://static.zybuluo.com/zhumin1990719/6xc9j5iqby6fbn3oj37sb32k/image_1bi11g7qkkrtt5ud4sop536d2n.png
  [8]: http://static.zybuluo.com/zhumin1990719/sxu6eoau4d10t21cy45v5ijr/image_1bi11n5ke12tr19kve631ot01p6a34.png
  [9]: http://static.zybuluo.com/zhumin1990719/c3lmwkxat1yv6xxvwexfggq7/image_1bi11pgoo1kdfao6mi3r8e1i6p3h.png
  [10]: http://static.zybuluo.com/zhumin1990719/t8ga6azf75757rytx4xcba7d/image_1bi11rlob15li1k8r1ars5pt1mb33u.png