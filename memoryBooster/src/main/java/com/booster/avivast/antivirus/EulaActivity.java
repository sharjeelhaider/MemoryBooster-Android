package com.booster.avivast.antivirus;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.booster.avivast.MainActivity;
import com.booster.avivast.R;


/**
 * Created by Magic Frame on 27/01/2016.
 */
public class EulaActivity extends Activity
{




    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eula);
        Button _acceptEula = (Button) findViewById(R.id.accept_eula_button);
        Button _declineEula = (Button) findViewById(R.id.decline_eula_button);
        if (Build.VERSION.SDK_INT >= 21)
            getWindow().setStatusBarColor(Color.parseColor("#ff790b"));

        final AppData appData = AppData.getInstance(this);
        if(appData.getEulaAccepted())
        {
            Intent intent = new Intent(EulaActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        /*Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(500);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{

                    if(appData.getEulaAccepted())
                    {
                        Intent intent = new Intent(EulaActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else
                    {
                        *//*Intent intent = new Intent(EulaActivity.this,EulaActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);*//*
                    }

                }
            }
        };
        timerThread.start();
*/
        _acceptEula.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AppData appData = AppData.getInstance(EulaActivity.this);
                appData.setEulaAccepted(true);
                appData.serialize(EulaActivity.this);
                Intent intent = new Intent(EulaActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        _declineEula.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

    }



}
