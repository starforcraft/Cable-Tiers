plugins {
    id("refinedarchitect.fabric")
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
        name = "ModMenu"
        url = uri("https://maven.terraformersmc.com/")
    }
    maven {
        name = "Cloth Config"
        url = uri("https://maven.shedaniel.me/")
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
    fabric()
    publishing {
        maven = true
    }
}

base {
    archivesName.set("cabletiers-fabric")
}

val refinedstorageVersion: String by project
val minecraftVersion: String by project

val commonJava by configurations.existing
val commonResources by configurations.existing

dependencies {
    compileOnly(project(":common"))
    commonJava(project(path = ":common", configuration = "commonJava"))
    commonResources(project(path = ":common", configuration = "commonResources"))
    modApi("com.refinedmods.refinedstorage:refinedstorage-fabric:${refinedstorageVersion}")
}
