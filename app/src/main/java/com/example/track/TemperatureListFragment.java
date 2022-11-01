package com.example.track;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.track.entity.Safety;

import java.util.ArrayList;
import java.util.List;

public class TemperatureListFragment extends Fragment {//import androidx.fragment.app.Fragment;
    private static final String TAG = "test";
    private TemperatureListViewModel mTemperatureListViewModel;
    RecyclerView mTemperatureRecyclerView;
    private TemperatureAdapter mTemperatureAdapter;
    private List<Safety> safetyList;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//      利用接口取值
        mTemperatureListViewModel =new ViewModelProvider(this).get(TemperatureListViewModel.class);
    }

//
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_temperature_list,container,false);
        mTemperatureRecyclerView = v.findViewById(R.id.crime_recycler_view);
//        获取safetyList对象
        mTemperatureRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        try {
            safetyList =  mTemperatureListViewModel.getSafetyList(null);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mTemperatureRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
                updateUI();
        return v;

    }
    private void updateUI(){
        mTemperatureAdapter =new TemperatureAdapter(safetyList);
        mTemperatureRecyclerView.setAdapter(mTemperatureAdapter);//调用下面的类方法。
    }

//    内部类
    class TemperatureHolder extends RecyclerView.ViewHolder{
    private TextView mTemperatureTextView_list_item_temperature;
    private TextView mInsertTimeTextView_list_item_temperature;
    private LinearLayout mLinearLayout_list_item_temperature;
    private Safety mSafety;
        public TemperatureHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(view -> Toast.makeText(getContext(),"已点击查看明细！!",Toast.LENGTH_SHORT).show());//通过lambda设置监听器
            mLinearLayout_list_item_temperature=itemView.findViewById(R.id.LinearLayout_list_item_temperature);
            mTemperatureTextView_list_item_temperature=itemView.findViewById(R.id.temperature_data);
            mInsertTimeTextView_list_item_temperature=itemView.findViewById(R.id.temperature_insert_time);
        }
        public void bind(Safety safety){
            mSafety=safety;
            mTemperatureTextView_list_item_temperature.setText(mSafety.getTemperature());
            mInsertTimeTextView_list_item_temperature.setText(mSafety.getInsert_time());
            mLinearLayout_list_item_temperature.setBackgroundColor(Color.WHITE);
            if (safety.getWarning_flag()==0)
            mLinearLayout_list_item_temperature.setBackgroundColor(Color.RED);
        }
    }
    private class TemperatureAdapter extends RecyclerView.Adapter<TemperatureHolder>{
        private List<Safety> safetyList;//????????
        public TemperatureAdapter(List<Safety> safeties){
            safetyList=safeties;
        }
        @NonNull
        @Override
        public TemperatureHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v=LayoutInflater.from(getContext()).inflate(R.layout.list_item_temperature,parent,false);
            return new TemperatureHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull TemperatureHolder holder, int position) {//当前滑动的位置
            Safety safety= safetyList.get(position);
            holder.bind(safety);
//            holder.mTemperatureTextView_list_item_temperature.setText(safety.getTemperature()+"°C");
//            holder.mInsertTimeTextView_list_item_temperature.setText(safety.getInsert_time());
//            if (safety.getWarning_flag()==0){//条件要换safety.getWarning_flag()==0
//                Log.d("color", String.valueOf(position));
//            holder.mLinearLayout_list_item_temperature.setBackgroundColor(Color.RED);
//            }
        }
        @Override
        public int getItemCount() {//ok
            Log.d("test","sd:"+safetyList.size());
            return safetyList.size();
        }
    }
}
