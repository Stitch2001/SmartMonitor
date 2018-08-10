package com.gdbjzx.smartmonitor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Administrator on 2018/8/3.
 */

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ViewHolder> {

    private List<mClass> mClassList;

    private mClass aClass;

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

    private int grade,classroom,max,currentNum = 0;//用作循环变量

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

    public ClassAdapter(List<mClass> classList){
        mClassList = classList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /*加载布局*/
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.class_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        initOnClickListener(holder);
        if (!isReadMax){
            max = mClassList.get(1).getMax();
            isReadMax = true;
        }//读取最大值
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        aClass = mClassList.get(position);
        holder.gradeText.setText(aClass.getGrade());

        for (classroom = 1;classroom<=18;classroom++){
            holder.classroomView[classroom].setImageResource(aClass.getImageId(classroom));
            if (aClass.getClassArray(classroom) != 0){
                holder.classroomView[classroom].setImageResource(LIGHT_IMAGE_ID[classroom-1]);
                aClass.setImageId(classroom,LIGHT_IMAGE_ID[classroom-1]);
                if (aClass.getBadge(classroom) == null){
                    aClass.setBadge(classroom,holder.classroomView[classroom],aClass.getClassArray(classroom));
                } else {
                    aClass.setBadgeText(classroom,aClass.getClassArray(classroom));
                }
            } else if (aClass.getBadge(classroom) != null){
                aClass.hideBadge(classroom);
                aClass.deleteBadge(classroom);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mClassList.size();
    }

    private void initOnClickListener(final ViewHolder holder){
        for (classroom = 1;classroom<=18;classroom++){
            holder.classroomView[classroom].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    grade = holder.getAdapterPosition();
                    switch (view.getId()){
                        case R.id.class_1:classroom = 1;break;
                        case R.id.class_2:classroom = 2;break;
                        case R.id.class_3:classroom = 3;break;
                        case R.id.class_4:classroom = 4;break;
                        case R.id.class_5:classroom = 5;break;
                        case R.id.class_6:classroom = 6;break;
                        case R.id.class_7:classroom = 7;break;
                        case R.id.class_8:classroom = 8;break;
                        case R.id.class_9:classroom = 9;break;
                        case R.id.class_10:classroom = 10;break;
                        case R.id.class_11:classroom = 11;break;
                        case R.id.class_12:classroom = 12;break;
                        case R.id.class_13:classroom = 13;break;
                        case R.id.class_14:classroom = 14;break;
                        case R.id.class_15:classroom = 15;break;
                        case R.id.class_16:classroom = 16;break;
                        case R.id.class_17:classroom = 17;break;
                        case R.id.class_18:classroom = 18;break;
                        default:break;
                    }
                    aClass = mClassList.get(grade);
                    aClass.setImageId(classroom,LIGHT_IMAGE_ID[classroom-1]);//见定义，数组从0开始，故classroom-1
                    setOnClickMethod(grade,classroom,(ImageView)view);
                    notifyDataSetChanged();
                }
            });
        }
    }

    private void setOnClickMethod(int grade,int classroom,ImageView view){
        if (aClass.getClassArray(classroom) == 0){
            max++;aClass.setClassArray(classroom,max);//将此班级置于队列末尾
            //renewButtonImage();
        } else {
            if (aClass.getBadge(classroom) != null){
                aClass.hideBadge(classroom);
            }//隐藏角标
            aClass.setImageId(classroom,IMAGE_ID[classroom-1]);//见定义，数组从0开始，故classroom-1
            renewList(grade,classroom);
            //renewButtonImage();
        }
    }

    private void renewButtonImage(){
        for (grade = SENIOR_1;grade<=JUNIOR_3;grade++){
            aClass = mClassList.get(grade);
            for (classroom = 1;classroom<=18;classroom++){
                if (aClass.getClassArray(classroom) != 0){
                    //if (aClass.getBadge(classroom) == null){
                    //    aClass.setImageId(classroom,LIGHT_IMAGE_ID[classroom-1]);
                        //aClass.setBadge(classroom,aClass.getClassArray(classroom));
                    //} else {
                        aClass.setImageId(classroom,LIGHT_IMAGE_ID[classroom-1]);
                        aClass.setBadgeText(classroom,aClass.getClassArray(classroom));
                    //}
                }
            }
        }
        notifyDataSetChanged();
    }

    /*对检查顺序表重新排序并实时保存*/
    private void renewList(int currentGrade,int currentClassroom){
        aClass = mClassList.get(currentGrade);
        currentNum = aClass.getClassArray(currentClassroom);
        max--;aClass.setClassArray(currentClassroom,0);
        for (grade = SENIOR_1;grade <= JUNIOR_3;grade++){
            aClass = mClassList.get(grade);
            for (classroom = 1;classroom <= 18;classroom++){
                if (aClass.getClassArray(classroom) >= currentNum)
                    aClass.setClassArray(classroom,aClass.getClassArray(classroom)-1);
            }
        }
    }


}
