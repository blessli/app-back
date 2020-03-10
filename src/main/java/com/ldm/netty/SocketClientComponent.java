package com.ldm.netty;

import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOClient;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * socketio client 操作组件
 * @author haishui211
 */
@Component
public class SocketClientComponent {

    private Map<String, SocketIOClient> clients = new HashMap();

    /**
     * 保存socketio client 客户端
     * @param client
     */
    public void storeClientId(SocketIOClient client) {
        clients.put(getKeyFromClient(client), client);
        if(clients.size()==2){
            Map<String,Object> map=new HashMap<>();
            map.put("test","12312给12311发消息");
            send("12311","firstPage","testEvent",map);
            System.out.println("发出");
        }
    }

    /**
     * 移除socketio client 客户端
     */
    public void delClientId(SocketIOClient client) {
        clients.remove(getKeyFromClient(client));
    }

    /**
     * 给指定client发送指定事件的数据
     * @param businessName
     * @param data
     */
    public void send(String userId, String pageSign, String businessName, Map<String, Object> data) {
        SocketIOClient client = clients.get(getKey(userId, pageSign));
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
        return "userId:" + userId + ":pageSign:" + pageSign;
    }
}
