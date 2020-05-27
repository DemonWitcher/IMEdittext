package com.witcher.imedittext;

import android.content.Context;
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

/**
 * 1. 选择一个人@  @部分整体删除 点击
 * 2  选择一个话题#话题#，#话题#部分整体删除 点击 FullTpoic
 * 3  通过识别输入内容 识别出#话题# 并且高亮，话题内容可以选中修改 InputTopic
 * 4  输入一个表情
 * <p>
 * 和IMEditText 区别在于 这个类可以在手输话题中添加表情，@人，选择话题等
 */
public class IMEditText2 extends AppCompatEditText {

    private boolean isNeedATClick = true;//@人是否需要点击事件
    //对应2 是否识别手动输入修改的#话题#
    private boolean isNeedInputTopic = true;

    private Paint mAtPaint, mFullTopicPaint;


    public IMEditText2(Context context) {
        super(context);
        init();
    }

    public IMEditText2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IMEditText2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mAtPaint = new Paint();
        mAtPaint.setColor(Color.BLUE);
        mAtPaint.setAntiAlias(true);
        mAtPaint.setTextSize(getTextSize());

        mFullTopicPaint = new Paint();
        mFullTopicPaint.setColor(Color.GREEN);
        mFullTopicPaint.setAntiAlias(true);
        mFullTopicPaint.setTextSize(getTextSize());

        if (isNeedInputTopic) {
            addTextChangedListener(new TextWatcher() {
                String lastContent;
                int index;
                int offset;

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    offset = 0;
                    lastContent = s.toString();
                    index = getSelectionEnd();
                    offset = offset + after;
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
    }

    private SpannableString getEmotionContent(String content) {
        SpannableString spannableString = new SpannableString(content);
            String regexEmotionInputTopic =  "\\u0023+(\\w|!|！|\\?|？|$|￥|-|=|\\+|\\*|、|/|,|，|\\.|。|<|>|;|:|：|'|\"|【|】|\\&|^|%|~|`|·|……|～|@|\\[|\\])+\\u0023";
        //u4e00-u9fa5是基本中文区间  u0040是"@"符号  u0023是#号
        Pattern patternEmotionInputTopic = Pattern.compile(regexEmotionInputTopic);
        Matcher matcherEmotionInputTopic = patternEmotionInputTopic.matcher(spannableString);

        while (matcherEmotionInputTopic.find()) {
            String key = matcherEmotionInputTopic.group();
            int start = matcherEmotionInputTopic.start();
            if (isNeedInputTopic && key.startsWith("#") && key.endsWith("#")) {
                handlerInputTopic(spannableString,key,start);
            }
        }

        String regexEmotionOther = "\\[([(\u4e00-\u9fa5)|" +
                "(\\u0040\\w)|" +
                "(\\u0023\\w\\u0023)])+]";
        Pattern patternEmotionOther = Pattern.compile(regexEmotionOther);
        Matcher matcherEmotionOther = patternEmotionOther.matcher(spannableString);

        while (matcherEmotionOther.find()) {
            String key = matcherEmotionOther.group();
            int start = matcherEmotionOther.start();
            if (key.startsWith("[@")) {
                handlerAt(spannableString,key, start);
            } else if (key.startsWith("[#") && key.endsWith("#]")) {
                handlerFullTopic(spannableString,key, start);
            } else {
                handlerEmoji(spannableString,key, start);
            }
        }
        return spannableString;
    }


    private void handlerAt(SpannableString spannableString, String key, int start) {
        Bitmap imgBitmap;
        String ATName = key.substring(1, key.length() - 1);
        imgBitmap = str2Bitmap(ATName, mAtPaint);
        ImageSpan span = new ImageSpan(getContext(), imgBitmap);
        spannableString.setSpan(span, start, start + key.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (isNeedATClick) {
            final String finalATName = ATName;
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    L.i("@的名字:" + finalATName);
                }
            };
            spannableString.setSpan(clickableSpan, start, start + key.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    private void handlerFullTopic(SpannableString spannableString, String key, int start) {
        Bitmap imgBitmap;
        String topic = key.substring(1, key.length() - 1);
        imgBitmap = str2Bitmap(topic, mFullTopicPaint);
        ImageSpan span = new ImageSpan(getContext(), imgBitmap);
        spannableString.setSpan(span, start, start + key.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        final String finalTopic = topic;
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                L.i("话题:" + finalTopic);
            }
        };
        spannableString.setSpan(clickableSpan, start, start + key.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void handlerInputTopic(SpannableString spannableString, String key, int start) {
        ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.RED);
        spannableString.setSpan(redSpan, start, start + key.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void handlerEmoji(SpannableString spannableString, String key, int start) {
        Bitmap imgBitmap = BitmapFactory.decodeResource(getResources(), StrToResUtil.str2Res(key));
        ImageSpan span = new ImageSpan(getContext(), imgBitmap);
        spannableString.setSpan(span, start, start + key.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void notifyContent() {
        setText(getEmotionContent(getText().toString()));
    }

    private Bitmap str2Bitmap(String content, Paint paint) {
        Paint.FontMetrics metrics = paint.getFontMetrics();
        int height = (int) (metrics.descent - metrics.ascent);
        int width = (int) paint.measureText(content, 0, content.length());
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        canvas.drawText(content, 0, height - 12, paint);
        return bmp;
    }

    public void delete() {
        dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
    }

    public void addIcon(String source) {
        //改成在下标位置添加
        int index = getSelectionEnd();
        StringBuilder sb = new StringBuilder(getText().toString());
        sb.insert(index, source);
        setText(getEmotionContent(sb.toString()));
        setSelection(index + source.length());
    }

    public void at(String content) {
        addIcon(content);
    }

    //对应3 加一个整体的话题 整体删除和点击
    public void fullTopic(String content) {
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
