plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.androidx.room)
    alias(libs.plugins.dagger.hilt)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "io.github.vulka.database"
    compileSdk = libs.versions.android.sdk.compile.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.sdk.min.get().toInt()
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    ksp {
        arg("room.generateKotlin", "true")
    }
}

dependencies {
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)

    implementation(project(":vulcan-impl"))
    ksp(libs.androidx.room.compiler)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.datastore.annotations)
    ksp(libs.datastore.annotations.processor)

    implementation(projects.coreApi)

    implementation(libs.dagger.hilt)
    ksp(libs.dagger.hilt.compiler)

    // credential type converter
    implementation(libs.gson)
    implementation(libs.gson.extras)

    implementation(libs.androidx.datastore)
    implementation(libs.androidx.datastore.core)

    coreLibraryDesugaring(libs.desugar.jdk.libs)
}

room {
    schemaDirectory("$projectDir/schemas")
}
