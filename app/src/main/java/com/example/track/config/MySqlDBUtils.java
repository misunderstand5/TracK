package com.example.track.config;

import android.util.Log;

import com.alibaba.fastjson.JSON;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MySqlDBUtils {
    //    数据库驱动
    private static final String HOST = "jdbc:mysql://120.25.145.148:3306/test?serverTimezone=GMT%2B8";
    // 用户名
    private static final String USER = "root";
    // 密码
    private static final String PASSWORD = "root";
    // Java数据库连接JDBC驱动
    private static final String DRIVER = "com.mysql.jdbc.Driver";
    private Connection connection;

    public static Connection getConn(){//数据库驱动连接
        Connection connection = null;
        try{
            Class.forName(DRIVER);
            connection= DriverManager.getConnection(HOST, USER, PASSWORD);

            Log.e("数据库连接", "成功!");
        } catch (Exception e) {
            Log.e("数据库连接", "失败!");
            e.printStackTrace();
        }
        return connection;
    }

    public static  String convertList(ResultSet rs) throws SQLException {//返回List<T>(List<Bean>)数据
        List list = new ArrayList();//??
        ResultSetMetaData md = rs.getMetaData();//获取键名
        int columnCount = md.getColumnCount();//获取行的数量
        Log.d("数据库连接_数据条数", String.valueOf(columnCount));
        int coun = 0;
        while (rs.next()) {//遍历取数据
            Map rowData = new HashMap();//声明Map用List
            for (int i = 1; i <= columnCount; i++) {
                rowData.put(md.getColumnName(i), rs.getObject(i));//获取键名及值
            }
            list.add(rowData);
            coun++;
        }
        String json = JSON.toJSONString(list);//List<Map<String, Object>> 转化为json格式的String
        return json;
    }

}
