pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        maven {
            url = uri("https://tokbox.bintray.com/maven")
            content {
                includeGroupByRegex("com\\.opentok.*")
            }
        }
        maven { url = uri("https://storage.zego.im/maven")  }
        maven { url = uri("https://www.jitpack.io")  }
    }
}

rootProject.name = "1"
include(":app")