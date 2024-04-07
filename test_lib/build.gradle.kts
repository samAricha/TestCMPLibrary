import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi



plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.mavenPublishVanniktech) apply false
    id("maven-publish")
    id("org.jetbrains.dokka") version "1.9.20"
    id("signing")
}

kotlin {
//    kotlin.applyDefaultHierarchyTemplate()

    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
        publishAllLibraryVariants()
    }
    
    jvm("desktop")
    
    sourceSets {
        val desktopMain by getting
        
        androidMain.dependencies {
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
    }
}

android {
    namespace = "org.teka.image_preview_cmp_library"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    dependencies {
        debugImplementation(libs.compose.ui.tooling)
    }
}


group = "io.revenuemonster.sdk"
// remove prefix v if the version included, eg `v1.0.0`
//version = "(System.getenv("RM_KOTLIN_SDK_VERSION") ?: "1.0.0").removePrefix("v")"
version = "0.0.1"

val artifact = "rm-kotlin-sdk"
val pkgUrl = "https://github.com/samAricha/TestCMPLibrary.git"
val gitUrl = "github.com:samAricha/TestCMPLibrary.git"
val ktorVersion = "2.1.0"

val dokkaOutputDir = "$projectDir/dokka"

tasks.dokkaHtml {
    outputDirectory.set(file(dokkaOutputDir))
}

val dokkaJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
//    description = "Assembles Kotlin docs with Dokka"
    archiveClassifier.set("javadoc")
    from(tasks.dokkaHtml)
}

//mavenPublishing {
////    publishToMavenCentral(SonatypeHost.DEFAULT)
//    // or when publishing to https://s01.oss.sonatype.org
//    publishToMavenCentral(SonatypeHost.S01, automaticRelease = true)
//    signAllPublications()
//    coordinates("com.example.mylibrary", "mylibrary-runtime", "1.0.0")
//
//    pom {
//        name.set(project.name)
//        description.set("A description of what my library does.")
//        inceptionYear.set("2023")
//        url.set("https://github.com/username/mylibrary/")
//        licenses {
//            license {
//                name.set("The Apache License, Version 2.0")
//                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
//                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
//            }
//        }
//        developers {
//            developer {
//                id.set("username")
//                name.set("User Name")
//                url.set("https://github.com/username/")
//            }
//        }
//        scm {
//            url.set("https://github.com/username/mylibrary/")
//            connection.set("scm:git:git://github.com/username/mylibrary.git")
//            developerConnection.set("scm:git:ssh://git@github.com/username/mylibrary.git")
//        }
//    }
//}

publishing {
    repositories {
        maven {
            name = "Oss"
            setUrl {
                "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
            }
            credentials {
                username = System.getenv("SONATYPE_USERNAME")
                password = System.getenv("SONATYPE_PASSWORD")
            }
        }
        maven {
            name = "Snapshot"
            setUrl { "https://s01.oss.sonatype.org/content/repositories/snapshots/" }
            credentials {
                username = System.getenv("SONATYPE_USERNAME")
                password = System.getenv("SONATYPE_PASSWORD")
            }
        }
    }

    publications {
        publications.configureEach {
            if (this is MavenPublication) {
                artifact(dokkaJar)
                pom {
                    name.set(artifact)
                    description.set("Revenue Monster Kotlin Multiplatform SDK")
                    url.set(pkgUrl)

                    licenses {
                        license {
                            name.set("MIT license")
                            url.set("https://opensource.org/licenses/MIT")
                        }
                    }

                    issueManagement {
                        system.set("GitHub Issues")
                        url.set("$pkgUrl/issues")
                    }

                    developers {
                        developer {
                            id.set("si3nloong")
                            name.set("Lee Sian Loong")
                            email.set("sianloong90@gmail.com")
                        }
                        developer {
                            id.set("SnorSnor9998")
                            name.set("Snor")
                            email.set("snorsnor9998@gmail.com")
                        }
                    }

                    scm {
                        connection.set("scm:git:git://$gitUrl")
                        developerConnection.set("scm:git:ssh://$gitUrl")
                        url.set(pkgUrl)
                    }
                }
            }
        }
//        create<MavenPublication>("maven") {
//            withType<MavenPublication> {
//                groupId = "$group"
//                artifactId = artifact
//                version = version
//                artifact(dokkaJar)
//            }
//        }
    }

}

if (System.getenv("GPG_PRIVATE_KEY") != null && System.getenv("GPG_PRIVATE_PASSWORD") != null) {
    signing {
        useInMemoryPgpKeys(
            System.getenv("GPG_PRIVATE_KEY"),
            System.getenv("GPG_PRIVATE_PASSWORD")
        )
        sign(publishing.publications)
    }
}




