package com.example.secondapp;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyList2Activity extends ListActivity implements Runnable ,AdapterView.OnItemClickListener{

    Handler handler;
    private ArrayList<HashMap<String, String>> listItems;//存放文字、图片信息
    private SimpleAdapter listItemAdapter;//适配器
    private final String TAG = "myList2";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_my_list2);
        initListview();
        MyAdapter myAdapter = new MyAdapter(this, R.layout.activity_my_list2, listItems);
        this.setListAdapter(myAdapter);
        Thread t = new Thread(this);
        t.start();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 7) {
                    List<HashMap<String, String>> list2 = (List<HashMap<String, String>>) msg.obj;
                    listItemAdapter = new SimpleAdapter(MyList2Activity.this, listItems,
                            R.layout.activity_my_list2,
                            new String[]{"ItermTitle", "ItemDetai"},
                            new int[]{R.id.itemTitle, R.id.itemDetail}
                    );
                    setListAdapter(listItemAdapter);
                }
                super.handleMessage(msg);

            }
        };
        getListView().setOnItemClickListener(this);
    }


    private  void initListview(){
        listItems = new ArrayList<HashMap<String, String>>();
        for(int i = 0;i<10;i++){
            HashMap<String, String> map;
            map = new HashMap<String, String>();
            map.put("ItermTitle","Rate:"+i);
            map.put("ItermDetail","Detail:"+i);
            listItems.add(map);

        }
        //生成适配器的item和动态数据相对应的元素
        listItemAdapter = new SimpleAdapter(this,listItems,R.layout.activity_my_list2,
                new String[]{"ItermTitle","ItermDetail"},
                new int[]{R.id.itemTitle,R.id.itemDetail}
                );

    }


    public void run(){
        //获取网络数据，放入list带回主线程
        List<HashMap<String,String>> retlist = new ArrayList<HashMap<String,String>>();
        Document doc = null;
        try {
            Thread.sleep(1000);
            doc = Jsoup.connect("http://www.usd-cny.com/bankofchina.htm").get();
            Log.i(TAG,"run: "+doc.title());
            Elements tables = doc.getElementsByTag("table");
            int u=1;
            Element table6=tables.get(5);
            Log.i(TAG, "run:tables"+table6);
            //获取TD中的数据
            Elements tds = table6.getElementsByTag("td");
            for(int i=0;i<tds.size();i+=8){
                Element td1 = tds.get(i);
                Element td2 = tds.get(i+5);

                String str1 = td1.text();
                String val = td2.text();

                Log.i(TAG,"run:"+str1+"==>"+val);
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("ItenmTitle", str1);//标题文字
                map.put("ItenmDatil", val);//详情描述
                retlist.add(map);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Message msg = handler.obtainMessage(7);
        msg.obj = retlist;
        handler.sendMessage(msg);
    }
    @Override
    public  void  onItemClick(AdapterView<?>parent,View view,int position,long id){
        getListView().getItemAtPosition(position);
        HashMap<String, String> map = (HashMap<String, String>) getListView().getItemAtPosition(position);
        String titlestr = map.get("ItemTitle");
        String detailstr = map.get("ItemDetail");
        Log.i(TAG,"onitemclick:titlestr="+titlestr);
        Log.i(TAG,"onitemclick:detailstr="+detailstr);

        TextView title = (TextView) view.findViewById(R.id.itemTitle);
        TextView detail = (TextView) view.findViewById(R.id.itemDetail);
        String title2 = String.valueOf(title.getText());
        String detail2 = String.valueOf(detail.getText());
        Log.i(TAG,"onitemclick:titlestr="+title2);
        Log.i(TAG,"onitemclick:detailstr="+detail2);

        //打开新的页面，传入参数
        Intent rateClac = new Intent(this,RateClickActivity.class);
        rateClac.putExtra("title",titlestr);
        rateClac.putExtra("rate",Float.parseFloat(detailstr));
        startActivity(rateClac);




    }
}



