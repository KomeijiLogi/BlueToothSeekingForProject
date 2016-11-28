package com.example.administrator.bluetoothseekingforproject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import tools.CustomViewByMySelf;
import tools.RandomTextView;

public class DetailDistance extends AppCompatActivity {
    CustomViewByMySelf customViewByMySelf;
    private TextView txt_distance;
    private TextView txt_direction;
    Handler handler=new Handler(){
        /**
         * Subclasses must implement this to receive messages.
         *
         * @param msg
         */
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){
                if(customViewByMySelf.getCustomCanvas()!=null){
                    Log.d("++++", "执行绘点操作！");
                    customViewByMySelf.reDraw(rssi);
                    //customViewByMySelf.pointPaint(rssi, customViewByMySelf.getCustomPaint());
                    //Log.d("++++","activity canvas:"+customViewByMySelf.getCustomCanvas());
                    customViewByMySelf.invalidate();

                }else {
                    Log.d("++++","canvans为null");
                }

            }

        }
    };
    int rssi,distance;
    RandomTextView randomTextView;
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_distance);

        txt_distance= (TextView) findViewById(R.id.txt_distance_detail);
        txt_direction= (TextView) findViewById(R.id.txt_direction_detail);



        customViewByMySelf=new CustomViewByMySelf(this,null);
        randomTextView= (RandomTextView) findViewById(R.id.randomTextView_DetailDistance);


        Intent it=getIntent();
        Bundle bundle=it.getBundleExtra("detailbundle");
        rssi=bundle.getInt("rssi");
        Log.d("++++", "rssi:detail:" + rssi);
        int powTemp=(rssi-50)/(10*2);
        distance= (int) Math.pow(10, powTemp);
        setRssi(rssi);
        txt_distance.setText("距离：" + distance + "米");
        txt_direction.setText("方向："+"W10,N50");


        randomTextView.setOnRippleViewClickListener(new RandomTextView.OnRippleViewClickListener() {
            @Override
            public void onRippleViewClicked(View view) {

            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                randomTextView.addKeyWord("测试点");
                randomTextView.show();
                randomTextView.invalidate();
            }
        },2 * 1000);


        //customview重绘时进行回调
        customViewByMySelf.getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
            @Override
            public void onDraw() {


            }
        });


        //customview加载完成时进行回调
        customViewByMySelf.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Message msg=new Message();
                msg.what=1;
                handler.sendMessage(msg);
                customViewByMySelf.getViewTreeObserver().removeOnGlobalLayoutListener(this);

            }
        });

    }

    /**
     * Called when the current {@link } of the activity gains or loses
     * focus.  This is the best indicator of whether this activity is visible
     * to the user.  The default implementation clears the key tracking
     * state, so should always be called.
     * <p/>
     * <p>Note that this provides information about global focus state, which
     * is managed independently of activity lifecycles.  As such, while focus
     * changes will generally have some relation to lifecycle changes (an
     * activity that is stopped will not generally get window focus), you
     * should not rely on any particular order between the callbacks here and
     * those in the other lifecycle methods such as {@link #onResume}.
     * <p/>
     * <p>As a general rule, however, a resumed activity will have window
     * focus...  unless it has displayed other dialogs or popups that take
     * input focus, in which case the activity itself will not have focus
     * when the other windows have it.  Likewise, the system may display
     * system-level windows (such as the status bar notification panel or
     * a system alert) which will temporarily take window input focus without
     * pausing the foreground activity.
     *
     * @param hasFocus Whether the window of this activity has focus.
     * @see #hasWindowFocus()
     * @see #onResume
     *
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //activity加载完毕
        if(hasFocus){


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_distance, menu);
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

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }
}

