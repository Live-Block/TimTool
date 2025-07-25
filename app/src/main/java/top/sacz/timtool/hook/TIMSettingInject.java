package top.sacz.timtool.hook;

import android.content.Context;
import androidx.annotation.NonNull;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import de.robv.android.xposed.XC_MethodHook;
import top.sacz.timtool.R;
import top.sacz.timtool.hook.base.BaseHookItem;
import top.sacz.timtool.hook.core.annotation.HookItem;
import top.sacz.timtool.hook.util.LogUtils;
import top.sacz.timtool.ui.dialog.SettingDialog;
import top.sacz.xphelper.XpHelper;
import top.sacz.xphelper.reflect.ClassUtils;
import top.sacz.xphelper.reflect.ConstructorUtils;
import top.sacz.xphelper.reflect.FieldUtils;
import top.sacz.xphelper.reflect.MethodUtils;

/**
 * 注入TIM设置界面入口
 *
 * @author suzhelan
 */
@HookItem("注入TIM设置界面")
public class TIMSettingInject extends BaseHookItem {

    private void onCreate(XC_MethodHook.MethodHookParam param) {
        Context context = (Context) param.args[0];
        XpHelper.injectResourcesToContext(context);

        List<Object> itemGroupWraperList = (List<Object>) param.getResult();
        Class<?> itemGroupWraperClass = itemGroupWraperList.get(0).getClass();

        for (Object wrapper : itemGroupWraperList) {
            try {
                List<Object> itemList = FieldUtils.create(wrapper.getClass())
                        .fieldType(List.class)
                        .firstValue(wrapper);

                if (itemList == null || itemList.isEmpty()) continue;

                String name = itemList.get(0).getClass().getName();
                int iconRes = context.getResources().getIdentifier("qui_tuning", "drawable", context.getPackageName());
                if (!name.startsWith("com.tencent.mobileqq.setting.processor")) continue;

                Class<?> itemClass = itemList.get(0).getClass();
                Object mItem = ConstructorUtils.newInstance(itemClass,
                    new Class[]{Context.class, int.class, CharSequence.class, int.class},
                    context, 0x520a, context.getString(R.string.app_name), iconRes);

                List<Method> setOnClickMethods = MethodUtils.create(itemClass)
                        .returnType(void.class)
                        .params(ClassUtils.findClass("kotlin.jvm.functions.Function0"))
                        .getResult();

                Object onClickListener = Proxy.newProxyInstance(HookEnv.getHostClassLoader(),
                        new Class[]{ClassUtils.findClass("kotlin.jvm.functions.Function0")},
                        new OnClickListener(context));

                for (Method setOnClickMethod : setOnClickMethods) {
                    setOnClickMethod.invoke(mItem, onClickListener);
                }

                List<Object> mItemGroup = new ArrayList<>();
                mItemGroup.add(mItem);

                Constructor<?> itemGroupWraperConstructor = ConstructorUtils.create(itemGroupWraperClass).paramCount(5).first();
                Object itemGroupWrap = itemGroupWraperConstructor.newInstance(mItemGroup, null, null, 6, null);

                itemGroupWraperList.add(0, itemGroupWrap);
                break;
            } catch (Exception e) {
                LogUtils.addError(e);
            }
        }
    }

    @Override
    public void loadHook(@NonNull ClassLoader loader) {
        Method onCreate = MethodUtils.create("com.tencent.mobileqq.setting.main.MainSettingConfigProvider")
                .returnType(List.class)
                .params(Context.class)
                .first();
        hookAfter(onCreate, 500, this::onCreate);
    }

    private static class OnClickListener implements InvocationHandler {
        private final Context context;

        private OnClickListener(Context context) {
            this.context = context;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            XpHelper.injectResourcesToContext(context);
            new SettingDialog().show(context);
            return null;
        }
    }
}
