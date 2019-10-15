package ru.vincetti.vimoney.data.models;

public class TransactionStatDayModel {

    private int day;
    private int sum;

    public void setDay(int day) {
        this.day = day;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public int getDay() {
        return day;
    }

    public int getSum() {
        return sum;
    }
}
