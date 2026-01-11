import com.chakchak.convention.extensions.setNamespace

plugins {
    alias(libs.plugins.chakchak.android.library)
    alias(libs.plugins.chakchak.android.compose)
}

android {
    setNamespace("core.designsystem")
}
