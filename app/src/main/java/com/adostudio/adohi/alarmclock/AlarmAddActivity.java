package io.realm.examples.alarmclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;

import java.util.Calendar;

import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Switch;

import io.realm.Realm;
import io.realm.examples.alarmclock.model.AlarmDB;

public class AlarmAddActivity extends Activity implements View.OnClickListener{
    private EditText memoEditText;
    private Button saveButton;
    private Button cancleButton;
    private CheckBox mondayCheckBox;
    private CheckBox tuesdayCheckBox;
    private CheckBox wednesdayCheckBox;
    private CheckBox thursdayCheckBox;
    private CheckBox fridayCheckBox;
    private CheckBox saturdayCheckBox;
    private CheckBox sundayCheckBox;
    private CheckBox bellCheckBox;
    private CheckBox vbCheckBox;
    private CheckBox weatherCheckBox;
    private CheckBox locationCheckBox;
    private NumberPicker hourNumberPicker;
    private NumberPicker minuteNumberPicker;
    private Switch ampmSwitch;
    private SeekBar volumeSeekBar;
    private CalendarView calendarView;
    private Realm realm;
    private int id;
    private int intentId;
    private int yearInt, monthInt, dayInt;
    private boolean reset;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_add);
        saveButton = (Button) findViewById(R.id.bt_save);
        saveButton.setOnClickListener(this);
        cancleButton = (Button) findViewById(R.id.bt_cancle);
        cancleButton.setOnClickListener(this);
        hourNumberPicker = (NumberPicker) findViewById(R.id.np_hour);
        hourNumberPicker.setMinValue(0);
        hourNumberPicker.setMaxValue(11);
        minuteNumberPicker = (NumberPicker) findViewById(R.id.np_minute);
        minuteNumberPicker.setMinValue(0);
        minuteNumberPicker.setMaxValue(59);
        memoEditText = (EditText) findViewById(R.id.et_memo);
        mondayCheckBox = (CheckBox) findViewById(R.id.cb_mon);
        tuesdayCheckBox = (CheckBox) findViewById(R.id.cb_tue);
        wednesdayCheckBox = (CheckBox) findViewById(R.id.cb_wed);
        thursdayCheckBox = (CheckBox) findViewById(R.id.cb_thr);
        fridayCheckBox = (CheckBox) findViewById(R.id.cb_fri);
        saturdayCheckBox = (CheckBox) findViewById(R.id.cb_sat);
        sundayCheckBox = (CheckBox) findViewById(R.id.cb_sun);
        bellCheckBox = (CheckBox) findViewById(R.id.cb_bell);
        vbCheckBox = (CheckBox) findViewById(R.id.cb_vb);
        weatherCheckBox = (CheckBox) findViewById(R.id.cb_weather);
        locationCheckBox = (CheckBox) findViewById(R.id.cb_location);
        ampmSwitch = (Switch) findViewById(R.id.sw_ampm);
        ampmSwitch.setOnClickListener(this);
        volumeSeekBar = (SeekBar) findViewById(R.id.sb_volume);
        calendarView = (CalendarView) findViewById(R.id.cv_calendar);
        Calendar cal = Calendar.getInstance();
        yearInt = cal.get(Calendar.YEAR);
        monthInt = cal.get(Calendar.MONTH);
        dayInt = cal.get(Calendar.DAY_OF_MONTH);


        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                yearInt = year;
                monthInt = month;
                dayInt = dayOfMonth;
                //캘린더 뷰가 바뀔때마다 값을 변화시켜 준다.
            }
        });
        realm = Realm.getDefaultInstance();
        Intent intent = getIntent();
        id = intent.getExtras().getInt("id");

        if(id != -1){//새로운 등록이 아니라면 알람 설정 화면을 기존의 알람 설정에 맞게 초기화한다.
            alarmInit(id);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_save:
                realm.addChangeListener(new AlarmChangeListener());
                final String memo = memoEditText.getText().toString();
                final int hour = hourNumberPicker.getValue();
                final int minute = minuteNumberPicker.getValue();
                final boolean ampm = ampmSwitch.isChecked();
                final boolean monday = mondayCheckBox.isChecked();
                final boolean tuesday = tuesdayCheckBox.isChecked();
                final boolean wednesday = wednesdayCheckBox.isChecked();
                final boolean thursday = thursdayCheckBox.isChecked();
                final boolean friday = fridayCheckBox.isChecked();
                final boolean saturday = saturdayCheckBox.isChecked();
                final boolean sunday = sundayCheckBox.isChecked();
                final boolean bell = bellCheckBox.isChecked();
                final boolean vb = vbCheckBox.isChecked();
                final boolean weather = weatherCheckBox.isChecked();
                final boolean location = locationCheckBox.isChecked();
                final int volume = volumeSeekBar.getProgress();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        int nextkey;
                        try {
                            nextkey = realm.where(AlarmDB.class).max("id").intValue() + 1;
                        } catch (Exception e)
                        { nextkey =  0; } //autoincrement key를 위한 기작. 다음 키는 항상 DB에서 가장 큰 키의 +1을 선택한다.
                        AlarmDB alarm = realm.where(AlarmDB.class).equalTo("id", id).findFirst();
                        if(alarm == null){//알람이 새로 추가하는 경우일 때
                            alarm = realm.createObject(AlarmDB.class);
                            alarm.setId(nextkey);
                            alarm.setOnoff(true); //알람을 on으로 설정하고 새로운 키를 부여한다.
                            reset = false; //알람 추가임을 알림
                        } else {
                            reset = true;  //알람 재설정임을 알림
                        }
                        alarm.setMemo(memo);
                        alarm.setHour(hour);
                        alarm.setMinute(minute);
                        alarm.setAmpm(ampm);
                        if(!(monday||tuesday||wednesday||thursday||friday||saturday||sunday)){//요일을 아무것도 선택하지 않았을 시
                            alarm.setMonday(true);//모든 요일을 선택한 것으로 간주
                            alarm.setTuesday(true);
                            alarm.setWednesday(true);
                            alarm.setThursday(true);
                            alarm.setFriday(true);
                            alarm.setSaturday(true);
                            alarm.setSunday(true);
                        } else {
                            alarm.setMonday(monday);
                            alarm.setTuesday(tuesday);
                            alarm.setWednesday(wednesday);
                            alarm.setThursday(thursday);
                            alarm.setFriday(friday);
                            alarm.setSaturday(saturday);
                            alarm.setSunday(sunday);
                        }
                        alarm.setBell(bell);
                        alarm.setVb(vb);
                        alarm.setWeather(weather);
                        alarm.setLocation(location);
                        alarm.setVolume(volume);
                        alarm.setYear(yearInt);
                        alarm.setMonth(monthInt);
                        alarm.setDay(dayInt);//모두 선택한 값에 맞게 넣는다
                        alarm.setInstant(false);//알람 종료창에서 추가한 것이 아니기 때문에 false
                        intentId = alarm.getId();

                    }
                });
                AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
                if(reset){//알람이 재설정이라면 기존의 알람을 삭제하고 다시 설정
                    final Intent intent = new Intent(getApplicationContext(), AlarmService.class);
                    final PendingIntent pending = PendingIntent.getService(getApplicationContext(), intentId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    if (pending != null) {
                        am.cancel(pending);
                        pending.cancel();
                    }
                }
                //알람 추가
                final Intent intent = new Intent(getApplicationContext(), AlarmService.class);
                intent.putExtra("id", intentId);
                final PendingIntent pending = PendingIntent.getService(getApplicationContext(), intentId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                Calendar calendar = Calendar.getInstance();
                if(ampm) {
                    calendar.set(Calendar.HOUR_OF_DAY, hour+12);//오후로 설정한다면 시간 보정
                }
                else {
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                }
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                Calendar calendar1 = Calendar.getInstance();
                if (calendar1.get(Calendar.HOUR_OF_DAY) > calendar.get(Calendar.HOUR_OF_DAY)) {
                    calendar.add(Calendar.DATE, 1);//현재 시간이 알람 시간을 넘어선 경우 알람에 하루 추가
                } else if((calendar1.get(Calendar.HOUR_OF_DAY) == calendar.get(Calendar.HOUR_OF_DAY))
                    && (calendar1.get(Calendar.MINUTE) >= calendar.get(Calendar.MINUTE))){
                    calendar.add(Calendar.DATE, 1);
                 }

                if(Build.VERSION.SDK_INT  >= 23){//도즈모드용으로 버전별로 다른 set함수 사용
                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending );
                }
                else if(Build.VERSION.SDK_INT  >= 19){
                    am.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending );
                }
                else am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending );

                finish();
                break;
            case R.id.bt_cancle: //취소버튼 일시에는 그냥 취소한다.
                finish();
                break;
            case R.id.sw_ampm: //am/pm스위치를 클릭할때마다 텍스트를 변경시켜준다
                if(ampmSwitch.isChecked()){
                    ampmSwitch.setText("PM");
                } else ampmSwitch.setText("AM");
        }
    }

    public void alarmInit(final int id){//알람이 추가가 아닌 재설정일 경우 DB에서 값을 읽어서 UI 설정 값을 바꿔준다
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                AlarmDB alarmDB = realm.where(AlarmDB.class).equalTo("id", id).findFirst();
                memoEditText.setText(alarmDB.getMemo());
                hourNumberPicker.setValue(alarmDB.getHour());
                minuteNumberPicker.setValue(alarmDB.getMinute());
                ampmSwitch.setChecked(alarmDB.getAmpm());
                mondayCheckBox.setChecked(alarmDB.getMonday());
                tuesdayCheckBox.setChecked(alarmDB.getTuesday());
                wednesdayCheckBox.setChecked(alarmDB.getWednesday());
                thursdayCheckBox.setChecked(alarmDB.getThursday());
                fridayCheckBox.setChecked(alarmDB.getFriday());
                saturdayCheckBox.setChecked(alarmDB.getSaturday());
                sundayCheckBox.setChecked(alarmDB.getSunday());
                bellCheckBox.setChecked(alarmDB.getBell());
                vbCheckBox.setChecked(alarmDB.getVb());
                weatherCheckBox.setChecked(alarmDB.getWeather());
                locationCheckBox.setChecked(alarmDB.getLocation());
                volumeSeekBar.setProgress(alarmDB.getVolume());
                Calendar tt = Calendar.getInstance();
                tt.set(alarmDB.getYear(), alarmDB.getMonth(), alarmDB.getDay());
                yearInt = alarmDB.getYear();
                monthInt = alarmDB.getMonth();
                dayInt = alarmDB.getDay();
                calendarView.setDate(tt.getTime().getTime());
            }
        });
        if(ampmSwitch.isChecked()){
            ampmSwitch.setText("PM");
        } else ampmSwitch.setText("AM");

    }


}
