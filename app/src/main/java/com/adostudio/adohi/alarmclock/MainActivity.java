//메인 액티비티
package io.realm.examples.alarmclock;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import io.realm.Realm;
import io.realm.examples.alarmclock.model.AlarmDB;
import io.realm.examples.alarmclock.ui.DividerItemDecoration;
import io.realm.examples.alarmclock.ui.recyclerview.MyRecyclerViewAdapter;


public class MainActivity extends AppCompatActivity {

    private Realm realm;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        realm = Realm.getDefaultInstance();
        realm.addChangeListener(new AlarmChangeListener()); //content provider를 다른 식으로 구현해 보기 위해 만들어둔 것
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        setUpRecyclerView();
        //리사이클러 뷰를 불러온다.
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.listview_options, menu);
        return true;
    }

    @Override//add버튼 클릭 시
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(getApplicationContext(), AlarmAddActivity.class);
        int id = -1;//defalut id 값을 넘겨줘서 새로운 알람 추가임을 알 수 있다.
        intent.putExtra("id", id);
        int addId = item.getItemId();
        if (addId == R.id.action_add) {
            startActivity(intent);//알람 추가 액티비티 실행
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpRecyclerView() { //리사이클러뷰 어댑터 설정
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MyRecyclerViewAdapter(this, realm.where(AlarmDB.class).findAllSortedAsync("id")));//아이디 순으로 정렬
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST)); //줄눈금 데코레이션 추가
    }

    public void deleteItem(final AlarmDB item) {//아이템 삭제 함수
        realm.addChangeListener(new AlarmChangeListener());
        final int id = item.getId();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(AlarmDB.class).equalTo("id", id)
                        .findAll()
                        .deleteAllFromRealm();
            }
        });//클릭 시 아이디를 넘겨 받아 그 아이디에 해당하는 데이터 삭제
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        final Intent intent = new Intent(getApplicationContext(), AlarmService.class);
        final PendingIntent pending = PendingIntent.getService(getApplicationContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (pending != null) {
            am.cancel(pending);
            pending.cancel();
        }//알람매니저에서도 삭제를 해 준다
    }

    public void toggleAlarm(final AlarmDB item) {//on/off 버튼이 눌릴때 db를 수정해 준다.
        final int id = item.getId();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                AlarmDB alarmDB = realm.where(AlarmDB.class).equalTo("id", id).findFirst();
                alarmDB.setOnoff(!alarmDB.getOnoff());
            }
        });
    }

}
