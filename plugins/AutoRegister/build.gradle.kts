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
version = PluginsVersion.autoRegister

gradlePlugin {
    plugins {
        create("AutoRegister") {
            id = "com.aqrlei.plugin.autoregister"
            implementationClass = "com.aqrlei.plugin.autoregister.RegisterPlugin"
        }
    }
}
apply(from = rootProject.file("gradle/plugin_maven_publish.gradle.kts"))


