package com.example.util

/**
 * Centralized AdMob Ad Unit IDs configuration.
 *
 * Current Phase: Development & Testing.
 * Uses official Google Test Ad Unit IDs to prevent invalid traffic penalties.
 *
 * Swap these with your production AdMob IDs prior to publishing to Google Play Store.
 */
object AdConstants {
    // Official Google AdMob Test App ID
    const val APP_ID = "ca-app-pub-3940256099942544~3347511713"

    // Official Google AdMob Test Banner Ad ID
    const val BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"

    // Official Google AdMob Test Native Advanced Ad ID
    const val NATIVE_AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110"

    // Official Google AdMob Test Rewarded Video Ad ID
    const val REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"
}
