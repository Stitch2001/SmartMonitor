package com.gdbjzx.smartmonitor;

    /**
     * Created by Administrator on 2018/8/3.
     */

public class mClass {

    private String grade;

    private int classroom[];//此数组存放对应班级的检查顺序，对于第一位数，1为七年级，2为八年级，以此类推

    public mClass(String grade, int[] classroom) {
            this.grade = grade;
            this.classroom = classroom;
    }


        public int[] getClassroom() {
            return classroom;
        }

        public void setClassroom(int[] classroom) {
            this.classroom = classroom;
        }


        public String getGrade() {
            return grade;
        }

        public void setGrade(String grade) {
            this.grade = grade;
        }
    }
