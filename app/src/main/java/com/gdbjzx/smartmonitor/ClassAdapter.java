package com.gdbjzx.smartmonitor;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2018/8/3.
 */

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ViewHolder> {

    private List<mClass> mClassList;

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView gradeText;

        public ViewHolder(View itemView) {
            super(itemView);
            gradeText = (TextView) itemView.findViewById(R.id.grade_text);
        }
    }

    public ClassAdapter(List<mClass> classList){
        mClassList = classList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /*加载布局*/
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.class_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
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
