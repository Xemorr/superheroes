group = "me.xemor"
version = "8.2.2"
description = "superheroes"
java.sourceCompatibility = JavaVersion.VERSION_17

plugins {
    java
    `maven-publish`
    id("com.gradleup.shadow") version("8.3.6")
    id("io.sentry.jvm.gradle") version("3.12.0")
}

repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
    maven { url = uri("https://papermc.io/repo/repository/maven-public/")}
    maven { url = uri("https://repo.codemc.org/repository/maven-public") }
    maven { url = uri("https://repo.minebench.de/") }
    maven { url = uri("https://repo.maven.apache.org/maven2/") }
    maven { url = uri("https://maven.enginehub.org/repo/")}
    maven { url = uri("https://mvn-repo.arim.space/lesser-gpl3")}
    maven { url = uri("https://repo.xemor.zip/releases")}
    maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots/") {
        name = "sonatype-oss-snapshots"
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21.5-R0.1-SNAPSHOT")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.9")
    compileOnly("me.xemor:skillslibrary:4.1.1")
    compileOnly("com.fasterxml.jackson.core:jackson-core:2.18.0")
    compileOnly("com.fasterxml.jackson.core:jackson-databind:2.18.2")
    compileOnly("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.7.0")
    shadow("me.xemor:configurationdata:4.3.9")
    shadow("org.bstats:bstats-bukkit:1.7")
    shadow("me.xemor:userinterfaces:2.0.2-SNAPSHOT")
    shadow("org.jetbrains:annotations:20.1.0")
    shadow("net.kyori:adventure-platform-bukkit:4.3.3-SNAPSHOT")
    shadow("net.kyori:adventure-text-minimessage:4.17.0")
    shadow("mysql:mysql-connector-java:8.0.29")
    shadow("me.sepdron:HeadCreator:2.0.0")
    shadow("com.zaxxer:HikariCP:4.0.3")
    shadow("org.apache.commons:commons-lang3:3.12.0")
    shadow("io.sentry:sentry:6.29.0")
    shadow("space.arim.morepaperlib:morepaperlib:0.4.3")
    shadow("me.xemor:foliahacks:1.7.4")
    shadow("io.papermc:paperlib:1.0.7")
}

java {
    configurations.shadow.get().dependencies.remove(dependencies.gradleApi())
}

tasks.shadowJar {
    minimize()
    relocate("net.kyori", "me.xemor.superheroes.kyori")
    relocate("me.xemor.configurationdata", "me.xemor.superheroes.configurationdata")
    relocate("org.jetbrains", "me.xemor.superheroes.org.jetbrains")
    relocate("mysql", "me.xemor.superheroes.mysql")
    relocate("com.zaxxer", "me.xemor.superheroes.com.zaxxer")
    relocate("me.sepdron", "me.xemor.superheroes.me.sepdron")
    relocate("org.apache.commons", "me.xemor.superheroes.org.apache.commons")
    relocate("org.bstats", "me.xemor.superheroes.org.bstats")
    relocate("io.sentry", "me.xemor.sentry")
    relocate("space.arim.morepaperlib", "me.xemor.superheroes.morepaperlib")
    relocate("me.xemor.foliahacks", "me.xemor.superheroes.foliahacks")
    relocate("io.papermc.paperlib", "me.xemor.superheroes.paperlib")
    configurations = listOf(project.configurations.shadow.get())
    val folder = System.getenv("pluginFolder")
    destinationDirectory.set(file(folder))
}

sentry {
    val token = System.getenv("SENTRY_AUTH_TOKEN")
    if (token != null) {
        includeSourceContext.set(true)
        org.set("samuel-hollis-139014fe7")
        projectName.set("superheroes")
        authToken.set(token)
        autoInstallation {
            enabled.set(true)
        }
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
