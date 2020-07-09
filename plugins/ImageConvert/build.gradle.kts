plugins {
    id("java-gradle-plugin")
}

dependencies {
    compileOnly("com.android.tools.build:gradle:4.0.0")
}
repositories {
    jcenter()
    google()
    mavenCentral()
}
group = PublishConfig.pluginGroupId
version = PluginsVersion.imageConvert

gradlePlugin {
    plugins {
        create("imageConvert") {
            id = "com.aqrlei.plugin.imageConvert"
            implementationClass = "com.aqrlei.plugin.imageconvert.ImageConvertPlugin"
        }
    }
}
apply(from = rootProject.file("gradle/plugin_maven_publish.gradle.kts"))


