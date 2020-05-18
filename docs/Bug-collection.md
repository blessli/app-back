> 在pipeline.sync()执行之前，通过response.get()获取值，在pipeline.sync()执行前，命令没有执行(可以通过monitor做验证),会报"Please close pipeline or multi block before calling this method"
- https://blog.csdn.net/fly910905/article/details/79760162
> ES内置了很多分词器，但内置的分词器对中文的处理不好。有时，可能ik自身提供的分词词典无法满足特定的一些需求（如专用名词等），ik提供了自定义词典的功能，也就是用户可以自己定义一些词汇,这样ik就会把它们当作词典中的内容来处理。

> 定时任务@Scheduled执行阻塞问题

> 热度排序算法,实时更新问题

> ORDER BY publish_time不走索引,使用ORDER BY id会走索引

> 在压测的时候,jedis报错Could not get a resource from the pool] with root cause
> LEAK: ByteBuf.release() was not called before it's garbage-collected. See https://netty.io/wiki/reference-counted-objects.html for more information.
  Recent access records
```
2020-04-23 23:25:42.790 ERROR 7814 --- [ient_boss][T#1]] io.netty.util.ResourceLeakDetector       : LEAK: ByteBuf.release() was not called before it's garbage-collected. See https://netty.io/wiki/reference-counted-objects.html for more information.
Recent access records: 
Created at:
	io.netty.buffer.PooledByteBufAllocator.newDirectBuffer(PooledByteBufAllocator.java:349)
	io.netty.buffer.AbstractByteBufAllocator.directBuffer(AbstractByteBufAllocator.java:187)
	io.netty.buffer.AbstractByteBufAllocator.directBuffer(AbstractByteBufAllocator.java:178)
	io.netty.buffer.AbstractByteBufAllocator.ioBuffer(AbstractByteBufAllocator.java:131)
	com.corundumstudio.socketio.protocol.PacketEncoder.allocateBuffer(PacketEncoder.java:55)
	com.corundumstudio.socketio.handler.EncoderHandler.handleWebsocket(EncoderHandler.java:241)
	com.corundumstudio.socketio.handler.EncoderHandler.write(EncoderHandler.java:216)
	io.netty.channel.AbstractChannelHandlerContext.invokeWrite0(AbstractChannelHandlerContext.java:716)
	io.netty.channel.AbstractChannelHandlerContext.invokeWriteAndFlush(AbstractChannelHandlerContext.java:763)
	io.netty.channel.AbstractChannelHandlerContext.write(AbstractChannelHandlerContext.java:789)
	io.netty.channel.AbstractChannelHandlerContext.writeAndFlush(AbstractChannelHandlerContext.java:757)
	io.netty.channel.AbstractChannelHandlerContext.writeAndFlush(AbstractChannelHandlerContext.java:812)
	io.netty.channel.DefaultChannelPipeline.writeAndFlush(DefaultChannelPipeline.java:1036)
	io.netty.channel.AbstractChannel.writeAndFlush(AbstractChannel.java:293)
	com.corundumstudio.socketio.handler.ClientHead.sendPackets(ClientHead.java:150)
	com.corundumstudio.socketio.handler.ClientHead.send(ClientHead.java:146)
	com.corundumstudio.socketio.handler.ClientHead.send(ClientHead.java:115)
	com.corundumstudio.socketio.transport.WebSocketTransport.channelInactive(WebSocketTransport.java:151)
	io.netty.channel.AbstractChannelHandlerContext.invokeChannelInactive(AbstractChannelHandlerContext.java:257)
	io.netty.channel.AbstractChannelHandlerContext.invokeChannelInactive(AbstractChannelHandlerContext.java:243)
	io.netty.channel.AbstractChannelHandlerContext.fireChannelInactive(AbstractChannelHandlerContext.java:236)
	io.netty.channel.ChannelInboundHandlerAdapter.channelInactive(ChannelInboundHandlerAdapter.java:81)
	io.netty.handler.codec.MessageAggregator.channelInactive(MessageAggregator.java:438)
	io.netty.channel.AbstractChannelHandlerContext.invokeChannelInactive(AbstractChannelHandlerContext.java:257)
	io.netty.channel.AbstractChannelHandlerContext.invokeChannelInactive(AbstractChannelHandlerContext.java:243)
	io.netty.channel.AbstractChannelHandlerContext.fireChannelInactive(AbstractChannelHandlerContext.java:236)
	io.netty.channel.ChannelInboundHandlerAdapter.channelInactive(ChannelInboundHandlerAdapter.java:81)
	com.corundumstudio.socketio.transport.PollingTransport.channelInactive(PollingTransport.java:190)
	io.netty.channel.AbstractChannelHandlerContext.invokeChannelInactive(AbstractChannelHandlerContext.java:257)
	io.netty.channel.AbstractChannelHandlerContext.invokeChannelInactive(AbstractChannelHandlerContext.java:243)
	io.netty.channel.AbstractChannelHandlerContext.fireChannelInactive(AbstractChannelHandlerContext.java:236)
	io.netty.handler.codec.ByteToMessageDecoder.channelInputClosed(ByteToMessageDecoder.java:393)
	io.netty.handler.codec.ByteToMessageDecoder.channelInactive(ByteToMessageDecoder.java:358)
	io.netty.channel.AbstractChannelHandlerContext.invokeChannelInactive(AbstractChannelHandlerContext.java:257)
	io.netty.channel.AbstractChannelHandlerContext.invokeChannelInactive(AbstractChannelHandlerContext.java:243)
	io.netty.channel.AbstractChannelHandlerContext.fireChannelInactive(AbstractChannelHandlerContext.java:236)
	io.netty.channel.DefaultChannelPipeline$HeadContext.channelInactive(DefaultChannelPipeline.java:1416)
	io.netty.channel.AbstractChannelHandlerContext.invokeChannelInactive(AbstractChannelHandlerContext.java:257)
	io.netty.channel.AbstractChannelHandlerContext.invokeChannelInactive(AbstractChannelHandlerContext.java:243)
	io.netty.channel.DefaultChannelPipeline.fireChannelInactive(DefaultChannelPipeline.java:912)
	io.netty.channel.AbstractChannel$AbstractUnsafe$8.run(AbstractChannel.java:816)
	io.netty.util.concurrent.AbstractEventExecutor.safeExecute(AbstractEventExecutor.java:163)
	io.netty.util.concurrent.SingleThreadEventExecutor.runAllTasks(SingleThreadEventExecutor.java:416)
	io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:515)
	io.netty.util.concurrent.SingleThreadEventExecutor$5.run(SingleThreadEventExecutor.java:918)
	io.netty.util.internal.ThreadExecutorMap$2.run(ThreadExecutorMap.java:74)
	io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)
	java.lang.Thread.run(Thread.java:748)

```
https://developer.aliyun.com/article/712285
https://blog.csdn.net/hannuotayouxi/article/details/78827499

初始堆外内存:33554432 byte==32MB

- socket.io 由于使用了 Nginx ,所以需要特别配置