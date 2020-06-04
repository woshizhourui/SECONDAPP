package com.example.secondapp;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Links_Titles_listActivity extends ListActivity implements Runnable {
    private final String TAG = "L_T_List";
    String data[] = {"wait..."};
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_liinks__titles_list);
        ListAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,data);
        setListAdapter(adapter);
        Thread t = new Thread(this);
        t.start();

        handler = new Handler(){
            public void handleMessage(Message msg){
                if(msg.what==7){
                    List<String> list2 = (List<String>) msg.obj;
                    ListAdapter adapter = new ArrayAdapter<String>(Links_Titles_listActivity.this,android.R.layout.simple_list_item_1,list2);
                    setListAdapter(adapter);
                }
                super.handleMessage(msg);
            }
        };
    }

    public void run(){
        //获取网络数据，放入list带回主线程
        List<String> retlist = new ArrayList<String>();
        Document doc = null;
        try {
            Thread.sleep(3000);
            doc = Jsoup.connect("https://it.swufe.edu.cn/index/tzgg.htm").get();
            Log.i(TAG,"run: "+doc.title());
            Elements list = doc.select("div#article-list wow fadeInRight ul li");
            String links;
            links = list.attr("herf");
            String titles = list.attr("title");

            Log.i(TAG,"run: "+links+titles);
            retlist.add(links+"==>"+titles);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Message msg = handler.obtainMessage(7);
        msg.obj = retlist;
        handler.sendMessage(msg);
    }

}



