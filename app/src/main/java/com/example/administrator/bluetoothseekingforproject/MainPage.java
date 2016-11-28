package com.example.administrator.bluetoothseekingforproject;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import adapter.BTBaseAdapter;
import application.BSApplication;
import database.DBhelper;
import entry.BtInfo;
import entry.BtInfoDao;
import receiver.BluetoothReceive;
import service.BluetoothService;
import tools.RftUtils;


/*
*
*
* 主页面
*
* */
public class MainPage extends AppCompatActivity {

    private ImageButton settingsbutton;
    private ImageButton morebutton;
    private Button seekbutton;
    private Switch openswitch;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothReceive bluetoothReceive;
    private PullToRefreshListView btlv;
    private TextView txt_MyBlt;
    private ImageView img_loading;
    List<BtInfo> btdata;
    BTBaseAdapter btBaseAdapter;
    CountDownTimer timer;
    Intent intent,serviceIntent;
    //此UUID表示串口服务
    static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    public  static  final String ACTION_DISCOVERY_STARTED="android.bluetooth.adapter.action.DISCOVERY_STARTED";
    public  static  final String ACTION_DISCOVERY_FINISH="android.bluetooth.adapter.action.DISCOVERY_FINISHED";
    BluetoothDevice bluetoothDevice;
    BluetoothSocket bluetoothSocket;
    String choice="";
    int rssi,selectflag,itemPosition;
    private CallInterface mBinder;
    private BtServiceConnection conn;
    boolean dialogFlag;
    AnimationDrawable ad;
    int distance;
    DBhelper dBhelper;
    //public static String EXTRA_DEVICE_ADDRESS = "device_address";
    //SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        settingsbutton= (ImageButton) findViewById(R.id.settingsbutton_main);
        morebutton= (ImageButton) findViewById(R.id.morebutton_main);
        btlv= (PullToRefreshListView) findViewById(R.id.showSeekBtInfo_listview_main);
        seekbutton= (Button) findViewById(R.id.seekingForBt_button_main);
        openswitch= (Switch) findViewById(R.id.openbluetooth_switch_main);
        txt_MyBlt= (TextView) findViewById(R.id.showMyBtInfo_txt_main);
        img_loading= (ImageView) findViewById(R.id.loading_image_main);
        ad= (AnimationDrawable) img_loading.getDrawable();
        dBhelper= DBhelper.getInstance(this);


        btdata=new ArrayList<BtInfo>();
        bluetoothReceive=new BluetoothReceive(){
            @Override
            public void onReceive(Context context, Intent intent) {
                super.onReceive(context, intent);
                String action=intent.getAction();
                dBhelper.clearBtInfo();
                if(BluetoothDevice.ACTION_FOUND.equals(action)){
                    BluetoothDevice device=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    BtInfo btInfo=new BtInfo();
                    if(device.getBondState()!=BluetoothDevice.BOND_BONDED){
                       rssi=intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
                       btInfo.setBtname(device.getName());
                       btInfo.setRssi(rssi);
                       btInfo.setBtmac(device.getAddress());
                       btdata.add(btInfo);

//                       sp=getSharedPreferences("btInfo",0);
//                       SharedPreferences.Editor editor=sp.edit();
//                       editor.putString("btname",device.getName());
//                       editor.putString("btmac",device.getAddress());
//                       editor.putInt("rssi", rssi);
//                       editor.commit();

                        dBhelper.addBtInfo(btInfo);
                        Log.d("++++", "执行添加数据库");
                    }else {
                        //读取数据库,加载本地数据的操作
                        BtInfoDao bid= BSApplication.getDaoSession(context).getBtInfoDao();
                        List<BtInfo> listdata=bid.loadAll();
                        if(listdata!=null&&listdata.size()>0){
                            btdata.addAll(listdata);
                        }





                        Log.d("++++", "执行对已经配对蓝牙的操作");
                        rssi=intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
                        btInfo.setBtname(device.getName());
                        btInfo.setRssi(rssi);
                        btInfo.setBtmac(device.getAddress());
                        btdata.add(btInfo);
                        Log.d("++++", "蓝牙名称：" + btInfo.getBtname());

                        dBhelper.addBtInfo(btInfo);
                        Log.d("++++", "执行添加数据库");
                    }




                }else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                    if(!btdata.isEmpty()){

                    }else{

                    }
                }else {

                }
            }
        };




        /*
        *
        *
        * 蓝牙扫描模块
        *
        * */
        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();

        //注册蓝牙BroadcastReceiver,调用onReceiver
        IntentFilter filter=new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bluetoothReceive, filter);

        //当搜素结束时，调用onReceiver
        filter=new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(bluetoothReceive,filter);



        //检测蓝牙设备
        if(bluetoothAdapter!=null){

            //对开关按钮进行同步
            if(bluetoothAdapter.isEnabled()){
                openswitch.setChecked(true);
            }else {

            }
            txt_MyBlt.setText(""+bluetoothAdapter.getName());



            //打开蓝牙操作
            openswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        bluetoothAdapter.enable();
                        if(bluetoothAdapter.isEnabled()){
                            ensureDiscoverable();
                            Toast.makeText(getApplication(),"正在打开蓝牙...",Toast.LENGTH_LONG).show();
                            Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivity(intent);

                        }
                    }else {
                        bluetoothAdapter.disable();
                        Toast.makeText(getApplication(),"正在关闭蓝牙...",Toast.LENGTH_LONG).show();
                        if(ad.isRunning()){
                            ad.stop();

                        }
                        if(BluetoothService.mediaPlayer.isPlaying()){
                            BluetoothService.mediaPlayer.stop();
                        }
                    }
                }
            });

            //扫描蓝牙
            seekbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(bluetoothAdapter.isEnabled()){
                        Toast.makeText(getApplication(),"开始扫描周边蓝牙设备..",Toast.LENGTH_LONG).show();
                        new Thread(new Runnable() {
                             @Override
                             public void run() {

                                 //扫描蓝牙设备  因为扫描是耗时操作，开个子线程执行
                                 bluetoothAdapter.startDiscovery();


                             }
                         }).start();
                        ad.start();
                        intent=new Intent();
                        intent.setAction("com.example.administrator.bluetoothseekingforproject");
                        intent.setPackage("com.example.administrator.bluetoothseekingforproject");
                        sendBroadcast(intent);
                        Log.d("++++", "发送广播");

                        timer=new CountDownTimer(3*10000,3*1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                //called every 1000 milliseconds, which could be used to
                                //send messages or some other action
                                Log.d("++++","扫描中...");


                            }

                            @Override
                            public void onFinish() {
                                //After 30000 milliseconds (30 sec) finish current
                                //if you would like to execute something when time finishes
                                Log.d("++++", "扫描结束或者超时");
                                bluetoothAdapter.cancelDiscovery();
                                initData();
                                ad.stop();

                            }
                        }.start();





                    }else{
                        Log.d("++++","扫描蓝牙失败");
                    }
                }
            });




        }else{
            Toast.makeText(getApplication(),"设备不支持蓝牙",Toast.LENGTH_LONG).show();
        }








        /*
        *
        *
        页面跳转部分
        *
        */
        settingsbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it=new Intent(MainPage.this,Settings.class);
                //如果没创建，则创建；否则从activity栈中搬上来，防止重复创建
                it.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                overridePendingTransition(R.anim.transition_in_left_anim,R.anim.tansition_out_right_anim);
                startActivity(it);

            }
        });

        morebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it=new Intent(MainPage.this,MoreFunctionPage.class);
                it.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                overridePendingTransition(R.anim.transition_in_left_anim, R.anim.tansition_out_right_anim);
                startActivity(it);

            }
        });

    }

    public void initData(){
        if(btdata!=null){
            btBaseAdapter=new BTBaseAdapter(getBaseContext(),btdata);
            btlv.setAdapter(btBaseAdapter);
            btBaseAdapter.notifyDataSetChanged();
            selectItem();
            Toast.makeText(getApplication(),"扫描结束",Toast.LENGTH_LONG).show();

        }else {
            Log.d("++++","btdata为空");
            Toast.makeText(getApplication(),"扫描结果为空，周围无蓝牙",Toast.LENGTH_LONG).show();
        }

        /*
        intent=getIntent();
        String action=intent.getAction();

        //判断扫描是否结束
        if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){

            Log.d("++++","进入扫描结束判断");

            if(intent!=null){
                //btdata= (List<BtInfo>) intent.getSerializableExtra("btdata");
                btBaseAdapter=new BTBaseAdapter(getBaseContext(),btdata);
                btlv.setAdapter(btBaseAdapter);
                btBaseAdapter.notifyDataSetChanged();

            }else {
                Log.d("++++","获取扫描结果失败");
            }


        }else {
            Log.d("++++","没运行？");
        }
        */



    }

    /*功能选择模块*/
    public void selectItem(){
        btlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                   dialogDiy(view);

                   Log.d("++++", "position:" + position);
                   itemPosition=position;

            }
        });
    }

    public void itemOptions(int position){
        if(selectflag==1){
            //执行配对操作
            bluetoothDevice=bluetoothAdapter.getRemoteDevice(""+btdata.get(position-1).getBtmac());
            Log.d("++++",btdata.get(position-1).getBtname());
            //调用RftUtils工具类
            RftUtils rftUtils=new RftUtils();
            rftUtils.createBond(BluetoothDevice.class, bluetoothDevice);
            Log.d("++++", "开始配对");
            connect(bluetoothDevice);
            BSApplication.getBsApplication().setConnectFlag(true);
        }else if(selectflag==2){
            //执行传输操作
            bluetoothDevice=bluetoothAdapter.getRemoteDevice(""+btdata.get(position-1).getBtmac());
            Log.d("++++",btdata.get(position-1).getBtname());
            //调用RftUtils工具类
            RftUtils rftUtils=new RftUtils();
            rftUtils.createBond(BluetoothDevice.class, bluetoothDevice);
            Log.d("++++", "开始传输");
            connect(bluetoothDevice);
            Intent it=new Intent(MainPage.this,BlueToothTransMissionPage.class);
            Bundle bundle=new Bundle();
            bundle.putString("address",btdata.get(position-1).getBtmac());
            bundle.putString("pageflag","True");
            it.putExtra("BlueToothTransMission",bundle);
            startActivity(it);


        }else if(selectflag==3){
            //执行粗略测距操作
            Log.d("++++", "rssi：" + btdata.get(position - 1).getRssi());
            if(btdata.get(position-1).getRssi()<-10){
                conn=new BtServiceConnection();
                bindService(serviceIntent,conn,BIND_AUTO_CREATE);
                Log.d("++++","绑定服务");
            }

            /*
            *
            * 测距公式distance=10^((abs(RSSI)-A)/(10*n))
            *
            * A是发射端和接收端相隔1米时的信号强度，n是环境衰减因子（经验赋值2.0）
            *
            * */
            int aimRssi=Math.abs(btdata.get(position - 1).getRssi());
            int powTemp=(aimRssi-50)/(10*2);
            distance= (int) Math.pow(10,powTemp);
            Toast.makeText(getApplication(),"两设备间的距离为"+distance+"米",Toast.LENGTH_LONG).show();

        }else if(selectflag==4){
            //执行停止操作
            if(BluetoothService.mediaPlayer.isPlaying()){
                BluetoothService.mediaPlayer.stop();
            }
        }else if(selectflag==5){

            bluetoothDevice=bluetoothAdapter.getRemoteDevice(""+btdata.get(position-1).getBtmac());
            Log.d("++++",btdata.get(position-1).getBtname());
            //调用RftUtils工具类
            RftUtils rftUtils=new RftUtils();
            rftUtils.createBond(BluetoothDevice.class, bluetoothDevice);
            Log.d("++++", "开始配对");
            connect(bluetoothDevice);
            Intent it=new Intent(MainPage.this,DetailDistance.class);
            Bundle bundle=new Bundle();
            bundle.putInt("rssi",btdata.get(position-1).getRssi());
            it.putExtra("detailbundle",bundle);
            startActivity(it);

        }else if(selectflag ==6){
            Intent it=new Intent(MainPage.this,MoreFunctionPage.class);
            it.putExtra("address",btdata.get(position-1).getBtmac());
            startActivity(it);
        }
    }



    /*连接实现部分*/
    public void connect(BluetoothDevice bluetoothDevice){
        UUID uuid=UUID.fromString(SPP_UUID);
        try {
            bluetoothSocket=bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
            Log.d("++++","开始连接");
            bluetoothSocket.connect();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*对话框生成部分*/
    public void dialogDiy(View view){
        final String[] options={"配对","传输","粗略测距","详细测距","停止","聊天"};
        choice="配对";
        dialogFlag=false;
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
                      builder.setTitle("蓝牙功能列表")
                      .setSingleChoiceItems(options, 0, new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialog, int which) {
                              choice=options[which];
                              //Log.d("++++","选择"+choice);
                          }
                      })
                      .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialog, int which) {
                          }
                      })
                      .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialog, int which) {
                              Toast.makeText(getApplication(),"执行功能："+choice,Toast.LENGTH_LONG).show();
                              Log.d("++++", "确定后的:" + choice);

                              dialogFlag=true;

                              if(choice.equals("配对")){
                                  selectflag=1;
                              }else if(choice.equals("传输")){
                                  selectflag=2;
                              }else if(choice.equals("粗略测距")){
                                  selectflag=3;
                              }else if(choice.equals("停止")){
                                  selectflag=4;
                              }else if(choice.equals("详细测距")){
                                  selectflag=5;
                              }else if(choice.equals("聊天")){
                                  selectflag=6;
                              }else {
                                  selectflag=0;
                              }
                              Log.d("++++", "selectflag:" + selectflag);
                              Log.d("++++", "itemPosition:" + itemPosition);
                              itemOptions(itemPosition);
                          }
                      })
                      .setCancelable(true)
                      .show();
    }


    private class BtServiceConnection implements ServiceConnection{

        /**
         * Called when a connection to the Service has been established, with
         * the {@link IBinder} of the communication channel to the
         * Service.
         *
         * @param name    The concrete component name of the service that has
         *                been connected.
         * @param service The IBinder of the Service's communication channel,
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MainPage.this.mBinder= (CallInterface) service;
            mBinder.CallPlay();
        }

        /**
         * Called when a connection to the Service has been lost.  This typically
         * happens when the process hosting the service has crashed or been killed.
         * This does <em>not</em> remove the ServiceConnection itself -- this
         * binding to the service will remain active, and you will receive a call
         * to {@link #onServiceConnected} when the Service is next running.
         *
         * @param name The concrete component name of the service whose
         *             connection has been lost.
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.  This means
     * that in some cases the previous state may still be saved, not allowing
     * fragment transactions that modify the state.  To correctly interact
     * with fragments in their proper state, you should instead override
     * {@link #onResumeFragments()}.
     */
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter=new IntentFilter("com.example.administrator.bluetoothseekingforproject.RECEIVER_ACTION");
        registerReceiver(bluetoothReceive,filter);
        Log.d("++++","动态注册广播");
    }

    public void ensureDiscoverable() {
        if (bluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }
    /**
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        super.onPause();



    }

    /**
     * Dispatch onStart() to all fragments.  Ensure any created loaders are
     * now started.
     */
    @Override
    protected void onStart() {
        super.onStart();
        //开启蓝牙服务
        serviceIntent=new Intent(MainPage.this,BluetoothService.class);
        startService(serviceIntent);
        Log.d("++++", "开启服务");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bluetoothReceive);
        Log.d("++++", "解除广播注册");
        if(timer!=null){
            timer.cancel();
            timer=null;
        }
        if(conn!=null){
            unbindService(conn);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        stopService(serviceIntent);

    }

    /**
     * Handle onNewIntent() to inform the fragment manager that the
     * state is not saved.  If you are handling new intents and may be
     * making changes to the fragment state, you want to be sure to call
     * through to the super-class here first.  Otherwise, if your state
     * is saved but the activity is not stopped, you could get an
     * onNewIntent() call which happens before onResume() and trying to
     * perform fragment operations at that point will throw IllegalStateException
     * because the fragment manager thinks the state is still saved.
     *
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_page, menu);
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


    public interface CallInterface{
        public void CallPlay();
    }
}
