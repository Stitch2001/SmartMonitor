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

    private String grade;

    private int[] imageId = new int[19];

    private int max;

    private Badge[] badge = new Badge[19];

    private int[] classArray = new int[19];

    public mClass(String grade,int[] classArray,int max) {
        this.grade = grade;
        this.classArray = classArray;
        this.max = max;
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
    }

    public String getGrade() {
            return grade;
        }

    public void setGrade(String grade) {
            this.grade = grade;
        }

    public int getImageId(int position) {
        return imageId[position];
    }

    public void setImageId(int position,int imageId) {
        this.imageId[position] = imageId;
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

    public void hideBadge(int position){
        this.badge[position].hide(true);
    }

    public void deleteBadge(int position){
        hideBadge(position);
        this.badge[position] = null;
    }

    public int getClassArray(int classroom) {
        return classArray[classroom];
    }

    public void setClassArray(int classroom,int classArrayNum) {
        this.classArray[classroom] = classArrayNum;
    }

    public int getMax() {
        return max;
    }
}
