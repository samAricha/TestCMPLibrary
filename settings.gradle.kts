rootProject.name = "TestCMPLibrary"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":sample:composeApp")
include(":test_lib")

pluginManagement {
    repositories {
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
        gradlePluginPortal()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace == "com.android" || requested.id.name == "kotlin-android-extensions") {
                useModule("com.android.tools.build:gradle:8.2.0")
            }
        }
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven(url = "https://jitpack.io")
    }
}
