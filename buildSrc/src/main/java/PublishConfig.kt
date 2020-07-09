/**
 * created by AqrLei on 2020/6/10
 */
object PublishConfig {
    const val repoUserName = "admin"
    const val repoPassword = "Aqr123456"

    const val nexusReleaseUrl = "http://localhost:8081/repository/android-lib-release/"

    const val groupId = "com.aqrlei.lib"
    const val pluginGroupId = "com.aqrlei.plugin"
    const val websiteUrl = "https://github.com/AqrLei/"

    const val licenseName = "The Apache Software License, Version 2.0"
    const val licenseUrl = "http://www.apache.org/licenses/LICENSE-2.0.txt"

    val defaultDeveloper = DeveloperEntity.default()
}

object LibKey {
    const val artifactIdKey = "artifactId"
    const val versionKey = "version"
    const val descKey = "desc"

    const val developerKey = "developer"
}

object Libs {
    val bannerView =
        PublishEntity("bannerview", "1.0.0-alpha01", "BannerView of Android's ui widget")
    val logHelper = PublishEntity("loghelper", "1.0.0-alpha01", "Log util ")
}

object PluginsVersion {
    const val imageConvert = "1.0.0-beta01"
    const val component = "1.0.0-beta01"
    const val depDeduplicate = "1.0.0-alpha23"
}

data class PublishEntity(
    val artifactId: String,
    val version: String,
    val desc: String,
    val developer: DeveloperEntity = DeveloperEntity.default()
) {
    override fun toString(): String {
        return "${PublishConfig.groupId}:$artifactId:$version"
    }
}

data class DeveloperEntity(val id: String, val name: String, val email: String) {
    companion object {
        fun default() = DeveloperEntity("aqrlei", "AqrLei", "aqrdeveloper@gmail.com")
    }
}