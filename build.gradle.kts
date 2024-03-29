group = "me.xemor"
version = "4.6.3"
description = "superheroes"
java.sourceCompatibility = JavaVersion.VERSION_17

plugins {
    java
    `maven-publish`
    id("com.github.johnrengelman.shadow") version("7.1.2")
    id("io.sentry.jvm.gradle") version("3.12.0")
}

repositories {
    mavenLocal()
    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
    maven { url = uri("https://oss.sonatype.org/content/groups/public/") }
    maven { url = uri("https://repo.codemc.org/repository/maven-public") }
    maven { url = uri("https://github.com/deanveloper/SkullCreator/raw/mvn-repo/") }
    maven { url = uri("https://repo.minebench.de/") }
    maven { url = uri("https://repo.maven.apache.org/maven2/") }
}

dependencies {
    shadow("de.themoep:minedown-adventure:1.7.1-SNAPSHOT")
    shadow("me.xemor:configurationdata:2.0.0-SNAPSHOT")
    shadow("org.bstats:bstats-bukkit:1.7")
    shadow("me.xemor:UserInterface:1.6.6-SNAPSHOT")
    shadow("dev.dbassett:skullcreator:3.0.1")
    shadow("org.jetbrains:annotations:20.1.0")
    shadow("net.kyori:adventure-platform-bukkit:4.1.0")
    shadow("mysql:mysql-connector-java:8.0.29")
    shadow("com.zaxxer:HikariCP:4.0.3")
    shadow("net.kyori:adventure-api:4.10.1")
    shadow("org.apache.commons:commons-lang3:3.12.0")
    shadow("io.sentry:sentry:6.29.0")
    compileOnly("org.spigotmc:spigot-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("me.xemor:skillslibrary2:2.9.0")
}

java {
    configurations.shadow.get().dependencies.remove(dependencies.gradleApi())
}

tasks.shadowJar {
    minimize()
    relocate("net.kyori", "me.xemor.superheroes.kyori")
    relocate("me.xemor.configurationdata", "me.xemor.superheroes.configurationdata")
    relocate("de.themoep", "me.xemor.superheroes.de.themoep")
    relocate("org.jetbrains", "me.xemor.superheroes.org.jetbrains")
    relocate("mysql", "me.xemor.superheroes.mysql")
    relocate("com.zaxxer", "me.xemor.superheroes.com.zaxxer")
    relocate("org.apache.commons", "me.xemor.superheroes.org.apache.commons")
    relocate("org.bstats", "me.xemor.superheroes.org.bstats")
    relocate("dev.bassett", "me.xemor.superheroes.dev.bassett")
    relocate("io.sentry", "me.xemor.sentry")
    configurations = listOf(project.configurations.shadow.get())
    val folder = System.getenv("pluginFolder")
    destinationDirectory.set(file(folder))
}

sentry {
    includeSourceContext.set(true)
    org.set("samuel-hollis-139014fe7")
    projectName.set("superheroes")
    authToken.set(System.getenv("SENTRY_AUTH_TOKEN"))
    autoInstallation {
        enabled.set(true)
    }
}

publishing {
    repositories {
        maven {
            name = "xemorReleases"
            url = uri("https://repo.xemor.zip/releases")
            credentials(PasswordCredentials::class)
            authentication {
                isAllowInsecureProtocol = true
                create<BasicAuthentication>("basic")
            }
        }

        maven {
            name = "xemorSnapshots"
            url = uri("https://repo.xemor.zip/snapshots")
            credentials(PasswordCredentials::class)
            authentication {
                isAllowInsecureProtocol = true
                create<BasicAuthentication>("basic")
            }
        }
    }

    publications {
        create<MavenPublication>("maven") {
            groupId = rootProject.group.toString()
            artifactId = rootProject.name
            version = rootProject.version.toString()
            from(project.components["java"])
        }
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}
//End of auto generated

// Handles version variables etc
tasks.processResources {
    inputs.property("version", rootProject.version)
    expand("version" to rootProject.version)
}

publishing {

}
