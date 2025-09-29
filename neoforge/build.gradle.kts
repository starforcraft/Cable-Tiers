import net.neoforged.moddevgradle.dsl.NeoForgeExtension as NeoForge

plugins {
    id("refinedarchitect.neoforge")
}

repositories {
    maven {
        url = uri("https://maven.pkg.github.com/refinedmods/refinedstorage2")
        credentials {
            username = "anything"
            password = "\u0067hp_oGjcDFCn8jeTzIj4Ke9pLoEVtpnZMP4VQgaX"
        }
    }
    maven {
        name = "JEI"
        url = uri("https://maven.blamejared.com/")
    }
}

val modVersion: String by project

refinedarchitect {
    modId = "cabletiers"
    version = modVersion
    neoForge()
    dataGeneration(project(":common"))

    project.afterEvaluate {
        project.extensions.getByType<NeoForge>().runs.named("data") {
            programArguments.addAll(
                "--existing-mod", "refinedstorage"
            )
        }
    }
}

base {
    archivesName.set("cabletiers-neoforge")
}

val minecraftVersion: String by project
val refinedstorageVersion: String by project

val commonJava by configurations.existing
val commonResources by configurations.existing

dependencies {
    compileOnly(project(":common"))
    commonJava(project(path = ":common", configuration = "commonJava"))
    commonResources(project(path = ":common", configuration = "commonResources"))
    api("com.refinedmods.refinedstorage:refinedstorage-neoforge:${refinedstorageVersion}")
}
