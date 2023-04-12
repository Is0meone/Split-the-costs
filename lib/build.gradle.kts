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
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.register("prepareKotlinBuildScriptModel"){}