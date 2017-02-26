//content provider... thread에 따란 realm 접근 문제로 다른 앱에서 접근이 불가함
//같은 어플리케이션에서는 주석 부분을 제거하면 접근할 수 있음
//대신에 전역 변수를 활용하였으나 전역변수는 앱을 종료하면 초기화되어
//앱이 켜져있을 때만 다른 앱에서 접근 가능함
//FAIL!!!!!!!!!


package io.realm.examples.alarmclock.contentProvider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import io.realm.examples.alarmclock.AlarmChangeListener;

/**
 * Created by ADOHI on 2017-01-28.
 */

public class RealmContentProvider extends ContentProvider{

   /* static final String[] sColums = new String[] {"id", "memo", "hour", "minute"
            , "ampm", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"
            , "sunday", "bell", "vb", "weather", "location", "onoff"};
    public static MatrixCursor matrixCursor = new MatrixCursor(sColums);*/

    @Override
    public boolean onCreate() {
        Log.d("powerlog", "isok?");
       /* Realm.init(getContext());
        realm = Realm.getDefaultInstance();*/
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        /*
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmQuery<AlarmDB> query = realm.where(AlarmDB.class);
                RealmResults<AlarmDB> results = query.findAll();
                //MatrixCursor matrixCursor = new MatrixCursor(sColums);
                for (AlarmDB alarmDB : results) {
                    Object[] rowData =
                            new Object[]{alarmDB.getId(), alarmDB.getMemo(), alarmDB.getHour(), alarmDB.getMinute()
                                    , alarmDB.getAmpm(), alarmDB.getMonday(), alarmDB.getTuesday(), alarmDB.getWednesday()
                                    , alarmDB.getThursday(), alarmDB.getFriday(), alarmDB.getSaturday(), alarmDB.getSunday()
                                    , alarmDB.getBell(), alarmDB.getVb(), alarmDB.getWeather(), alarmDB.getLocation(), alarmDB.getOnoff()};

                    matrixCursor.addRow(rowData);
                }

            }
        });*/
        return AlarmChangeListener.matrixCursor;

    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
