package com.witcher.imedittext;

public class StrToResUtil {

    public static int str2Res(String content) {
        switch (content) {
            case "[1]": {
                return R.drawable.icon1;
            }
            case "[2]": {
                return R.drawable.icon2;
            }
            case "[3]": {
                return R.drawable.icon3;
            }
            case "[4]": {
                return R.drawable.icon4;
            }
            default:
                return R.drawable.icon1;
        }
    }

}
