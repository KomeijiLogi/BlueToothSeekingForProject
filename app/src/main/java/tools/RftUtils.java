package tools;

import android.bluetooth.BluetoothDevice;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Administrator on 2016/3/17.
 */
public class RftUtils {
    Boolean result,returnValue;

    public boolean autoBond(Class btClass,BluetoothDevice device,String strPin){
        try {
            Method autoBondMethod =btClass.getMethod("setPin",new Class[]{byte.class});
            result = (Boolean) autoBondMethod.invoke(device,new Object[]{strPin.getBytes()});
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean createBond(Class btClass,BluetoothDevice device){

        try {
            Method createBondMethod=btClass.getMethod("createBond");
            returnValue= (Boolean) createBondMethod.invoke(device);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return returnValue;

    }

    public boolean createRfcommSocket(Class btClass,BluetoothDevice device){
        //映射方法可能有问题返回了NULL
        try {
            Method createRfcommSocketMethod=btClass.getMethod("createRfcommSocket");
            returnValue= (Boolean) createRfcommSocketMethod.invoke(device);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return returnValue;
    }
}
