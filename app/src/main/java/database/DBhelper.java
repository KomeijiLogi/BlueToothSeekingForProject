package database;

import android.content.Context;

import java.util.List;

import application.BSApplication;
import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.QueryBuilder;
import entry.BtInfo;
import entry.BtInfoDao;
import entry.DaoSession;

/**
 * Created by Administrator on 2016/3/16.
 */
public class DBhelper {
     private static Context dbcontext;
     private static DBhelper instance;

     private BtInfoDao btInfoDao;

     private BtInfo btInfo;

     private DBhelper(){

     }

     public static DBhelper getInstance(Context context){
         if(instance==null){
              instance=new DBhelper();
              if(dbcontext==null){
                  dbcontext=context;
              }
             //数据库对象获取
             DaoSession daoSession= BSApplication.getDaoSession(dbcontext);
             instance.btInfoDao=daoSession.getBtInfoDao();
         }
         return instance;
     }
     /*添加数据*/
     public void addBtInfo(BtInfo item){
          btInfoDao.insert(item);
     }


     /*查询数据*/
     public List getBtInfoList(){
         QueryBuilder<BtInfo> qb=btInfoDao.queryBuilder();
         return  qb.list();

     }
     /*删除数据*/
     public void deleteBtInfoList(int id){
         QueryBuilder<BtInfo> qb=btInfoDao.queryBuilder();
         DeleteQuery<BtInfo>  dq=qb.where(BtInfoDao.Properties.Id.eq(id)).buildDelete();
         dq.executeDeleteWithoutDetachingEntities();
     }

     /*清空数据*/
     public void clearBtInfo(){
         btInfoDao.deleteAll();
     }

}
