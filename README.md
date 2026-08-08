# Wedding Bingo 💍🎉

A tiny, single-player Android bingo game for surviving — and celebrating — the wonderfully predictable bits of a wedding.

## How to play

- Launch the app to get a newly shuffled 5×5 card.
- Tap a square whenever you spot that wedding cliché; tap it again to undo it.
- The middle square is free, because love is priceless and software is not.
- Complete any row, column, or diagonal to get a short confetti celebration.
- After the celebration, tap **Play Again** for a fresh randomized card.

Everything is intentionally family-friendly. The jokes target wedding traditions, speeches, photos, dancing, tiny formalwear, and other harmless chaos — not the people getting married.

## Replay variety

The app contains 193 distinct randomized wedding prompts and draws 24 per card. Assuming independent shuffles, the expected fraction of prompts seen after five full cards is:

`1 - ((193 - 24) / 193)^5 ≈ 48.5%`

That makes roughly five games the halfway point for seeing the available material.

## Building

GitHub Actions is the supported build environment for this project. The workflow intentionally pins the Android/Java/Gradle toolchain so contributors do not need a local Android setup.

1. Push a commit to `main`, open/update a pull request, or run **Build Android APK** manually from the Actions tab.
2. The workflow runs JVM unit tests and compiles `app-debug.apk`.
3. Download the `wedding-bingo-debug-apk` artifact from the completed workflow run.

The CI toolchain uses Android Gradle Plugin 9.3.0, Gradle 9.5.0, JDK 17, compile/target SDK 37, and Android Build Tools 36.0.0.
