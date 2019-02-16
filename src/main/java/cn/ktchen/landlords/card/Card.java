package cn.ktchen.landlords.card;

public class Card {
    public enum CardBigType {
        HEI_TAO, HONG_TAO, MEI_HUA, FANG_KUAI, XIAO_WANG, DA_WANG
    }
    public enum CardSmallType {
        A, ER, SAN, SI, WU, LIU, QI, BA, JIU, SHI, J, Q, K, XIAO_WANG, DA_WANG
    }

    public int id;

    @Override
    public String toString() {
        return this.id + "";

    }

    public CardBigType bigType;
    public CardSmallType smallType;
    public int grade;


    public Card(int id) {
        this.id = id;
        bigType = getBigType(id);
        smallType = getSmallType(id);
        grade = getGrade(id);

    }

    public String getName() {
        String prefix = "";
        if (id == 53) {
            prefix += "小王";
        } else if (id == 54) {
            prefix += "大王";
        } else {
            int mod = id % 13;
            String firstLetter = "";
            switch (mod) {
                case 0:
                    firstLetter = "K";
                    break;
                case 1:
                    firstLetter = "A";
                    break;
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                    firstLetter = "" + mod;
                    break;
                case 11:
                    firstLetter = "J";
                    break;
                case 12:
                    firstLetter = "Q";
                    break;
                default:
                    break;
            }

            String secondLetter = "";
            // 得到图片的后一个字符，表示什么颜色的牌
            if (id >= 1 && id <= 13) {
                secondLetter = "红桃";
            } else if (id >= 14 && id <= 26) {
                secondLetter = "黑桃";
            } else if (id >= 27 && id <= 39) {
                secondLetter = "梅花";
            } else if (id >= 40 && id <= 52) {
                secondLetter = "方块";
            }

            prefix = secondLetter +  firstLetter;

        }
        return prefix;
    }

    public static CardBigType getBigType(int id) {
        CardBigType bigType = null;
        if (id >= 1 && id <= 13) {
            bigType = CardBigType.FANG_KUAI;
        } else if (id >= 14 && id <= 26) {
            bigType = CardBigType.MEI_HUA;
        } else if (id >= 27 && id <= 39) {
            bigType = CardBigType.HONG_TAO;
        } else if (id >= 40 && id <= 52) {
            bigType = CardBigType.HEI_TAO;
        } else if (id == 53) {
            bigType = CardBigType.XIAO_WANG;
        } else if (id == 54) {
            bigType = CardBigType.DA_WANG;
        }
        return bigType;
    }

    public static CardSmallType getSmallType(int id) {
        if (id < 1 || id > 54) {
            throw new RuntimeException("牌的数字不合法");
        }

        CardSmallType smallType = null;

        if (id >= 1 && id <= 52) {
            smallType = numToType(id % 13);
        } else if (id == 53) {
            smallType = CardSmallType.XIAO_WANG;
        } else if (id == 54) {
            smallType = CardSmallType.DA_WANG;
        } else {
            smallType = null;
        }
        return smallType;
    }
    private static CardSmallType numToType(int num) {
        CardSmallType type = null;
        switch (num) {
            case 0:
                type = CardSmallType.K;
                break;
            case 1:
                type = CardSmallType.A;
                break;
            case 2:
                type = CardSmallType.ER;
                break;
            case 3:
                type = CardSmallType.SAN;
                break;
            case 4:
                type = CardSmallType.SI;
                break;
            case 5:
                type = CardSmallType.WU;
                break;
            case 6:
                type = CardSmallType.LIU;
                break;
            case 7:
                type = CardSmallType.QI;
                break;
            case 8:
                type = CardSmallType.BA;
                break;
            case 9:
                type = CardSmallType.JIU;
                break;
            case 10:
                type = CardSmallType.SHI;
                break;
            case 11:
                type = CardSmallType.J;
                break;
            case 12:
                type = CardSmallType.Q;
                break;

        }
        return type;
    }

    public static int getGrade(int id) {

        if (id < 1 || id > 54) {
            throw new RuntimeException("牌的数字不合法");
        }

        int grade = 0;

        // 2个王必须放在前边判断
        if (id == 53) {
            grade = 16;
        } else if (id == 54) {
            grade = 17;
        }

        else {
            int modResult = id % 13;

            if (modResult == 1) {
                grade = 14;
            } else if (modResult == 2) {
                grade = 15;
            } else if (modResult == 3) {
                grade = 3;
            } else if (modResult == 4) {
                grade = 4;
            } else if (modResult == 5) {
                grade = 5;
            } else if (modResult == 6) {
                grade = 6;
            } else if (modResult == 7) {
                grade = 7;
            } else if (modResult == 8) {
                grade = 8;
            } else if (modResult == 9) {
                grade = 9;
            } else if (modResult == 10) {
                grade = 10;
            } else if (modResult == 11) {
                grade = 11;
            } else if (modResult == 12) {
                grade = 12;
            } else if (modResult == 0) {
                grade = 13;
            }

        }

        return grade;
    }

}
