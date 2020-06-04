package com.example.secondapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class rateActivity extends AppCompatActivity implements Runnable{

    private final String TAG = "Rate";
    private  float dollorRate = 0.1f;
    private  float euroRate = 0.2f;
    private  float wonRate = 0.3f;
    private  String updateDate = "";

    EditText rmb ;
    TextView show;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        rmb=(EditText) findViewById(R.id.rmb);
        show=(TextView) findViewById(R.id.showout);

        //获取SP里保存的数据
        SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
        dollorRate = sharedPreferences.getFloat("dolar_rate",0.0f);
        euroRate = sharedPreferences.getFloat("euro_rate",0.0f);
        wonRate = sharedPreferences.getFloat("won_rate",0.0f);
        updateDate = sharedPreferences.getString("update_date","");

        //获取当前系统时间
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String todaystr = sdf.format(today);


        Log.i(TAG,"onCreate:sp dollarRate="+dollorRate);
        Log.i(TAG,"onCreate:sp euroRate="+euroRate);
        Log.i(TAG,"onCreate:sp wonRate="+wonRate);
        Log.i(TAG,"onCreate:sp updateDate="+updateDate);
        Log.i(TAG,"onCreate:sp todaystr="+todaystr);

        //判断时间
        if(todaystr.equals(updateDate)){
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
                    dollorRate = bdl.getFloat("dollar_rate");
                    euroRate = bdl.getFloat("euro_rate");
                    wonRate = bdl.getFloat("won_rate");

                    Log.i(TAG,"handlemessage:dollarRate:"+dollorRate);
                    Log.i(TAG,"handlemessage:euroRate:"+euroRate);
                    Log.i(TAG,"handlemessage:wonRate:"+wonRate);

                    //保存更新的日期
                    SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putFloat("dollar_rate",dollorRate);
                    editor.putFloat("euro_rate",euroRate);
                    editor.putFloat("won_rate",wonRate);
                    editor.putString("update_date",todaystr);
                    editor.apply();

                    Toast.makeText(rateActivity.this,"汇率已经更新",Toast.LENGTH_SHORT).show();
                }
                super.handleMessage(msg);
            }
         };


    }

        public void onClick(View btn){
              String str=rmb.getText().toString();
              float r=0;
              if(str.length()>0){
                  r=Float.parseFloat(str);
              }else{
                  Toast.makeText(this,"请输入金额",  Toast.LENGTH_SHORT).show();
              }

              if(btn.getId()==R.id.but_dollor){
                  show.setText(String.format( "%.2f",r*dollorRate));

              }else if(btn.getId()==R.id.but_euro){
                  show.setText(String.format( "%.2f",r*euroRate));

              }else {

                  show.setText(String.format( "%.2f",r*wonRate));
              }


        }

        public void openOne(View btn){
        //打开一个新的页面

            openconfig();
        }

    private void openconfig() {
        Intent config = new Intent(this, configActivity.class);
        config.putExtra("dollar_rate_key", dollorRate);
        config.putExtra("euro_rate_key", euroRate);
        config.putExtra("won_rate_key", wonRate);

        Log.i(TAG, "openOne: dollarRate=" + dollorRate);
        Log.i(TAG, "openOne: euroRate=" + euroRate);
        Log.i(TAG, "openOne: wonRate=" + wonRate);

        startActivityForResult(config, 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rate,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.menu_set){
            openconfig();
        }else if(item.getItemId()==R.id.open_list){
            //打开列表窗口
            Intent list = new Intent(this,MyList2Activity.class);
            startActivity(list);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==1&& resultCode==2){
            //bdl.putFloat("key_dollar",newDollar);
            //        bdl.putFloat("key_euro",newEuro);
            //        bdl.putFloat("key_won",newWon);
            assert data != null;
            Bundle bundle = data.getExtras();
            assert bundle != null;
            dollorRate = bundle.getFloat("key_dollar",0.1f);
            euroRate = bundle.getFloat("key_euro",0.1f);
            wonRate = bundle.getFloat("key_won",0.1f);
            Log.i(TAG,"onActivityResult:dollarRate="+dollorRate);
            Log.i(TAG,"onActivityResult:euroRate="+euroRate);
            Log.i(TAG,"onActivityResult:wonRate="+wonRate);

            //将新设置的汇率保存到sp中
            SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat("dollar_rate",dollorRate);
            editor.putFloat("euro_rate",euroRate);
            editor.putFloat("won_rate",wonRate);
            editor.commit();
            Log.i(TAG,"onActivityResult:数据已保存到SharedPreferences");


        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void run(){
      Log.i(TAG,"run:run.....");
      try{
          Thread.sleep(2000);
      }catch (InterruptedException e){
              e.printStackTrace();
      }
      //用于保存获取汇率
      Bundle bundle = new Bundle();




        //获取网络数据
//        URL url = null;
//        try{
//            url = new URL("http://www.usd-cny.com/bankofchina.htm");
//            HttpURLConnection http = (HttpURLConnection) url.openConnection();
//            InputStream in = http.getInputStream();
//
//            String html = inputStream2String(in);
//            Log.i(TAG,"run:html"+html);
//            Document doc = Jsoup.parse(html);
//
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        bundle=getFromBOC( );
        //bubdle保存获取汇率
        //获取msg方法；用于返回主线程
        Message msg;
        msg = handler.obtainMessage(5);
        msg.obj = bundle;
        handler.sendMessage(msg);




    }

    private  Bundle getFromBOC() {
        Bundle bundle = new Bundle();
        Document doc = null;
        try {
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
                Log.i(TAG,"run:"+ td1.text()+"==>"+td2.text());
                String str1 = td1.text();
                String val = td2.text();

                if("美元".equals(str1)){
                    bundle.putFloat("dollar_rate",100f/Float.parseFloat(val));
                }else if ("欧元".equals(str1)){
                    bundle.putFloat("euro_rate",100f/Float.parseFloat(val));
                }else if("韩国元".equals(str1)){
                    bundle.putFloat("won_rate",100f/Float.parseFloat(val));
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bundle;
    }

    private String inputStream2String(InputStream inputStream ) throws IOException {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final  StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream,"gb2312");
        for(; ;){
            int rsz = in.read(buffer,0,buffer.length);
            if(rsz<0)
                break;
            out.append(buffer,0,rsz);
        }
        return out.toString();
    }
}
