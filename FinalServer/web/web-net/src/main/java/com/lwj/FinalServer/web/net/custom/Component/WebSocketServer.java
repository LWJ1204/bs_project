package com.lwj.FinalServer.web.net.custom.Component;


import cn.hutool.json.JSONObject;
import com.lwj.FinalServer.web.net.service.RecordStateService;
import com.lwj.FinalServer.web.net.vo.PageBean;
import com.lwj.FinalServer.web.net.vo.RecordStateVO;
import io.swagger.v3.core.util.Json;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
@Slf4j
@Component
@ServerEndpoint("/websocket/{uid}")
public class WebSocketServer {
    /**
     * 静态变量，用来记录当前在线连接数，线程安全的类。
     */
    private static AtomicInteger onlineSessionClientCount = new AtomicInteger(0);
    /**
     * 存放所有在线的客户端
     */
    private static Map<String, Session> onlineSessionClientMap = new ConcurrentHashMap<>();

    /**
     * 连接uid和连接会话
     */
    private String uid;
    private Session session;



    @Autowired
    private RecordStateService recordStateService;
    /**
     * 连接建立成功调用的方法。由前端<code>new WebSocket</code>触发
     *
     * @param uid     每次页面建立连接时传入到服务端的id，比如用户id等。可以自定义。
     * @param session 与某个客户端的连接会话，需要通过它来给客户端发送消息
     */
    @OnOpen
    public void onOpen(@PathParam("uid") String uid, Session session) {
        /**
         * session.getId()：当前session会话会自动生成一个id，从0开始累加的。
         */
        session.setMaxIdleTimeout(60000*10);
        System.out.println("连接建立中 ==> session_id = {"+session.getId()+"}， sid = {"+uid+"}");
        //加入 Map中。将页面的uid和session绑定或者session.getId()与session
        //onlineSessionIdClientMap.put(session.getId(), session);
        onlineSessionClientMap.put(uid, session);
        //在线数加1
        onlineSessionClientCount.incrementAndGet();
        this.uid = uid;
        this.session = session;
        log.info("连接建立成功，当前在线数为：{} ==> 开始监听新连接：session_id = {}， sid = {},。", onlineSessionClientCount, session.getId(), uid);
    }

    /**
     * 连接关闭调用的方法。由前端<code>socket.close()</code>触发
     *
     * @param uid
     * @param session
     */
    @OnClose
    public void onClose(@PathParam("uid") String uid, Session session) {
        //onlineSessionIdClientMap.remove(session.getId());
        // 从 Map中移除
        onlineSessionClientMap.remove(uid);

        //在线数减1
        log.info("连接关闭成功，当前在线数为：{} ==> 关闭该连接信息：session_id = {}， sid = {},。", onlineSessionClientCount, session.getId(), uid);
    }

    /**
     * 收到客户端消息后调用的方法。由前端<code>socket.send</code>触发
     * * 当服务端执行toSession.getAsyncRemote().sendText(xxx)后，前端的socket.onmessage得到监听。
     *
     * @param message
     * @param session
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        /**
         * html界面传递来得数据格式，可以自定义.
         * {"sid":"user-1","message":"hello websocket"}
         */
        JSONObject jo=new JSONObject(message);
        String type= (String) jo.get("type");
        if(type.equals("heartbeat"))
        {
            Map<String,Object>mp=new HashMap<>();
            mp.put("state",1);
            JSONObject json=new JSONObject(mp);
            session.getAsyncRemote().sendText(json.toString());
        }
        log.info("接收到消息-----------------"+type);
        //A给B发送消息，A要知道B的信息，发送消息的时候把B的信息携带过来

    }

    /**
     * 发生错误调用的方法
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("WebSocket发生错误，错误信息为：" + error.getMessage());
        error.printStackTrace();
    }

    /**
     * 群发消息
     *
     * @param message 消息
     */
    private void sendToAll(String message) {
        // 遍历在线map集合
        onlineSessionClientMap.forEach((onlineSid, toSession) -> {
            // 排除掉自己
            if (!uid.equalsIgnoreCase(onlineSid)) {
                log.info("服务端给客户端群发消息 ==> sid = {}, toSid = {}, message = {}", uid, onlineSid, message);
                toSession.getAsyncRemote().sendText(message);
            }
        });
    }

    /**
     * 指定发送消息
     *
     * @param toUid
     * @param message
     */
    public static void sendToOne(String toUid, String message) {
        /*
         * 判断发送者是否在线
         */
        Session toSession = onlineSessionClientMap.get(toUid);
        if (toSession != null && toSession.isOpen()) {
            Map<String,Object>mp=new HashMap<>();
            mp.put("changed",message);
            JSONObject js=new JSONObject(mp);
            log.info("服务端给客户端发送消息 ==> toSid = {}, message = {}", toUid, message);
            toSession.getAsyncRemote().sendText(js.toString());
        } else {
            log.error("服务端给客户端发送消息 ==> toSid = {} 不存在或会话已关闭, message = {}", toUid, message);
        }
    }
}
