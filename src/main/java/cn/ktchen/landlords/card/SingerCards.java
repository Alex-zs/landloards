package cn.ktchen.landlords.card;

import cn.ktchen.landlords.util.CardUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SingerCards {

    public static void deal() {
        shuffle();
        divide();
    }
    public static List<Card> getPlay1_cards() {
        return play1_cards;
    }

    public static List<Card> getPlay2_cards() {
        return play2_cards;
    }

    private static List<Card> allCards;
    private static List<Card> play1_cards;
    private static List<Card> play2_cards;

    private static void shuffle() {
        allCards = new ArrayList<>();
        play1_cards = new ArrayList<>();
        play2_cards = new ArrayList<>();

        for (int i = 0; i < 54; ++i) {
            Card card = new Card(i + 1);
            allCards.add(card);
        }

        for (int i = 0; i < 100; i++) {
            Collections.shuffle(allCards);
        }
    }

    private static void divide() {
        for (int i = 0; i < 27; i++) {
            play1_cards.add(allCards.get(i));
            play2_cards.add(allCards.get(27 + i));
        }
        CardUtil.sortCards(play1_cards);
        CardUtil.sortCards(play2_cards);
    }
}
