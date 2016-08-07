package com.raihanbd.easyrambooster.antivirus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import geniuscloud.memory.booster.R;


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

        _acceptEula.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AppData appData = AppData.getInstance(EulaActivity.this);
                appData.setEulaAccepted(true);
                appData.serialize(EulaActivity.this);
                Intent intent = new Intent(EulaActivity.this,AntivirusActivity.class);
                startActivity(intent);
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
