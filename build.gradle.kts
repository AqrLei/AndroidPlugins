buildscript {

    addRepos(repositories)
    dependencies {
        classpath(ClassPath.android_gradle)
        classpath(ClassPath.kotlin_gradle)
        classpath("com.aqrlei.plugin:ImageConvert:1.0.0-beta01")
        classpath("com.aqrlei.plugin:Component:1.0.0-beta01")
        classpath("com.aqrlei.plugin:DepDeduplicate:1.0.0-alpha23")
    }
}

allprojects {
    addRepos(repositories)
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}