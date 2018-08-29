package com.witcher.imedittext;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private IMEditText editText;
    private ImageView iv1,iv2,iv3,iv4;
    private Button test1,test2,at1,at2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.et_input);
        iv1 = findViewById(R.id.icon1);
        iv2 = findViewById(R.id.icon2);
        iv3 = findViewById(R.id.icon3);
        iv4 = findViewById(R.id.icon4);
        test1 = findViewById(R.id.test1);
        test2 = findViewById(R.id.test2);
        at1 = findViewById(R.id.tv_at1);
        at2 = findViewById(R.id.tv_at2);
        iv1.setOnClickListener(this);
        iv2.setOnClickListener(this);
        iv3.setOnClickListener(this);
        iv4.setOnClickListener(this);
        test1.setOnClickListener(this);
        test2.setOnClickListener(this);
        at1.setOnClickListener(this);
        at2.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.icon1:{
                addIcon("[1]");
            }break;
            case R.id.icon2:{
                addIcon("[2]");
            }break;
            case R.id.icon3:{
                addIcon("[3]");
            }break;
            case R.id.icon4:{
                addIcon("[4]");
            }break;
            case R.id.test1:{
                editText.test1();
            }
                break;
            case R.id.test2:{
                editText.delete();
            }
            break;
            case R.id.tv_at1:{
                editText.at("[@大胖猫]");
            }
            break;
            case R.id.tv_at2:{
                editText.at("[@大黄]");
            }
            break;
        }
    }

    private void addIcon(String source){
        editText.addIcon(source);
    }
}
