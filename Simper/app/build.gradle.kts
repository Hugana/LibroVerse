plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.simper"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.simper"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures{
        viewBinding = true
        compose = true;
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("eu.freme-project:epublib-tools:0.5") {
        exclude (group, "xmlpull")
        exclude (group, "hamcrest-core")
    }
    implementation("eu.freme-project:epublib-core:0.5"){
        exclude (group, "xmlpull")
        exclude (group, "hamcrest-core")
    }
    implementation("net.sf.kxml:kxml2:2.3.0") {
        exclude (group, "xmlpull")
        exclude (group, "hamcrest-core")
    }

    implementation("com.android.volley:volley:1.2.1")

    implementation("org.slf4j:slf4j-api:1.7.30")
    implementation("org.slf4j:slf4j-simple:1.6.1")
    implementation("commons-io:commons-io:2.13.0")
    implementation("commons-lang:commons-lang:2.4")
    implementation("commons-vfs:commons-vfs:1.0")
    implementation("junit:junit:4.13.2")
    implementation("net.sourceforge.htmlcleaner:htmlcleaner:2.15")
    implementation("org.mockito:mockito-all:1.10.19") {
        exclude (group, "hamcrest-core")
    }
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("androidx.privacysandbox.tools:tools-core:1.0.0-alpha07")
    implementation("androidx.activity:activity:1.8.0")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")



    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))

    // When using the BoM, you don't specify versions in Firebase library dependencies

    // Add the dependency for the Firebase SDK for Google Analytics
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-firestore")


    implementation ("com.google.firebase:firebase-storage:21.0.0")
    implementation ("com.google.firebase:firebase-auth:23.0.0")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    configurations.all {
        exclude (module= "xmlpull")
        exclude (module= "xmlpull-api")
        exclude (module= "xmlpull_jaxp_api")
        exclude (group= "org.hamcrest", module= "hamcrest-core")
        exclude (module= "protobuf-java")
    }
}

