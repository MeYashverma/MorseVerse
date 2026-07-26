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
    @Suppress("UnstableApiUsage")
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "MorseVerse"

include(":app")
include(":core:data")
include(":core:domain")
include(":core:common")
include(":core:designsystem")
include(":feature:home")
include(":feature:learn")
include(":feature:practice")
include(":feature:morseTree")
include(":feature:translator")
include(":feature:decoder")
include(":feature:statistics")
include(":feature:achievements")
include(":feature:story")
include(":feature:ham")
