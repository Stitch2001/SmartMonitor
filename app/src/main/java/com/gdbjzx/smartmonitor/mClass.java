package com.gdbjzx.smartmonitor;

import android.app.Activity;
import android.view.Gravity;
import android.widget.ImageView;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

/**
     * Created by Administrator on 2018/8/3.
     */

public class mClass extends Activity{

    private int grade;

    private boolean[] classroomBool = new boolean[19];

    private int[] imageId = new int[19];

    private int[] lightImageId = new int[19];

    private int[] array = new int[19];

    private int max;

    private Badge[] badge = new Badge[19];

    public mClass(int grade,boolean[] classroomBool,int max,int[] array) {
        this.grade = grade;
        this.classroomBool = classroomBool;
        this.max = max;
        this.array = array;
        imageId[1] = R.drawable.class_1;
        imageId[2] = R.drawable.class_2;
        imageId[3] = R.drawable.class_3;
        imageId[4] = R.drawable.class_4;
        imageId[5] = R.drawable.class_5;
        imageId[6] = R.drawable.class_6;
        imageId[7] = R.drawable.class_7;
        imageId[8] = R.drawable.class_8;
        imageId[9] = R.drawable.class_9;
        imageId[10] = R.drawable.class_10;
        imageId[11] = R.drawable.class_11;
        imageId[12] = R.drawable.class_12;
        imageId[13] = R.drawable.class_13;
        imageId[14] = R.drawable.class_14;
        imageId[15] = R.drawable.class_15;
        imageId[16] = R.drawable.class_16;
        imageId[17] = R.drawable.class_17;
        imageId[18] = R.drawable.class_18;
        lightImageId[1] = R.drawable.class_light_1;
        lightImageId[2] = R.drawable.class_light_2;
        lightImageId[3] = R.drawable.class_light_3;
        lightImageId[4] = R.drawable.class_light_4;
        lightImageId[5] = R.drawable.class_light_5;
        lightImageId[6] = R.drawable.class_light_6;
        lightImageId[7] = R.drawable.class_light_7;
        lightImageId[8] = R.drawable.class_light_8;
        lightImageId[9] = R.drawable.class_light_9;
        lightImageId[10] = R.drawable.class_light_10;
        lightImageId[11] = R.drawable.class_light_11;
        lightImageId[12] = R.drawable.class_light_12;
        lightImageId[13] = R.drawable.class_light_13;
        lightImageId[14] = R.drawable.class_light_14;
        lightImageId[15] = R.drawable.class_light_15;
        lightImageId[16] = R.drawable.class_light_16;
        lightImageId[17] = R.drawable.class_light_17;
        lightImageId[18] = R.drawable.class_light_18;
    }

    public int getGrade() {
            return grade;
        }

    public void setGrade(int grade) {
            this.grade = grade;
        }

    public int getImageId(int position) {
        return imageId[position];
    }

    public void setImageId(int position,int imageId) {
        this.imageId[position] = imageId;
    }

    public int getLightImageId(int position) {
        return lightImageId[position];
    }

    public void setLightImageId(int position,int lightImageId) {
        this.lightImageId[position] = lightImageId;
    }

    public Badge getBadge(int position) {
        return badge[position];
    }

    public void setBadge(int position, ImageView view, int classArrayNum) {
        this.badge[position] = new QBadgeView(MyApplication.getContext()).bindTarget(view)
                .setBadgeText(classArrayNum+"").setBadgeGravity(Gravity.TOP | Gravity.END)
                .setGravityOffset(0, 0, true);
    }

    public void setBadgeText(int position,int classArrayNum){
        this.badge[position].setBadgeText(classArrayNum+"");
    }

    public void deleteBadge(int position){
        this.badge[position].hide(true);
        this.badge[position] = null;
    }

    public void deleteAllBadge(){
        for (int i = 1;i <= max;i++){
            deleteBadge(i);
        }
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max){
        this.max = max;
    }

    public boolean getClassroomBool(int position) {
        return classroomBool[position];
    }

    public void setClassroomBool(int position,boolean classroom) {
        this.classroomBool[position] = classroom;
    }

    public int getArray(int position) {
        return array[position];
    }

    public void setArray(int position,int array) {
        this.array[position] = array;
    }
}
