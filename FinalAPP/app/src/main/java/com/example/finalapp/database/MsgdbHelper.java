package com.example.finalapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.example.finalapp.enity.MsgInfo;

import java.util.ArrayList;
import java.util.List;

public class MsgdbHelper extends SQLiteOpenHelper {
    private static final String DB_Classes="myapp.db";//数据库名称
    private static final int DB_Version=1;//版本号
    private static final String Table_Name="msg_table";//表明
    private static MsgdbHelper msgdbHelper=null;//单例
    private static final   String tag="recordhelper_tag";
    private SQLiteDatabase mRDB=null;//度链接
    private SQLiteDatabase mWDB =null;//写连接

    private MsgdbHelper(Context context){
        super(context,DB_Classes,null,DB_Version);
        Log.d(tag,"执行了");
    }
    public static MsgdbHelper getInstance(Context context){
        if(msgdbHelper==null){
            msgdbHelper=new MsgdbHelper(context);
        }
        return msgdbHelper;
    }


    //读连接
    public SQLiteDatabase openReadLink(){
        if(mRDB==null||!mRDB.isOpen()){
            mRDB=msgdbHelper.getReadableDatabase();
           //deleteTable(mRDB);
            createTable(mRDB);
        }
        return mRDB;
    }
    //写连接
    public SQLiteDatabase openWriteLink(){
        if(mWDB ==null||!mWDB.isOpen()){
            mWDB =msgdbHelper.getWritableDatabase();
            //deleteTable(mWDB);
            createTable(mWDB);
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

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public long insert(MsgInfo msgInfo){
        ContentValues value = new ContentValues();
        value.put("CourseId",msgInfo.getCourseid());
        value.put("RecordId",msgInfo.getRecordid());
        value.put("CourseName",msgInfo.getCoursename());
        value.put("ClassRoom",msgInfo.getClassroom());
        value.put("RecordSetTime",msgInfo.getRecordsettime());
        value.put("RecordEndTime",msgInfo.getRecordendtime());
//        value.put("RecordArrTime",msgInfo.getRecordarrtime());
        value.put("ReadFlag",msgInfo.getReadFlag());
        value.put("RecordState",msgInfo.getRecordstate());
        return mWDB.insert(Table_Name, null, value);
    }

    public List<MsgInfo> querryall(){
        List<MsgInfo> list=new ArrayList<>();
        Cursor cursor=mRDB.query(Table_Name,null,null,null,null,null,null);
        while(cursor.moveToNext()){
            String courseid=cursor.getString(0);
            Long recordid=cursor.getLong(1);
            String coursename=cursor.getString(2);
            String classroom=cursor.getString(3);
            Long recordsettime=cursor.getLong(4);
            Long recordendtime=cursor.getLong(5);
            Long recordarrtime=cursor.getLong(6);
            int readflag=cursor.getInt(7);
            int recordstate=cursor.getInt(8);
            MsgInfo msg=new MsgInfo(courseid,recordid,coursename,classroom,recordsettime,recordendtime,recordarrtime,readflag,recordstate);
            list.add(msg);
        }
        return list;
    }
    public void deleteTable(SQLiteDatabase db){
        String str="DROP TABLE IF EXISTS "+Table_Name;
        db.execSQL(str);
    }

    public int updataReadFlag(String courseid,Long recordid,int ReadFlag){
        ContentValues value=new ContentValues();
        value.put("readFlag",ReadFlag);
        return mWDB.update(Table_Name,value,"CourseId=? AND RecordId=?",new String[]{courseid,recordid+""});
    }

    private void createTable(SQLiteDatabase db){
        Log.i(tag, "记录表开始创建");
        String sql = "CREATE TABLE IF NOT EXISTS " + Table_Name + " (" +
                " CourseId VARCHAR(255) NOT NULL," +
                " RecordId BIGINT NOT NULL," +
                " CourseName VARCHAR(255) NOT NULL," +
                " ClassRoom VARCHAR(255) NOT NULL," +
                " RecordSetTime BIGINT NOT NULL," +
                " RecordEndtime BIGINT," +
                " RecordArrTime BIGINT," +
                " readFlag INT," +
                " qdFlag INT," +
                " RecordState INT," +
                " PRIMARY KEY (CourseId, RecordId)" +
                ")";
        db.execSQL(sql);
        Log.i(tag, "记录表创建成功");
    }


    public int update(MsgInfo msgInfo) {
        ContentValues values=new ContentValues();
        values.put("readFlag",msgInfo.getReadFlag());
        values.put("RecordArrTime",msgInfo.getRecordarrtime());
        values.put("RecordState",msgInfo.getRecordstate());
        return mWDB.update(Table_Name,values,"CourseId=? AND RecordId=?",new String[]{msgInfo.getCourseid(), String.valueOf(msgInfo.getRecordid())});
    }

    public void updatelist(List<MsgInfo> datas) {
        for (MsgInfo data:datas){
            ContentValues values=new ContentValues();
            if(data.getRecordarrtime()!=null){
                values.put("RecordArrTime",data.getRecordarrtime());
            }

            values.put("RecordState",data.getRecordstate());
            mWDB.update(Table_Name,values,"CourseId=? AND RecordId=?",new String[]{data.getCourseid(), String.valueOf(data.getRecordid())});
        }
    }

    public void deleteAll() {
        mWDB.delete(Table_Name,null,null);
    }
}
