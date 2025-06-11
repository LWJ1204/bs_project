package com.example.finalapp.Utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

public class YzmCodeGen {
    private List<Character>chars;
    private static YzmCodeGen yzmgen=null;
    private final String tag="yzm_tag";

    private int mLeft;
    private int mTop;
    private StringBuilder mBuilder=new StringBuilder();
    private Random mr=new Random();

    private static final int Code_Len=4;//验证码长度
    private static final int Font_Size=100;//验证码字体大小
    private static final int Base_Padding_Left=40;//
    private static final int Base_Padding_Top=90;//
    private static final int Base_Range_Left=20;
    private static final int Base_Range_Top=10;//
    private static final int Deafault_Width=300;
    private static final int Deafault_Height=200;
    private static final int Line_Num=3;//验证码线条数
    private static final int Deafault_Color=0xDF;

    private String code;

    public static YzmCodeGen getInstance(){
        if(yzmgen==null){
            yzmgen= new YzmCodeGen();
        }
        return yzmgen;
    }

    private YzmCodeGen(){
        chars=new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            chars.add((char)('0'+i));
        }
        for (int i = 0; i < 26; i++) {
            chars.add((char)('a'+i));
            chars.add((char)('A'+i));
        }
        Log.i(tag,chars.toString());
    }

    public Bitmap CreateCode(){
        mLeft = 0; //每次生成验证码图片时初始化
        mTop = 0;

        Bitmap bitmap = Bitmap.createBitmap(Deafault_Width,Deafault_Height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        code = createCode();

        canvas.drawColor(Color.rgb(Deafault_Color,Deafault_Color,Deafault_Color));
        Paint paint = new Paint();
        paint.setTextSize(Font_Size);

        for (int i = 0; i < code.length(); i++) {
            randomTextStyle(paint);
            randomPadding();
            canvas.drawText(code.charAt(i) + "", mLeft, mTop, paint);
        }

        //干扰线
        for (int i = 0; i < Line_Num; i++) {
            drawLine(canvas, paint);
        }

        canvas.save();//保存
        canvas.restore();
        return bitmap;

    }

    private void drawLine(Canvas canvas, Paint paint) {
        int color = randomColor();
        int startX = mr.nextInt(Deafault_Width);
        int startY = mr.nextInt(Deafault_Height);
        int stopX = mr.nextInt(Deafault_Width);
        int stopY = mr.nextInt(Deafault_Height);
        paint.setStrokeWidth(1);
        paint.setColor(color);
        canvas.drawLine(startX, startY, stopX, stopY, paint);
    }
    //随机颜色
    private int randomColor() {
        mBuilder.delete(0, mBuilder.length()); //使用之前首先清空内容
        String haxString;
        for (int i = 0; i < 3; i++) {
            haxString = Integer.toHexString(mr.nextInt(0xFF));
            if (haxString.length() == 1) {
                haxString = "0" + haxString;
            }

            mBuilder.append(haxString);
        }

        return Color.parseColor("#" + mBuilder.toString());
    }

    //生成验证码
    private String createCode() {
        mBuilder.delete(0,mBuilder.length());
        for (int i = 0; i < Code_Len; i++) {
            mBuilder.append(chars.get(mr.nextInt(chars.size())));
        }
        return mBuilder.toString();
    }
    //返回生成验证码
    public String getCode(){
        return code;
    }
    //随机文本样式
    private void randomTextStyle(Paint paint) {
        int color = randomColor();
        paint.setColor(color);
        paint.setFakeBoldText(mr.nextBoolean());  //true为粗体，false为非粗体
        float skewX = mr.nextInt(11) / 10;
        skewX = mr.nextBoolean() ? skewX : -skewX;
        paint.setTextSkewX(skewX); //float类型参数，负数表示右斜，整数左斜
//        paint.setUnderlineText(true); //true为下划线，false为非下划线
//        paint.setStrikeThruText(true); //true为删除线，false为非删除线
    }

    //随机间距
    private void randomPadding() {
        mLeft += Base_Padding_Left + mr.nextInt(Base_Range_Left);
        mTop= Base_Padding_Top +mr.nextInt(Base_Range_Top);
    }

}
