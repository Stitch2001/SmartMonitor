package com.gdbjzx.smartmonitor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.avos.avoscloud.search.SearchActivity;

import java.util.List;

/**
 * Created by Administrator on 2018/8/3.
 */

public class ClassAdapter extends ArrayAdapter<mClass> {

    private int resourceId;

    public ClassAdapter(Context context, int textViewResourceId, List<mClass> objects){
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        mClass mClass = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.gradeText = (TextView) view.findViewById(R.id.grade_text);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.gradeText.setText(mClass.getGrade());
        return view;
    }

    class ViewHolder {
        TextView gradeText;
    }
}
