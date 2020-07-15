plugins {
    id("java-gradle-plugin")
    id("kotlin")
}

dependencies {
    compileOnly("com.android.tools.build:gradle:4.0.0")
    implementation("org.javassist:javassist:3.27.0-GA")
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


