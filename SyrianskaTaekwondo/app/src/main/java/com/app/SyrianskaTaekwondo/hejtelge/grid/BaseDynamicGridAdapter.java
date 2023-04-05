package com.app.SyrianskaTaekwondo.hejtelge.grid;

import android.content.Context;
import android.util.Log;

import com.app.SyrianskaTaekwondo.hejtelge.SponserListActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Author: alex askerov
 * Date: 9/7/13
 * Time: 10:49 PM
 */
public abstract class BaseDynamicGridAdapter extends AbstractDynamicGridAdapter {
    private SponserListActivity mContext;
    public static List<HashMap<String, String>> arr = new ArrayList<>();
    private List<HashMap<String, String>> mItems = new ArrayList<>();
    private int mColumnCount;

    protected BaseDynamicGridAdapter(SponserListActivity context, int columnCount) {
        this.mContext = context;
        this.mColumnCount = columnCount;
    }

    public BaseDynamicGridAdapter(SponserListActivity context, List<HashMap<String, String>> items, int columnCount) {
        mContext = context;
        mColumnCount = columnCount;
        init(items);
    }

    private void init(List<HashMap<String, String>> items) {
        addAllStableId(items);
        this.mItems.addAll(items);
    }


    public void set(List<HashMap<String, String>> items) {
        clear();
        init(items);
        notifyDataSetChanged();
    }

    public void clear() {
        clearStableIdMap();
        mItems.clear();
        notifyDataSetChanged();
    }

    public void add(HashMap item) {
        addStableId(item);
        mItems.add(item);
        notifyDataSetChanged();
    }

    public void add(int position, HashMap item) {
        addStableId(item);
        mItems.add(position, item);
        notifyDataSetChanged();
    }

    public void add(List<HashMap<String, String>> items) {
        addAllStableId(items);
        this.mItems.addAll(items);
        notifyDataSetChanged();
    }


    public void remove(Object item) {
        mItems.remove(item);
        removeStableID(item);
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public int getColumnCount() {
        return mColumnCount;
    }

    public void setColumnCount(int columnCount) {
        this.mColumnCount = columnCount;
        notifyDataSetChanged();
    }

    @Override
    public void reorderItems(int originalPosition, int newPosition) {
        if (newPosition < getCount()) {
            arr.clear();
            ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();

            DynamicGridUtils.reorder(mItems, originalPosition, newPosition);
            notifyDataSetChanged();
            arr.addAll(mItems);
            for (int i = 0; i < arr.size(); i++) {
                Log.e("datasponser", "" + arr);
                String id1 = arr.get(i).get("id");

                HashMap map = new HashMap();
                map.put("sponser_id", id1);
                map.put("order",String .valueOf(i));
                arrayList.add(map);
            }
           mContext.getAddAPI(arrayList);
            Log.e("ARRAY",""+arr);
        }
    }


    @Override
    public boolean canReorder(int position) {
        return true;
    }

    public List<HashMap<String, String>> getItems() {
        return mItems;
    }

    protected Context getContext() {
        return mContext;
    }
}
