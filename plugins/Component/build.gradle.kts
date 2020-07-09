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
version = PluginsVersion.component

gradlePlugin {
    plugins {
        create("Component") {
            id = "com.aqrlei.plugin.component"
            implementationClass = "com.aqrlei.plugin.component.ComponentPlugin"
        }
    }
}
apply(from = rootProject.file("gradle/plugin_maven_publish.gradle.kts"))


