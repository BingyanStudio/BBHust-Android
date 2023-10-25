pluginManagement {
    repositories {
        maven(url = "https://repo.nju.edu.cn/repository/maven-public/")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven(url = "https://repo.nju.edu.cn/repository/maven-public/")
    }
}

rootProject.name = "BBHust"
include(":app")
include(":richtext")
