package com.example.secondapp;

import android.content.Intent;
import android.os.Bundle;
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

public class rateActivity extends AppCompatActivity {

    private final String TAG = "Rate";
    private  float dollorRate = 0.1f;
    private  float euroRate = 0.2f;
    private  float wonRate = 0.3f;

    EditText rmb ;
    TextView show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        rmb=(EditText) findViewById(R.id.rmb);
        show=(TextView) findViewById(R.id.showout);

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
        if(item.getItemId()==R.id.setting){
            openconfig();


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


        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
