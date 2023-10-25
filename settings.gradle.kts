pluginManagement {
    repositories {
        maven(url = "https://repo.nju.edu.cn/repository/maven-public/")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven(url = "https://repo.nju.edu.cn/repository/maven-public/")
        maven(url = "https://mvn.getui.com/nexus/content/repositories/releases/")

        maven {
            url = uri("https://maven.columbus.heytapmobi.com/repository/releases/")
            credentials.username = "nexus"
            credentials.password = "c0b08da17e3ec36c3870fed674a0bcb36abc2e23"
        }
    }
}

rootProject.name = "BBHust"
include(":app")
include(":richtext")
