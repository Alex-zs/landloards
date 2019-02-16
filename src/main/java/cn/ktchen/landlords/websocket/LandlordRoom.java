package cn.ktchen.landlords.websocket;

import cn.ktchen.landlords.card.Card;
import cn.ktchen.landlords.card.SingerCards;
import cn.ktchen.landlords.entity.Message;
import cn.ktchen.landlords.entity.MessageType;
import cn.ktchen.landlords.util.CompareCard;
import cn.ktchen.landlords.util.JsonUtil;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/room/landlordroom")
@Component
public class LandlordRoom {

    //每个房间的人数
    private static Integer numberOfEachRoom = 2;

    //连接人数
    private static Map<String, Session> livingSessions = new ConcurrentHashMap<>();
    //每个连接的人的卡片
    private static Map<String, List<Card>> currentCards = new ConcurrentHashMap<>();

    //轮到出牌的回话
    private static String nowSessionId;

    //上次出牌
    private static List<Card> prevCards = new ArrayList<>();
    private static CompareCard.CardType prevCardType;

    //本次出牌
    private static List<Card> nowCards = new ArrayList<>();
    private static CompareCard.CardType nowCardType;

    //开始标志
    private static boolean startFlag = false;


    @OnOpen
    public void openSession(Session session) {

        //保存回话
        putSession(session);

        //发送用户名
        sendUsername(session);

        //如果人数满则发送开始游戏提示
        sendStartWarning();
    }

    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
       Map map = JsonUtil.getMapper().readValue(message, Map.class);
       if (map.get("type").equals("start")){
           start(session);
       }
       if (map.get("type").equals("play")){
           play(session, map);
       }
       if (map.get("type").equals("giveup")){
           giveUp(session);
       }
       if (map.get("type").equals("status")){
           sendNowStatus(session);
       }

    }

    @OnClose
    public void onClose(Session session) {
        livingSessions.remove(session.getId());
        startFlag = false;
    }

    @OnError
    public void onError(Session session, Throwable error) {
        livingSessions.remove(session.getId());
        startFlag = false;
    }

    //人数已满则发送可以开始游戏的提示
    private static void sendStartWarning() {
        if (livingSessions.size() == numberOfEachRoom){
            sendMessageAll(MessageType.Warning, "人数已满，可以开始游戏");
        }
    }

    //开始
    private void start(Session session) {
        if (livingSessions.size() == numberOfEachRoom && startFlag == false){
            nowSessionId = session.getId();
            readyBeforeStart();
            divide();
        }else if (livingSessions.size() < numberOfEachRoom){
            sendMessage(session, MessageType.Warning, "当前人数不足");
        }else if (startFlag == true) {
            sendMessage(session, MessageType.Warning, "游戏已开始，请稍后再试");
        }
    }

    //发牌前准备
    private static void readyBeforeStart(){
        currentCards.clear();
        SingerCards.deal();
        currentCards.put(livingSessions.get(nowSessionId).getId(), SingerCards.getPlay1_cards());
        currentCards.put(getAnotherSessionId(), SingerCards.getPlay2_cards());
        prevCards = new ArrayList<>();
        prevCardType = null;
        nowCards = new ArrayList<>();
        nowCardType = null;
        sendTurnFlagAll();
    }

    //给所有人发牌
    private static void divide() {

        for (Map.Entry<String, Session> entry:
             livingSessions.entrySet()) {

            String _sessionId = entry.getKey();
            //发送剩余牌
            sendCards(_sessionId, currentCards.get(_sessionId));
            //发送出牌次序
            sendPlayWarning(_sessionId);
        }

    }

    //发牌
    private static void sendCards(String _sessionId, List<Card> cards) {
        Message message = new Message();
        message.setType(MessageType.Cards.toString());
        message.setContent(currentCards.get(_sessionId).toString());
        sendText(livingSessions.get(_sessionId), JsonUtil.decode(message));
    }

    //发送提示
    private static void sendWarning(String sessionId, String content){
        sendMessage(livingSessions.get(sessionId) , MessageType.Warning, content);
    }

    //发送出牌提示
    private static void sendPlayWarning(String _sessionId) {
        if (_sessionId == nowSessionId){
            sendWarning(_sessionId, "轮到你出牌");
        }else {
            sendWarning(_sessionId, "请等待对方出牌");
        }
    }

    //给所有人发送出牌提示
    private static void sendPlayWarningAll(){
        for (Map.Entry<String, Session> entry :
                livingSessions.entrySet()) {
            sendPlayWarning(entry.getKey());
        }
    }

    //获取另一个不出牌的回话ID
    private static String getAnotherSessionId() {
        for (Map.Entry<String, Session> entry :
                livingSessions.entrySet()) {
            if (!entry.getKey().equals(nowSessionId))
                return entry.getKey();
        }
        return null;
    }

    private static boolean putSession(Session session) {
        boolean flag = false;
        if (livingSessions.size() >= numberOfEachRoom){
           sendMessage(session, MessageType.Warning, "人数已满，请稍后再试");
            try {
                session.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            livingSessions.put(session.getId(), session);
            flag = true;
        }
        return flag;
    }

    private static void sendAll(String message) {
        livingSessions.forEach((sessionId, session) ->{
            sendText(session, message);
        });
    }

    private static void sendText(Session session, String message){
        RemoteEndpoint.Basic basic = session.getBasicRemote();
        try {
            basic.sendText(message);
        } catch (IOException e) {
            livingSessions.remove(session.getId());
            e.printStackTrace();
        }
    }

    //收到牌
    private static void play(Session session, Map map) {
        if (nowSessionId == session.getId() && !isWin()){
            List<Card> cards = getCards(map);
            nowCardType = CompareCard.getCardType(cards);
            if (nowCardType != null){
                if (CompareCard.isOvercomePrev(cards, nowCardType, prevCards, prevCardType)) {
                    //抹去已打出牌
                    removeAll(session.getId(), cards);

                    //更改出牌回话
                    nowSessionId = getAnotherSessionId();

                    //发送出牌信息
                    String message = "玩家" + session.getId()+ ":[" + CompareCard.getCardTypeName(nowCardType) + "] ";
                    for (int i = 0; i < cards.size(); i++) {
                        message += cards.get(i).getName() + " ";
                    }
                    message += "\n";
                    sendPlayCardInfo(message);

                    //发送当前剩余牌信息和出牌次序
                    divide();
                    sendTurnFlagAll();

                    //判断输赢
                    if (isWin()) {
                        //游戏结束
                        startFlag = false;
                        sendWinMessage();
                    }

                    //交换
                    prevCards = cards;
                    prevCardType = nowCardType;

                } else {
                    //发送牌不大的提示
                    sendCardNotBigWarning();
                }
            }else {
                //发送牌不规范的提示
                sendNotRightCardsWarning();
            }
        }
    }

    //将消息生成牌
    private static List<Card> getCards(Map map) {
        String cardStr = (String) map.get("content");
        cardStr = cardStr.substring(1, cardStr.length()-1);
        String[] cardList = cardStr.split(",");

        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < cardList.length; i++) {
            cardList[i] = cardList[i].trim();
            cards.add(new Card(Integer.parseInt(cardList[i])));
        }
        return cards;
    }

    //移除已出的牌
    private static void removeAll(String sessionId, List<Card> cards) {
        List<Card> oldCards = currentCards.get(sessionId);
        for (int i = 0; i < cards.size(); i++) {
            for (int j = 0; j < oldCards.size(); j++) {
                if (cards.get(i).id == oldCards.get(j).id){
                    oldCards.remove(j);
                }
            }
        }
    }

    //出牌信息
    private static void sendPlayCardInfo(String message){

        Message message1 = new Message();

        message1.setType(MessageType.Message.toString());

        message1.setContent(message);

        sendAll(JsonUtil.decode(message1));

    }

    //出牌不规范提示
    private static void sendNotRightCardsWarning() {

       sendWarning(nowSessionId, "出牌不规范，亲人两行泪");

    }

    //出牌打不起上家提示
    private static void sendCardNotBigWarning() {

        sendWarning(nowSessionId, "你这牌不大呀," +
                "换个大的牌，要不就别要了");

    }

    private static void giveUp(Session session) {

        if (session.getId() == nowSessionId){
            //更改出牌回话ID
            nowSessionId = getAnotherSessionId();

            //更改上次出牌
            prevCards = new ArrayList<>();
            prevCardType = null;

            //发送不要提示
            sendMessageAll(MessageType.Message, "玩家" + session.getId() + ":不要");

            //发送出牌提示
            sendPlayWarningAll();

            //发送出牌次序标志
            sendTurnFlagAll();
        }
    }

    //判断输赢
    private static boolean isWin() {

        for (Map.Entry<String, List<Card>> entry :
                currentCards.entrySet()) {

            if (entry.getValue().size() == 0)
                return true;
        }

        return false;
    }

    //发送输赢信息
    private static void sendWinMessage() {
        Message message = new Message();
        message.setType(MessageType.Result.toString());
        for (Map.Entry<String, List<Card>> entry :
                currentCards.entrySet()) {

            if (entry.getValue().size() == 0){
                message.setContent("恭喜你赢了" );
                sendText(livingSessions.get(entry.getKey()),JsonUtil.decode(message));

            }else {
                message.setContent("你输了，不要灰心，再接再厉");
                sendText(livingSessions.get(entry.getKey()), JsonUtil.decode(message));
            }
        }
    }

    //发送用户名
    private static void sendUsername(Session session) {

        Message message = new Message();

        message.setType(MessageType.UserInfo.toString());

        message.setContent("玩家" + session.getId());

        sendText(session, JsonUtil.decode(message));

    }

    //发送当前状态
    private static void sendNowStatus(Session session) {
        boolean flag = true;

        if (livingSessions.size() != numberOfEachRoom)
            flag = false;

        if (currentCards.size() != numberOfEachRoom)
            flag = false;

        for (Map.Entry<String, List<Card>> entry:
        currentCards.entrySet()){
            if (entry.getValue() == null)
                flag = false;
        }

        if (flag){
            Message message = new Message();
            message.setType(MessageType.Status.toString());
            StringBuilder builder = new StringBuilder();

            //另一位玩家剩余卡片数
            for (Map.Entry<String, List<Card>> entry:
                    currentCards.entrySet()){
                if (entry.getKey() != session.getId()) {
                    builder.append("玩家" + entry.getKey() + "剩余:" + entry.getValue().size() + "张<br>");
                }
            }

            //上次出牌信息
            if (prevCards != null){
                builder.append("上次出牌:");
                if (prevCards.size() != 0){
                    for (int i = 0; i < prevCards.size(); i++) {
                        builder.append(prevCards.get(i).getName() + " ");
                    }
                }else{
                    builder.append("无");
                }
                builder.append("<br>");
            }

            //出牌次序
            if (session.getId() == nowSessionId){
                builder.append("出牌次序:轮到你了，快出牌");
            }else {
                builder.append("出牌次序:请等待对方出牌");
            }

            sendTurnFlagAll();


            message.setContent(builder.toString());
            sendText(session, JsonUtil.decode(message));
        }
    }

    //发送消息
    private static void sendMessage(Session session, MessageType type, String content) {
        Message message = new Message();
        message.setType(type.toString());
        message.setContent(content);
        sendText(session, JsonUtil.decode(message));
    }

    //向所有人发送信息
    private static void sendMessageAll(MessageType type, String content) {
       livingSessions.forEach((sessionId, session) -> {
           sendMessage(session, type, content);
       });
    }


    //发送出牌次序标志
    private static void sendTurnFlag(Session session) {
        if (session.getId() == nowSessionId) {
            sendMessage(session, MessageType.Turn, "true");
        }else{
            sendMessage(session, MessageType.Turn, "false");
        }
    }

    //向所有人发送出牌次序标志
    private static void sendTurnFlagAll() {
        livingSessions.forEach((sessionId, session) -> {
            sendTurnFlag(session);
        });
    }
}
