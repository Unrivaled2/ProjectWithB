package com.lemonxq_laplace.pregnantmonitor.nearby_location;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;
import com.lemonxq_laplace.pregnantmonitor.R;

import java.util.List;

/**
 * 显示位置列表的ListView适配器
 */
public class PlaceAdapter extends BaseAdapter {

    private Context mContext;
    private List<PoiInfo> mPoiInfoList;
    private LayoutInflater mInflater;

    public PlaceAdapter(Context context, List<PoiInfo> poiInfoList) {
        mContext = context;
        mPoiInfoList = poiInfoList;
        mInflater = LayoutInflater.from(mContext);
    }

    public void replaceAll(List<PoiInfo> poiInfoList) {
        mPoiInfoList = poiInfoList;
        notifyDataSetChanged();
    }

    public void addAll(List<PoiInfo> poiInfoList) {
        mPoiInfoList.addAll(poiInfoList);
        notifyDataSetChanged();
    }

    public void clear() {
        mPoiInfoList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mPoiInfoList == null ? 0 : mPoiInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return mPoiInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PoiInfo poiInfo = mPoiInfoList.get(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_location_address, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvLocationTitle.setText(poiInfo.name);
        viewHolder.tvLocationAddress.setText(poiInfo.address);
        return convertView;
    }

    static class ViewHolder {

        TextView tvLocationTitle, tvLocationAddress;

        public ViewHolder(View view) {
            tvLocationTitle = view.findViewById(R.id.item_title);
            tvLocationAddress = view.findViewById(R.id.item_address);
        }
    }

}
