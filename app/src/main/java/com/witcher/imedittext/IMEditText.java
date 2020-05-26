package com.witcher.imedittext;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.widget.AppCompatEditText;

public class IMEditText extends AppCompatEditText {

    private boolean isNeedATClick = true;

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
        addTextChangedListener(new TextWatcher() {
            String lastContent;
            int index;
            int offset;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                offset = 0;
                lastContent = s.toString();
                index = getSelectionEnd();
                offset = offset+ after;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                offset = offset - before;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals(lastContent)) {
                    return;
                }
                notifyContent();
                setSelection(index + offset);
            }
        });
    }


    public void addIcon(String source) {
        //改成在下标位置添加
        int index = getSelectionEnd();
        StringBuilder sb = new StringBuilder(getText().toString());
        sb.insert(index, source);

        setText(getEmotionContent(sb.toString()));

        setSelection(index + source.length());
    }

    SpannableString spannableString;

    private SpannableString getEmotionContent(String content) {
        SpannableString spannableString = new SpannableString(content);
        Resources resources = getResources();
        String regexEmotion = "\\[([\u4e00-\u9fa5|\\u0040\\w])+]|\\u0023+(\\w)+\\u0023";//|\u0023\w\u0023
        //u4e00-u9fa5是基本中文区间  u0040是"@"符号  u0023是#号
        Pattern patternEmotion = Pattern.compile(regexEmotion);
        Matcher matcherEmotion = patternEmotion.matcher(spannableString);

        while (matcherEmotion.find()) {
            String key = matcherEmotion.group();
            int start = matcherEmotion.start();
            if (key.startsWith("#") && key.endsWith("#")) {
                ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.RED);
                spannableString.setSpan(redSpan, start, start + key.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                Bitmap imgBitmap;
                boolean isAT = key.startsWith("[@");
                String ATName = null;
                if (isAT) {
                    ATName = key.substring(1, key.length() - 1);
                    imgBitmap = str2Bitmap(ATName);
                } else {
                    imgBitmap = BitmapFactory.decodeResource(resources, str2Res(key));
                }
                ImageSpan span = new ImageSpan(getContext(), imgBitmap);
                spannableString.setSpan(span, start, start + key.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (isAT && isNeedATClick) {
                    final String finalATName = ATName;
                    ClickableSpan clickableSpan = new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {
                            L.i("ATName:" + finalATName);
                        }
                    };
                    spannableString.setSpan(clickableSpan, start, start + key.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    setMovementMethod(LinkMovementMethod.getInstance());
                }
            }
        }
        this.spannableString = spannableString;
        return spannableString;
    }

    public void notifyContent() {
        setText(getEmotionContent(getText().toString()));
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

    private Bitmap str2Bitmap(String content) {
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setAntiAlias(true);
        paint.setTextSize(getTextSize());

        Paint.FontMetrics metrics = paint.getFontMetrics();
        int height = (int) (metrics.descent - metrics.ascent);
//        Rect rect = new Rect();
//        paint.getTextBounds(content,0,content.length(), rect);
//        int height = rect.height();

        int width = (int) paint.measureText(content, 0, content.length());

        L.i("width:" + width);
        L.i("height:" + height);
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        canvas.drawText(content, 0, height - 12, paint);
        return bmp;
    }

    public void delete() {
        dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
    }

    public void at(String content) {
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
