
package com.storm.fliplayout.helper;

import android.app.Activity;
import android.content.Context;
import android.text.ClipboardManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class InputMethodUtils {

    /**
     * view获取去焦点，弹出输入法
     * 
     * @param activity
     * @param view
     */
    public static void showInputMethod(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * 隐藏输入法
     * 
     * @param activity
     * @param view
     */
    public static void hiddenInputMethod(Activity activity, View view){
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    
    /**
     * 复制文字到剪贴板
     * 
     * @param context
     * @param content
     */
    public static void copyStringToClipboard(Context context, String content, boolean showHint){
        ClipboardManager cmb = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content);
        if(showHint)
            CommonUtil.showToast(context, "已复制到剪贴板");
    }
    
}
