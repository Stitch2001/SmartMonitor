package com.gdbjzx.smartmonitor;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2018/8/3.
 */

public class SetRegulationAdapter extends RecyclerView.Adapter<SetRegulationAdapter.ViewHolder> {

    private List<mClass> mClassList;

    private mClass aClass;

    private SetRegulationActivity context;

    private static final int SENIOR_1 = 0;
    private static final int SENIOR_2 = 1;
    private static final int SENIOR_3 = 2;
    private static final int JUNIOR_1 = 3;
    private static final int JUNIOR_2 = 4;
    private static final int JUNIOR_3 = 5;

    private static final int[] LIGHT_IMAGE_ID = {R.drawable.class_light_1,R.drawable.class_light_2,R.drawable.class_light_3,
            R.drawable.class_light_4,R.drawable.class_light_5,R.drawable.class_light_6,R.drawable.class_light_7,
            R.drawable.class_light_8,R.drawable.class_light_9,R.drawable.class_light_10,R.drawable.class_light_11,
            R.drawable.class_light_12,R.drawable.class_light_13,R.drawable.class_light_14,R.drawable.class_light_15,
            R.drawable.class_light_16,R.drawable.class_light_17,R.drawable.class_light_18};

    private static final int[] IMAGE_ID = {R.drawable.class_1,R.drawable.class_2,R.drawable.class_3,
            R.drawable.class_4,R.drawable.class_5,R.drawable.class_6,R.drawable.class_7,
            R.drawable.class_8,R.drawable.class_9,R.drawable.class_10,R.drawable.class_11,
            R.drawable.class_12,R.drawable.class_13,R.drawable.class_14,R.drawable.class_15,
            R.drawable.class_16,R.drawable.class_17,R.drawable.class_18};

    private int grade,classroom,max,currentNum,count = 0;

    private boolean isReadMax = false;

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView gradeText;
        ImageView[] classroomView = new ImageView[19];

        public ViewHolder(View itemView) {
            super(itemView);
            gradeText = (TextView) itemView.findViewById(R.id.grade_text);
            classroomView[1] = (ImageView) itemView.findViewById(R.id.class_1);
            classroomView[2] = (ImageView) itemView.findViewById(R.id.class_2);
            classroomView[3] = (ImageView) itemView.findViewById(R.id.class_3);
            classroomView[4] = (ImageView) itemView.findViewById(R.id.class_4);
            classroomView[5] = (ImageView) itemView.findViewById(R.id.class_5);
            classroomView[6] = (ImageView) itemView.findViewById(R.id.class_6);
            classroomView[7] = (ImageView) itemView.findViewById(R.id.class_7);
            classroomView[8] = (ImageView) itemView.findViewById(R.id.class_8);
            classroomView[9] = (ImageView) itemView.findViewById(R.id.class_9);
            classroomView[10] = (ImageView) itemView.findViewById(R.id.class_10);
            classroomView[11] = (ImageView) itemView.findViewById(R.id.class_11);
            classroomView[12] = (ImageView) itemView.findViewById(R.id.class_12);
            classroomView[13] = (ImageView) itemView.findViewById(R.id.class_13);
            classroomView[14] = (ImageView) itemView.findViewById(R.id.class_14);
            classroomView[15] = (ImageView) itemView.findViewById(R.id.class_15);
            classroomView[16] = (ImageView) itemView.findViewById(R.id.class_16);
            classroomView[17] = (ImageView) itemView.findViewById(R.id.class_17);
            classroomView[18] = (ImageView) itemView.findViewById(R.id.class_18);
        }
    }

    public SetRegulationAdapter(List<mClass> classList, SetRegulationActivity context){
        mClassList = classList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /*加载布局*/
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.class_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        if (!isReadMax){
            max = mClassList.get(mClassList.size()-1).getMax();
            isReadMax = true;
        }//读取最大值
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        aClass = mClassList.get(position);
        switch (aClass.getGrade()){
            case SENIOR_1:holder.gradeText.setText("高一");break;
            case SENIOR_2:holder.gradeText.setText("高二");break;
            case SENIOR_3:holder.gradeText.setText("高三");break;
            case JUNIOR_1:holder.gradeText.setText("初一");break;
            case JUNIOR_2:holder.gradeText.setText("初二");break;
            case JUNIOR_3:holder.gradeText.setText("初三");break;
        }
        int i = 1;
        for (classroom = 1;classroom <= 18;classroom++){
            if (aClass.getClassroomBool(classroom)){
                holder.classroomView[i].setVisibility(View.VISIBLE);//显示该班级
                holder.classroomView[i].setImageResource(aClass.getImageId(classroom));
                if (aClass.getBadge(i) == null) aClass.setBadge(i,holder.classroomView[i],aClass.getArray(classroom));
                i++;
            }
        }
        /*隐藏该年级中的其他控件，以腾出空间*/
        if (i <= 6) {
            for (int j = 6;j >= i;j--) holder.classroomView[j].setVisibility(View.INVISIBLE);
            i = 7;
        }
        else if (i <= 12) {
            for (int j = 12;j >= i;j--) holder.classroomView[j].setVisibility(View.INVISIBLE);
            if (i != 7) i = 13;
        }
        else if (i <= 18) {
            for (int j = 18;j >= i;j--) holder.classroomView[j].setVisibility(View.INVISIBLE);
            if (i != 13 )i = 19;//只GONE掉整一行都是INVISIBLE的控件，以保持界面结构
        }
        for (int j = 18;j >= i;j--) {
            holder.classroomView[j].setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mClassList.size();
    }

}
