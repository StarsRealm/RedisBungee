plugins {
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("xyz.jpenilla.run-velocity") version "2.0.0"

}


dependencies {
    api(project(":RedisBungee-API")) {
        // Since velocity already includes guava / configurate exlude them
        exclude("com.google.guava", "guava")
        exclude("com.google.code.gson", "gson")
        exclude("org.spongepowered", "configurate-yaml")
        // exclude also adventure api
        exclude("net.kyori", "adventure-api")
        exclude("net.kyori", "adventure-text-serializer-gson")
        exclude("net.kyori", "adventure-text-serializer-legacy")
        exclude("net.kyori", "adventure-text-serializer-plain")
        exclude("net.kyori", "adventure-text-minimessage")
    }
    compileOnly(libs.platform.velocity)
    annotationProcessor(libs.platform.velocity)
    implementation(project(":RedisBungee-Commands"))
    implementation(libs.acf.velocity)

}

description = "RedisBungee Velocity implementation"

java {
    withJavadocJar()
    withSourcesJar()
}

tasks {
    withType<Javadoc> {
        dependsOn(project(":RedisBungee-API").getTasksByName("javadoc", false))
        val options = options as StandardJavadocDocletOptions
        options.use()
        options.isDocFilesSubDirs = true
        options.links(
            "https://jd.papermc.io/velocity/3.0.0/", // velocity api
        )
        val apiDocs = File(rootProject.projectDir, "RedisBungee-API/build/docs/javadoc")
        options.linksOffline("https://ci.limework.net/RedisBungee/RedisBungee-API/build/docs/javadoc", apiDocs.path)
    }
    runVelocity {
        velocityVersion("3.3.0-SNAPSHOT")
        environment["REDISBUNGEE_PROXY_ID"] = "velocity-1"
        environment["REDISBUNGEE_NETWORK_ID"] = "dev"
    }
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }
    shadowJar {
        relocate("redis.clients.jedis", "com.imaginarycode.minecraft.redisbungee.internal.jedis")
        relocate("redis.clients.util", "com.imaginarycode.minecraft.redisbungee.internal.jedisutil")
        relocate("org.apache.commons.pool", "com.imaginarycode.minecraft.redisbungee.internal.commonspool")
        relocate("com.squareup.okhttp", "com.imaginarycode.minecraft.redisbungee.internal.okhttp")
        relocate("okio", "com.imaginarycode.minecraft.redisbungee.internal.okio")
        relocate("org.json", "com.imaginarycode.minecraft.redisbungee.internal.json")
        relocate("com.github.benmanes.caffeine", "com.imaginarycode.minecraft.redisbungee.internal.caffeine")
        // acf shade
        relocate("co.aikar.commands", "com.imaginarycode.minecraft.redisbungee.internal.acf.commands")
    }

}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
