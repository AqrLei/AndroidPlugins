plugins {
    id("java-gradle-plugin")
    id("kotlin")
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
version = PluginsVersion.lifecycle

gradlePlugin {
    plugins {
        create("AutoRegister") {
            id = "com.aqrlei.plugin.lifecycleobserver"
            implementationClass = "com.aqrlei.plugin.lifecycleobserver.LifecyclePlugin"
        }
    }
}
apply(from = rootProject.file("gradle/plugin_maven_publish.gradle.kts"))


