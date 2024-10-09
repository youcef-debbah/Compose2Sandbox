import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    signingConfigs {
        create("release") {
            storeFile =
                file("C:\\Users\\Administrator\\Documents\\main docs\\keystores\\test\\TestStore.jks")
            storePassword = "123456789"
            keyPassword = "123456789"
            keyAlias = "key0"
        }
    }
    namespace = "dz.nexatech.tests.compose2sandbox"
    compileSdk = 34

    defaultConfig {
        applicationId = "dz.nexatech.tests.compose2sandbox"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
        create("benchmark") {
            initWith(buildTypes.getByName("release"))
            matchingFallbacks += listOf("release")
            isDebuggable = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
}

val composeMetricsDir: File = file("out/compose_metrics")

composeCompiler {
//    Default: false
//    If true, generate function key meta classes with annotations
//    indicating the functions and their group keys.
    this.generateFunctionKeyMetaClasses = true

//    Default: false (true for Android)
//    If true, include source information in generated code.
//    Records source information that can be used for tooling
//    to determine the source location of the corresponding composable function.
//    This option does not affect the presence of symbols or line information
//    normally added by the Kotlin compiler; it only controls source information
//    added by the Compose compiler.
    this.includeSourceInformation = true

//    Default: true
//    If true, include composition trace markers in the generated code.
//    The Compose compiler can inject additional tracing information into the bytecode,
//    which allows it to show composable functions in the Android Studio system trace profiler.
//    For details, see this Android Developers blog post.
    this.includeTraceMarkers = true

    this.reportsDestination = composeMetricsDir
    this.metricsDestination = composeMetricsDir

    this.stabilityConfigurationFile = File("stability.conf")

    this.targetKotlinPlatforms = objects.setProperty<KotlinPlatformType>().apply {
        this.add(KotlinPlatformType.androidJvm)
    }
}

dependencies {
    implementation(libs.androidx.core)
    implementation(libs.lifecycle.runtime)
    implementation(libs.compose.activity)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.junit4)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test)
    implementation(libs.compose.tracing)
    implementation(libs.kotlin.immutable)
    implementation("com.google.accompanist:accompanist-drawablepainter:0.36.0")
}

tasks.clean {
    composeMetricsDir.listFiles()?.let {
        delete(*it)
    }
}