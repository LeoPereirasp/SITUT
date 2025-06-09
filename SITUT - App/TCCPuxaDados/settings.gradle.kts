pluginManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io") // Adicionado JitPack aqui
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io") // Adicionado JitPack aqui
    }
}

rootProject.name = "TCCPuxxaDados"
include(":app")
