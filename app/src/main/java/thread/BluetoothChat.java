package thread;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import bluetoothinterface.Contants;

/**
 * Created by Administrator on 2016/4/5.
 */
public class BluetoothChat {


    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
    private int mState;

    private AcceptThread mSecureAcceptThread;
    private AcceptThread mInsecureAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;

    private static final String NAME_SECURE = "BluetoothChatSecure";
    private static final String NAME_INSECURE = "BluetoothChatInsecure";

    private BluetoothSocket socket;

    //fa87c0d0-afac-11de-8a39-0800200c9a66
    private static final UUID MY_UUID_SECURE =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //8ce255c0-200a-11e0-ac64-0800200c9a66
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device

    public BluetoothChat(Context context,Handler mHandler) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mState=STATE_NONE;
        this.mHandler = mHandler;
    }

    public synchronized int getmState() {
        return mState;
    }

    public synchronized void setmState(int mState) {
        this.mState = mState;
        mHandler.obtainMessage(Contants.MESSAGE_STATE_CHANGE, mState, -1).sendToTarget();
    }

    public synchronized void start() {
        Log.d("++++","start run");

        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        setmState(STATE_LISTEN);

        // Start the thread to listen on a BluetoothServerSocket
        if (mSecureAcceptThread == null) {
            mSecureAcceptThread = new AcceptThread(true);
            mSecureAcceptThread.start();
        }
        if (mInsecureAcceptThread == null) {
            mInsecureAcceptThread = new AcceptThread(false);
            mInsecureAcceptThread.start();
        }
    }
    public synchronized void connect(BluetoothDevice device, boolean secure) {

        Log.d("++++","connect run");
        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device, secure);
        mConnectThread.start();
        setmState(STATE_CONNECTING);
    }
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice
            device, final String socketType) {

        Log.d("++++","connected run");
        // Cancel the thread that completed the connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Cancel the accept thread because we only want to connect to one device
//        if (mSecureAcceptThread != null) {
//            mSecureAcceptThread.cancel();
//            mSecureAcceptThread = null;
//        }
//        if (mInsecureAcceptThread != null) {
//            mInsecureAcceptThread.cancel();
//            mInsecureAcceptThread = null;
//        }

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket,socketType);
        mConnectedThread.start();

        // Send the name of the connected device back to the UI Activity
//        Message msg = mHandler.obtainMessage(Contants.MESSAGE_DEVICE_NAME);
//        Bundle bundle = new Bundle();
//        bundle.putString(Contants.DEVICE_NAME, device.getName());
//        msg.setData(bundle);
//        mHandler.sendMessage(msg);

        setmState(STATE_CONNECTED);
    }
    public synchronized void stop() {

        Log.d("++++","stop run");
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

//        if (mSecureAcceptThread != null) {
//            mSecureAcceptThread.cancel();
//            mSecureAcceptThread = null;
//        }
//
//        if (mInsecureAcceptThread != null) {
//            mInsecureAcceptThread.cancel();
//            mInsecureAcceptThread = null;
//        }
        setmState(STATE_NONE);
    }
    public void write(byte[] out) {

        Log.d("++++","write run");
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }


    private void connectionFailed() {
        Log.d("++++","connectionFailed run");
        // Send a failure message back to the Activity
//        Message msg = mHandler.obtainMessage(Contants.MESSAGE_TOAST);
//        Bundle bundle = new Bundle();
//        bundle.putString(Contants.TOAST, "Unable to connect device");
//        msg.setData(bundle);
//        mHandler.sendMessage(msg);

        // Start the service over to restart listening mode
        BluetoothChat.this.start();
    }

    private void connectionLost() {
        Log.d("++++","connectionLost run");
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(Contants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Contants.TOAST, "Device connection was lost");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        // Start the service over to restart listening mode
        BluetoothChat.this.start();
    }
    public class AcceptThread extends Thread{

        private final BluetoothServerSocket mmServerSocket;
        private String mSocketType;

        public AcceptThread(boolean secure) {
            BluetoothServerSocket tmp = null;
            mSocketType = secure ? "Secure" : "Insecure";

            // Create a new listening server socket
            try {
                if (secure) {
                    tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME_SECURE,
                            MY_UUID_SECURE);
                } else {
                    tmp = mAdapter.listenUsingInsecureRfcommWithServiceRecord(
                            NAME_INSECURE, MY_UUID_INSECURE);
                }
            } catch (IOException e) {
                Log.d("++++","secure异常捕获："+e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            Log.d("++++","AcceptThread run");
            setName("AcceptThread" + mSocketType);

            socket = null;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // Listen to the server socket if we're not connected
                    while (mState != STATE_CONNECTED) {

                        try {
                            Thread.sleep(300);
                            // This is a blocking call and will only return on a
                            // successful connection or an exception
                            socket = mmServerSocket.accept();

                            Log.d("++++","socket获取成功");
                        } catch (IOException e) {
                            Log.d("++++","socket获取失败："+e);
                            break;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            break;
                        }

                        // If a connection was accepted
                        if (socket != null) {
                            synchronized (BluetoothChat.this) {
                                switch (mState) {
                                    case STATE_LISTEN:
                                    case STATE_CONNECTING:
                                        // Situation normal. Start the connected thread.
                                        connected(socket, socket.getRemoteDevice(),
                                                mSocketType);
                                        break;
                                    case STATE_NONE:
                                    case STATE_CONNECTED:
                                        // Either not ready or already connected. Terminate new socket.
                                        try {
                                            socket.close();
                                            Log.d("++++","socket关闭");
                                        } catch (IOException e) {
                                            Log.d("++++","mState异常捕获："+e);
                                        }
                                        break;
                                }
                            }
                        }
                    }
                }
            }).start();



        }

        public void cancel() {
            Log.d("++++","AcceptThread cancel");
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.d("++++","mmServerSocket异常捕获："+e);
            }
        }
    }

    public class ConnectThread extends Thread{
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private String mSocketType;

        public ConnectThread(BluetoothDevice device, boolean secure) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            mSocketType = secure ? "Secure" : "Insecure";

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                if (secure) {
                    tmp = device.createRfcommSocketToServiceRecord(
                            MY_UUID_SECURE);
                } else {
                    tmp = device.createInsecureRfcommSocketToServiceRecord(
                            MY_UUID_INSECURE);
                }
            } catch (IOException e) {
                Log.d("++++","secure异常捕获："+e);
            }
            mmSocket = tmp;
            if(mmSocket==null){
                Log.d("++++","socket为空");
            }else {

            }

        }
        public void run() {
            Log.d("++++","ConnectThread run");
            setName("ConnectThread" + mSocketType);

            // Always cancel discovery because it will slow down a connection
            //mAdapter.cancelDiscovery();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // Make a connection to the BluetoothSocket
                    try {
                        // This is a blocking call and will only return on a
                        // successful connection or an exception

                        if(mmSocket.isConnected()){
                            Log.d("++++","socket已连接");
                        }else {
                            mmSocket.connect();
                        }

                    } catch (IOException e) {

                        Log.d("++++","socket连接失败:"+e);

                        // Close the socket
                        try {
                            mmSocket.close();
                        } catch (IOException e2) {
                            Log.d("++++","mmSocket异常捕获："+e2);
                        }
                        connectionFailed();
                        return;
                    }
                }
            }).start();


            // Reset the ConnectThread because we're done
            synchronized (BluetoothChat.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(mmSocket, mmDevice, mSocketType);
        }

        public void cancel() {
            Log.d("++++","ConnectThread cancel");
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.d("++++","mmSocket异常捕获："+e);
            }
        }
    }

    public class ConnectedThread extends Thread{


        private BluetoothSocket mmSocket;
        private InputStream mmInStream;
        private OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket, String socketType) {
            Log.d("++++","ConnectedThread进入构造");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                //mmSocket.connect();
                tmpIn = mmSocket.getInputStream();
                tmpOut = mmSocket.getOutputStream();
            } catch (IOException e) {
                Log.d("++++","流异常捕获："+e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;

        }

        public void run() {
            Log.d("++++","ConnectedThread run");
            byte[] buffer = new byte[1024];
            int bytes;

            if(mmInStream==null){
                Log.d("++++","mmInStream为null");
            }else {
                Log.d("++++","mmInStream非null");
            }
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);

                    // Send the obtained bytes to the UI Activity
                    mHandler.obtainMessage(Contants.MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget();
                } catch (IOException e) {
                    Log.d("++++","mmInStream异常捕获："+e);
                    connectionLost();
                    // Start the service over to restart listening mode
                    BluetoothChat.this.start();
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         *
         * @param buffer The bytes to write
         */
        public void write(byte[] buffer) {
            Log.d("++++","ConnectedThread write");
            try {
                mmOutStream.write(buffer);

                // Share the sent message back to the UI Activity
                mHandler.obtainMessage(Contants.MESSAGE_WRITE, -1, -1, buffer)
                        .sendToTarget();
            } catch (IOException e) {
                Log.d("++++","mmOutStream异常捕获："+e);
            }
        }

        public void cancel() {
            Log.d("++++","ConnectedThread cancel");
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.d("++++","mmSocket异常捕获："+e);
            }
        }
    }

}
