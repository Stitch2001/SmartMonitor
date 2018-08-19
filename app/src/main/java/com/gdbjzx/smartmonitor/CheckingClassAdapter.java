package com.gdbjzx.smartmonitor;

import android.app.Activity;
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

import java.io.File;
import java.util.List;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Administrator on 2018/8/19.
 */

public class CheckingClassAdapter extends RecyclerView.Adapter<CheckingClassAdapter.ViewHolder> {

    private List<mClass> mClassList;

    private mClass aClass;

    private static final int SENIOR_1 = 0;
    private static final int SENIOR_2 = 1;
    private static final int SENIOR_3 = 2;
    private static final int JUNIOR_1 = 3;
    private static final int JUNIOR_2 = 4;
    private static final int JUNIOR_3 = 5;

    private NotifyCheckingSituationActivity context;

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

    public CheckingClassAdapter(List<mClass> classList, NotifyCheckingSituationActivity context){
        mClassList = classList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /*加载布局*/
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.class_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        /*设置点击事件*/
        for (int i = 1;i <= 18;i++) setOnclickMethod(holder,i);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        aClass = mClassList.get(position);
        boolean isEmpty = false;//该年级中是否有检查任务标记，无则为false
        for (int classroom = 1;classroom <= 18;classroom++){
            if (aClass.getClassArray(classroom) == 0){
                holder.classroomView[classroom].setVisibility(View.GONE);//如果不用检查该班级，则不显示
            } else {
                File classImage = new File(MyApplication.getContext().getExternalCacheDir(),position+""+classroom+".jpg");
                if (!classImage.exists()){
                    holder.classroomView[classroom].setImageResource(aClass.getImageId(classroom));//如果该班级还未检查，则该项显示为灰色
                    context.leftClass++;//剩余未检查班级数+1
                }
                isEmpty = true;
            }
            if (isEmpty) holder.gradeText.setText(aClass.getGrade());
                else holder.gradeText.setVisibility(View.GONE);//如果该年级没有检查任务，则不显示
        }
    }

    @Override
    public int getItemCount() {
        return mClassList.size();
    }

    private void setOnclickMethod(final ViewHolder holder,final int classroom){
        holder.classroomView[classroom].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int grade = holder.getAdapterPosition();
                Intent intent = context.getIntent();
                intent.putExtra("currentGrade",grade);
                intent.putExtra("currentRoom",classroom);
                context.setResult(Activity.RESULT_CANCELED,intent);
                context.finish();
            }
        });
    }
}
