package com.gdbjzx.smartmonitor;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2018/8/19.
 */

public class NotifyCheckingSituationAdapter extends RecyclerView.Adapter<NotifyCheckingSituationAdapter.ViewHolder> {

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

    public NotifyCheckingSituationAdapter(List<mClass> classList, NotifyCheckingSituationActivity context){
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
        switch (aClass.getGrade()){
            case SENIOR_1:holder.gradeText.setText("高一");break;
            case SENIOR_2:holder.gradeText.setText("高二");break;
            case SENIOR_3:holder.gradeText.setText("高三");break;
            case JUNIOR_1:holder.gradeText.setText("初一");break;
            case JUNIOR_2:holder.gradeText.setText("初二");break;
            case JUNIOR_3:holder.gradeText.setText("初三");break;
        }
        int i = 1;
        for (int classroom = 1;classroom <= 18;classroom++){
            if (aClass.getClassroomBool(classroom)){
                holder.classroomView[i].setVisibility(View.VISIBLE);//显示该班级
                holder.classroomView[i].setTag(classroom);
                holder.classroomView[i].setImageResource(aClass.getImageId(classroom));
                File classImage = new File(mApplication.getContext().getExternalCacheDir(),position+""+classroom+".jpg");
                if (!classImage.exists()){
                    holder.classroomView[i].setImageResource(aClass.getImageId(classroom));//如果该班级还未检查，则该项显示为灰色
                    context.leftClass++;//剩余未检查班级数+1
                } else holder.classroomView[i].setImageResource(aClass.getLightImageId(classroom));//如果已检查，则显示为亮色
                i++;
            }
        }
        /*隐藏该年级中的其他控件，以腾出空间*/
        if (i <= 6) i = 7;
        else if (i <= 12) i = 13;
        else if (i <= 18) i = 19;//只GONE掉整一行都是INVISIBLE的控件，以保持界面结构
        for (int j = 18;j >= i;j--){
            holder.classroomView[j].setVisibility(View.GONE);
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
                intent.putExtra("currentRoom",(int)holder.classroomView[classroom].getTag());
                context.setResult(Activity.RESULT_CANCELED,intent);
                context.finish();
            }
        });
    }
}
