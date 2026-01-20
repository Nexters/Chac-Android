import com.chac.convention.extensions.setNamespace

plugins {
    alias(libs.plugins.chac.android.library)
    alias(libs.plugins.chac.android.room)
    alias(libs.plugins.chac.android.hilt)
}

android {
    setNamespace("data.album")
}

dependencies {
    implementation(projects.domain.album)
    implementation(libs.commons.math3)
}
