import org.gradle.api.artifacts.dsl.RepositoryHandler
import java.net.URI

/**
 * created by AqrLei on 2020/6/8
 */
fun addRepos(handler: RepositoryHandler) {
    handler.google()
    handler.jcenter()
    handler.maven {
        url = URI.create("http://127.0.0.1:8081/repository/maven-public/")
    }
    handler.mavenCentral()


}

object App {
    const val versionCode = 1000001
    const val versionName = "1.0.0.1"

    const val minSdk = 21
    const val compileSdk = 29
    const val targetSdk = 29
}

object Helper {
    const val versionCode = 10000
    const val versionName = "1.0.0"
}

object Widget {
    const val versionCode = 10000
    const val versionName = "1.0.0"
}

object ClassPath {
    const val android_gradle = "com.android.tools.build:gradle:4.0.0"
    const val kotlin_gradle = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    const val dokka = "org.jetbrains.dokka:dokka-android-gradle-plugin:0.9.18"
}

class Lib {
    val bannerView = Libs.bannerView.toString()
    val logHelper = Libs.logHelper.toString()
}

object Deps {
    val androidx = Androidx()
    val exoplayer = ExoPlayer()
    val gson = "com.google.code.gson:gson:2.8.6"
    val junit = "junit:junit:4.12"
    val kotlin = Kotlin()
    val lib = Lib()
    val leakCanary = "com.squareup.leakcanary:leakcanary-android:2.2"
    val material = "com.google.android.material:material:1.1.0"

    val okhttp = SquareUp.OkHttp()
    val moshi = SquareUp.Moshi()
    val zxing = "com.google.zxing:core:3.4.0"

}

class Androidx {
    val appcompat = "androidx.appcompat:appcompat:1.1.0"
    val annotations = "androidx.annotation:annotation:jar:1.1.0"
    val constraint = "androidx.constraintlayout:constraintlayout:1.1.3"
    val recyclerview = "androidx.recyclerview:recyclerview:1.1.0"
    val core_ktx = "androidx.core:core-ktx:1.3.0"
    val paging = "androidx.paging:paging-runtime:2.1.0"
    val viewPager = "androidx.viewpager:viewpager:1.0.0"
    val viewpager2 = "androidx.viewpager2:viewpager2:1.0.0"

    val runner = "androidx.test:runner:1.2.0"
    val espresso = "androidx.test.espresso:espresso-core:3.2.0"
    val junit_ext = "androidx.test.ext:junit:1.1.1"
    val junit_ktx_ext = "androidx.test.ext:junit-ktx:1.1.1"
}

class ExoPlayer {
    val core = "com.google.android.exoplayer:exoplayer-core:${Versions.exoPlayer}"
    val dash = "com.google.android.exoplayer:exoplayer-dash:${Versions.exoPlayer}"
    val hls = "com.google.android.exoplayer:exoplayer-hls:${Versions.exoPlayer}"
    val smoothStreaming =
        "com.google.android.exoplayer:exoplayer-smoothstreaming:${Versions.exoPlayer}"
    val ui = "com.google.android.exoplayer:exoplayer-ui:${Versions.exoPlayer}"
}

class Kotlin {
    val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
    val extensions = "org.jetbrains.kotlin:kotlin-android-extensions:${Versions.kotlin}"
    val coroutines =
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.kotlin_coroutines}"
    val coroutines_core =
        "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlin_coroutines}"
}

sealed class SquareUp {
    class OkHttp : SquareUp() {
        val logging = "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp}"
        val okhttp = "com.squareup.okhttp3:okhttp:${Versions.okhttp}"
        val okio = "com.squareup.okio:okio:${Versions.okio}"
    }

    class Moshi : SquareUp() {
        val moshi = "com.squareup.moshi:moshi:${Versions.moshi}"
        val moshi_kotlin = "com.squareup.moshi:moshi-kotlin:${Versions.moshi}"
        val moshi_kotlin_codegen = "com.squareup.moshi:moshi-kotlin-codegen:${Versions.moshi}"
    }
}

private object Versions {
    const val exoPlayer = "2.11.5"

    const val kotlin = "1.3.72"
    const val kotlin_coroutines = "1.3.7"

    const val okhttp = "4.4.1"
    const val okio = "2.4.3"
    const val moshi = "1.9.2"
}