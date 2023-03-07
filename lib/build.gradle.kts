import kotlin.io.path.toPath

/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Java library project to get you started.
 * For more details take a look at the 'Building Java & JVM projects' chapter in the Gradle
 * User Manual available at https://docs.gradle.org/8.0.2/userguide/building_java_projects.html
 * This project uses @Incubating APIs which are subject to change.
 */

plugins {
    // Apply the java-library plugin for API and implementation separation.
    `java-library`
    `maven-publish`
    jacoco
    signing
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/evantill/sample-lib")
            credentials {
                username = "GITHUB_ACTOR".byProperty
                password = "GITHUB_TOKEN".byProperty
            }
        }
        maven {
            name = "Local"
            url = uri(project.layout.buildDirectory.dir("localMavenRepository"))
        }
    }

    publications {
        create<MavenPublication>("sampleLib") {
            from(components["java"])

            pom {
                name.set("Sample Library")
                description.set("Sample library build using gradle and github workflow")
                url.set("https://github.com/evantill/sample-lib")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("evantill")
                        name.set("Eric Vantillard")
                        email.set("eric.vantillard@evaxion.fr")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/evantill/sample-lib.git")
                    developerConnection.set("scm:git:git@github.com:evantill/sample-lib.git")
                    url.set("https://github.com/evantill/sample-lib")
                }
            }
        }
    }
}
val localRepository = publishing.repositories["Local"] as MavenArtifactRepository
val sampleLibPublication = publishing.publications["sampleLib"] as MavenPublication
val publishAllPublicationsToLocalRepository by tasks.existing

val isCiBuild = System.getenv("CI") != null


val signingKeyId: String? by project
val signingKey: String? by project
val signingPassword: String? by project
val signingEnabled: Provider<Boolean> = provider {
    signingKeyId != null && signingKey != null && signingPassword != null
}

signing {
    if(isCiBuild){
        logger.lifecycle("sign using in memory pgp keys")
        if(!signingEnabled.get()){
            logger.warn("invalid signing configuration")
        }
        useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
    }else {
        logger.lifecycle("sign using gpg cmd")
        useGpgCmd()
    }
    sign(sampleLibPublication)
}


group = "com.github.evantill.sample-lib"
version = "0.0.2-SNAPSHOT"

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    // This dependency is exported to consumers, that is to say found on their compile classpath.
    api(libs.commons.math3)

    // This dependency is used internally, and not exposed to consumers on their own compile classpath.
    implementation(libs.guava)
}

testing {
    suites {
        // Configure the built-in test suite
        val test by getting(JvmTestSuite::class) {
            // Use JUnit Jupiter test framework
            useJUnitJupiter(libs.versions.junit.get())
        }
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}

val String.byProperty: String? get() = providers.gradleProperty(this).orNull

val printLocalRepo by tasks.registering{
    group = "help"
    description = "print "+localRepository.name+ " repository content"
    dependsOn(publishAllPublicationsToLocalRepository)
    doLast{
        logger.lifecycle(""+localRepository.name+" repository content:")
        val repositoryDir = localRepository.url.toPath().toFile()

        fileTree(repositoryDir).filter {
            it.isFile()
        }.forEach {
            logger.lifecycle("\t"+it.relativeTo(repositoryDir).toString());
        }
    }
}

tasks.withType<Sign>().configureEach {
    notCompatibleWithConfigurationCache("https://github.com/gradle/gradle/issues/13470")
}
