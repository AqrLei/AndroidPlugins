apply(plugin = "maven-publish")
apply(plugin = "kotlin")

configure<PublishingExtension> {
    repositories {
        maven {
            url = uri(PublishConfig.nexusReleaseUrl)
            credentials {
                username = PublishConfig.repoUserName
                password = PublishConfig.repoPassword
            }
        }
        mavenLocal {
            url = uri("../maven")
        }
    }
}