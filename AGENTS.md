# Repository Guidelines

## Project Structure & Module Organization
This is a multi-module Android project organized by layer:
- `app/`: application entry point, navigation host, app-level wiring.
- `feature/album/`: UI screens and presentation logic (`*Screen`, `*ViewModel`).
- `domain/album/`: business use cases and repository contracts (`*UseCase`, interfaces).
- `data/album/`: repository implementations, workers, clustering and data access.
- `core/designsystem`, `core/permission`, `core/resources`: shared UI, permission, and resource modules.
- `build-logic/`: custom Gradle convention plugins used across modules.

Place production code in `src/main`. Add tests under `src/test` (unit) and `src/androidTest` (instrumented) inside each module.

## Build, Test, and Development Commands
- `./gradlew :app:assembleDebug`: build a debug APK.
- `./gradlew :app:installDebug`: install debug build on a connected device/emulator.
- `./gradlew test`: run JVM unit tests for all modules.
- `./gradlew connectedAndroidTest`: run instrumented tests on a connected device.
- `./gradlew ktlintCheck`: run Kotlin style checks.
- `./gradlew ktlintFormat`: auto-format Kotlin sources.
- `./gradlew :core:designsystem:generateDesignSystemTokens -PdesignTokensFile=/abs/path/design_tokens.json`: sync and generate design token code.

## Coding Style & Naming Conventions
Follow `.editorconfig`:
- 4-space indentation, LF line endings, UTF-8, max line length 140.
- ktlint is applied to all subprojects; use it as the source of truth for formatting.

Naming patterns used in the repo:
- Types/files: `GalleryScreen`, `GalleryViewModel`, `SaveAlbumUseCase`, `MediaRepositoryImpl`.
- Packages: `com.chac.<layer>.<feature>...`.
- Compose function naming is flexible (ktlint function naming rule is intentionally relaxed).

## Testing Guidelines
Use JUnit4 for unit tests and `AndroidJUnitRunner` for instrumentation tests.
- Unit tests: `Module/src/test/kotlin/...`, file names like `ClassNameTest.kt`.
- UI/integration tests: `Module/src/androidTest/kotlin/...`.
- Prefer test names in `action_expectedResult` form.

Before opening a PR, run `./gradlew test ktlintCheck` and `connectedAndroidTest` when UI/device behavior changed.

## Commit & Pull Request Guidelines
Recent history follows Conventional Commit style with optional issue tags, e.g. `[#73] fix: ...`, `refactor: ...`, `chore: ...`.
- Keep commits focused and descriptive.
- Use types like `feat`, `fix`, `refactor`, `chore`.

PRs should follow `.github/pull_request_template.md`:
- 목적 요약 (goal summary)
- 주요 변경사항 (bullet list)
- 추가 참고/검토 포인트

Include linked issue numbers and screenshots/videos for UI changes.

## Security & Configuration Tips
Release signing uses environment variables (`CHAC_KEYSTORE_PATH`, `CHAC_KEYSTORE_PASSWORD`, `CHAC_KEY_ALIAS`, `CHAC_KEY_PASSWORD`).
Never commit secrets or keystore credentials.
