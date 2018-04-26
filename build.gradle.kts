buildscript {
    val kotlinVersion by extra("1.2.40")
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}
plugins {
    id("org.jetbrains.intellij") version "0.3.1"
    id("org.jetbrains.kotlin.jvm") version "1.2.40"
}

group = "cn.javaer.intellij.plugin"
version = "1.0"
repositories {
    mavenCentral()
}
intellij {
    version = "2018.1.1"
    updateSinceUntilBuild = false
}
java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    compile("io.takari:maven-wrapper:0.4.0")
    compileOnly(fileTree(mapOf("dir" to "libs", "include" to "*.jar")))
}