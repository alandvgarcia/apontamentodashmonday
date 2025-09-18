import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
}

val osName = System.getProperty("os.name")
val targetOs = when {
    osName == "Mac OS X" -> "macos"
    osName.startsWith("Win") -> "windows"
    osName.startsWith("Linux") -> "linux"
    else -> error("Unsupported OS: $osName")
}

val osArch = System.getProperty("os.arch")
val targetArch = when (osArch) {
    "x86_64", "amd64" -> "x64"
    "aarch64" -> "arm64"
    else -> error("Unsupported arch: $osArch")
}

val version = "0.9.24" // or any more recent version
val target = "${targetOs}-${targetArch}"


kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    jvm()

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
//            implementation(compose.material3)
            implementation("org.jetbrains.compose.material3:material3:1.10.0-alpha01")
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.jetbrains.icons)

            implementation(libs.csv)
            implementation("io.github.koalaplot:koalaplot-core:0.9.1")
            implementation("io.github.vinceglb:filekit-core:0.11.0")
            implementation("io.github.vinceglb:filekit-dialogs:0.11.0")
            implementation("io.github.vinceglb:filekit-dialogs-compose:0.11.0")
            implementation("io.github.vinceglb:filekit-coil:0.11.0")
            implementation("com.squareup.okio:okio:3.16.0")
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1")


        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)

            implementation("org.apache.poi:poi:5.4.1")
            implementation("org.apache.poi:poi-ooxml:5.4.1")
            implementation("org.apache.logging.log4j:log4j-api:2.24.3")
            implementation("org.apache.logging.log4j:log4j-core:2.24.3")




            implementation("org.jetbrains.skiko:skiko-awt-runtime-$target:$version")
        }


    }
}

android {
    namespace = "com.solinftec.apontamentosmondaydash"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.solinftec.apontamentosmondaydash"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.desktop {


    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    application {
        mainClass = "com.solinftec.apontamentosmondaydash.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Rpm)
            packageName = "com.solinftec.apontamentosmondaydash"
            packageVersion = "1.0.0"
        }
    }
}

tasks.withType<JavaExec> {
    jvmArgs("--enable-native-access=ALL-UNNAMED")
}
