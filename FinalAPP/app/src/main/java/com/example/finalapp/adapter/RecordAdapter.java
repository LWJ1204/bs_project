package com.example.finalapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalapp.R;
import com.example.finalapp.enity.MsgInfo;

import java.util.List;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordHolder> {
    private List<MsgInfo> msgs;
    private final Context context;

    public RecordAdapter(Context context,List<MsgInfo>msgs){
        this.context=context;
        this.msgs=msgs;
    }
    @NonNull
    @Override
    public RecordHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemview=View.inflate(context, R.layout.item_record,null);
        return new RecordHolder(itemview);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordHolder holder, int position) {
        MsgInfo msgInfo=msgs.get(position);
        holder.msgTime.setText(msgInfo.formattedDate(msgInfo.getRecordsettime()));
        holder.msgClassnanme.setText(msgInfo.getCoursename());
        if(msgInfo.getReadFlag()==0){
            holder.setState.setText("未读");
            holder.setState.setTextColor(Color.RED);
        }else{
            holder.setState.setText("已读");
            holder.setState.setTextColor(Color.GREEN);
        }
    }

    @Override
    public int getItemCount() {
        return msgs.size();
    }

    public void setDates(List<MsgInfo> datas) {
        msgs=datas;
    }

    class RecordHolder extends RecyclerView.ViewHolder{
        private TextView setState;
        private TextView msgClassnanme;
        private TextView msgTime;

        public RecordHolder(@NonNull View itemView) {
            super(itemView);
            setState=itemView.findViewById(R.id.read_state);
            msgClassnanme=itemView.findViewById(R.id.message_classname);
            msgTime=itemView.findViewById(R.id.message_time);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onItemClickListener!=null){
                        try {
                            onItemClickListener.onItemClick(view,msgs.get(getLayoutPosition()));
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
        }
    }
    /*
   view:点击item的视图
   data：点击得到的数据
   */
    public interface OnItemClickListener{
        public void onItemClick(View view, MsgInfo data) throws InterruptedException;
    }

    private RecordAdapter.OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(RecordAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
