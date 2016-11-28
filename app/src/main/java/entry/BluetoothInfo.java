package entry;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/3/10.
 */
public class BluetoothInfo implements Serializable{
    private String btname;
    private String btaddress;
    private String rssi;

    public String getBtname() {
        return btname;
    }

    public void setBtname(String btname) {
        this.btname = btname;
    }

    public String getBtaddress() {
        return btaddress;
    }

    public void setBtaddress(String btaddress) {
        this.btaddress = btaddress;
    }

    public String getRssi() {
        return rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }
}
