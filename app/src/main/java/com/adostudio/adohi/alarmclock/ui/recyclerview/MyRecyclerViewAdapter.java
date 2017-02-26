/*
 * Copyright 2016 Realm Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.realm.examples.alarmclock.ui.recyclerview;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.examples.alarmclock.AlarmAddActivity;
import io.realm.examples.alarmclock.MainActivity;
import io.realm.examples.alarmclock.R;
import io.realm.examples.alarmclock.model.AlarmDB;

public class MyRecyclerViewAdapter extends RealmRecyclerViewAdapter<AlarmDB, MyRecyclerViewAdapter.MyViewHolder> {

    private final MainActivity activity;

    public MyRecyclerViewAdapter(MainActivity activity, OrderedRealmCollection<AlarmDB> data) {
        super(data, true);
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AlarmDB obj = getData().get(position);
        holder.data = obj;
        if(obj.getAmpm())holder.ampmTextView.setText("오후");
        else holder.ampmTextView.setText("오전");
        holder.hourTextView.setText(Integer.toString(obj.getHour()));
        holder.minuteTextView.setText(Integer.toString(obj.getMinute()));
        holder.memoTextView.setText(obj.getMemo());
        String day = "";
        if(obj.getMonday()){day += "월";}
        if(obj.getTuesday()){day += "화";}
        if(obj.getWednesday()){day += "수";}
        if(obj.getThursday()){day += "목";}
        if(obj.getFriday()){day += "금";}
        if(obj.getSaturday()){day += "토";}
        if(obj.getSunday()){day += "일";}
        holder.dayTextView.setText(day);
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener{
        public TextView ampmTextView;
        public TextView hourTextView;
        public TextView minuteTextView;
        public TextView memoTextView;
        public TextView dayTextView;
        public Switch alarmSwitch;
        public AlarmDB data;

        public MyViewHolder(View view) {
            super(view);
            ampmTextView = (TextView) view.findViewById(R.id.tv_ampm);
            hourTextView = (TextView) view.findViewById(R.id.tv_hour);
            minuteTextView = (TextView) view.findViewById(R.id.tv_minute);
            memoTextView = (TextView) view.findViewById(R.id.tv_memo);
            dayTextView = (TextView) view.findViewById(R.id.tv_day);
            alarmSwitch = (Switch) view.findViewById(R.id.sw_alarm);
            view.setOnClickListener(this);
            alarmSwitch.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            activity.deleteItem(data);
            return true;
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if(id == R.id.sw_alarm){
                activity.toggleAlarm(data);
            }
            else {
                Intent intent = new Intent(activity, AlarmAddActivity.class);
                intent.putExtra("id", data.getId());
                activity.startActivity(intent);
            }
        }
    }
}