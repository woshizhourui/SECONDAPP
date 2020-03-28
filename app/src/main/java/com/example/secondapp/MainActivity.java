package com.example.secondapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    private TextView tvTitle;       //静态文本框
    private TextView tvResult;
    private EditText editInput;     //定义文本框
    private Button btnC2F;          //定义按钮
    private Button btnF2C;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //通过finViewById方法实例化以上控件类
        tvTitle = (TextView)findViewById(R.id.titleshow);
        tvResult = (TextView)findViewById(R.id.out_temp);
        editInput = (EditText) findViewById(R.id.in_temp);
        btnC2F = (Button)findViewById(R.id.btnc2f);
        btnF2C = (Button)findViewById(R.id.btnf2c);

        //设置按钮点击监听
        btnC2F.setOnClickListener((View.OnClickListener) this);
        btnF2C.setOnClickListener((View.OnClickListener) this);
    }



    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnc2f:
                outputValue(false);
                break;
            case R.id.btnf2c:
                outputValue(true);
                break;
            default:
        }
    }
    private boolean checkValidInput(){
        if(editInput.getText().length()==0){
            String errorMsg = getResources().getString(R.string.msg_error_input);
            Toast.makeText(this,errorMsg,Toast.LENGTH_LONG).show();
            return false;
        }else{
            return true;
        }
    }
    private void outputValue(boolean isF2C){
        if(checkValidInput()){
            float inputValue = Float.parseFloat(editInput.getText().toString());
            if(isF2C){
                String title = getResources().getString(R.string.fahren);
                title = title + String.valueOf(inputValue);
                title = title + getResources().getString(R.string.celsius);
                tvTitle.setText(title);
                tvResult.setText(String.valueOf(getF2C(inputValue)));
            }else{
                String title = getResources().getString(R.string.celsius);
                title = title + String.valueOf(inputValue);
                title = title + getResources().getString(R.string.fahren);
                tvTitle.setText(title);
                tvResult.setText(String.valueOf(getC2F(inputValue)));
            }
        }
    }
    private float getF2C(float f){
        return ((f-32.0f)/1.8f);
    }
    private float getC2F(float c){
        return (c*1.8f)+32.0f;
    }
}

