package com.ldm.netty;

import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.ldm.aop.Action;
import com.ldm.dao.ChatDao;
import com.ldm.entity.ChatHistory;
import com.ldm.entity.ChatMsg;
import com.ldm.service.CacheService;
import com.ldm.util.DateHandle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Slf4j
@Component
public class NettySocketIoServer implements InitializingBean, DisposableBean {

    @Autowired
    private SocketIOServer socketIOServer;

    @Autowired
    private SocketClientComponent socketClientComponent;

    @Autowired
    private ChatDao chatDao;

    @OnConnect
    public void onConnect(SocketIOClient client) {
        this.socketClientComponent.storeClientId(client);
        System.out.println("客户端连接:" + getParamsFromClient(client));
        HandshakeData data = client.getHandshakeData();
        String userId = data.getSingleUrlParam("userId");
        String pageSign = data.getSingleUrlParam("pageSign");
        if (pageSign.equals("msgPage")){
            List<ChatMsg> chatMsgList=chatDao.selectChatList(Integer.valueOf(userId));
            System.out.println(chatMsgList);
            socketClientComponent.sendList(userId,pageSign,"latestMsgList",
                    chatMsgList);
        }


    }

    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        this.socketClientComponent.delClientId(client);
        System.out.println("客户端断开:" + getParamsFromClient(client));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
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
    public void destroy() throws Exception {
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

    @Action(name = "发送聊天消息")
	@OnEvent(value="chat")
	public void chatEvent(SocketIOClient client, Map<String, Object> chatData) {

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
            socketClientComponent.send(toUserId,"chatPage","receiveMsg",map);
            log.info("发送成功");
        }else {
		    log.error(userId+"给"+toUserId+"发送聊天信息失败");
        }
		//this.socketClientComponent.storeClient(userId, pageId, client);
	}
    @Action(name = "获取双方历史聊天记录")
    @OnEvent(value="history")
    public void historyEvent(SocketIOClient client, Map<String, Object> chatData) {

        String msgFlag = (String) chatData.get("msgFlag");
        String userId= (String) chatData.get("userId");
        String pageSign= (String) chatData.get("pageSign");
        // 实际上这里是一个"2:3",而不是"3:2"
        List<ChatHistory> chatHistoryList=chatDao.selectChatHistory(msgFlag);
        System.out.println(chatHistoryList);
        socketClientComponent.sendList(userId,pageSign,"historyMsgList",chatHistoryList);
        //this.socketClientComponent.storeClient(userId, pageId, client);
    }
}