package application;

import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;

import entry.DaoMaster;
import entry.DaoSession;

/**
 * Created by Administrator on 2016/1/7.
 */
public class BSApplication extends Application {
    private static DaoMaster daoMaster;
    private static DaoSession daoSession;
    private final  int version=1;
    public static BSApplication bsApplication;
    private RequestQueue requestQueue;
    private Boolean connectFlag=false;
    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public static BSApplication getBsApplication() {
        return bsApplication;
    }

    public static void setBsApplication(BSApplication bsApplication) {
        BSApplication.bsApplication = bsApplication;
    }

    /**
     * Called when the application is starting, before any activity, service,
     * or receiver objects (excluding content providers) have been created.
     * Implementations should be as quick as possible (for example using
     * lazy initialization of state) since the time spent in this function
     * directly impacts the performance of starting the first activity,
     * service, or receiver in a process.
     * If you override this method, be sure to call super.onCreate().
     */
    @Override
    public void onCreate() {
        super.onCreate();
        bsApplication=this;
    }

    public  static DaoMaster getDaoMaster(Context context){
        if(daoMaster==null){
            DaoMaster.OpenHelper helper=new DaoMaster.DevOpenHelper(context,"btInfo.db",null);
            daoMaster =new DaoMaster(helper.getWritableDatabase());
        }
        return daoMaster;

    }
    public static DaoSession getDaoSession(Context context){
        if(daoSession==null){
            if(daoMaster==null){
                daoMaster=getDaoMaster(context);
            }
            daoSession=daoMaster.newSession();
        }
        return  daoSession;
    }
    public int getVersion() {
        return version;
    }


    public Boolean getConnectFlag() {
        return connectFlag;
    }

    public void setConnectFlag(Boolean connectFlag) {
        this.connectFlag = connectFlag;
    }
}
