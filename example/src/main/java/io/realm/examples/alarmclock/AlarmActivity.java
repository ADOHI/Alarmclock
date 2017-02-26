//알람을 띄워주는 액티비티
//시간이 모자라서 급하게 하느라 gps및 날씨 정보 파싱이 같은 파일안에 있어
//파악이 힘듭니다..........

package io.realm.examples.alarmclock;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.StrictMode;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.examples.alarmclock.model.AlarmDB;

public class AlarmActivity extends AppCompatActivity implements View.OnClickListener, LocationListener{
    private TextView alarmHourTextView;
    private TextView alarmMinuteTextView;
    private TextView alarmWeatherTextView;
    private TextView alarmMemoTextView;
    private TextView alarmAmpmTextView;
    private TextView alarmLocationTextView;
    private Button alarmCancleButton;
    private Button alarmReButton;
    private ImageView alarmWeatherImageView;
    private Realm realm;
    private int id;

    private LocationManager locManager;
    Geocoder geoCoder;
    private Location myLocation = null;
    double latPoint = 0;
    double lngPoint = 0;
    boolean locationBoolean;
    boolean weatherBoolean;
    MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //핸드폰 화면이 꺼져있고 잠금설정이 되어있어도 알람이 보이도록 함
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                |WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                |WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);


        setContentView(R.layout.activity_alarm);
        alarmHourTextView = (TextView) findViewById(R.id.tv_alarmHour);
        alarmMinuteTextView = (TextView) findViewById(R.id.tv_alarmMinute);
        alarmWeatherTextView = (TextView) findViewById(R.id.tv_weather);
        alarmMemoTextView = (TextView) findViewById(R.id.tv_alarmMemo);
        alarmAmpmTextView = (TextView) findViewById(R.id.tv_alarmAmpm);
        alarmLocationTextView = (TextView) findViewById(R.id.tv_location);
        alarmCancleButton = (Button) findViewById(R.id.bt_alarmCancle);
        alarmCancleButton.setOnClickListener(this);
        alarmReButton = (Button) findViewById(R.id.bt_realarm);
        alarmReButton.setOnClickListener(this);
        alarmWeatherImageView = (ImageView)findViewById(R.id.iv_weather);



        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        locManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null); //gps 정보 수신
        geoCoder = new Geocoder(this, Locale.KOREAN);
        StrictMode.enableDefaults(); // 백그라운드에서 안하고 간단하게 하기 위해서 설정

        mediaPlayer = MediaPlayer.create(this, R.raw.bell1); //미디어 플레이어 설정

        Intent intent = getIntent();
        id = intent.getExtras().getInt("id");

        realm = Realm.getDefaultInstance();



        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                AlarmDB alarmDB = realm.where(AlarmDB.class).equalTo("id", id).findFirst();
                alarmHourTextView.setText(Integer.toString(alarmDB.getHour()) + "시");
                alarmMinuteTextView.setText(Integer.toString(alarmDB.getMinute()) + "분");
                alarmMemoTextView.setText(alarmDB.getMemo());

                if(alarmDB.getBell()){//벨로 설정되어 있다면 볼륨에 맞게 음악 재생
                    mediaPlayer.setVolume(alarmDB.getVolume()*0.01f, alarmDB.getVolume()*0.01f);
                    if(mediaPlayer != null)mediaPlayer.start();
                }
                if(alarmDB.getVb()){//진동으로 설정되어 있다면 진동 시작
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(3000);
                }
                if(alarmDB.getAmpm()){ alarmAmpmTextView.setText("오후"); }
                else { alarmAmpmTextView.setText("오전");}//오전 오후 나눠서 알려줌

                locationBoolean = alarmDB.getLocation(); //지역 정보 반환 여부
                if(!locationBoolean){ alarmLocationTextView .setText("설정 안함"); }

                weatherBoolean = alarmDB.getWeather(); //날씨 정보 반환 여부
                if(!weatherBoolean){ alarmWeatherTextView.setText("설정 안함"); };


                //알람을 다음날로 재설정
                AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
                final Intent intent = new Intent(getApplicationContext(), AlarmService.class);
                intent.putExtra("id", alarmDB.getId());
                final PendingIntent pending = PendingIntent.getService(getApplicationContext(), alarmDB.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                Calendar calendar = Calendar.getInstance();
                if(alarmDB.getAmpm()) {// 오전 오후 시간 조정
                    calendar.set(Calendar.HOUR_OF_DAY, alarmDB.getHour()+12);
                }
                else {
                    calendar.set(Calendar.HOUR_OF_DAY, alarmDB.getHour());
                }
                calendar.set(Calendar.MINUTE, alarmDB.getMinute());
                calendar.set(Calendar.SECOND, 0);
                Calendar calendar1 = Calendar.getInstance();
                if (calendar1.get(Calendar.HOUR_OF_DAY) > calendar.get(Calendar.HOUR_OF_DAY)) {
                    calendar.add(Calendar.DATE, 1);// 시간 지났다면 하루 미룸
                } else if((calendar1.get(Calendar.HOUR_OF_DAY) == calendar.get(Calendar.HOUR_OF_DAY))
                        && (calendar1.get(Calendar.MINUTE) >= calendar.get(Calendar.MINUTE))){
                    calendar.add(Calendar.DATE, 1);// 시간 지났다면 하루 미룸
                }

                if(Build.VERSION.SDK_INT  >= 23){//도즈모드용으로 버전별로 다른 set함수 사용
                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending );
                }
                else if(Build.VERSION.SDK_INT  >= 19){
                    am.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending );
                }
                else am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending );
            }
        });


    }

    public void GetLocations() {

        StringBuffer location = new StringBuffer();
        if (myLocation != null) {
            latPoint = myLocation.getLatitude();
            lngPoint = myLocation.getLongitude();
            try {
                // 위도,경도를 이용하여 현재 위치의 주소를 가져온다.
                List<Address> addresses;
                addresses = geoCoder.getFromLocation(latPoint, lngPoint, 1);
                for (Address addr : addresses) {
                    int index = addr.getMaxAddressLineIndex();
                    for (int i = 0; i <= index; i++) {
                        location.append(addr.getAddressLine(i));
                        location.append(" ");
                    }
                    location.append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        if(locationBoolean)alarmLocationTextView.setText(String.valueOf(location));//뷰에 띄워준다
        if(weatherBoolean){
            GetWeather();//얻어낸 위경도 값을 바탕으로 날씨 정보를 획득함
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
    }

    public void GetWeather(){
        String Weather_icon = null; //날씨 아이콘
        String Weather_value = null; //날씨 현황 , 사용 안함
        String Weather_temperature = null; // 기온


        try{//오픈웨더맵, 위, 경도값, 키를 가지고 url을 생성한다.
            StringBuffer weather = new StringBuffer("http://api.openweathermap.org/data/2.5/weather?lat=")
                    .append(latPoint)
                    .append("&lon=")
                    .append(lngPoint)
                    .append("&appid=2700fd5c2cf30c93721e5fb57443fb4a");
            URL url = new URL(weather.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = new BufferedInputStream(connection.getInputStream());
            //커넥션이 성공하면 데이터를 파싱해서 원하는 값을 구함
            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Reader reader = new InputStreamReader(inputStream, "utf-8");
                BufferedReader bufferedReader = new BufferedReader(reader);
                String read = null;
                String jsonStr = "";
                String result[];
                while ((read = bufferedReader.readLine()) != null) {
                    jsonStr += read;
                }
                JSONObject obj = new JSONObject(jsonStr);
                result = obj.get("weather").toString().split(",");
                Weather_value= result[2].substring(15, result[2].lastIndexOf("\""));
                Weather_icon = result[3].substring(8, result[3].lastIndexOf("\""));
                result = obj.get("main").toString().split(":");
                Weather_temperature = result[1].substring(0, result[1].lastIndexOf(".") );

            }
            connection.disconnect();
        } catch (Exception e) { e.printStackTrace();}

        int temp = Integer.valueOf(Weather_temperature) - 273; //절대온도 보정
        alarmWeatherTextView.setText(Integer.toString(temp) + "도");
        StringBuffer icon = new StringBuffer();
        icon.append("@drawable/").append("ic_").append(Weather_icon); //가져온 값에 맞게 아이콘 설정
        int resID = getResources().getIdentifier(icon.toString(), "drawable", this.getPackageName());
        alarmWeatherImageView.setImageResource(resID);

    }
    @Override
    public void onClick(View v) {
        int bt_id = v.getId();
        if(bt_id == R.id.bt_alarmCancle){ //알람을 종료하면 이제껏 10분 추가로 맞춰왔던 알람들을 instant값을 보고 모두 삭제한다.
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    for(AlarmDB alarm : realm.where(AlarmDB.class).equalTo("instant", true).findAll()) {
                        AlarmManager am = (AlarmManager)getApplicationContext().getSystemService(ALARM_SERVICE);
                        final Intent intent = new Intent(getApplicationContext(), AlarmService.class);
                        final PendingIntent pending = PendingIntent.getService(getApplicationContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        if (pending != null) {
                            am.cancel(pending);
                            pending.cancel();
                        }
                        alarm.deleteFromRealm();
                    }
                }
            });
            finish();
        } else if(bt_id == R.id.bt_realarm){ //10분 후로 instant알람을 설정한다
            SetReAlarm();
            finish();
        }
    }
    private void SetReAlarm(){ //10분 후로 instant알람을 설정한다. 설정 값은 기존 알람과 동일하다
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                int nextkey;
                try {
                    nextkey = realm.where(AlarmDB.class).max("id").intValue() + 1;
                } catch (Exception e) {
                    nextkey = 0;
                }
                AlarmDB alarmDB = realm.where(AlarmDB.class).equalTo("id", id).findFirst(); // 기존 알람의 설정을 따오기 위해
                AlarmDB addAlarm = realm.createObject(AlarmDB.class);
                addAlarm.setId(nextkey);
                addAlarm.setMemo(alarmDB.getMemo());
                //날짜 혹은 오전 오후, 시간이 변경될 시 그에 맞도록 보정을 추가한다.
                if (alarmDB.getMinute() >= 50 && alarmDB.getHour() == 11) {
                    addAlarm.setHour(0);
                    addAlarm.setMinute(alarmDB.getMinute() - 50);
                    addAlarm.setAmpm(!alarmDB.getAmpm());
                } else if (alarmDB.getMinute() >= 50) {
                    addAlarm.setHour(alarmDB.getHour() + 1);
                    addAlarm.setMinute(alarmDB.getMinute() - 50);
                    addAlarm.setAmpm(alarmDB.getAmpm());
                } else {
                    addAlarm.setHour(alarmDB.getHour());
                    addAlarm.setMinute(alarmDB.getMinute() + 10);
                    addAlarm.setAmpm(alarmDB.getAmpm());
                }
                addAlarm.setMonday(true);
                addAlarm.setTuesday(true);
                addAlarm.setWednesday(true);
                addAlarm.setThursday(true);
                addAlarm.setFriday(true);
                addAlarm.setSaturday(true);
                addAlarm.setSunday(true);
                addAlarm.setBell(alarmDB.getBell());
                addAlarm.setVb(alarmDB.getVb());
                addAlarm.setVolume(alarmDB.getVolume());
                addAlarm.setWeather(alarmDB.getWeather());
                addAlarm.setLocation(alarmDB.getLocation());
                addAlarm.setYear(alarmDB.getYear());
                addAlarm.setMonth(alarmDB.getMonth());
                addAlarm.setDay(alarmDB.getDay());
                addAlarm.setOnoff(alarmDB.getOnoff());
                addAlarm.setInstant(true); // instant값을 true 둬서 종료시 이 알람들만 삭제되도록 함

                //알람 추가
                AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
                final Intent intent = new Intent(getApplicationContext(), AlarmService.class);
                intent.putExtra("id", addAlarm.getId());
                final PendingIntent pending = PendingIntent.getService(getApplicationContext(), addAlarm.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                Calendar calendar = Calendar.getInstance();
                if(addAlarm.getAmpm()) {
                    calendar.set(Calendar.HOUR_OF_DAY, addAlarm.getHour()+12);
                }
                else {
                    calendar.set(Calendar.HOUR_OF_DAY, addAlarm.getHour());
                }
                calendar.set(Calendar.MINUTE, addAlarm.getMinute());
                calendar.set(Calendar.SECOND, 0);
                Calendar calendar1 = Calendar.getInstance();
                if (calendar1.get(Calendar.HOUR_OF_DAY) > calendar.get(Calendar.HOUR_OF_DAY)) {
                    calendar.add(Calendar.DATE, 1);
                } else if((calendar1.get(Calendar.HOUR_OF_DAY) == calendar.get(Calendar.HOUR_OF_DAY))
                        && (calendar1.get(Calendar.MINUTE) >= calendar.get(Calendar.MINUTE))){
                    calendar.add(Calendar.DATE, 1);
                }

                if(Build.VERSION.SDK_INT  >= 23){
                   // am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending );
                    am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending );
                }
                else if(Build.VERSION.SDK_INT  >= 19){
                    am.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending );
                }
                else am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending );
            }

        });
    }

    @Override
    protected void onDestroy() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {
        //gps값을 수신 하면 그 정보를 바탕으로 지역정보를 획득한다.
        myLocation = location;
        GetLocations();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
