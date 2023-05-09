plugins {
	java
	id("org.springframework.boot") version "3.0.5"
	id("io.spring.dependency-management") version "1.1.0"
}

group = "pl.edu.pw"
version = "0.0.1-SNAPSHOT"

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation(project(":lib"))
	implementation("org.neo4j:neo4j-ogm-core:4.0.5")
	implementation("org.neo4j:neo4j-ogm-bolt-driver:4.0.5")
	implementation("org.springframework.boot:spring-boot-starter-data-neo4j")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("commons-logging:commons-logging:1.2")
	implementation("commons-logging:commons-logging:1.2")
	implementation("commons-logging:commons-logging:1.2")
	implementation("commons-logging:commons-logging:1.2")
	implementation("commons-logging:commons-logging:1.2")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	// https://mvnrepository.com/artifact/org.springdoc/springdoc-openapi-starter-webmvc-ui
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
tasks.register("prepareKotlinBuildScriptModel"){}