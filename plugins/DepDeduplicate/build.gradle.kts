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
version = PluginsVersion.depDeduplicate

gradlePlugin {
    plugins {
        create("DepDeduplicate") {
            id = "com.aqrlei.plugin.depDeduplicate"
            implementationClass = "com.aqrlei.plugin.depdeduplicate.DepDeduplicatePlugin"
        }
    }
}
apply(from = rootProject.file("gradle/plugin_maven_publish.gradle.kts"))


