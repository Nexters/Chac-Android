import com.chakchak.convention.extensions.setNamespace

plugins {
    alias(libs.plugins.chakchak.android.library)
    alias(libs.plugins.chakchak.android.room)
}

android {
    setNamespace("data.photo")
}
