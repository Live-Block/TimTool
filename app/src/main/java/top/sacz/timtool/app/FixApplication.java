package top.sacz.timtool.app;

import android.app.Application;

import com.google.android.material.color.DynamicColors;
import com.kongzue.dialogx.DialogX;
import com.kongzue.dialogxmaterialyou.style.MaterialYouStyle;

import top.sacz.xphelper.util.ConfigUtils;


public class FixApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DynamicColors.applyToActivitiesIfAvailable(this);
        //初始化dialogx
        ConfigUtils.initialize(this);
        DialogX.init(this);
        DialogX.globalTheme = DialogX.THEME.AUTO;
        DialogX.globalStyle = new MaterialYouStyle();
    }
}
