package top.sacz.timtool.hook.item.chat

import top.sacz.timtool.hook.base.BaseSwitchFunctionHookItem
import top.sacz.timtool.hook.base.IMethodFinder
import top.sacz.timtool.hook.core.annotation.HookItem
import java.lang.reflect.Method

/**
 * 思路 https://github.com/cinit/QAuxiliary -> cc.ioctl.hook.ui.chat.ReplyNoAtHook
 */
@HookItem("辅助功能/聊天/禁止回复自动艾特")
class DisableReplyAutoAt : BaseSwitchFunctionHookItem(), IMethodFinder {
    lateinit var method: Method

    override fun find() {
        try {
            method = buildMethodFinder()
                .searchPackages("com.tencent.mobileqq.aio.input.reply")
                .usedString("msgItem.msgRecord.senderUid")
                .first()
        } catch (e: Throwable) {
            // 静默处理异常，避免日志输出
        }
    }

    override fun loadHook(loader: ClassLoader) {
        try {
            if (::method.isInitialized) {
                hookBefore(method) { param ->
                    param.result = null
                }
            }
        } catch (e: Throwable) {
            // 静默处理异常，避免日志输出
        }
    }
}
