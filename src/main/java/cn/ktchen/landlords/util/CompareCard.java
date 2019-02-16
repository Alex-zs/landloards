package cn.ktchen.landlords.util;

import cn.ktchen.landlords.card.Card;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static cn.ktchen.landlords.valid.ValidCard.*;

public class CompareCard {

    public enum CardType {
        DAN, WANG_ZHA, DUI_ZI, ZHA_DAN, SAN_DAI_YI, SAN_DAI_ER,  SAN_BU_DAI, SHUN_ZI, LIAN_DUI, SI_DAI_ER, FEI_JI
    }

    public static Map<CardType, String> CardTypeName;

    public static String getCardTypeName(CardType type) {
        if (CardTypeName == null){
            CardTypeName = new HashMap<>();
            CardTypeName.put(CardType.DAN, "单张牌");
            CardTypeName.put(CardType.WANG_ZHA, "王炸");
            CardTypeName.put(CardType.DUI_ZI, "对子");
            CardTypeName.put(CardType.ZHA_DAN, "炸弹");
            CardTypeName.put(CardType.SAN_DAI_YI, "三带一");
            CardTypeName.put(CardType.SAN_DAI_ER, "三带二");
            CardTypeName.put(CardType.SAN_BU_DAI,"三张牌");
            CardTypeName.put(CardType.SHUN_ZI, "顺子");
            CardTypeName.put(CardType.LIAN_DUI, "连对");
            CardTypeName.put(CardType.SI_DAI_ER, "四带二");
            CardTypeName.put(CardType.FEI_JI, "飞机");
        }
        return CardTypeName.get(type);
    }

    public static boolean isOvercomePrev(List<Card> myCards,
                                         CardType myCardType, List<Card> prevCards, CardType prevCardType) {
        // 我的牌和上家的牌都不能为null
        if (myCards == null && prevCards == null) {
            return false;
        }

        if (myCardType == null && prevCardType == null) {

            return false;
        }

        // 上一首牌的个数
        int prevSize = prevCards.size();
        int mySize = myCards.size();

        // 我先出牌，上家没有牌
        if (prevSize == 0 && mySize != 0) {
            return true;
        }

        // 集中判断是否王炸，免得多次判断王炸
        if (prevCardType == CardType.WANG_ZHA) {
            return false;
        } else if (myCardType == CardType.WANG_ZHA) {
            return true;
        }

        // 集中判断对方不是炸弹，我出炸弹的情况
        if (prevCardType != CardType.ZHA_DAN && myCardType == CardType.ZHA_DAN) {
            return true;
        }

        // 默认情况：上家和自己想出的牌都符合规则
        CardUtil.sortCards(myCards);// 对牌排序
        CardUtil.sortCards(prevCards);// 对牌排序

        int myGrade = myCards.get(0).grade;
        int prevGrade = prevCards.get(0).grade;

        // 比较2家的牌，主要有2种情况，1.我出和上家一种类型的牌，即对子管对子；
        // 2.我出炸弹，此时，和上家的牌的类型可能不同
        // 王炸的情况已经排除

        // 单
        if (prevCardType == CardType.DAN && myCardType == CardType.DAN) {
            // 一张牌可以大过上家的牌
            return compareGrade(myGrade, prevGrade);
        }
        // 对子
        else if (prevCardType == CardType.DUI_ZI
                && myCardType == CardType.DUI_ZI) {
            // 2张牌可以大过上家的牌
            return compareGrade(myGrade, prevGrade);

        }
        // 3不带
        else if (prevCardType == CardType.SAN_BU_DAI
                && myCardType == CardType.SAN_BU_DAI) {
            // 3张牌可以大过上家的牌
            return compareGrade(myGrade, prevGrade);
        }
        // 炸弹
        else if (prevCardType == CardType.ZHA_DAN
                && myCardType == CardType.ZHA_DAN) {
            // 4张牌可以大过上家的牌
            return compareGrade(myGrade, prevGrade);

        }
        // 3带1
        else if (prevCardType == CardType.SAN_DAI_YI
                && myCardType == CardType.SAN_DAI_YI) {

            // 3带1只需比较第2张牌的大小
            myGrade = myCards.get(1).grade;
            prevGrade = prevCards.get(1).grade;
            return compareGrade(myGrade, prevGrade);

        }
        // 3带2
        else if (prevCardType == CardType.SAN_DAI_ER
                && myCardType == CardType.SAN_DAI_ER){
            myGrade =  myCards.get(2).grade;
            prevGrade = prevCards.get(2).grade;
            return compareGrade(myGrade, prevGrade);
        }
        // 4带2
        else if (prevCardType == CardType.SI_DAI_ER
                && myCardType == CardType.SI_DAI_ER) {

            // 4带2只需比较第3张牌的大小
            myGrade = myCards.get(2).grade;
            prevGrade = prevCards.get(2).grade;

        }
        // 顺子
        else if (prevCardType == CardType.SHUN_ZI
                && myCardType == CardType.SHUN_ZI) {
            if (mySize != prevSize) {
                return false;
            } else {
                // 顺子只需比较最大的1张牌的大小
                myGrade = myCards.get(mySize - 1).grade;
                prevGrade = prevCards.get(prevSize - 1).grade;
                return compareGrade(myGrade, prevGrade);
            }

        }
        // 连对
        else if (prevCardType == CardType.LIAN_DUI
                && myCardType == CardType.LIAN_DUI) {
            if (mySize != prevSize) {
                return false;
            } else {
                // 顺子只需比较最大的1张牌的大小
                myGrade = myCards.get(mySize - 1).grade;
                prevGrade = prevCards.get(prevSize - 1).grade;
                return compareGrade(myGrade, prevGrade);
            }

        }
        // 飞机
        else if (prevCardType == CardType.FEI_JI
                && myCardType == CardType.FEI_JI) {
            if (mySize != prevSize) {
                return false;
            } else {
                // 顺子只需比较第5张牌的大小(特殊情况333444555666没有考虑，即12张的飞机，可以有2种出法)
                myGrade = myCards.get(4).grade;
                prevGrade = prevCards.get(4).grade;
                return compareGrade(myGrade, prevGrade);
            }
        }

        // 默认不能出牌
        return false;
    }


    private static boolean compareGrade(int grade1, int grade2) {
        return grade1 > grade2;
    }

    public static CardType getCardType(List<Card> myCards) {
        CardType cardType = null;
        if (myCards != null) {
            // 大概率事件放前边，提高命中率
            if (isDan(myCards)) {
                cardType = CardType.DAN;
            } else if (isDuiWang(myCards)) {
                cardType = CardType.WANG_ZHA;
            } else if (isDuiZi(myCards)) {
                cardType = CardType.DUI_ZI;
            } else if (isZhaDan(myCards)) {
                cardType = CardType.ZHA_DAN;
            } else if (isSanDaiYi(myCards) != -1) {
                cardType = CardType.SAN_DAI_YI;
            } else if (isSanDaiEr(myCards)){
                cardType = CardType.SAN_DAI_ER;
            } else if (isSanBuDai(myCards)) {
                cardType = CardType.SAN_BU_DAI;
            } else if (isShunZi(myCards)) {
                cardType = CardType.SHUN_ZI;
            } else if (isLianDui(myCards)) {
                cardType = CardType.LIAN_DUI;
            } else if (isSiDaiEr(myCards)) {
                cardType = CardType.SI_DAI_ER;
            } else if (isFeiJi(myCards)) {
                cardType = CardType.FEI_JI;
            }
        }

        return cardType;

    }

}
