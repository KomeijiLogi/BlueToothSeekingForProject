package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.bluetoothseekingforproject.R;

import java.util.List;

import entry.BtInfo;

/**
 * Created by Administrator on 2015/12/31.
 */
public class BTBaseAdapter extends BaseAdapter {
    private Context context;
    private List<BtInfo> btdata;


    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return btdata.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return btdata.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHold vh=null;
        View v;
        if(convertView==null){
            vh=new ViewHold();
            convertView=LayoutInflater.from(context).inflate(R.layout.fill_bt_listview,null);
            vh.btname= (TextView) convertView.findViewById(R.id.bluetooth_name_txt);
            vh.distance= (TextView) convertView.findViewById(R.id.bluetooth_distance_txt);
            convertView.setTag(vh);
        }else {
            vh= (ViewHold) convertView.getTag();
        }
        BtInfo btInfo=btdata.get(position);
        vh.btname.setText(btInfo.getBtname());
        vh.distance.setText(btInfo.getDistance());

        return convertView;
    }

    public class ViewHold{
        public TextView btname;
        public TextView distance;
    }

}
