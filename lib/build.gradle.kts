plugins {
    id("java")
}

group = "pl.edu.pw"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.neo4j:neo4j-ogm-core:4.0.5")
    implementation("org.neo4j:neo4j-ogm-bolt-driver:4.0.5")
    implementation("commons-logging:commons-logging:1.2")
    implementation("commons-logging:commons-logging:1.2")
    implementation("commons-logging:commons-logging:1.2")
    implementation("commons-logging:commons-logging:1.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    implementation("org.slf4j:slf4j-api:2.0.7")
    implementation("ch.qos.logback:logback-classic:1.4.7")
    implementation("org.springframework.security:spring-security-crypto:6.0.3")
    implementation("org.springframework.security:spring-security-crypto:5.1.4.RELEASE")
    implementation("org.apache.commons:commons-lang3:3.0")
    implementation("org.bouncycastle:bcprov-jdk15on:1.64")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.register("prepareKotlinBuildScriptModel"){}