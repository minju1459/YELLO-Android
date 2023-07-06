import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.serialization") version Versions.kotlinVersion
}

android {
    compileSdk = Constants.compileSdk

    defaultConfig {
        minSdk = Constants.minSdk
        targetSdk = Constants.targetSdk

        buildConfigField(
            "String",
            "BASE_URL",
            gradleLocalProperties(rootDir).getProperty("base.url"),
        )

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = Versions.javaVersion
        targetCompatibility = Versions.javaVersion
    }

    kotlinOptions {
        jvmTarget = Versions.jvmVersion
    }

    buildFeatures {
        buildConfig = true
    }

    namespace = "com.yello.data"
}

dependencies {
    implementation(project(":domain"))
    AndroidXDependencies.run {
        implementation(hilt)
        implementation(security)
        implementation(coreKtx)
    }

    KotlinDependencies.run {
        implementation(kotlin)
        implementation(jsonSerialization)
        implementation(coroutines)
        implementation(dateTime)
    }

    ThirdPartyDependencies.run {
        implementation(retrofit)
        implementation(retrofitJsonConverter)
        implementation(timber)
    }

    TestDependencies.run {
        testImplementation(jUnit)
        androidTestImplementation(androidTest)
        androidTestImplementation(espresso)
    }
}