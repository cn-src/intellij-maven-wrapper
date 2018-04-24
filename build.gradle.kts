plugins {
    java
    id("org.jetbrains.intellij") version "0.3.1"
}

group = "cn.javaer.intellij.plugin"
version = "1.0-SNAPSHOT"

intellij {
    version = "2018.1.1"
    updateSinceUntilBuild = false
}

tasks.withType(JavaCompile::class.java) {
    options.encoding = "UTF-8"
}
configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}