package com.example.track;


import static android.content.ContentValues.TAG;

import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import androidx.lifecycle.ViewModel;

import com.example.track.config.MySqlDBUtils;
import com.example.track.entity.Safety;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class TemperatureListViewModel extends ViewModel {
    //变量定义
    private List<Safety> mSafetyList;
    private String str;
    private String search_time;
    //    提取数据
    public List<Safety> getSafetyList(String time) throws InterruptedException {//给外部一个接口
        search_time=time;
        Thread thread2 = new Thread(new JoinRunnable2());
        thread2.start();
        thread2.join();
        Log.d(TAG, "线程名字:" + Thread.currentThread().getName());
        return mSafetyList;
    }
    class JoinRunnable2 implements Runnable {
        @Override
        public void run() {
            Log.d(TAG, "线程名字getText():" + Thread.currentThread().getName());
            String result  ;
            ResultSet rs = null;
            Connection connection= MySqlDBUtils.getConn();
            try {
                Statement stmt = connection.createStatement();//
                String sql="SELECT id,temperature,insert_time,warning_flag FROM safety_long WHERE insert_time LIKE '%"+search_time+"%'";
                if (search_time==null)
                    sql="SELECT id,temperature,insert_time,warning_flag FROM safety_long";
                Log.d("testid","sql:"+sql);
                rs = stmt.executeQuery(sql);
                result = MySqlDBUtils.convertList(rs);//直接转化为List<T>
                System.out.println("result:"+result);
//                将json格式的String转化为List<T>
                mSafetyList = JSONArray.parseArray(result, Safety.class);
                Log.d("test","class:"+Safety.class);
                System.out.println("mSafetyList"+mSafetyList.get(1).getInsert_time());//没有转化成功
                rs.close();
                stmt.close();
                connection.close();
                System.out.println("Database connected successfully!");
            } catch (SQLException e) {
                System.out.println(e);
            } catch (Exception e) {
                System.out.println(e);
            }

        }
    }
}