plugins {
    id("com.refinedmods.refinedarchitect.fabric")
}

repositories {
    maven {
        name = "Refined Storage"
        url = uri("https://maven.creeperhost.net")
        content {
            includeGroup("com.refinedmods.refinedstorage")
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
        url = uri("https://cursemaven.com")
        content {
            includeGroup("curse.maven")
        }
    }
    maven {
        url = uri("https://api.modrinth.com/maven")
    }
}

val modVersion: String by project

refinedarchitect {
    modId = "cabletiers"
    version = modVersion
    fabric()
}

base {
    archivesName.set("cabletiers-fabric")
}

val minecraftVersion: String by project
val refinedstorageVersion: String by project
val jadeVersionFabric: String by project

val commonJava by configurations.existing
val commonResources by configurations.existing

dependencies {
    compileOnly(project(":common"))
    commonJava(project(path = ":common", configuration = "commonJava"))
    commonResources(project(path = ":common", configuration = "commonResources"))
    api("com.refinedmods.refinedstorage:refinedstorage-fabric:${refinedstorageVersion}")
    runtimeOnly("maven.modrinth:jade:${jadeVersionFabric}+fabric");
}

tasks.withType(ProcessResources::class.java) {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    from(commonResources) {
        filesMatching("assets/cabletiers/blockstates/*.json") {
            filter { line ->
                line.replace("\"type\"", "\"fabric:type\"")
            }
        }
    }
}
