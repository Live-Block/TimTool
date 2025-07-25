package top.sacz.timtool.hook.item.chat

import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem
import top.sacz.timtool.hook.core.annotation.HookItem
import top.sacz.xphelper.reflect.MethodUtils

@HookItem("辅助功能/聊天/移除点击 小程序 时的更新Toast")
class IgnoreUpdateToast : BaseSwitchFunctionHookItem() {
    override fun getTip(): String {
        return "开启此功能可直接点击 小程序 并进入"
    }

    override fun loadHook(loader: ClassLoader) {
        val ignToast = MethodUtils.create("com.tencent.mobileqq.aio.msglist.holder.component.ark.d")
            .methodName("a")
            .params(String::class.java, String::class.java)
            .returnType(Boolean::class.javaObjectType)
            .first()
        hookAfter(ignToast) { param ->
            param.result = true
        }
    }
}
