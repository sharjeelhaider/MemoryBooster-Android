package com.booster.avivast.antivirus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.booster.avivast.R;


/**
 * Created by Magic Frame on 27/01/2016.
 */
public class SplashActivity extends Activity
{


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        //Log.i("Yeahhh", "============= SPLASHACTIVITY: oncreate called============");
        super.onCreate(savedInstanceState);


        setContentView(R.layout.splash);

        final AppData appData = AppData.getInstance(this);

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(3000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{

                    if(appData.getEulaAccepted())
                    {
                        Intent intent = new Intent(SplashActivity.this,AntivirusActivity.class);
                        startActivity(intent);
                    }else
                    {
                        Intent intent = new Intent(SplashActivity.this,EulaActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                    }

                }
            }
        };
        timerThread.start();
    }


    @Override
    protected void onPause()
    {
        super.onPause();
        finish();
    }
}
