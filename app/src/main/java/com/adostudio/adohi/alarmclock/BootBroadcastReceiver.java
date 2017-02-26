//핸드폰 리부팅시 알람을 새로 등록해 주기 위한 브로드캐스트 리시버

package io.realm.examples.alarmclock;


import java.util.Calendar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import io.realm.Realm;
import io.realm.examples.alarmclock.model.AlarmDB;

import static android.content.Context.ALARM_SERVICE;

public class BootBroadcastReceiver extends BroadcastReceiver{
    private Realm realm;
    @Override public void onReceive(final Context context, Intent intent) {
        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){//부팅시
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    for(AlarmDB alarm : realm.where(AlarmDB.class).findAll()) {//모든 DB를 조회하여 새롭게 알람매니저로 등록해준다.
                        AlarmManager am = (AlarmManager)context.getSystemService(ALARM_SERVICE);
                        final Intent intent = new Intent(context, AlarmService.class);
                        intent.putExtra("id", alarm.getId());
                        final PendingIntent pending = PendingIntent.getService(context, alarm.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        Calendar calendar = Calendar.getInstance();
                        if(alarm.getAmpm()) { //오후 알람일 시 시간 12시간 보정
                            calendar.set(Calendar.HOUR_OF_DAY, alarm.getHour()+12);
                        }
                        else {
                            calendar.set(Calendar.HOUR_OF_DAY, alarm.getHour());
                        }
                        calendar.set(Calendar.MINUTE, alarm.getMinute());
                        calendar.set(Calendar.SECOND, 0);
                        Calendar calendar1 = Calendar.getInstance();
                        if (calendar1.get(Calendar.HOUR_OF_DAY) > calendar.get(Calendar.HOUR_OF_DAY)) {
                            calendar.add(Calendar.DATE, 1); //알람 시간이 이미 지났을 경우 하루 뒤로 등록한다
                        } else if((calendar1.get(Calendar.HOUR_OF_DAY) == calendar.get(Calendar.HOUR_OF_DAY))
                                && (calendar1.get(Calendar.MINUTE) >= calendar.get(Calendar.MINUTE))){
                            calendar.add(Calendar.DATE, 1);
                        }
                        //버전별로 다른 set함수를 사용
                        if(Build.VERSION.SDK_INT  >= 23){//도즈모드 예방
                            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending );
                        }
                        else if(Build.VERSION.SDK_INT  >= 19){
                            am.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending );
                        }
                        else am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending );

                    }
                }
            });

        }
    }
}

