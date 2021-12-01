import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.31"
}

group = "com.dosu"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation( "org.jetbrains.kotlin:kotlin-stdlib:1.5.31")
    implementation( "org.jcodec:jcodec:0.2.5")
    implementation( "org.jcodec:jcodec-javase:0.2.5")
    implementation( "org.bytedeco:ffmpeg-platform:4.3.2-1.5.5")
    implementation( "org.bytedeco:javacv:1.5.5")
    implementation( "org.bytedeco:opencv-platform:4.5.1-1.5.5")
    implementation( "org.bytedeco:javacpp:1.5.5")
    implementation( "org.jdom:jdom2:2.0.6")
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}