package com.example.finalapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.example.finalapp.enity.ClassInfo;

import java.util.ArrayList;
import java.util.List;


public class ClassdbHelper extends SQLiteOpenHelper {

    private static final String DB_Classes="myapp.db";//数据库名称
    private static final int DB_Version=1;//版本号
    private static final String Table_Name="class_table";//表明
    private static ClassdbHelper classhelper=null;//单例
    private static final   String tag="class_tag";
    private SQLiteDatabase mRDB=null;//度链接
    private SQLiteDatabase mWDB =null;//写连接


    private ClassdbHelper(Context context){
        super(context,DB_Classes,null,DB_Version);
        checkAndCreateTable();
        Log.d(tag,"执行了");
    }

    public static ClassdbHelper getInstance(Context context){
        if(classhelper==null){
            classhelper=new ClassdbHelper(context);
        }
        return classhelper;
    }

    //读连接
    public SQLiteDatabase openReadLink(){
        if(mRDB==null||!mRDB.isOpen()){
            mRDB=classhelper.getReadableDatabase();
        }
        return mRDB;
    }
    //写连接
    public SQLiteDatabase openWriteLink(){
        if(mWDB ==null||!mWDB.isOpen()){
            mWDB =classhelper.getWritableDatabase();
        }
        return mWDB;
    }

    //关闭连接
    public void closeLink(){
        if(mRDB!=null&&mRDB.isOpen()){
            mRDB.close();
            mRDB=null;
        }

        if(mWDB !=null&& mWDB.isOpen()){
            mWDB.close();
            mWDB =null;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(tag, "开始创建表");
        String sql = "CREATE TABLE IF NOT EXISTS " + Table_Name + " (" +
                "class_name TEXT NOT NULL, " +
                "class_id TEXT NOT NULL, " +
                "class_room TEXT NOT NULL, " +
                "class_teacher TEXT NOT NULL, " +
                "class_value REAL NOT NULL, " +
                "class_finish TEXT NOT NULL, " +
                "class_time TEXT NOT NULL, " +
                "class_year TEXT NOT NULL, " +
                "class_semester INTEGER NOT NULL, " +
                "class_academy TEXT NOT NULL, " +
                "PRIMARY KEY (class_id)" +
                ")";
        db.execSQL(sql);
        Log.d(tag, "表创建成功");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public long insert(ClassInfo classinfo){
        Log.i(tag,Table_Name+"开始插入数据");
        ContentValues values=new ContentValues();
        values.put("class_name",classinfo.getClass_name());
        values.put("class_id",classinfo.getClass_id());
        values.put("class_room",classinfo.getClass_room());
        values.put("class_teacher",classinfo.getClass_teacher());
        values.put("class_value",classinfo.getClass_value());
        values.put("class_finish",classinfo.getClass_finish());
        values.put("class_time",classinfo.getClass_time());
        values.put("class_year",classinfo.getClass_year());
        values.put("class_semester",classinfo.getClass_semester());
        values.put("class_academy",classinfo.getAcademy());
        return mWDB.insert(Table_Name,null,values);
    }

    public boolean insertlist(List<ClassInfo> list){
        Log.i(tag,"插入课程列表");
        boolean isok=true;
        for (int i = 0; i < list.size(); i++) {
            long index= insert(list.get(i));
            if(index<=0) isok=false;
        }
        return isok;
    }

    public List<ClassInfo>queryall(){
        List<ClassInfo> classes=new ArrayList<>();
        Cursor cursor=mRDB.query(Table_Name,null,null,null,null,null,null);
        while (cursor.moveToNext()){
            String cname=cursor.getString(0);
            String cid=cursor.getString(1);
            String croom=cursor.getString(2);
            String cteacher=cursor.getString(3);
            Double cvalue=cursor.getDouble(4);
            String cfinish=cursor.getString(5);
            String ctime=cursor.getString(6);
            String cyear=cursor.getString(7);
            Integer csemester=cursor.getInt(8);
            String cacademy=cursor.getString(9);
            classes.add(new ClassInfo(cname,cid,croom,cteacher,cvalue,cfinish,ctime,cyear,csemester,cacademy));
        }
        return classes;
    }

    //删除
    public boolean deleteByid(String classid){
       int isok= mWDB.delete(Table_Name,"class_id=?",new String[]{classid});
       if(isok>0) {
           Log.i(tag,"成功删除："+isok);
           return true;
       }
       Log.i(tag,"发生错误："+isok);
       return false;
    }

    //全部删除
    public void deleteAll(){
        mWDB.delete(Table_Name,null,null);
    }
    public void checkAndCreateTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=?", new String[]{Table_Name});
        if (!cursor.moveToFirst()) {
            Log.d(tag, "表不存在，正在创建...");
            onCreate(db);
        } else {
            Log.d(tag, "表已存在");
        }
        cursor.close();
    }
}
