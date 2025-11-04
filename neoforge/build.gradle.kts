import net.neoforged.moddevgradle.dsl.NeoForgeExtension as NeoForge

plugins {
    id("com.refinedmods.refinedarchitect.neoforge")
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
        name = "Modmaven"
        url = uri("https://modmaven.dev/")
        content {
            includeGroup("mekanism")
            includeGroup("dev.technici4n")
        }
    }
    maven {
        url = uri("https://cursemaven.com")
        content {
            includeGroup("curse.maven")
        }
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
val refinedstorageMekanismIntegrationVersion: String by project
val mekanismVersion: String by project
val refinedTypesVersion: String by project
val grandPowerVersion: String by project
val industrialForegoingSoulsVersion: String by project
val jadeVersion: String by project

val commonJava by configurations.existing
val commonResources by configurations.existing

dependencies {
    compileOnly(project(":common"))
    commonJava(project(path = ":common", configuration = "commonJava"))
    commonResources(project(path = ":common", configuration = "commonResources"))
    api("com.refinedmods.refinedstorage:refinedstorage-neoforge:${refinedstorageVersion}")

    compileOnly("com.refinedmods.refinedstorage:refinedstorage-mekanism-integration:${refinedstorageMekanismIntegrationVersion}")  {
        isTransitive = false
    }
    compileOnly("mekanism:Mekanism:${mekanismVersion}")
    compileOnly("curse.maven:refined-types-1327983:${refinedTypesVersion}")
    compileOnly("dev.technici4n:GrandPower:${grandPowerVersion}")
    compileOnly("curse.maven:industrial-foregoing-souls-904394:${industrialForegoingSoulsVersion}")
    implementation("curse.maven:jade-324717:${jadeVersion}")
}

