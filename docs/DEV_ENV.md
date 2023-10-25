# 开发环境配置

## Android Studio

推荐使用 Jetbrains Toolbox 进行版本管理

### 版本选择

优先使用 RC 版本，RC 版本作为最后的发布候选版本，常见的 bug 都已经修复的差不多，且较稳定版拥有更多的新特性
如

```
Hedgehog 2023.1.1 RC 1
```

### 修复 Markdown 预览功能

双击 Shift 打开搜索框，搜索 `Choose Boot Java Runtime for the IDE`，选择带有 JCEF 的运行时，安装后重启即可

## Gradle 版本配置

更新升级版本镜像后将 [gradle-wrapper.properties](../gradle/wrapper/gradle-wrapper.properties)
下载地址修改为 NJU Mirror
如(记得修改版本号)：

```
distributionUrl=http://mirror.nju.edu.cn/gradle/gradle-8.4-bin.zip
```

## Gradle 国内镜像

[NJU Mirror](https://doc.nju.edu.cn/books/35f4a/page/gradle)

Linux 下 `~/.gradle/init.gradle` or Windows 下 `C:\Users\<UserName>\.gradle\init.gradle`

覆盖写入以下内容:

```groovy
settingsEvaluated { settings ->
    settings.pluginManagement {
        repositories {
        def NJU_REPOSITORY_URL = 'https://repo.nju.edu.cn/repository/maven-public/'
        all { ArtifactRepository repo ->
            if (repo instanceof MavenArtifactRepository) {
                def url = repo.url.toString()
                if (url.startsWith('https://repo1.maven.org/maven2')) {
                    project.logger.lifecycle "Repository ${repo.url} replaced by $NJU_REPOSITORY_URL."
                    remove repo
                }
                if (url.startsWith('https://jcenter.bintray.com/')) {
                    project.logger.lifecycle "Repository ${repo.url} deleted."
                    remove repo
                }
                if (url.startsWith('https://dl.google.com/dl/android/maven2/')) {
                    project.logger.lifecycle "Repository ${repo.url} deleted."
                    remove repo
                }
                if (url.contains('plugins.gradle.org/m2')) {
                    project.logger.lifecycle "Repository ${repo.url} deleted."
                    remove repo
                }
            }
        }
        maven { url NJU_REPOSITORY_URL }
        mavenLocal()
        }
    }
    settings.dependencyResolutionManagement {
        repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
        repositories {
            def NJU_REPOSITORY_URL = 'https://repo.nju.edu.cn/repository/maven-public/'
            all { ArtifactRepository repo ->
                if (repo instanceof MavenArtifactRepository) {
                    def url = repo.url.toString()
                    if (url.startsWith('https://repo1.maven.org/maven2')) {
                        project.logger.lifecycle "Repository ${repo.url} replaced by $NJU_REPOSITORY_URL."
                        remove repo
                    }
                    if (url.startsWith('https://jcenter.bintray.com/')) {
                        project.logger.lifecycle "Repository ${repo.url} deleted."
                        remove repo
                    }
                    if (url.startsWith('https://dl.google.com/dl/android/maven2/')) {
                        project.logger.lifecycle "Repository ${repo.url} deleted."
                        remove repo
                    }
                    if (url.contains('plugins.gradle.org/m2')) {
                        project.logger.lifecycle "Repository ${repo.url} deleted."
                        remove repo
                    }
                }
            }
            maven { url NJU_REPOSITORY_URL }
            mavenLocal()
        }
    }
}
```

如果已经运行过 gradle ，请先执行一次 `gradle --stop` 或 `./gradlew --stop` 命令关闭所有 gradle 的
daemon， 然后重新运行即可。
