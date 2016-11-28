package com.example.administrator.bluetoothseekingforproject;


import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


/*
*
* 设置页面
*
*
* */

public class Settings extends AppCompatActivity {

    private TextView btname;
    private TextView btaddress;
    private BluetoothAdapter bluetoothAdapter;
    private Button btcheckversion;
    private ImageButton btButton;
    private ImageButton moreButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        btname= (TextView) findViewById(R.id.txt_settings_btname);
        btaddress= (TextView) findViewById(R.id.txt_settings_btaddress);
        btcheckversion= (Button) findViewById(R.id.bt_settings_checkversion);
        btButton= (ImageButton) findViewById(R.id.bluetoothbutton_settings);
        moreButton= (ImageButton) findViewById(R.id.morebutton_settings);

        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        btname.setText(bluetoothAdapter.getName());
        btaddress.setText(bluetoothAdapter.getAddress());

        btname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出对话框，修改蓝牙名称

            }
        });

        btcheckversion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it=new Intent(Settings.this,CheckVersion.class);
                startActivity(it);
                finish();
            }
        });

        btButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Settings.this,MainPage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                overridePendingTransition(R.anim.transition_in_left_anim, R.anim.tansition_out_right_anim);
                startActivity(intent);

            }
        });
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Settings.this,MoreFunctionPage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                overridePendingTransition(R.anim.transition_in_left_anim, R.anim.tansition_out_right_anim);
                startActivity(intent);

            }
        });

    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
