package com.ldm.netty;

import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOClient;
import com.ldm.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisCluster;

import java.util.HashMap;
import java.util.Map;

/**
 * socketio client 操作组件
 * @author lidongming
 */
@Component
public class SocketClientComponent {

    @Autowired
    private JedisCluster jedis;

    private Map<String,SocketIOClient> clients=new HashMap<>();

    /**
     * 保存socketio client 客户端
     * @param client
     */
    public void storeClientId(SocketIOClient client) {
        clients.put(getKeyFromClient(client),client);
        jedis.set(getKeyFromClient(client), JsonUtil.beanToString(client));
    }

    /**
     * 移除socketio client 客户端
     */
    public void delClientId(SocketIOClient client) {
        clients.remove(getKeyFromClient(client));
        jedis.del(getKeyFromClient(client));
    }

    /**
     * 给指定client发送指定事件的数据
     * @param businessName
     * @param data
     */
    public void send(String userId, String pageSign, String businessName, Object data) {
        SocketIOClient client=clients.get(getKey(userId, pageSign));
        System.out.println(client.toString());
        if(client != null) {
            client.sendEvent(businessName, data);
        }
    }

    private String getKeyFromClient(SocketIOClient client) {
        HandshakeData data = client.getHandshakeData();
        String userId = data.getSingleUrlParam("userId");
        String pageSign = data.getSingleUrlParam("pageSign");
        return getKey(userId, pageSign);
    }

    private String getKey(String userId, String pageSign) {
        return "online:userId:" + userId + ":pageSign:" + pageSign;
    }
}
