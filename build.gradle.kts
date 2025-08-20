plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.5.3"
	id("io.spring.dependency-management") version "1.1.7"
	kotlin("plugin.jpa") version "1.9.25"
	kotlin("plugin.serialization") version "1.9.25"

}

group = "com.gabriel"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("com.google.api-client:google-api-client:1.25.0")
	implementation("com.google.oauth-client:google-oauth-client:1.35.0")
	implementation("com.google.apis:google-api-services-youtube:v3-rev222-1.25.0")


//	implementation("org.keycloak:keycloak-spring-boot-starter:25.0.3")
	implementation("org.keycloak:keycloak-admin-client:26.0.6")


	implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.13")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

	val ktor_version = "2.3.13"
	//val ktor_version = "3.2.3"
	implementation("io.ktor:ktor-client-core:$ktor_version")
	implementation("io.ktor:ktor-client-cio:$ktor_version")
	implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")

	implementation("br.com.efipay.efisdk:sdk-java-apis-efi:1.2.4")


	implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-security")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	implementation(platform("io.github.jan-tennert.supabase:bom:3.0.0"))
	implementation("io.github.jan-tennert.supabase:postgrest-kt")
	implementation("io.ktor:ktor-client-apache5:$ktor_version")

	runtimeOnly("com.h2database:h2")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
