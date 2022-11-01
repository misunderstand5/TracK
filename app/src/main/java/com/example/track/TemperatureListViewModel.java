package com.example.track;

import static android.content.ContentValues.TAG;
import android.util.Log;
import com.alibaba.fastjson.JSONArray;
import androidx.lifecycle.ViewModel;

import com.example.track.entity.Safety;

import java.io.IOException;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TemperatureListViewModel extends ViewModel {
    private List<Safety> mSafetyList;
    private String str;
    private String search_time;
    public TemperatureListViewModel() throws InterruptedException {
//        创建子线程延长主线程时间，以便OKhttp线程可以跑完;---**可能有问题

    }
    public List<Safety> getSafetyList(String time) throws InterruptedException {//给外部一个接口
//        Log.d("test","getSafetyList()返回List对象"+mSafetyList.size());
        search_time=time;
        Thread thread1 = new Thread(new JoinRunnable());
        getJson();
        thread1.start();
        thread1.join();//将子线程和主线程合并(子线程运行晚以后才会继续运行主线程)
//        Log.i(TAG, Thread.currentThread().getName() + "正在运行....");
        return mSafetyList;
    }
    class JoinRunnable implements Runnable {
        @Override
        public void run() {
            try {
                //    --*延长主线程时间后期可能还需要加大
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d("test","子线程");
        }
    }
    private void getJson(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                //建立表单请求体()
                FormBody.Builder formBody= new FormBody.Builder();
                Request request;
                if (search_time==null){
                    Log.d("time","null:");
                    request =new Request.Builder().url("http://120.25.145.148:8078/track_safety_time_list").post(formBody.build()).build();
                }else {
                    formBody.add("date", search_time);
//                    http://120.25.145.148:8078/track_safety_time_list?date=2022-10-22
//                    request =new Request.Builder().url("http://120.25.145.148:8078/track_safety_time_list?date="+search_time).post(body).build();
                    request =new Request.Builder().url("http://120.25.145.148:8078/track_safety_time_list").post(formBody.build()).build();
                }
                Call call =client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d("test", "response failed");
                        Log.d("error",e.toString()) ;//打印错误
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {//请求成功时
                        Log.d("test", "response success");
                        Log.d("test",response.toString());
                        str = response.body().string();//格式时String
                        Log.d("test",str);
                        mSafetyList = JSONArray.parseArray(str, Safety.class);
                        Log.d("test","safetyList"+mSafetyList.size());
                        Log.i(TAG, Thread.currentThread().getName() + "正在运行....");
                    }
                });
            }
        }).start();
    }
}