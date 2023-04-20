import com.google.android.libraries.mapsplatform.secrets_gradle_plugin.androidProject
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        val kotlinVersion = "1.8.0"
        classpath("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1")
        classpath(kotlin("serialization", version = kotlinVersion))
    }
}// Top-level build file where you can add configuration options common to all sub-projects/modules.
subprojects {
    afterEvaluate{
        tasks.withType<KotlinCompile> {
            kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
        }
    }
}
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.hilt) apply false
}
