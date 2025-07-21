package top.sacz.timtool.hook

import android.app.Application
import android.content.Context
import com.kongzue.dialogx.DialogX
import com.kongzue.dialogxmaterialyou.style.MaterialYouStyle
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage
import top.sacz.timtool.hook.core.HookItemLoader
import top.sacz.timtool.hook.core.HookItemMethodFindProcessor
import top.sacz.timtool.hook.util.PathTool
import top.sacz.timtool.net.NewLoginTask
import top.sacz.timtool.net.UpdateService
import top.sacz.xphelper.XpHelper

class HookSteps {

    fun initHandleLoadPackage(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        HookEnv.setProcessName(loadPackageParam.processName)
        HookEnv.setCurrentHostAppPackageName(loadPackageParam.packageName)
    }

    fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        XpHelper.initZygote(startupParam)
        HookEnv.setModuleApkPath(startupParam.modulePath)
    }

    fun initContext(application: Application) {
        val context = application.baseContext
        val packageManager = context.packageManager
        val packageInfo = packageManager.getPackageInfo(context.packageName, 0)
        val appName = packageManager.getApplicationLabel(context.applicationInfo).toString()
        HookEnv.setHostAppContext(context)
        HookEnv.setApplication(application)
        HookEnv.setHostApkPath(context.applicationInfo.sourceDir)
        HookEnv.setAppName(appName)
        HookEnv.setVersionCode(packageInfo.versionCode)
        HookEnv.setVersionName(packageInfo.versionName)
        HookEnv.setHostClassLoader(context.classLoader)
        XpHelper.initContext(context)
        XpHelper.injectResourcesToContext(context)
        val dataDir = PathTool.getModuleDataPath() + "/data"
        XpHelper.setConfigPath(dataDir)//设置存储路径
        XpHelper.setConfigPassword("TimToolConfigEncryptKey")//设置加密密码
        initDialogX(context)
    }

    private fun initDialogX(context: Context) {
        DialogX.init(context)
        DialogX.globalTheme = DialogX.THEME.AUTO
        DialogX.globalStyle = MaterialYouStyle()
    }

    fun initHooks() {
        val methodFindProcessor = HookItemMethodFindProcessor()
        //环境初始化 开始进行hook项目的初始化
        if (HookEnv.isMainProcess()) {
            if (methodFindProcessor.isDataExpire()) {
                methodFindProcessor.startFindAsync { initHooks() }
                return
            }
            XposedBridge.log("[TimTool]环境初始化完成")
            //登录
            NewLoginTask().loginAndGetUserInfoAsync()
            //检查更新
            val service = UpdateService()
            service.requestUpdateAsyncAndToast()
        }
        methodFindProcessor.scanConfigMethod()
        val hookItemLoader = HookItemLoader()
        hookItemLoader.loadConfig()
        hookItemLoader.loadHookItem()
    }
}
