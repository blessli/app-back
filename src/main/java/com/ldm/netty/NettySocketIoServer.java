package com.ldm.netty;

import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NettySocketIoServer implements InitializingBean, DisposableBean {

    @Autowired
    private SocketIOServer socketIOServer;

    @Autowired
    private SocketClientComponent socketClientComponent;

    @OnConnect
    public void onConnect(SocketIOClient client) {
        this.socketClientComponent.storeClientId(client);
        System.out.println("客户端连接:" + getParamsFromClient(client));
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

//	@OnEvent(value="loginEvent")
//	public void loginEvent(SocketIOClient client, Map<String, Object> loginData) {
//
//		String userId = (String) loginData.get("userId");
//		String pageId = (String) loginData.get("pageId");
//
//		this.socketClientComponent.storeClient(userId, pageId, client);
//	}
}