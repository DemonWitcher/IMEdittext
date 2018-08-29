package com.witcher.imedittext;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.KeyEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IMEditText extends android.support.v7.widget.AppCompatEditText {


    public IMEditText(Context context) {
        super(context);
        init();
    }

    public IMEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IMEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
    }


    public void addIcon(String source) {
        //改成在下标位置添加
        int index = getSelectionEnd();
        StringBuilder sb = new StringBuilder(getText().toString());
        sb.insert(index, source);

        setText(getEmotionContent(sb.toString()));

        setSelection(index + source.length());
    }

    private SpannableString getEmotionContent(String content) {
        SpannableString spannableString = new SpannableString(content);
        Resources resources = getResources();
        String regexEmotion = "\\[([\u4e00-\u9fa5|\\u0040\\w])+]";
        //u4e00-u9fa5是基本中文区间  u0040是"@"符号
        Pattern patternEmotion = Pattern.compile(regexEmotion);
        Matcher matcherEmotion = patternEmotion.matcher(spannableString);

        while (matcherEmotion.find()) {
            String key = matcherEmotion.group();
            int start = matcherEmotion.start();
            Bitmap imgBitmap;

            if(key.startsWith("[@")){
                imgBitmap = str2Bitmap(key.substring(1,key.length()-1));
            }else{
                imgBitmap = BitmapFactory.decodeResource(resources, str2Res(key));
            }

//            int size = (int) getTextSize() * 13 / 10;
//            Bitmap scaleBitmap = Bitmap.createScaledBitmap(imgBitmap, size, size, true);

            ImageSpan span = new ImageSpan(getContext(), imgBitmap);
            spannableString.setSpan(span, start, start + key.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }

   private static int str2Res(String content) {
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

    private Bitmap str2Bitmap(String content){
        Bitmap bmp = Bitmap.createBitmap(200,100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        Paint paint  = new Paint();
        paint.setColor(Color.BLUE);
        paint.setAntiAlias(true);
        paint.setTextSize(getTextSize());
        canvas.drawText(content, 0,50, paint);
        return bmp;
    }

    public void delete() {
        dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
    }

    public void at(String content){
        addIcon(content);
    }

    public void test1() {
        L.i("content:" + getText().toString());
        L.i("SelectionEnd:" + getSelectionEnd());
        L.i("SelectionStart:" + getSelectionStart());
    }

    public void test2() {
    }
}
