package entry;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import entry.BtInfo;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "btInfo_android".
*/
public class BtInfoDao extends AbstractDao<BtInfo, Long> {

    public static final String TABLENAME = "btInfo_android";

    /**
     * Properties of entity BtInfo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Btname = new Property(1, String.class, "btname", false, "BTNAME");
        public final static Property IsVisible = new Property(2, Integer.class, "isVisible", false, "IS_VISIBLE");
        public final static Property Distance = new Property(3, String.class, "distance", false, "DISTANCE");
        public final static Property Status = new Property(4, String.class, "status", false, "STATUS");
        public final static Property Btpwd = new Property(5, String.class, "btpwd", false, "BTPWD");
        public final static Property Btmac = new Property(6, String.class, "btmac", false, "BTMAC");
        public final static Property Rssi = new Property(7, Integer.class, "rssi", false, "RSSI");
    };


    public BtInfoDao(DaoConfig config) {
        super(config);
    }
    
    public BtInfoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"btInfo_android\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"BTNAME\" TEXT," + // 1: btname
                "\"IS_VISIBLE\" INTEGER," + // 2: isVisible
                "\"DISTANCE\" TEXT," + // 3: distance
                "\"STATUS\" TEXT," + // 4: status
                "\"BTPWD\" TEXT," + // 5: btpwd
                "\"BTMAC\" TEXT," + // 6: btmac
                "\"RSSI\" INTEGER);"); // 7: rssi
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"btInfo_android\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, BtInfo entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String btname = entity.getBtname();
        if (btname != null) {
            stmt.bindString(2, btname);
        }
 
        Integer isVisible = entity.getIsVisible();
        if (isVisible != null) {
            stmt.bindLong(3, isVisible);
        }
 
        String distance = entity.getDistance();
        if (distance != null) {
            stmt.bindString(4, distance);
        }
 
        String status = entity.getStatus();
        if (status != null) {
            stmt.bindString(5, status);
        }
 
        String btpwd = entity.getBtpwd();
        if (btpwd != null) {
            stmt.bindString(6, btpwd);
        }
 
        String btmac = entity.getBtmac();
        if (btmac != null) {
            stmt.bindString(7, btmac);
        }
 
        Integer rssi = entity.getRssi();
        if (rssi != null) {
            stmt.bindLong(8, rssi);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public BtInfo readEntity(Cursor cursor, int offset) {
        BtInfo entity = new BtInfo( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // btname
            cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2), // isVisible
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // distance
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // status
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // btpwd
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // btmac
            cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7) // rssi
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, BtInfo entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setBtname(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setIsVisible(cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2));
        entity.setDistance(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setStatus(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setBtpwd(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setBtmac(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setRssi(cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(BtInfo entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(BtInfo entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
