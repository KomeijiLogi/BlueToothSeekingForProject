package com.example.administrator.bluetoothseekingforproject;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import application.BSApplication;
import tools.AsyncTaskHttpUtilTools;

/*
*
*
* 版本检测页面
*
* */
public class CheckVersion extends AppCompatActivity {

    private TextView txt_showversion_old;
    private TextView txt_showversion_new;
    private Context context;
    private RequestQueue rq;
    private Button bt_checkversion;
    private File root;
    private File myDir;
    //服务器APK路径
    final String apkurl= AsyncTaskHttpUtilTools.url+"BluetoothService/apk";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_version);
        txt_showversion_old= (TextView) findViewById(R.id.txt_showversion_old);
        txt_showversion_new= (TextView) findViewById(R.id.txt_showversion_new);
        bt_checkversion= (Button) findViewById(R.id.bt_checkversion);
        txt_showversion_old.setText("当前版本号："+BSApplication.bsApplication.getVersion());

        context=getBaseContext();
        rq= Volley.newRequestQueue(this);
        bt_checkversion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringRequest sreq=new StringRequest(apkurl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject apk_json=new JSONObject(s);
                            int newversion= Integer.parseInt(apk_json.getString("vid"));
                            txt_showversion_new.setText("最新版本号："+newversion);
                            String dl_url= AsyncTaskHttpUtilTools.url+"BluetoothService/"+apk_json.getString("url");
                            int oldversion= BSApplication.getBsApplication().getVersion();
                            if(oldversion<newversion){


                                String stus= Environment.getExternalStorageState();
                                if(stus.equals(Environment.MEDIA_MOUNTED)){

                                    root=Environment.getExternalStorageDirectory();
                                    myDir=new File(root,getPackageName());
                                    if(!myDir.exists()){
                                        myDir.mkdir();
                                    }
                                    showDialog();



                                }

                            }else{
                                Toast.makeText(getApplication(), "当前版本为最新版，无需更新...", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.d("++++++", "下载错误");
                    }
                });
                rq.add(sreq);
            }
        });



    }
    public void showDialog(){
        new AlertDialog.Builder(this)
                .setTitle("版本检测")
                .setMessage("发现新版本，是否开始下载...")
                .setCancelable(true)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        download();

                    }
                })
                .setCancelable(true)
                .show();

    }
    public void download(){

        DownloadManager downloadManager=
                (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        String downurl= AsyncTaskHttpUtilTools.url+"BluetoothService/apk/aa.apk";
        DownloadManager.Request request=
                new DownloadManager.Request(Uri.parse(downurl));
        request.setDestinationInExternalPublicDir("/apk/","aa.apk");
        request.setNotificationVisibility(
                DownloadManager.Request.NETWORK_MOBILE |
                        DownloadManager.Request.NETWORK_WIFI);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        downloadManager.enqueue(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_check_version, menu);
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
