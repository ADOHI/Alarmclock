package io.realm.examples.alarmclock.model;


import io.realm.RealmObject;

public class AlarmDB extends RealmObject {

    public static final String ALARMDB = "alarmdb";

    private int id;
    private String memo;                //알람에 설정 될 메모 값
    private int hour;                   //알람 시간
    private int minute;                 //알람 분
    private boolean ampm;              //true = pm

    private boolean monday;             //알람 요일별로 작동 여부
    private boolean tuesday;
    private boolean wednesday;
    private boolean thursday;
    private boolean friday;
    private boolean saturday;
    private boolean sunday;

    private boolean bell;               //알람 벨소리 작동 여부
    private boolean vb;                 //알람 진동 여부
    private int volume;                 //알람 볼륨
    private boolean weather;            //날씨 정보 반환 여부
    private boolean location;           //지역 정보 반환 여부

    private int year;                   //알람 최초 작동 날짜
    private int month;
    private int day;

    private boolean onoff;              //알람 작동 여부

    private boolean instant;            //이 값이 TRUE면 알람 작동 중 10분 후 재설정 버튼을 클릭 함으로 생성된
                                            //instant 알람으로 다음 알람을 하나라도 종료하면 모두 삭제된다.


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMemo() { return memo; }
    public void setMemo(String memo) {
        this.memo = memo;
    }

    public int getHour() { return hour; }
    public void setHour(int hour) { this.hour = hour; }

    public int getMinute() { return minute; }
    public void setMinute(int minute) {
        this.minute = minute;
    }

    public boolean getAmpm() { return ampm; }
    public void setAmpm(boolean ampm) {
        this.ampm = ampm;
    }

    public boolean getMonday() { return monday; }
    public void setMonday(boolean monday) {
        this.monday = monday;
    }

    public boolean getTuesday() { return tuesday; }
    public void setTuesday(boolean tuesday) {
        this.tuesday = tuesday;
    }

    public boolean getWednesday() { return wednesday; }
    public void setWednesday(boolean wednesday) {
        this.wednesday = wednesday;
    }

    public boolean getThursday() { return thursday; }
    public void setThursday(boolean thursday) {
        this.thursday = thursday;
    }

    public boolean getFriday() { return friday; }
    public void setFriday(boolean friday) {
        this.friday = friday;
    }

    public boolean getSaturday() { return saturday; }
    public void setSaturday(boolean saturday) {
        this.saturday = saturday;
    }

    public boolean getSunday() { return sunday; }
    public void setSunday(boolean sunday) {
        this.sunday = sunday;
    }

    public boolean getBell() { return bell; }
    public void setBell(boolean bell) {
        this.bell = bell;
    }

    public boolean getVb() { return vb; }
    public void setVb(boolean vb) {
        this.vb = vb;
    }

    public int getVolume() { return volume; }
    public void setVolume(int volume) { this.volume = volume; }

    public boolean getWeather() { return weather; }
    public void setWeather(boolean weather) {
        this.weather = weather;
    }

    public boolean getLocation() { return location; }
    public void setLocation(boolean location) {
        this.location = location;
    }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }

    public int getDay() { return day; }
    public void setDay(int day) { this.day = day; }

    public boolean getOnoff() { return onoff; }
    public void setOnoff(boolean onoff) {
        this.onoff = onoff;
    }

    public boolean getInstant() { return instant; }
    public void setInstant(boolean instant) {
        this.instant = instant;
    }

}

