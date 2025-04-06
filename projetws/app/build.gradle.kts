plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.projetws"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.projetws"
        minSdk = 24
        targetSdk = 35
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
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("com.android.volley:volley:1.2.1")
    implementation("com.google.code.gson:gson:2.10")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation ("com.github.bumptech.glide:glide:4.13.2")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    // Picasso pour le chargement d'images
    implementation ("com.squareup.picasso:picasso:2.71828")

    // CardView pour les items de la liste
    implementation ("androidx.cardview:cardview:1.0.0")
}