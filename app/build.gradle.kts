import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.google.devtools.ksp)
}

android {
    namespace = "org.jahangostar.busincreasement"
    compileSdk = 36

    applicationVariants.all {
        val variant = this
        variant.outputs
            .map { it as BaseVariantOutputImpl }
            .forEach { output ->
                val outputFileName =
                    "BusIncreasementPos-${variant.versionName}.apk"
                output.outputFileName = outputFileName
            }
    }


    defaultConfig {

        val propertiesFile = rootProject.file("key.properties")

        if (propertiesFile.exists()) {
            val properties = Properties()
            properties.load(propertiesFile.inputStream())

            val createBuildConfigField: (String, String) -> Unit = { fieldName, propertyName ->
                val fieldValue = properties.getProperty(propertyName) ?: ""
                buildConfigField("String", fieldName, "\"$fieldValue\"")
            }

            createBuildConfigField("SECTOR_0_KEY_A", "SECTOR_0_KEY_A")
            createBuildConfigField("SECTOR_0_KEY_B", "SECTOR_0_KEY_B")

            createBuildConfigField("SECTOR_1_KEY_A", "SECTOR_1_KEY_A")
            createBuildConfigField("SECTOR_1_KEY_B", "SECTOR_1_KEY_B")

            createBuildConfigField("SECTOR_2_KEY_A", "SECTOR_2_KEY_A")
            createBuildConfigField("SECTOR_2_KEY_B", "SECTOR_2_KEY_B")

            createBuildConfigField("SECTOR_3_KEY_A", "SECTOR_3_KEY_A")
            createBuildConfigField("SECTOR_3_KEY_B", "SECTOR_3_KEY_B")

            createBuildConfigField("SECTOR_4_KEY_A", "SECTOR_4_KEY_A")
            createBuildConfigField("SECTOR_4_KEY_B", "SECTOR_4_KEY_B")

            createBuildConfigField("SECTOR_5_KEY_A", "SECTOR_5_KEY_A")
            createBuildConfigField("SECTOR_5_KEY_B", "SECTOR_5_KEY_B")

            createBuildConfigField("SECTOR_6_KEY_A", "SECTOR_6_KEY_A")
            createBuildConfigField("SECTOR_6_KEY_B", "SECTOR_6_KEY_B")

            createBuildConfigField("SECTOR_7_KEY_A", "SECTOR_7_KEY_A")
            createBuildConfigField("SECTOR_7_KEY_B", "SECTOR_7_KEY_B")

            createBuildConfigField("SECTOR_8_KEY_A", "SECTOR_8_KEY_A")
            createBuildConfigField("SECTOR_8_KEY_B", "SECTOR_8_KEY_B")

            createBuildConfigField("SECTOR_9_KEY_A", "SECTOR_9_KEY_A")
            createBuildConfigField("SECTOR_9_KEY_B", "SECTOR_9_KEY_B")

            createBuildConfigField("SECTOR_10_KEY_A", "SECTOR_10_KEY_A")
            createBuildConfigField("SECTOR_10_KEY_B", "SECTOR_10_KEY_B")

            createBuildConfigField("SECTOR_11_KEY_A", "SECTOR_11_KEY_A")
            createBuildConfigField("SECTOR_11_KEY_B", "SECTOR_11_KEY_B")

            createBuildConfigField("SECTOR_12_KEY_A", "SECTOR_12_KEY_A")
            createBuildConfigField("SECTOR_12_KEY_B", "SECTOR_12_KEY_B")

            createBuildConfigField("SECTOR_13_KEY_A", "SECTOR_13_KEY_A")
            createBuildConfigField("SECTOR_13_KEY_B", "SECTOR_13_KEY_B")

        } else {
            throw GradleException("Could not find key.properties file in root directory.")
        }


        applicationId = "org.jahangostar.busincreasement"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //permission
    implementation(libs.accompanist.permissions)

    // sql server
    implementation(libs.jtds)

    //navigation
    implementation(libs.androidx.navigation.compose)

    //icon
    implementation(libs.androidx.material.icons.extended)

    //room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.paging)

    //hilt di
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    ksp(libs.androidx.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    //compose navigation
    implementation(libs.androidx.navigation.compose)

    //serialization
    implementation(libs.kotlinx.serialization.json)

    //paging
    implementation(libs.androidx.paging.compose)

    //date picker
    implementation(libs.compose.persian.date.picker)


    //xml
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.activity.ktx)

    //persian date
    implementation(libs.persiandate)
    implementation(libs.persianrangedatepicker)

    //pagination
    implementation(libs.androidx.paging.runtime.ktx)

    //coil
    implementation(libs.coil.compose)
}