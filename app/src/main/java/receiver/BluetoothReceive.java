package receiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;

import com.example.administrator.bluetoothseekingforproject.MainPage;

import java.util.ArrayList;
import java.util.List;

import entry.BluetoothInfo;

/**
 * Created by Administrator on 2016/1/25.
 */
public class BluetoothReceive extends BroadcastReceiver {
    ArrayList<BluetoothInfo> btdata;


    @Override
    public void onReceive(Context context, Intent intent) {

//        btdata=new ArrayList();
//
//        String action=intent.getAction();
//
//        if(BluetoothDevice.ACTION_FOUND.equals(action)){
//            BluetoothDevice device=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//            //判断蓝牙设备是否未曾配对过
//            if(device.getBondState()!=BluetoothDevice.BOND_BONDED){
//
//                //Log.d("++++", device.getAddress() + "  " + device.getName() + " ");
//                BluetoothInfo bluetoothInfo=new BluetoothInfo();
//                bluetoothInfo.setBtname(device.getName());
//                bluetoothInfo.setBtaddress(device.getAddress());
//                bluetoothInfo.setRssi("");
//                btdata.add(bluetoothInfo);
//
//
//
//
//            }else {
//                Log.d("++++", "执行配对蓝牙的操作");
//
//                BluetoothInfo bluetoothInfo=new BluetoothInfo();
//                bluetoothInfo.setBtname(device.getName());
//                bluetoothInfo.setBtaddress(device.getAddress());
//                bluetoothInfo.setRssi("");
//                btdata.add(bluetoothInfo);
//                Log.d("++++", "蓝牙名称：" + bluetoothInfo.getBtname());
//
//
//
//
//            }
//        }else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
//            if(btdata.isEmpty()){
//
//            }else{
//                intent.setClass(context, MainPage.class);
//                intent.putExtra("btdata",btdata);
//                context.startActivity(intent);
//            }
//        }else {
//            Log.d("++++", "未知错误");
//        }
//
//
//
//
//



    }
}
