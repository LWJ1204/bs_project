package com.example.finalapp.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


import com.example.finalapp.enity.Point;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LocalHirbertUtil {
    public static LocalHirbertUtil mhbUtil;
    private String tag="location_tag";
    //尺寸
    private int N;
    //希尔伯特曲线阶数
    private int h;
    //隐私预算
    private double epsilon;
    private Context context;
    //希尔伯特编码
    private Map<Point, String> mp=new HashMap<>();
    private final Random r=new Random();
    SharedPreferences sharedPreferences;
    private Gson gson;
    public static LocalHirbertUtil getInstance(Context context, int Nsize, double epsilon){
        if(mhbUtil==null){
            mhbUtil=new LocalHirbertUtil( context,Nsize,epsilon);
        }
        return mhbUtil;
    }

    public LocalHirbertUtil(Context context,int N, double epsilon){
        sharedPreferences=context.getSharedPreferences("hirbert",Context.MODE_PRIVATE);
        this.context=context;
        this.N=N;
        this.epsilon=epsilon;
        this.h=(int)Math.ceil(Math.log(this.N) / Math.log(2));//得到阶数
        gson = new Gson();
        if(sharedPreferences.getAll().isEmpty()){
            initCode();
            SharedPreferences.Editor editor = sharedPreferences.edit();

            // 遍历 Map 并将每个键值对存入 SharedPreferences
            for (Map.Entry<Point, String> entry : mp.entrySet()) {
                editor.putString(gson.toJson(entry.getKey()),entry.getValue());
            }
            editor.apply();
        }
    }

    public String applyPrivacy(Point point){
        String code=sharedPreferences.getString(gson.toJson(point),"default");
        if(code.equals("default")) return  null;
        char []bits=code.toCharArray();
        //随机扰动位置
        int pos=r.nextInt(bits.length);
        double p=Math.exp(epsilon)/(1+Math.exp(epsilon));//扰动概率
        if(bits[pos]=='1'){
            if(r.nextDouble()>p){
                bits[pos]='0';
            }
        }else{
            if (r.nextDouble()<1/(1+Math.exp(epsilon))){
                bits[pos]='1';
            }
        }
        return new String(bits);
    }

    public void initCode(){
        //为每个格编码
        for (int i = 0; i < this.N; i++) {
            for (int j = 0; j < this.N; j++) {
                Point tpoint=new Point(i,j);
                String code=getCode(tpoint);
                mp.put(tpoint,code);
            }
        }
    }

    private String getCode(Point p){
        StringBuilder str= new StringBuilder();
        double x=p.x;
        double y=p.y;
        int w=(int)Math.pow(2,this.h-1);
        for (int i = 0; i < this.h; i++,w/=2) {

            int qx=getQ(x,i);
            int qy=getQ(y,i);

            if(qx==0&&qy==0){
                //Q=0  第一象限
                double tempx=x;
                x=y;
                y=tempx;
                str.append("00");
            }else if(qx==0&&qy==1){
                //Q=1 第二象限
                y=y-w;
                str.append("01");
            }else if(qx==1&&qy==1){
                //Q=2 第三象限
                x=x-w;
                y=y-w;
                str.append("10");
            }else if(qx==1&&qy==0){
                //Q=3 第四象限
                x=w-y-1;
                y=w*2-y-1;
                str.append("11");
            }
        }

        return str.toString();

    }
    public int getQ(double x,int h) {
        int w=this.N/2;
        for (int i = 0; i < h; i++) {

            x%=w;
            w/=2;
        }
        return (int)(x/w);
    }

    public void show(){
        for (Point point : mp.keySet()) {
           Log.i(tag,"x:"+point.x+" "+"y:"+point.y);
            Log.i(tag,mp.get(point).toString());
        }
    }

}
