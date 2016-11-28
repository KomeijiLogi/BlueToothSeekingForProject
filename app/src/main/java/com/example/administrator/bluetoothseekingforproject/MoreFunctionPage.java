package com.example.administrator.bluetoothseekingforproject;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import application.BSApplication;
import bluetoothinterface.Contants;
import thread.BluetoothChat;


public class MoreFunctionPage extends AppCompatActivity {
    private ImageButton bluetoothButton;
    private ImageButton settingsButton;

    //通信状态
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
    //布局视图
    private ListView mConversationView;
    private EditText mOutEditText;
    private Button mSendButton;
    //连接设备名称
    private String mConnectedDeviceName = null;

    private ArrayAdapter<String> mConversationArrayAdapter;
    //输出信息的StringBuffer
    private StringBuffer mOutStringBuffer;

    private BluetoothAdapter mBluetoothAdapter = null;

    public BluetoothChat bluetoothChat;
    Handler mhandler=new Handler(){
        /**
         * Subclasses must implement this to receive messages.
         *
         * @param msg
         */
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case Contants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChat.STATE_CONNECTED:

                            mConversationArrayAdapter.clear();
                            break;
                        case BluetoothChat.STATE_CONNECTING:

                            break;
                        case BluetoothChat.STATE_LISTEN:

                        case BluetoothChat.STATE_NONE:

                            break;
                    }
                    break;
                case Contants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // 从缓存中构建字符串
                    String writeMessage = new String(writeBuf);
                    mConversationArrayAdapter.add("Me:  " + writeMessage);
                    mConversationArrayAdapter.notifyDataSetChanged();
                    break;
                case Contants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // 从缓存中的有效字节构建字符串
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                    mConversationArrayAdapter.notifyDataSetChanged();
                    break;
                case Contants.MESSAGE_DEVICE_NAME:
                    // 保存设备名称
                    mConnectedDeviceName = msg.getData().getString(Contants.DEVICE_NAME);
                    break;
                case Contants.MESSAGE_TOAST:
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_page);
        bluetoothButton= (ImageButton) findViewById(R.id.bluetoothbutton_more);
        settingsButton= (ImageButton) findViewById(R.id.settingsbutton_more);
        mConversationView= (ListView) findViewById(R.id.in_lv_more);
        mOutEditText= (EditText) findViewById(R.id.edit_text_out_more);
        mSendButton= (Button) findViewById(R.id.button_send_more);

        mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();




        /*跳转页面*/
        bluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MoreFunctionPage.this,MainPage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MoreFunctionPage.this,Settings.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

    }


    public void sendMessage(String message){
          if(mBluetoothAdapter!=null){
              if(mBluetoothAdapter.enable()){
                  //判断是否与其他蓝牙设备连接
//                      if(bluetoothChat.getmState()!=BluetoothChat.STATE_CONNECTED){
//                        Toast.makeText(getApplication(),"请于右上角选择模式",Toast.LENGTH_LONG).show();
//                        return;
//
//                      }

                      if(message.length()>0){
                         byte[] send=message.getBytes();
                         bluetoothChat.write(send);

                         //对输入框进行清除操作
                         mOutStringBuffer.setLength(0);
                         mOutEditText.setText(mOutStringBuffer);

                      }


              }else {
                  Toast.makeText(getApplication(),"请先打开蓝牙...",Toast.LENGTH_LONG).show();
              }
          }


    }


    public void setupChat(){

        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.layout_for_message);

        mConversationView.setAdapter(mConversationArrayAdapter);


        mOutEditText.setOnEditorActionListener(mWriteListener);


        mSendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                    Intent data=getIntent();
                    connectDevice(data,true);
                    TextView textView = (TextView) findViewById(R.id.edit_text_out_more);
                    String message = textView.getText().toString();
                    sendMessage(message);


            }
        });


        bluetoothChat= new BluetoothChat(this,mhandler);
        //bluetoothChat.start();


        mOutStringBuffer = new StringBuffer("");
    }

    public TextView.OnEditorActionListener mWriteListener
            = new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                sendMessage(message);
            }
            return true;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bluetoothChat != null) {

        }
    }


    /**
     * Dispatch onStart() to all fragments.  Ensure any created loaders are
     * now started.
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else if (bluetoothChat == null) {
            setupChat();
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
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == this.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == this.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == this.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred

                    //this.finish();
                }
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
        if(bluetoothChat!=null){
            if (bluetoothChat.getmState() == BluetoothChat.STATE_NONE) {
                // Start the Bluetooth chat services
                bluetoothChat.start();
            }
        }
    }

    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString("address");
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        bluetoothChat.connect(device, secure);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_more_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
        Intent data=getIntent();
        if (id == R.id.action_secure_connect){

            connectDevice(data,true);
        }else if(id == R.id.action_insecure_connect){
            connectDevice(data,false);
        }
        return super.onOptionsItemSelected(item);
    }
}
