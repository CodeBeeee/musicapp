package com.app.musicapp.View.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.app.musicapp.R;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout usernicklayout,updatepdlayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        initView();
    }

    private void initView() {
        usernicklayout = findViewById(R.id.usernicklayout);
        updatepdlayout = findViewById(R.id.updatepdlayout);
        usernicklayout.setOnClickListener(this);
        updatepdlayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.usernicklayout:
                Intent i = new Intent(UserInfoActivity.this,UpdateNickNameActivity.class);
                startActivity(i);
                break;
            case R.id.updatepdlayout:
                Intent ii = new Intent(UserInfoActivity.this,UpdatePassWordActivity.class);
                startActivity(ii);
                break;
        }
    }
}
