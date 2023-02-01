import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.6.2"
	id("io.spring.dependency-management") version "1.0.15.RELEASE"
	kotlin("jvm") version "1.6.21"
	kotlin("plugin.spring") version "1.6.21"
	kotlin("plugin.jpa") version "1.6.21"
}

group = "com.grapqh"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation ("com.graphql-java-kickstart:graphql-spring-boot-starter:14.0.0")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("com.graphql-java-kickstart:graphql-spring-boot-starter:14.0.0")
	implementation("com.graphql-java-kickstart:graphiql-spring-boot-starter:11.0.0")
//	implementation("com.graphql-java-kickstart:graphql-java-tools:14.0.0")

	runtimeOnly("com.h2database:h2")
	testImplementation ("com.graphql-java-kickstart:graphql-spring-boot-starter-test:14.0.0")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework:spring-webflux")
	testImplementation("com.graphql-java-kickstart:graphql-spring-boot-starter:14.0.0")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
