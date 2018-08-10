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

    private static final int SENIOR_1 = 0;
    private static final int SENIOR_2 = 1;
    private static final int SENIOR_3 = 2;
    private static final int JUNIOR_1 = 3;
    private static final int JUNIOR_2 = 4;
    private static final int JUNIOR_3 = 5;

    private int[][] classArray = new int[6][19];

    private Badge[][] badges = new Badge[6][19];

    private int grade,classroom,max,currentNum;//用作循环变量

    private int viewId,lightImageId;

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

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public ClassAdapter(List<mClass> classList){
        mClassList = classList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /*加载布局*/
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.class_item,parent,false);
        ViewHolder holder = new ViewHolder(view);

        /*读取检查顺序
        max = 0;
        SharedPreferences pref = MyApplication.getContext().getSharedPreferences("RegulationData",MODE_PRIVATE);
        for (grade = SENIOR_1;grade <= JUNIOR_3;grade++){
            for (classroom = 1;classroom <= 18;classroom++){
                if (pref.getInt(grade+""+classroom+"",0) != 0){
                    classArray[grade][classroom] = pref.getInt(grade+""+classroom+"",0);
                    if (max < classArray[grade][classroom]) max = classArray[grade][classroom];
                }
            }
        }*/

        //initOnClickListener(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mClass mClass = mClassList.get(position);
        holder.gradeText.setText(mClass.getGrade());
        holder.gradeText.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mClassList.size();
    }

/*    private void initOnClickListener(final ViewHolder holder){
        holder.classroomView[1].findViewWithTag(SENIOR_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView = (ImageView) classroom.findViewById(R.id.class_1);
                holder.classroomView[1].findViewWithTag(SENIOR_1).setImageResource(R.drawable.class_light_1);
                setOnClickMethod(SENIOR_1,1,R.drawable.class_1);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_2);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_1);
                imageView = (ImageView) currentView.findViewById(R.id.class_2);
                imageView.setImageResource(R.drawable.class_light_2);
                setOnClickMethod(SENIOR_1,2,R.drawable.class_2);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_3);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_1);
                imageView = (ImageView) currentView.findViewById(R.id.class_3);
                imageView.setImageResource(R.drawable.class_light_3);
                setOnClickMethod(SENIOR_1,3,R.drawable.class_3);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_4);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_1);
                imageView = (ImageView) currentView.findViewById(R.id.class_4);
                imageView.setImageResource(R.drawable.class_light_4);
                setOnClickMethod(SENIOR_1,4,R.drawable.class_4);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_5);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_1);
                imageView = (ImageView) currentView.findViewById(R.id.class_5);
                imageView.setImageResource(R.drawable.class_light_5);
                setOnClickMethod(SENIOR_1,5,R.drawable.class_5);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_6);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_1);
                imageView = (ImageView) currentView.findViewById(R.id.class_6);
                imageView.setImageResource(R.drawable.class_light_6);
                setOnClickMethod(SENIOR_1,6,R.drawable.class_6);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_6);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_1);
                imageView = (ImageView) currentView.findViewById(R.id.class_6);
                imageView.setImageResource(R.drawable.class_light_6);
                setOnClickMethod(SENIOR_1,6,R.drawable.class_6);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_6);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_1);
                imageView = (ImageView) currentView.findViewById(R.id.class_6);
                imageView.setImageResource(R.drawable.class_light_6);
                setOnClickMethod(SENIOR_1,6,R.drawable.class_6);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_7);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_1);
                imageView = (ImageView) currentView.findViewById(R.id.class_7);
                imageView.setImageResource(R.drawable.class_light_7);
                setOnClickMethod(SENIOR_1,7,R.drawable.class_7);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_8);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_1);
                imageView = (ImageView) currentView.findViewById(R.id.class_8);
                imageView.setImageResource(R.drawable.class_light_8);
                setOnClickMethod(SENIOR_1,8,R.drawable.class_8);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_9);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_1);
                imageView = (ImageView) currentView.findViewById(R.id.class_9);
                imageView.setImageResource(R.drawable.class_light_9);
                setOnClickMethod(SENIOR_1,9,R.drawable.class_9);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_10);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_1);
                imageView = (ImageView) currentView.findViewById(R.id.class_10);
                imageView.setImageResource(R.drawable.class_light_10);
                setOnClickMethod(SENIOR_1,10,R.drawable.class_10);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_11);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_1);
                imageView = (ImageView) currentView.findViewById(R.id.class_11);
                imageView.setImageResource(R.drawable.class_light_11);
                setOnClickMethod(SENIOR_1,11,R.drawable.class_11);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_12);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_1);
                imageView = (ImageView) currentView.findViewById(R.id.class_12);
                imageView.setImageResource(R.drawable.class_light_12);
                setOnClickMethod(SENIOR_1,12,R.drawable.class_12);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_13);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_1);
                imageView = (ImageView) currentView.findViewById(R.id.class_13);
                imageView.setImageResource(R.drawable.class_light_13);
                setOnClickMethod(SENIOR_1,13,R.drawable.class_13);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_14);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_1);
                imageView = (ImageView) currentView.findViewById(R.id.class_14);
                imageView.setImageResource(R.drawable.class_light_14);
                setOnClickMethod(SENIOR_1,14,R.drawable.class_14);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_15);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_1);
                imageView = (ImageView) currentView.findViewById(R.id.class_15);
                imageView.setImageResource(R.drawable.class_light_15);
                setOnClickMethod(SENIOR_1,15,R.drawable.class_15);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_16);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_1);
                imageView = (ImageView) currentView.findViewById(R.id.class_16);
                imageView.setImageResource(R.drawable.class_light_16);
                setOnClickMethod(SENIOR_1,16,R.drawable.class_16);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_17);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_1);
                imageView = (ImageView) currentView.findViewById(R.id.class_17);
                imageView.setImageResource(R.drawable.class_light_17);
                setOnClickMethod(SENIOR_1,17,R.drawable.class_17);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_18);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_1);
                imageView = (ImageView) currentView.findViewById(R.id.class_18);
                imageView.setImageResource(R.drawable.class_light_18);
                setOnClickMethod(SENIOR_1,18,R.drawable.class_18);
            }
        });

/*        currentView = layoutManager.findViewByPosition(SENIOR_2);//设置当前年级为高二
        imageView = (ImageView) currentView.findViewById(R.id.class_1);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_2);
                imageView = (ImageView) currentView.findViewById(R.id.class_1);
                imageView.setImageResource(R.drawable.class_light_1);
                setOnClickMethod(SENIOR_2,1,R.drawable.class_1);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_2);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_2);
                imageView = (ImageView) currentView.findViewById(R.id.class_2);
                imageView.setImageResource(R.drawable.class_light_2);
                setOnClickMethod(SENIOR_2,2,R.drawable.class_2);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_3);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_2);
                imageView = (ImageView) currentView.findViewById(R.id.class_3);
                imageView.setImageResource(R.drawable.class_light_3);
                setOnClickMethod(SENIOR_2,3,R.drawable.class_3);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_4);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_2);
                imageView = (ImageView) currentView.findViewById(R.id.class_4);
                imageView.setImageResource(R.drawable.class_light_4);
                setOnClickMethod(SENIOR_2,4,R.drawable.class_4);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_5);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_2);
                imageView = (ImageView) currentView.findViewById(R.id.class_5);
                imageView.setImageResource(R.drawable.class_light_5);
                setOnClickMethod(SENIOR_2,5,R.drawable.class_5);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_6);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_2);
                imageView = (ImageView) currentView.findViewById(R.id.class_6);
                imageView.setImageResource(R.drawable.class_light_6);
                setOnClickMethod(SENIOR_2,6,R.drawable.class_6);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_6);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_2);
                imageView = (ImageView) currentView.findViewById(R.id.class_6);
                imageView.setImageResource(R.drawable.class_light_6);
                setOnClickMethod(SENIOR_2,6,R.drawable.class_6);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_6);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_2);
                imageView = (ImageView) currentView.findViewById(R.id.class_6);
                imageView.setImageResource(R.drawable.class_light_6);
                setOnClickMethod(SENIOR_2,6,R.drawable.class_6);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_7);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_2);
                imageView = (ImageView) currentView.findViewById(R.id.class_7);
                imageView.setImageResource(R.drawable.class_light_7);
                setOnClickMethod(SENIOR_2,7,R.drawable.class_7);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_8);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_2);
                imageView = (ImageView) currentView.findViewById(R.id.class_8);
                imageView.setImageResource(R.drawable.class_light_8);
                setOnClickMethod(SENIOR_2,8,R.drawable.class_8);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_9);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_2);
                imageView = (ImageView) currentView.findViewById(R.id.class_9);
                imageView.setImageResource(R.drawable.class_light_9);
                setOnClickMethod(SENIOR_2,9,R.drawable.class_9);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_10);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_2);
                imageView = (ImageView) currentView.findViewById(R.id.class_10);
                imageView.setImageResource(R.drawable.class_light_10);
                setOnClickMethod(SENIOR_2,10,R.drawable.class_10);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_11);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_2);
                imageView = (ImageView) currentView.findViewById(R.id.class_11);
                imageView.setImageResource(R.drawable.class_light_11);
                setOnClickMethod(SENIOR_2,11,R.drawable.class_11);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_12);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_2);
                imageView = (ImageView) currentView.findViewById(R.id.class_12);
                imageView.setImageResource(R.drawable.class_light_12);
                setOnClickMethod(SENIOR_2,12,R.drawable.class_12);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_13);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_2);
                imageView = (ImageView) currentView.findViewById(R.id.class_13);
                imageView.setImageResource(R.drawable.class_light_13);
                setOnClickMethod(SENIOR_2,13,R.drawable.class_13);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_14);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_2);
                imageView = (ImageView) currentView.findViewById(R.id.class_14);
                imageView.setImageResource(R.drawable.class_light_14);
                setOnClickMethod(SENIOR_2,14,R.drawable.class_14);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_15);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_2);
                imageView = (ImageView) currentView.findViewById(R.id.class_15);
                imageView.setImageResource(R.drawable.class_light_15);
                setOnClickMethod(SENIOR_2,15,R.drawable.class_15);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_16);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_2);
                imageView = (ImageView) currentView.findViewById(R.id.class_16);
                imageView.setImageResource(R.drawable.class_light_16);
                setOnClickMethod(SENIOR_2,16,R.drawable.class_16);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_17);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_2);
                imageView = (ImageView) currentView.findViewById(R.id.class_17);
                imageView.setImageResource(R.drawable.class_light_17);
                setOnClickMethod(SENIOR_2,17,R.drawable.class_17);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_18);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_2);
                imageView = (ImageView) currentView.findViewById(R.id.class_18);
                imageView.setImageResource(R.drawable.class_light_18);
                setOnClickMethod(SENIOR_2,18,R.drawable.class_18);
            }
        });

        currentView = layoutManager.findViewByPosition(SENIOR_3);//设置当前年级为高三
        imageView = (ImageView) currentView.findViewById(R.id.class_1);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_3);
                imageView = (ImageView) currentView.findViewById(R.id.class_1);
                imageView.setImageResource(R.drawable.class_light_1);
                setOnClickMethod(SENIOR_3,1,R.drawable.class_1);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_2);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_3);
                imageView = (ImageView) currentView.findViewById(R.id.class_2);
                imageView.setImageResource(R.drawable.class_light_2);
                setOnClickMethod(SENIOR_3,2,R.drawable.class_2);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_3);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_3);
                imageView = (ImageView) currentView.findViewById(R.id.class_3);
                imageView.setImageResource(R.drawable.class_light_3);
                setOnClickMethod(SENIOR_3,3,R.drawable.class_3);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_4);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_3);
                imageView = (ImageView) currentView.findViewById(R.id.class_4);
                imageView.setImageResource(R.drawable.class_light_4);
                setOnClickMethod(SENIOR_3,4,R.drawable.class_4);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_5);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_3);
                imageView = (ImageView) currentView.findViewById(R.id.class_5);
                imageView.setImageResource(R.drawable.class_light_5);
                setOnClickMethod(SENIOR_3,5,R.drawable.class_5);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_6);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_3);
                imageView = (ImageView) currentView.findViewById(R.id.class_6);
                imageView.setImageResource(R.drawable.class_light_6);
                setOnClickMethod(SENIOR_3,6,R.drawable.class_6);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_6);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_3);
                imageView = (ImageView) currentView.findViewById(R.id.class_6);
                imageView.setImageResource(R.drawable.class_light_6);
                setOnClickMethod(SENIOR_3,6,R.drawable.class_6);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_6);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_3);
                imageView = (ImageView) currentView.findViewById(R.id.class_6);
                imageView.setImageResource(R.drawable.class_light_6);
                setOnClickMethod(SENIOR_3,6,R.drawable.class_6);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_7);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_3);
                imageView = (ImageView) currentView.findViewById(R.id.class_7);
                imageView.setImageResource(R.drawable.class_light_7);
                setOnClickMethod(SENIOR_3,7,R.drawable.class_7);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_8);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_3);
                imageView = (ImageView) currentView.findViewById(R.id.class_8);
                imageView.setImageResource(R.drawable.class_light_8);
                setOnClickMethod(SENIOR_3,8,R.drawable.class_8);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_9);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_3);
                imageView = (ImageView) currentView.findViewById(R.id.class_9);
                imageView.setImageResource(R.drawable.class_light_9);
                setOnClickMethod(SENIOR_3,9,R.drawable.class_9);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_10);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_3);
                imageView = (ImageView) currentView.findViewById(R.id.class_10);
                imageView.setImageResource(R.drawable.class_light_10);
                setOnClickMethod(SENIOR_3,10,R.drawable.class_10);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_11);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_3);
                imageView = (ImageView) currentView.findViewById(R.id.class_11);
                imageView.setImageResource(R.drawable.class_light_11);
                setOnClickMethod(SENIOR_3,11,R.drawable.class_11);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_12);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_3);
                imageView = (ImageView) currentView.findViewById(R.id.class_12);
                imageView.setImageResource(R.drawable.class_light_12);
                setOnClickMethod(SENIOR_3,12,R.drawable.class_12);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_13);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_3);
                imageView = (ImageView) currentView.findViewById(R.id.class_13);
                imageView.setImageResource(R.drawable.class_light_13);
                setOnClickMethod(SENIOR_3,13,R.drawable.class_13);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_14);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_3);
                imageView = (ImageView) currentView.findViewById(R.id.class_14);
                imageView.setImageResource(R.drawable.class_light_14);
                setOnClickMethod(SENIOR_3,14,R.drawable.class_14);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_15);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_3);
                imageView = (ImageView) currentView.findViewById(R.id.class_15);
                imageView.setImageResource(R.drawable.class_light_15);
                setOnClickMethod(SENIOR_3,15,R.drawable.class_15);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_16);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_3);
                imageView = (ImageView) currentView.findViewById(R.id.class_16);
                imageView.setImageResource(R.drawable.class_light_16);
                setOnClickMethod(SENIOR_3,16,R.drawable.class_16);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_17);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_3);
                imageView = (ImageView) currentView.findViewById(R.id.class_17);
                imageView.setImageResource(R.drawable.class_light_17);
                setOnClickMethod(SENIOR_3,17,R.drawable.class_17);
            }
        });
        imageView = (ImageView) currentView.findViewById(R.id.class_18);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentView = layoutManager.findViewByPosition(SENIOR_3);
                imageView = (ImageView) currentView.findViewById(R.id.class_18);
                imageView.setImageResource(R.drawable.class_light_18);
                setOnClickMethod(SENIOR_3,18,R.drawable.class_18);
            }
        });
    }*/

/*    private void setOnClickMethod(int grade,int classroom,int imageId){
        if (classArray[grade][classroom] == 0){
            max++;classArray[grade][classroom] = max;//将此班级置于队列末尾
            renewButtonImage();
        } else {
            if (badges[grade][classroom] != null){
                badges[grade][classroom].hide(true);
                badges[grade][classroom] = null;
            }//隐藏角标
            imageView.setImageResource(imageId);
            renewList(grade,classroom);
            renewButtonImage();
        }
    }*/

    /*更新按钮图片和角标
    private void renewButtonImage(){
        for (grade = SENIOR_1;grade <= SENIOR_3;grade++) {
            mClass mClass = mClassList.get(grade);
            currentView = findViewByPosition(grade);
            for (classroom = 1; classroom <= 18; classroom++) {
                switch (classroom) {
                    case 1:
                        viewId = R.id.class_1;
                        lightImageId = R.drawable.class_light_1;
                        break;
                    case 2:
                        viewId = R.id.class_2;
                        lightImageId = R.drawable.class_light_2;
                        break;
                    case 3:
                        viewId = R.id.class_3;
                        lightImageId = R.drawable.class_light_3;
                        break;
                    case 4:
                        viewId = R.id.class_4;
                        lightImageId = R.drawable.class_light_4;
                        break;
                    case 5:
                        viewId = R.id.class_5;
                        lightImageId = R.drawable.class_light_5;
                        break;
                    case 6:
                        viewId = R.id.class_6;
                        lightImageId = R.drawable.class_light_6;
                        break;
                    case 7:
                        viewId = R.id.class_7;
                        lightImageId = R.drawable.class_light_7;
                        break;
                    case 8:
                        viewId = R.id.class_8;
                        lightImageId = R.drawable.class_light_8;
                        break;
                    case 9:
                        viewId = R.id.class_9;
                        lightImageId = R.drawable.class_light_9;
                        break;
                    case 10:
                        viewId = R.id.class_10;
                        lightImageId = R.drawable.class_light_10;
                        break;
                    case 11:
                        viewId = R.id.class_11;
                        lightImageId = R.drawable.class_light_11;
                        break;
                    case 12:
                        viewId = R.id.class_12;
                        lightImageId = R.drawable.class_light_12;
                        break;
                    case 13:
                        viewId = R.id.class_13;
                        lightImageId = R.drawable.class_light_13;
                        break;
                    case 14:
                        viewId = R.id.class_14;
                        lightImageId = R.drawable.class_light_14;
                        break;
                    case 15:
                        viewId = R.id.class_15;
                        lightImageId = R.drawable.class_light_15;
                        break;
                    case 16:
                        viewId = R.id.class_16;
                        lightImageId = R.drawable.class_light_16;
                        break;
                    case 17:
                        viewId = R.id.class_17;
                        lightImageId = R.drawable.class_light_17;
                        break;
                    case 18:
                        viewId = R.id.class_18;
                        lightImageId = R.drawable.class_light_18;
                        break;
                    default:
                        break;
                }
                if (classArray[grade][classroom] != 0) {
                    imageView = (ImageView) currentView.findViewById(viewId);
                    if (badges[grade][classroom] == null) {
                        imageView.setImageResource(lightImageId);
                        badges[grade][classroom] = new QBadgeView(MyApplication.getContext()).bindTarget(imageView)
                                .setBadgeText(classArray[grade][classroom] + "").setBadgeGravity(Gravity.TOP | Gravity.END)
                                .setGravityOffset(0, 0, true);
                    } else {
                        imageView.setImageResource(lightImageId);
                        badges[grade][classroom].setBadgeText(classArray[grade][classroom] + "");
                    }
                }
            }
        }
    }*/

    /*对检查顺序表重新排序并实时保存*/
    private void renewList(int currentGrade,int currentClassroom){
        currentNum = classArray[currentGrade][currentClassroom];
        max--;classArray[currentGrade][currentClassroom] = 0;
        for (grade = SENIOR_1;grade <= JUNIOR_3;grade++){
            for (classroom = 1;classroom <= 18;classroom++){
                if (classArray[grade][classroom] >= currentNum) classArray[grade][classroom]--;
            }
        }
    }


}
