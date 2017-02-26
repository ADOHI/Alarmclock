//Content provider를 다른 방식으로 구현할 수 있을까 연습해 본 결과물
//결론은 앱을 종료하는 순간 변수가 초기화되기 때문에 앱을 평생 켜놓을 것 아니라면 의미가 없음

package io.realm.examples.alarmclock;

import android.database.MatrixCursor;


import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.examples.alarmclock.model.AlarmDB;



public class AlarmChangeListener implements RealmChangeListener<Realm> {
    private Realm realm;
    static final String[] sColums = new String[] {"id", "memo", "hour", "minute"
            , "ampm", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"
            , "sunday", "bell", "vb", "volume", "weather", "location", "year", "month", "day",  "onoff", "instant"};//커서에 올리기 위한 db colums
    public static MatrixCursor matrixCursor = new MatrixCursor(sColums);
    @Override
    public void onChange(Realm element) {

        realm = Realm.getDefaultInstance();
        realm.removeAllChangeListeners();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmQuery<AlarmDB> query = realm.where(AlarmDB.class);
                RealmResults<AlarmDB> results = query.findAll();

                    MatrixCursor matrixCursor = new MatrixCursor(sColums);
                    for (AlarmDB alarmDB : results) {
                        Object[] rowData =
                                new Object[]{alarmDB.getId(), alarmDB.getMemo(), alarmDB.getHour(), alarmDB.getMinute()
                                        , alarmDB.getAmpm(), alarmDB.getMonday(), alarmDB.getTuesday(), alarmDB.getWednesday()
                                        , alarmDB.getThursday(), alarmDB.getFriday(), alarmDB.getSaturday(), alarmDB.getSunday()
                                        , alarmDB.getBell(), alarmDB.getVb(), alarmDB.getVolume(), alarmDB.getWeather(), alarmDB.getLocation()
                                        , alarmDB.getYear(), alarmDB.getMonth(), alarmDB.getYear(), alarmDB.getOnoff(), alarmDB.getInstant()
                                };

                        matrixCursor.addRow(rowData);//모든 결과를 조회하여 매트릭스커서에 올림
                    }


                }

        });
    }
}
