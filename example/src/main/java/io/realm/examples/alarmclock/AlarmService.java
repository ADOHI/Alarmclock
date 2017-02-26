//알람 액티비티를 띄워주기 위한 서비스

package io.realm.examples.alarmclock;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Calendar;

import io.realm.Realm;
import io.realm.examples.alarmclock.model.AlarmDB;

public class AlarmService extends Service {
    public AlarmService() {
    }
    private int id;
    private Realm realm;

    @Override
    public void onCreate() {
        realm = Realm.getDefaultInstance();
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        id = intent.getExtras().getInt("id");
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                AlarmDB alarmDB = realm.where(AlarmDB.class).equalTo("id", id).findFirst();//아이디를 사용하여 조회한다
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                Intent alarmIntent = new Intent(getApplicationContext(), AlarmActivity.class);
                alarmIntent.putExtra("id", alarmDB.getId());
                alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if(alarmDB.getOnoff() && ( (year > alarmDB.getYear())
                        || ( (year == alarmDB.getYear()) && (month > alarmDB.getMonth()) )
                        || ( (year == alarmDB.getYear()) && (month == alarmDB.getMonth()) && (dayOfMonth >= alarmDB.getDay()) ) )
                        ){ //넘어온 알람이 off상태이거나 설정된 날짜가 오늘 이후 일 경우 알람을 실행하지 않는다

                    switch (dayOfWeek) { // 설정된 요일이 아닐 경우 알람을 실행하지 않는다
                        case Calendar.SUNDAY:
                            if (alarmDB.getSunday()) {
                                startActivity(alarmIntent);
                            }
                            break;
                        case Calendar.MONDAY:
                            if (alarmDB.getMonday()) {
                                startActivity(alarmIntent);
                            }
                            break;
                        case Calendar.TUESDAY:
                            if (alarmDB.getTuesday()) {
                                startActivity(alarmIntent);
                            }
                            break;
                        case Calendar.WEDNESDAY:
                            if (alarmDB.getWednesday()) {
                                startActivity(alarmIntent);
                            }
                            break;
                        case Calendar.THURSDAY:
                            if (alarmDB.getThursday()) {
                                startActivity(alarmIntent);
                            }
                            break;
                        case Calendar.FRIDAY:
                            if (alarmDB.getFriday()) {
                                startActivity(alarmIntent);
                            }
                            break;
                        case Calendar.SATURDAY:
                            if (alarmDB.getSaturday()) {
                                startActivity(alarmIntent);
                            }
                            break;
                    }
                }
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

        throw new UnsupportedOperationException("Not yet implemented");

    }
}
