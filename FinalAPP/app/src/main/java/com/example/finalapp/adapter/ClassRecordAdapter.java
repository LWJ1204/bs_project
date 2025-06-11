package com.example.finalapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalapp.R;
import com.example.finalapp.enity.ClassInfo;
import com.example.finalapp.enity.MsgInfo;

import java.util.List;

public class ClassRecordAdapter extends RecyclerView.Adapter<ClassRecordAdapter.ClassRecordHolder>{
    private List<MsgInfo> msgs;
    private final Context context;
    private ClassInfo classinfo;
    private String tag="recordadapter_tag";
    public ClassRecordAdapter(Context context, ClassInfo classInfo, List<MsgInfo>msgs){
        this.context=context;
        this.classinfo=classInfo;
        this.msgs=msgs;
    }

    @NonNull
    @Override
    public ClassRecordHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemview=View.inflate(context, R.layout.item_classrecord,null);
        return new ClassRecordAdapter.ClassRecordHolder(itemview);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassRecordHolder holder, int position) {
        MsgInfo msgInfo=msgs.get(position);
        holder.itemSetCourse.setText(classinfo.getClass_name());
        holder.itemSetTime.setText(msgInfo.formattedDate(msgInfo.getRecordarrtime()));
        if(msgInfo.getRecordstate()==1){
            holder.itemSetState.setText("未签到");
            holder.itemSetState.setTextColor(Color.RED);
        }else if(msgInfo.getRecordstate()==0){
            holder.itemSetState.setTextColor(Color.GREEN);
            holder.itemSetState.setText("已签到");
        } else if (msgInfo.getRecordstate()==3) {
            holder.itemSetState.setText("病假");
            holder.itemSetState.setTextColor(Color.BLUE);
        }
    }

    @Override
    public int getItemCount() {
        return msgs.size();
    }

    public void setDate(List<MsgInfo> datas) {
        this.msgs=datas;
    }

    class ClassRecordHolder extends RecyclerView.ViewHolder{
        private TextView itemSetCourse;
        private TextView itemSetTime;
        private TextView itemSetState;
        public ClassRecordHolder(@NonNull View itemView) {
            super(itemView);
            itemSetCourse=itemView.findViewById(R.id.classrecord_coursename);
            itemSetTime=itemView.findViewById(R.id.classrecord_settime);
            itemSetState=itemView.findViewById(R.id.classrecord_state);
        }
    }
}
