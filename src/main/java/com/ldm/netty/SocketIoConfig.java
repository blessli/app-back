package com.ldm.netty;

import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SocketIoConfig {

//    @Value("${netty.socket.io.host}")
    private String host="0.0.0.0";

//    @Value("${netty.socket.io.port}")
    private int port=9999;

    /**
     * 创建socketIOServer实例
     * @return
     */
    @Bean
    public SocketIOServer socketIOServer() {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname(host);
        config.setPort(port);
        setAuthorizationListener(config);
        return new SocketIOServer(config);
    }

    /**
     * 设置socketio client连接时的安全校验
     * @param config
     */
    private void setAuthorizationListener(com.corundumstudio.socketio.Configuration config) {
        config.setAuthorizationListener(new AuthorizationListener() {
            @Override
            public boolean isAuthorized(HandshakeData data) {

                String userId = data.getSingleUrlParam("userId");
                String pageSign = data.getSingleUrlParam("pageSign");
                String token = data.getSingleUrlParam("token");
                System.out.println("userId:" + userId + ",pageSign:" + pageSign + ",token: " + token);

                return true;
            }
        });
    }

    /**
     * 开启netty socketio的注解功能
     * @param socketServer
     * @return
     */
    @Bean
    public SpringAnnotationScanner springAnnotationScanner(SocketIOServer socketServer) {
        return new SpringAnnotationScanner(socketServer);
    }
}
