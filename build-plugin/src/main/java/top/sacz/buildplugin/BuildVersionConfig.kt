package top.sacz.buildplugin

import org.gradle.api.JavaVersion

/*＊
 * 构建配置
 ＊*/
object BuildVersionConfig {
    const val nameSpace = "top.sacz.timtool"
    const val applicationId = "io.live.timtool"
    val javaVersion = JavaVersion.VERSION_21
    const val kotlin = "21"
    const val compileSdk = 36
    const val targetSdk = 36
    const val minSdk = 26
}
