plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.5"
    id("io.freefair.lombok") version "8.13.1"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    // https://mvnrepository.com/artifact/net.portswigger.burp.extensions/montoya-api
    implementation("net.portswigger.burp.extensions:montoya-api:2025.2")
    implementation("com.github.CoreyD97:Burp-Montoya-Utilities:234d21d")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}