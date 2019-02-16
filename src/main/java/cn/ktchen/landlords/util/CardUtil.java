package cn.ktchen.landlords.util;

import cn.ktchen.landlords.card.Card;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CardUtil {
    public static void sortCards(List<Card> cards) {
        Collections.sort(cards, new CardComparator());
    }

    public static class CardComparator implements Comparator<Card> {

        public int compare(Card card1, Card card2) {
            int result = -1;

            int grade1 = card1.grade;
            int grade2 = card2.grade;

            if (grade1 > grade2) {
                result = 1;
            } else if (grade1 < grade2) {
                result = -1;
            } else {
                // 等级相同的情况，比如都是9
                Card.CardBigType bigType1 = card1.bigType;
                Card.CardBigType bigType2 = card2.bigType;
                // 从左到右，方块、梅花、红桃、黑桃
                if (bigType1.equals(Card.CardBigType.HEI_TAO)) {
                    result = 1;
                } else if (bigType1.equals(Card.CardBigType.HONG_TAO)) {
                    if (bigType2.equals(Card.CardBigType.MEI_HUA)
                            || bigType2.equals(Card.CardBigType.FANG_KUAI)) {
                        result = 1;
                    }
                } else if (bigType1.equals(Card.CardBigType.MEI_HUA)) {
                    if (!bigType2.equals(Card.CardBigType.FANG_KUAI)) {
                        return result;
                    }
                    result = 1;
                }
                // 2张牌的等级不可能完全相同,程序内部采用这种设计
                else {
                    result = -1;
                }
            }

            return result;
        }

    }

}
