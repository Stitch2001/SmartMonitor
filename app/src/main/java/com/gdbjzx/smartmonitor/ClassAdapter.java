package com.gdbjzx.smartmonitor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.search.SearchActivity;

import java.util.List;

/**
 * Created by Administrator on 2018/8/3.
 */

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ViewHolder> {

    private List<mClass> mClassList;

    static class ViewHolder extends RecyclerView.ViewHolder{

        View classView;
        ImageView classImage1,classImage2,classImage3,classImage4,classImage5,classImage6,
                classImage7,classImage8,classImage9,classImage10,classImage11,classImage12,
                classImage13,classImage14,classImage15,classImage16,classImage17,classImage18;
        TextView gradeText;

        public ViewHolder(View itemView) {
            super(itemView);
            classView = itemView;
            classImage1 = (ImageView) itemView.findViewById(R.id.class_1);
            classImage2 = (ImageView) itemView.findViewById(R.id.class_2);
            classImage3 = (ImageView) itemView.findViewById(R.id.class_3);
            classImage4 = (ImageView) itemView.findViewById(R.id.class_4);
            classImage5 = (ImageView) itemView.findViewById(R.id.class_5);
            classImage6 = (ImageView) itemView.findViewById(R.id.class_6);
            classImage7 = (ImageView) itemView.findViewById(R.id.class_7);
            classImage8 = (ImageView) itemView.findViewById(R.id.class_8);
            classImage9 = (ImageView) itemView.findViewById(R.id.class_9);
            classImage10 = (ImageView) itemView.findViewById(R.id.class_10);
            classImage11 = (ImageView) itemView.findViewById(R.id.class_11);
            classImage12 = (ImageView) itemView.findViewById(R.id.class_12);
            classImage13 = (ImageView) itemView.findViewById(R.id.class_13);
            classImage14 = (ImageView) itemView.findViewById(R.id.class_14);
            classImage15 = (ImageView) itemView.findViewById(R.id.class_15);
            classImage16 = (ImageView) itemView.findViewById(R.id.class_16);
            classImage17 = (ImageView) itemView.findViewById(R.id.class_17);
            classImage18 = (ImageView) itemView.findViewById(R.id.class_18);
            gradeText = (TextView) itemView.findViewById(R.id.grade_text);
        }
    }

    public ClassAdapter(List<mClass> classList){
        mClassList = classList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.class_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.classImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                mClass mClass = mClassList.get(position);
                holder.classImage1.setImageResource(R.drawable.class_light_1);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mClass mClass = mClassList.get(position);
        holder.gradeText.setText(mClass.getGrade());
    }

    @Override
    public int getItemCount() {
        return mClassList.size();
    }

}
