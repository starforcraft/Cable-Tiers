plugins {
    id("refinedarchitect.common")
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
    version = modVersion
    common()
    publishing {
        maven = true
    }
}

base {
    archivesName.set("cabletiers-common")
}

val minecraftVersion: String by project
val refinedstorageVersion: String by project

dependencies {
    api("com.refinedmods.refinedstorage:refinedstorage-common:${refinedstorageVersion}")
}
