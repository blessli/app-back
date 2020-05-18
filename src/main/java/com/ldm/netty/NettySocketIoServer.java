package com.ldm.netty;

import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.ldm.dao.ChatDao;
import com.ldm.entity.ChatHistory;
import com.ldm.entity.ChatMsg;
import com.ldm.request.ChatHistoryRequest;
import com.ldm.service.CacheService;
import com.ldm.util.DateHandle;
import com.ldm.util.RedisKeys;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Slf4j
@Component
public class NettySocketIoServer implements InitializingBean, DisposableBean {

    @Autowired
    private SocketIOServer socketIOServer;

    @Autowired
    private SocketClientComponent socketClient;

    @Autowired
    private ChatDao chatDao;

    @Autowired
    private JedisCluster jedis;

    @OnConnect
    public void onConnect(SocketIOClient client) {
        this.socketClient.storeClientId(client);
        System.out.println("客户端连接:" + getParamsFromClient(client));
        HandshakeData data = client.getHandshakeData();
        String userId = data.getSingleUrlParam("userId");
        String pageSign = data.getSingleUrlParam("pageSign");
        if (pageSign.equals("msgPage")){
            List<ChatMsg> chatMsgList=chatDao.selectChatList(Integer.valueOf(userId));
            System.out.println(chatMsgList);
            socketClient.send(userId,pageSign,"latestMsgList",
                    chatMsgList);
            Map<String,Object> map=new HashMap<>();
            map.put("applyCount",jedis.get(RedisKeys.noticeUnread(0,Integer.valueOf(userId))));
            map.put("agreeCount",jedis.get(RedisKeys.noticeUnread(1,Integer.valueOf(userId))));
            map.put("replyCount",jedis.get(RedisKeys.noticeUnread(2,Integer.valueOf(userId))));
            map.put("followCount",jedis.get(RedisKeys.noticeUnread(3,Integer.valueOf(userId))));
            socketClient.send(userId,pageSign,"notice",map);
        }
    }

    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        this.socketClient.delClientId(client);
        System.out.println("客户端断开:" + getParamsFromClient(client));

    }

    @Override
    public void afterPropertiesSet() {
        start();
    }

    /**
     * 启动netty socketio 服务
     */
    private void start() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                socketIOServer.start();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    /**
     * 关闭netty socketio 服务
     */
    @Override
    public void destroy() {
        if(socketIOServer != null) {
            socketIOServer.stop();
        }
    }

    private String getParamsFromClient(SocketIOClient client) {

        HandshakeData data = client.getHandshakeData();

        String userId = data.getSingleUrlParam("userId");
        String pageSign = data.getSingleUrlParam("pageSign");
        String token = data.getSingleUrlParam("token");

        return "userId:" + userId + ",pageSign:" + pageSign + ",token: " + token;
    }

	@OnEvent(value="chat")
	public void chatEvent(SocketIOClient client, Map<String, Object> chatData) {

        log.info("发送聊天消息");
		String userId = (String) chatData.get("userId");
		String msg = (String) chatData.get("msg");
		String toUserId= (String) chatData.get("toUserId");
		String publishTime= DateHandle.currentDate();
		log.info(userId+" "+msg+" "+toUserId+" "+publishTime);
		String msgFlag;
		if (userId.compareTo(toUserId)<0) {
		    msgFlag=userId+":"+toUserId;
        }
		else {
		    msgFlag=toUserId+":"+userId;
        }
		int ans=chatDao.sendMsg(Integer.valueOf(userId),msg,Integer.valueOf(toUserId),publishTime,msgFlag);
		if(ans>0){
		    log.info(userId+"给"+toUserId+"发送聊天信息成功");
            Map<String,Object> map=new HashMap<>();
            map.put("userId",userId);
            map.put("msg",msg);
            map.put("publishTime",publishTime);
            socketClient.send(toUserId,"chatPage","receiveMsg",map);
            log.info("发送成功");
        }else {
		    log.error(userId+"给"+toUserId+"发送聊天信息失败");
        }
		//this.socketClient.storeClient(userId, pageId, client);
	}
    @OnEvent(value="history")
    public void historyEvent(SocketIOClient client, @RequestBody ChatHistoryRequest request) {
        log.info("获取双方历史聊天记录");
        log.info(request.toString());
        String msgFlag = request.getMsgFlag();
        int userId= request.getUserId();
        String pageSign= request.getPageSign();
        // 实际上这里是一个"2:3",而不是"3:2"
        List<ChatHistory> chatHistoryList=chatDao.selectChatHistory(msgFlag);
        log.info(chatHistoryList.toString());
        socketClient.send(String.valueOf(userId),pageSign,"historyMsgList",chatHistoryList);
        //this.socketClient.storeClient(userId, pageId, client);
    }
}