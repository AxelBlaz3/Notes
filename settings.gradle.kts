pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "Notes"
include(":app")
include(":core:designsystem")
include(":core:data")
include(":core:database")
include(":core:model")
include(":core:ui")
include(":feature:notes")
include(":core:common")
include(":core:domain")
