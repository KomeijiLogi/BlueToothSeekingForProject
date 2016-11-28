package com.example.administrator.bluetoothseekingforproject;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import tools.OpenFile;


/*
*
*
* 蓝牙传输页面
*
* */
public class BlueToothTransMissionPage extends AppCompatActivity implements View.OnClickListener{

    static  final String FilePath= Environment.getExternalStorageDirectory().getPath()+"/androidesk/wallpapers/4da99eba9e499133ce001220.jpg";
    private Button bt_transmission;
    PopupWindow window;
    Uri uri=Uri.parse("content://com.android.bluetooth.opp/btopp");
    View v_transmission;
    //默认文件
    public static final int DEFAULT_TRANSMISSION=0;
    //自定义文件
    public static final int LOCALHOST_TRANSMISSION=1;

    public static final int FLAG_CHOOSE_FILE=2;
    BluetoothDevice btDevice;
    BluetoothAdapter btAdapter;
    BluetoothSocket btSocket;
    String mFilePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_tooth_info_page);
        bt_transmission= (Button) findViewById(R.id.bt_transmission_bluetoothinfo);
        v_transmission= LayoutInflater.from(this).inflate(R.layout.layout_for_transmission,null);
        v_transmission.findViewById(R.id.bt_default).setOnClickListener(this);
        v_transmission.findViewById(R.id.bt_localhost).setOnClickListener(this);
        v_transmission.findViewById(R.id.bt_cancel).setOnClickListener(this);
        btAdapter=BluetoothAdapter.getDefaultAdapter();

        bt_transmission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window = getPopWindow(v_transmission);
            }
        });

        //Log.d("++++","path:"+ Environment.getExternalStorageDirectory().getPath());




    }

    public PopupWindow getPopWindow(View v){
        PopupWindow popupWindow=new PopupWindow(v,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        WindowManager.LayoutParams layoutParams=getWindow().getAttributes();
        layoutParams.alpha=0.7f;
        getWindow().setAttributes(layoutParams);
        popupWindow.setBackgroundDrawable(new ColorDrawable());

        popupWindow.showAtLocation(bt_transmission, Gravity.BOTTOM, 0, 0);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
                layoutParams.alpha = 1f;
                getWindow().setAttributes(layoutParams);
            }
        });
        return popupWindow;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_blue_tooth_info_page, menu);
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

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_default:
            Intent it=getIntent();
                if(it!=null){
                    Bundle bundle=it.getBundleExtra("BlueToothTransMission");
                    String pageFlag=bundle.getString("pageflag");
                    if(pageFlag.equals("True")){
                        btAdapter.enable();
                        String address=bundle.getString("address");
                        btDevice=btAdapter.getRemoteDevice(address);
                        Method m=null;
                        try {
                            m=btDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
                            btSocket= (BluetoothSocket) m.invoke(btDevice, 1);
                            btSocket.connect();
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        ContentValues cv=new ContentValues();
                        cv.put("FilePath",FilePath);
                        cv.put("address",address);
                        cv.put("direction",0);
                        long time=System.currentTimeMillis();
                        cv.put("time", time);
                        getContentResolver().insert(uri, cv);
//                         Intent sharingIntent=new Intent(android.content.Intent.ACTION_SEND);
//                        sharingIntent.setType("*/*");
//                        sharingIntent.setComponent(new ComponentName("com.android.bluetooth", "com.android.bluetooth.opp.BluetoothOppLauncherActivity"));
//                        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
//                        startActivity(sharingIntent);

                        try {
                            btSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
            }
                break;
            case R.id.bt_localhost:

                Intent it_local=getIntent();
                if(it_local!=null){
                    Bundle bundle=it_local.getBundleExtra("BlueToothTransMission");
                    String pageFlag=bundle.getString("pageflag");

                    if(pageFlag.equals("True")){
                        btAdapter.enable();
                        String address=bundle.getString("address");
                        btDevice=btAdapter.getRemoteDevice(address);
                        Method m=null;
                        try {
                            m=btDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
                            btSocket= (BluetoothSocket) m.invoke(btDevice, 1);
                            btSocket.connect();
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }



                        //打开文件浏览器
                        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("*/*");
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        startActivityForResult(intent, FLAG_CHOOSE_FILE);


                        ContentValues cv=new ContentValues();
                        cv.put("FilePath",FilePath);
                        cv.put("address",address);
                        cv.put("direction",0);
                        long time=System.currentTimeMillis();
                        cv.put("time", time);
                        getContentResolver().insert(uri, cv);


                        try {
                            btSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                break;
            case R.id.bt_cancel:
                window.dismiss();
                break;
            default:
                break;
        }
    }

    /**
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if(window != null) {
            window.dismiss();
            window = null;
        }
    }

    /**
     * Dispatch incoming result to the correct fragment.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //是否选择，没选择就不会继续
        if(resultCode!= Activity.RESULT_OK){
            return;
        }
        switch (requestCode){
            case DEFAULT_TRANSMISSION:
                dialogShow();
                break;
            case LOCALHOST_TRANSMISSION:
                break;
            case FLAG_CHOOSE_FILE:
                //得到uri，后面就是将uri转化成file的过程
                Uri uri = data.getData();
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor actualimagecursor = managedQuery(uri, proj, null, null, null);
                int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                actualimagecursor.moveToFirst();
                String img_path = actualimagecursor.getString(actual_image_column_index);
                File file = new File(img_path);
                Toast.makeText(BlueToothTransMissionPage.this, file.toString(), Toast.LENGTH_SHORT).show();
                dialogShow();
                break;

            default:
                break;
        }
    }

    public void dialogShow(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        View v_dialog=LayoutInflater.from(this).inflate(R.layout.layout_for_dialog, null);
        builder.setView(v_dialog);
        final AlertDialog ad=builder.create();
        v_dialog.findViewById(R.id.bt_ad_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               ad.dismiss();
            }
        });
        v_dialog.findViewById(R.id.bt_ad_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.dismiss();
            }
        });
        TextView txt_ad_title= (TextView) v_dialog.findViewById(R.id.title_ad);
        txt_ad_title.setText("蓝牙传输提示...");
        TextView txt_ad_subject= (TextView) v_dialog.findViewById(R.id.subject_ad);
        txt_ad_subject.setText("是否传输文件？");
        ad.show();

    }
}
