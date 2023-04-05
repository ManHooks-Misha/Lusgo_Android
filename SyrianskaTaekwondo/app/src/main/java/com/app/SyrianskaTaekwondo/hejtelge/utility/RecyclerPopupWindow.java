package com.app.SyrianskaTaekwondo.hejtelge.utility;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.SyrianskaTaekwondo.hejtelge.R;
import com.app.SyrianskaTaekwondo.hejtelge.adapters.RecyclerPopupWindowAdapter;
import com.app.SyrianskaTaekwondo.hejtelge.pojo.Item;

import java.util.List;

/**
 * Created by cmj on 2016/4/7.
 */
public class RecyclerPopupWindow extends PopupWindow implements RecyclerPopupWindowAdapter.OnItemClickListener, PopupWindow.OnDismissListener {
    private List<Item> items;
    private PopupWindow popupWindow;
    private RecyclerView recyclerView;
    private RecyclerPopupWindowAdapter recyclerPopupWindowAdapter;
    private CallBack mCallBack;
    //上一次点击位置
    private int position;
    //本次点击位置
    private int prePosition;
    //是否点击列表项
    private boolean isClickItem;


    public RecyclerPopupWindow(List<Item> items) {
        this.items = items;
        //获取之前点击的位置
        for (int i = 1; i < items.size(); ++i) {
            if (items.get(i).isActive()) {
                prePosition = i;
                break;
            }
        }
    }

    /**
     * 弹出向上或下的PopupWindow
     *
     * @param context       上下文
     * @param anchor        锚点
     * @param window_width  窗口宽度
     * @param window_height 窗口高度
     * @param isUp          是否向上弹出
     */
    public void showPopupWindow(Context context, View anchor, int window_width, int window_height, boolean isUp) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.popup_layout, null);
        popupWindow = new PopupWindow(contentView, window_width, window_height, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        popupWindow.setOutsideTouchable(true);

        popupWindow.setOnDismissListener(this);
        recyclerView = contentView.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerPopupWindowAdapter = new RecyclerPopupWindowAdapter(items,context);
        recyclerPopupWindowAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(recyclerPopupWindowAdapter);
        if (isUp) {
            popupWindow.setAnimationStyle(R.style.Popwindow_Anim_Up);
            popupWindow.showAsDropDown(anchor, 0, -(window_height + anchor.getHeight()));
        } else {
            popupWindow.setAnimationStyle(R.style.Popwindow_Anim_Down);
            popupWindow.showAsDropDown(anchor, 0, 0);
        }
    }

    public void setCallBack(CallBack callBack) {
        mCallBack = callBack;
    }

    @Override
    public void onItemClick(int pos) {
        position = pos;
        isClickItem = true;
        //改变选择项并关闭窗口
        changePos(true);
    }

    /**
     * 点击改变选择项并决定是否关闭window
     *
     * @param isCloseWindow 是否要关闭window
     */
    private void changePos(boolean isCloseWindow) {
        //如果两次点击不同上次选择的取消
        if (position != prePosition && prePosition != 0) {
            items.get(prePosition).setActive(false);
            recyclerPopupWindowAdapter.notifyItemChanged(prePosition);
        }
        //更新当前位置的选择项
        if (position > 0) {
            items.get(position).setActive(true);
            recyclerPopupWindowAdapter.notifyItemChanged(position);
        }
        if (isCloseWindow) {
            //动画消失后再关闭
            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    //回调并关闭window
                    mCallBack.callback(items.get(position).getTitle(),items.get(position).getUser_id());
                    destroyPopWindow();
                }
            }, 450);
        }
    }

    @Override
    public void onDismiss() {
        //点击的是window外面，回调返回-1说明，时间不用改变
        if (!isClickItem) {
            mCallBack.callback("-1","-1");
        }
    }


    private void destroyPopWindow() {
        if (popupWindow != null) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    public interface CallBack {
        void callback(String value, String role);
    }
}
