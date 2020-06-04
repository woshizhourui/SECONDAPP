package com.example.secondapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SesrchActivity extends AppCompatActivity implements Runnable{
    private final String TAG = "Search";
    TextView search;
    Handler handler;
    private  String updateDate = "";
    private  String links = ;
    private  String titles= ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sesrch);
        //获取SP里保存的数据
        SharedPreferences sharedPreferences = getSharedPreferences("notesearch", Activity.MODE_PRIVATE);
        updateDate = sharedPreferences.getString("update_date","");
        links = sharedPreferences.getString("link","");
        titles = sharedPreferences.getString("title","");


        //获取当前系统时间
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String todaystr = sdf.format(today);


        Log.i(TAG,"onCreate:sp todaystr="+todaystr);

        //判断时间
        if(todaystr.equals(updateDate+"7")){
            Log.i(TAG,"oncreate:需要更新");
            //开启子线程
            Thread t= new Thread(this);
            t.start();
        }else {
            Log.i(TAG,"oncreate:不需要更新");
        }

        handler = new Handler(){
            @Override
            public void handleMessage( Message msg) {
                if(msg.what==5){
                    Bundle bdl = (Bundle) msg.obj;
                    //保存更新的日期
                    SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("链接",links);
                    editor.putString("链接",titles);
                    editor.putString("update_date",todaystr);
                    editor.apply();

                    Toast.makeText(SesrchActivity.this,"通知数据已经更新",Toast.LENGTH_SHORT).show();
                }
                super.handleMessage(msg);
            }
        };

    }


    @Override
    public void run() {
        Log.i(TAG,"run:run.....");
        try{
            Thread.sleep(2000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        Bundle bundle = new Bundle();
        bundle=getFromITSwufe( );
        //bubdle保存获取汇率
        //获取msg方法；用于返回主线程
        Message msg;
        msg = handler.obtainMessage(5);
        msg.obj = bundle;
        handler.sendMessage(msg);

    }
    private  Bundle getFromITSwufe() {
        Bundle bundle = new Bundle();
        Document doc = null;
        try {
            doc = Jsoup.connect("https://it.swufe.edu.cn/index/tzgg.htm").get();
            Log.i(TAG,"run: "+doc.title());
            Elements list = doc.select("div#article-list wow fadeInRight ul li");


            String links;
            links = list.attr("herf");
            String titles = list.attr("title");
            bundle.putString("链接",links);
            bundle.putString("标题",titles);
            Log.i(TAG,"run: "+links+titles);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bundle;
    }


}
