package tools;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;

import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.example.administrator.bluetoothseekingforproject.R;

import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/3/30.
 */
public class CustomViewByMySelf extends View {
    //flag：Paint flag that enables antialiasing(抗锯齿) when drawing.
    public  Paint customPaint=new Paint(Paint.ANTI_ALIAS_FLAG);

    private float customRotate;
    //矩阵
    private Matrix customMatrix=new Matrix();
    //着色器
    private Shader customShader;
    private boolean customDotiming;

    int rssi;


    //旋转中心点坐标
    float x=320;
    float y=320;

    long lastBitmapSwitchMillis = System.currentTimeMillis();
    int currentBitmap = 1;
    int bitmapInterval = 500;
    int count=0;
    Bitmap bp_light,bp_dark,bp_anim;
    int randomNum,mx,my;





    public Canvas customCanvas=new Canvas();
    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public CustomViewByMySelf(Context context) {
        super(context);
    }

    /**
     * Constructor that is called when inflating a view from XML. This is called
     * when a view is being constructed from an XML file, supplying attributes
     * that were specified in the XML file. This version uses a default style of
     * 0, so the only attribute values applied are those in the Context's Theme
     * and the given AttributeSet.
     * <p/>
     * <p/>
     * The method onFinishInflate() will be called after all children have been
     * added.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     * @param attrs   The attributes of the XML tag that is inflating the view.
     * @see #(Context, AttributeSet, int)
     */
    public CustomViewByMySelf(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        setFocusableInTouchMode(true);
        //Color.TRANSPARENT获取系统自定义颜色
        customShader=new SweepGradient(x,y,new int[]{R.drawable.component_radar_pointer, Color.TRANSPARENT},null);
        customPaint.setShader(customShader);

    }

    /**
     * Implement this to do your drawing.
     *
     * @param canvas the canvas on which the background will be drawn
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.customCanvas=canvas;

        Paint paint=customPaint;
        Paint paintNew=new Paint();
        paintNew.setColor(Color.WHITE);

        canvas.drawColor(Color.TRANSPARENT);

        customMatrix.setRotate(customRotate, x, y);
        customShader.setLocalMatrix(customMatrix);
        customRotate +=2.5;
        //转一周后进行重置
        if(customRotate>=360){
           customRotate=0;
        }
        //刷新界面
        invalidate();

        if(customDotiming){
            long now=System.currentTimeMillis();
            for(int i=0;i<30;i++){
                canvas.drawCircle(x,y,320,paint);
            }
            now=System.currentTimeMillis()-now;
            Log.d("++++","sweep渲染时间"+now);

        }else {
            //canvas画布绘制图形是一种如栈的堆载形式，先绘制的会在底层
            //圆心坐标想x，y，半径300/10
            canvas.drawCircle(x, y, 300, paint);
            canvas.drawCircle(x, y, 10, paintNew);
            setCustomCanvas(customCanvas);
            setCustomPaint(customPaint);
            //pointPaint(50, customPaint);
            //设置绘制路径的效果，如点画线等
            //customPaint.setPathEffect(new CornerPathEffect(10));
        }
    }



    public void pointPaint(int rssi, Paint paint){

        Resources r = getResources();
        //第二个参数就是发光点的源文件
        bp_light= BitmapFactory.decodeResource(r, R.drawable.component_radar_light_point);
        bp_dark = BitmapFactory.decodeResource(r, R.drawable.component_radar_dark_point);
        //bp_anim = BitmapFactory.decodeResource(r,R.drawable.component_radar_anim_point);





        customCanvas.restore();
        //randomNum= (int) (Math.random()*10);
        //mx= (int)(((rssi/randomNum)%2==0)? (x+randomNum):(x-randomNum));
        //my= (int)(((rssi/randomNum)%2==0)? (y+randomNum):(y-randomNum));
         mx=400;
         my=460;


        customCanvas.drawBitmap(bp_light, mx, my, paint);
        //customCanvas.drawBitmap(bp_anim,mx,my,paint);

//        Timer timer=new Timer();
//        TimerTask task=new TimerTask() {
//            @Override
//            public void run() {
//                if (System.currentTimeMillis() >= lastBitmapSwitchMillis + bitmapInterval) {
//                    lastBitmapSwitchMillis = System.currentTimeMillis();   //Save current time of bitmap switch
//                    count++;
//                    if (currentBitmap == 1) {
//                        currentBitmap = 2;
//                    }
//                    else if (currentBitmap == 2) {
//                        currentBitmap = 1;
//                    }
//                }
//                if (currentBitmap == 1) {
//                    customCanvas.drawBitmap(bp_light, mx, my, customPaint);
//                }
//                else if (currentBitmap == 2) {
//                    customCanvas.drawBitmap(bp_dark, mx, my,customPaint);
//                }
//                 try {
//                      Thread.sleep(1000);
//                  } catch (InterruptedException e) {
//                      e.printStackTrace();
//                  }
//            }
//        };
//
//        timer.schedule(task,1,600);
//        invalidate();



    }

    public void reDraw(int rssi){
        draw(customCanvas);

        pointPaint(rssi,customPaint);
    }

    /**
     * Default implementation of {@link KeyEvent.Callback#onKeyDown(int, KeyEvent)
     * KeyEvent.Callback.onKeyDown()}: perform press of the view
     * when {@link KeyEvent#KEYCODE_DPAD_CENTER} or {@link KeyEvent#KEYCODE_ENTER}
     * is released, if the view is enabled and clickable.
     * <p/>
     * <p>Key presses in software keyboards will generally NOT trigger this listener,
     * although some may elect to do so in some situations. Do not rely on this to
     * catch software key presses.
     *
     * @param keyCode A key code that represents the button pressed, from
     *                {@link KeyEvent}.
     * @param event   The KeyEvent object that defines the button action.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_VOLUME_UP:
                //设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
                customPaint.setDither(!customPaint.isDither());
                invalidate();
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                customDotiming=!customDotiming;
                invalidate();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public Paint getCustomPaint() {
        return customPaint;
    }

    public void setCustomPaint(Paint customPaint) {
        this.customPaint = customPaint;
    }

    public Canvas getCustomCanvas() {
        return customCanvas;
    }

    public void setCustomCanvas(Canvas customCanvas) {
        this.customCanvas = customCanvas;
    }
}

