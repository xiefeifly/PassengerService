package com.passenger.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;

import com.passenger.R;


/**
 * @ProjectName: SubWarningApps
 * @Package: com.jyd.spiapp.utils
 * @ClassName: ToastUtil
 * @Description: java类作用描述
 * @Author: lzz
 * @CreateDate: 2022/7/1 15:34
 */
public class ToastUtil {

    public static final int TOP = 0;
    public static final int MID = 1;
    public static final int BOTTOM = 2;
    public static final int LEFT = 3;
    public static final int RIGHT = 4;

    private static Context mContext;
    private static CharSequence text;
    private static int mXOffset;
    private static int mYffset;
    private static int mGravity;

    public static void show(Context context, CharSequence s, int gravity, int xOffset, int yOffset) {
        mContext = context;
        mGravity = gravity;
        mXOffset = xOffset;
        mYffset = yOffset;
        text = s;
        create().show();
    }

    public static void showBottom(Context context, CharSequence s) {
        mContext = context;
        mGravity = BOTTOM;
        mXOffset = 0;
        mYffset = 130;
        text = s;
        create().show();
    }
    public static void showBottom(Context context, @StringRes int id) {
        mContext = context;
        mGravity = BOTTOM;
        mXOffset = 0;
        mYffset = 130;
        text = context.getResources().getText(id);
        create().show();
    }

    public static void show(Context context, CharSequence s, int gravity) {
        mContext = context;
        mGravity = gravity;
        text = s;
        create().show();
    }

    private ToastUtil() {

    }

    public static Toast toast;
    public static View view;
    public static TextView textView;

    private static Toast create() {
        toast = new Toast(mContext);
        view = View.inflate(mContext, R.layout.widget_toast, null);
        textView = view.findViewById(R.id.tv_toast);
        textView.setText(text);
        textView.setTextColor(Color.BLACK);
        toast.setView(view);
        toast.setGravity(selectGravity(mGravity), mXOffset, mYffset);
        return toast;
    }

    private static int selectGravity(int gravity) {
        switch (gravity) {
            case TOP:
                return Gravity.TOP;
            case MID:
                return Gravity.CENTER;
            case BOTTOM:
                return Gravity.BOTTOM;
            case LEFT:
                return Gravity.LEFT;
            case RIGHT:
                return Gravity.RIGHT;
            default:
                return 0;
        }
    }

}
