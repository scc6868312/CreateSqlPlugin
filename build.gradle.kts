plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.10.1"
}

group = "com.scc"
version = "1.0.3"

repositories {
    mavenCentral()
    mavenLocal()
}


dependencies {
    implementation(fileTree(mapOf("dir" to "lib", "include" to "*.jar")))
    implementation("cn.hutool:hutool-all:5.8.14")
    implementation("mysql:mysql-connector-java:8.0.32")
    implementation("com.oracle.database.jdbc:ojdbc10:19.18.0.0")
    implementation("org.postgresql:postgresql:42.5.4")
    implementation("com.jcraft:jsch:0.1.55")
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2022.1.4")
    type.set("IC") // Target IDE Platform
    plugins.set(listOf(/* Plugin Dependencies */))
    updateSinceUntilBuild.set(false)
}
sourceSets {
    main {
        resources {
            srcDir("src/main/resources/com/scc/createsqlplugin")
            include("**/*.xml")
        }
    }
}
