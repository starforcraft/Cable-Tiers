plugins {
    id("com.refinedmods.refinedarchitect.common")
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
        url = uri("https://cursemaven.com")
        content {
            includeGroup("curse.maven")
        }
    }
}

val modVersion: String by project

refinedarchitect {
    version = modVersion
    common()
    testing()
    publishing {
        maven = true
    }
}

base {
    archivesName.set("cabletiers-common")
}

val minecraftVersion: String by project
val refinedstorageVersion: String by project
val jadeVersion: String by project

dependencies {
    api("com.refinedmods.refinedstorage:refinedstorage-common:${refinedstorageVersion}")

    testImplementation(libs.junit.api)
    testImplementation(libs.junit.params)
    testImplementation(libs.assertj)
    testRuntimeOnly(libs.junit.engine)
}
