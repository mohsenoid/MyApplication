@file:Suppress("MagicNumber", "SpellCheckingInspection")

import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

val envReleaseNote: String = System.getenv("RELEASE_NOTE") ?: "LOCAL_BUILD"

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("org.jlleitschuh.gradle.ktlint") version "10.2.1"
    id("io.gitlab.arturbosch.detekt") version "1.19.0"
    id("com.google.firebase.appdistribution")
    id("com.google.gms.google-services")
}

val composeVersion = "1.1.1"

android {
    namespace = "com.mohsenoid.myapplication"

    compileSdk = 31

    signingConfigs {
        create("release") {
            storeFile = file("${rootProject.projectDir}/keystore.jks")
            storePassword = "123456"
            keyAlias = "app"
            keyPassword = "123456"
        }
    }

    defaultConfig {
        applicationId = "com.mohsenoid.myapplication"

        minSdk = 23
        targetSdk = 31

        versionCode = 4
        versionName = "1.3.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        signingConfig = signingConfigs["release"]

        firebaseAppDistribution {
            artifactType = "APK"
            groups = "QA"
            releaseNotes = envReleaseNote
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }

        val environmentFlavorDimension = "environment"
        flavorDimensions.add(environmentFlavorDimension)
        productFlavors {
            create("production") {
                isDefault = true
                dimension = environmentFlavorDimension
            }

            create("staging") {
                dimension = environmentFlavorDimension

                applicationIdSuffix = ".stg"
                versionNameSuffix = "-stg"
            }

            create("development") {
                dimension = environmentFlavorDimension

                applicationIdSuffix = ".dev"
                versionNameSuffix = "-dev"
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        // allWarningsAsErrors = true
        jvmTarget = JavaVersion.VERSION_1_8.toString()
        freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn")
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = composeVersion
    }

    packagingOptions {
        resources {
            excludes += "/META-INFinstabug_comment_hint_bug/{AL2.0,LGPL2.1}"
            excludes += "META-INF/*"
        }
    }

    kotlinOptions {
        allWarningsAsErrors = true
    }

    lint {
        ignoreWarnings = false
        warningsAsErrors = true
    }
}

detekt {
    allRules = true
    config = files("detekt-config.yml")
    buildUponDefaultConfig = true
}

ktlint {
    reporters {
        reporter(ReporterType.HTML)
        reporter(ReporterType.CHECKSTYLE)
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("com.google.android.material:material:1.6.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.1")
    implementation("androidx.activity:activity-compose:1.4.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

    // kotlin
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.6.10"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // compose
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeVersion")
    debugImplementation("androidx.compose.ui:ui-tooling:$composeVersion")

    // security
    implementation("androidx.security:security-crypto:1.0.0")
}
