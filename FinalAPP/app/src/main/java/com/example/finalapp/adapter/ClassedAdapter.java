package com.example.finalapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;


import com.example.finalapp.R;
import com.example.finalapp.enity.ClassInfo;

import java.util.ArrayList;
import java.util.List;

public class ClassedAdapter extends RecyclerView.Adapter<ClassedAdapter.classholder> {

    private final Context context;
    private List<ClassInfo>datas;
    private String tag="classAdaptertag";
    public ClassedAdapter(Context context, List<ClassInfo>datas){
        this.context=context;
        this.datas=datas;

    }
    //创建view和viewhodler
    @NonNull
    @Override
    public classholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemview=View.inflate(context, R.layout.item_class,null);
        return new classholder(itemview);
    }

    //绑定数据
    @Override
    public void onBindViewHolder(@NonNull classholder holder, int position) {
            ClassInfo classinfo=datas.get(position);
            holder.item_class_name.setText(classinfo.getClass_name());
            holder.item_teacher_name.setText(classinfo.getClass_teacher());
    }

    //得到总条数
    @Override
    public int getItemCount() {
        return datas != null ? datas.size() : 0;
    }

    class classholder extends RecyclerView.ViewHolder {
        private TextView item_class_name;
        private TextView item_teacher_name;
        public classholder(@NonNull View itemView) {
            super(itemView);
            item_class_name=itemView.findViewById(R.id.item_class_name);
            item_teacher_name=itemView.findViewById(R.id.item_class_teacher);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onItemClickListener!=null){
                        onItemClickListener.onItemClick(view,datas.get(getLayoutPosition()));
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
       void onItemClick(View view,ClassInfo data);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
