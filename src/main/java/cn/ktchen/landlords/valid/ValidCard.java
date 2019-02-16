package cn.ktchen.landlords.valid;

import cn.ktchen.landlords.card.Card;
import cn.ktchen.landlords.util.CardUtil;

import java.util.ArrayList;
import java.util.List;

public class ValidCard {

    //单张判断
    public static boolean isDan(List<Card> myCards) {
        // 默认不是单
        boolean flag = false;
        if (myCards != null && myCards.size() == 1) {
            flag = true;
        }
        return flag;
    }

    //对子判断
    public static boolean isDuiZi(List<Card> myCards) {
        // 默认不是对子
        boolean flag = false;

        if (myCards != null && myCards.size() == 2) {

            int grade1 = myCards.get(0).grade;
            int grade2 = myCards.get(1).grade;
            if (grade1 == grade2) {
                flag = true;
            }
        }
        return flag;
    }


    //三带二
    public static boolean isSanDaiEr(List<Card> myCards) {
        boolean flag = false;

        if (myCards != null && myCards.size() == 5){
            CardUtil.sortCards(myCards);

            int[] grades = new int[5];
            grades[0] = myCards.get(0).grade;
            grades[1] = myCards.get(1).grade;
            grades[2] = myCards.get(2).grade;
            grades[3] = myCards.get(3).grade;
            grades[4] = myCards.get(4).grade;
            if (grades[0] == grades[1] && grades[2] == grades[3] && grades[2] == grades[4])
                flag = true;
            if (grades[0] == grades[1] && grades[0] == grades[2] && grades[3] == grades[4])
                flag = true;
        }
        return flag;
    }

    //三带一
    public static int isSanDaiYi(List<Card> myCards) {
        int flag = -1;
        // 默认不是3带1
        if (myCards != null && myCards.size() == 4) {
            // 对牌进行排序
            CardUtil.sortCards(myCards);

            int[] grades = new int[4];
            grades[0] = myCards.get(0).grade;
            grades[1] = myCards.get(1).grade;
            grades[2] = myCards.get(2).grade;
            grades[3] = myCards.get(3).grade;

            // 暂时认为炸弹不为3带1
            if ((grades[1] == grades[0]) && (grades[2] == grades[0])
                    && (grades[3] == grades[0])) {
                return -1;
            }
            // 3带1，被带的牌在牌头
            else if ((grades[1] == grades[0] && grades[2] == grades[0])) {
                return 0;
            }
            // 3带1，被带的牌在牌尾
            else if (grades[1] == grades[3] && grades[2] == grades[3]) {
                return 3;
            }
        }
        return flag;
    }

    //三不带
    public static boolean isSanBuDai(List<Card> myCards) {
        // 默认不是3不带
        boolean flag = false;

        if (myCards != null && myCards.size() == 3) {
            int grade0 = myCards.get(0).grade;
            int grade1 = myCards.get(1).grade;
            int grade2 = myCards.get(2).grade;

            if (grade0 == grade1 && grade2 == grade0) {
                flag = true;
            }
        }
        return flag;
    }

    //顺子
    public static boolean isShunZi(List<Card> myCards) {
        // 默认是顺子
        boolean flag = true;

        if (myCards != null) {

            int size = myCards.size();
            // 顺子牌的个数在5到12之间
            if (size < 5 || size > 12) {
                return false;
            }

            // 对牌进行排序
            CardUtil.sortCards(myCards);

            for (int n = 0; n < size - 1; n++) {
                int prev = myCards.get(n).grade;
                int next = myCards.get(n + 1).grade;
                // 小王、大王、2不能加入顺子
                if (prev == 17 || prev == 16 || prev == 15 || next == 17
                        || next == 16 || next == 15) {
                    flag = false;
                    break;
                } else {
                    if (prev - next != -1) {
                        flag = false;
                        break;
                    }
                }
            }
        }
        return flag;
    }

    //炸弹
    public static boolean isZhaDan(List<Card> myCards) {
        // 默认不是炸弹
        boolean flag = false;
        if (myCards != null && myCards.size() == 4) {

            int[] grades = new int[4];
            for (int i = 0; i < 4; i++) {
                grades[i] = myCards.get(i).grade;
            }
            if ((grades[1] == grades[0]) && (grades[2] == grades[0])
                    && (grades[3] == grades[0])) {
                flag = true;
            }
        }
        return flag;
    }

    //王炸
    public static boolean isDuiWang(List<Card> myCards) {
        // 默认不是对王
        boolean flag = false;

        if (myCards != null && myCards.size() == 2) {

            int gradeOne = myCards.get(0).grade;
            int gradeTwo = myCards.get(1).grade;

            // 只有小王和大王的等级之后才可能是33
            if (gradeOne + gradeTwo == 33) {
                flag = true;
            }
        }
        return flag;
    }


    //连对
    public static boolean isLianDui(List<Card> myCards) {
        // 默认是连对
        boolean flag = true;
        if (myCards == null) {
            flag = false;
            return flag;
        }

        int size = myCards.size();
        if (size < 6 || size % 2 != 0) {
            flag = false;
        } else {
            // 对牌进行排序
            CardUtil.sortCards(myCards);
            for (int i = 0; i < size; i = i + 2) {
                if (myCards.get(i).grade != myCards.get(i + 1).grade) {
                    flag = false;
                    break;
                }

                if (i < size - 2) {
                    if (myCards.get(i).grade - myCards.get(i + 2).grade != -1) {
                        flag = false;
                        break;
                    }
                }
            }
        }

        return flag;
    }


    public static boolean isFeiJi(List<Card> myCards) {
        boolean flag = false;
        // 默认不是飞机
        if (myCards != null) {

            int size = myCards.size();
            if (size >= 6) {
                // 对牌进行排序
                CardUtil.sortCards(myCards);

                if (size % 3 == 0 && size % 4 != 0) {
                    flag = isFeiJiBuDai(myCards);
                } else if (size % 3 != 0 && size % 4 == 0) {
                    flag = isFeiJiDaiYi(myCards);
                } else if (size == 12) {
                    flag = isFeiJiBuDai(myCards) || isFeiJiDaiYi(myCards);
                }
            }
        }
        return flag;
    }

    public static boolean isFeiJiBuDai(List<Card> myCards) {
        if (myCards == null) {
            return false;
        }

        int size = myCards.size();
        int n = size / 3;

        int[] grades = new int[n];

        if (size % 3 != 0) {
            return false;
        } else {
            for (int i = 0; i < n; i++) {
                if (!isSanBuDai(myCards.subList(i * 3, i * 3 + 3))) {
                    return false;
                } else {
                    // 如果连续的3张牌是一样的，记录其中一张牌的grade
                    grades[i] = myCards.get(i * 3).grade;
                }
            }
        }

        for (int i = 0; i < n - 1; i++) {
            if (grades[i] == 15) {// 不允许出现2
                return false;
            }

            if (grades[i + 1] - grades[i] != 1) {
                System.out.println("等级连续,如 333444"
                        + (grades[i + 1] - grades[i]));
                return false;// grade必须连续,如 333444
            }
        }

        return true;
    }

    public static boolean isFeiJiDaiYi(List<Card> myCards) {
        int size = myCards.size();
        int n = size / 4;// 此处为“除”，而非取模
        int i = 0;
        for (i = 0; i + 2 < size; i = i + 3) {
            int grade1 = myCards.get(i).grade;
            int grade2 = myCards.get(i + 1).grade;
            int grade3 = myCards.get(i + 2).grade;
            if (grade1 == grade2 && grade3 == grade1) {

                // return isFeiJiBuDai(myCards.subList(i, i + 3 *
                // n));8张牌时，下标越界,subList不能取到最后一个元素
                ArrayList<Card> cards = new ArrayList<Card>();
                for (int j = i; j < i + 3 * n; j++) {// 取字串
                    cards.add(myCards.get(j));
                }
                return isFeiJiBuDai(cards);
            }

        }
        return false;
    }

    public static boolean isSiDaiEr(List<Card> myCards) {
        boolean flag = false;
        if (myCards != null && myCards.size() == 6) {

            // 对牌进行排序
            CardUtil.sortCards(myCards);
            for (int i = 0; i < 3; i++) {
                int grade1 = myCards.get(i).grade;
                int grade2 = myCards.get(i + 1).grade;
                int grade3 = myCards.get(i + 2).grade;
                int grade4 = myCards.get(i + 3).grade;

                if (grade2 == grade1 && grade3 == grade1 && grade4 == grade1) {
                    flag = true;
                }
            }
        }
        return flag;
    }


}
