package com.gdbjzx.smartmonitor;

/**
 * Created by Administrator on 2018/9/1.
 */

public class mGradeAndClass {

    private int grade;

    private int classroom;

    private int array;

    public mGradeAndClass(int grade, int classroom, int array) {
        this.grade = grade;
        this.classroom = classroom;
        this.array = array;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public int getClassroom() {
        return classroom;
    }

    public void setClassroom(int classroom) {
        this.classroom = classroom;
    }

    public int getArray() {
        return array;
    }

    public void setArray(int array) {
        this.array = array;
    }
}
