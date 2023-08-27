package com.lib.eyes.consent

import android.app.Activity
import android.util.Log
import androidx.annotation.IntRange
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentForm
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import java.util.concurrent.atomic.AtomicBoolean

object ConsentHelper {
    private const val TAG = "ConsentHelper"
    private lateinit var consentInformation: ConsentInformation

    // The UMP SDK consent form.
    private var consentForm: ConsentForm? = null
    private var isMobileAdsInitializeCalled = AtomicBoolean(false)

    val isPrivacyOptionsRequired: Boolean
        get() =
            consentInformation.privacyOptionsRequirementStatus ==
                    ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED

    val canRequestAds: Boolean
        get() = consentInformation.canRequestAds()

    fun revokeConsent(activity: Activity, callback: (errorMessage: String?) -> Unit) {
        if (isPrivacyOptionsRequired) {
            UserMessagingPlatform.showPrivacyOptionsForm(activity) { formError ->
                callback.invoke(formError?.message)
            }
        }

    }

    fun showConsent(activity: Activity, configBuilder: Config.() -> Unit) {
        val config = Config().apply(configBuilder)

        val params = if (config.isDebug) {
            val debugSettings = ConsentDebugSettings.Builder(activity)
                .apply {
                    when (config.debugGeography) {
                        1 -> ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_DISABLED.let {
                            setDebugGeography(
                                it
                            )
                        }

                        2 -> ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA.let {
                            setDebugGeography(
                                it
                            )
                        }

                        3 -> ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_NOT_EEA.let {
                            setDebugGeography(
                                it
                            )
                        }
                    }

                    config.testDeviceHashId?.let {
                        addTestDeviceHashedId(it)
                    }
                }
                .build()

            ConsentRequestParameters
                .Builder()
                .setConsentDebugSettings(debugSettings)
        } else {
            ConsentRequestParameters
                .Builder()
                .setTagForUnderAgeOfConsent(true)
        }.build()

        consentInformation = UserMessagingPlatform.getConsentInformation(activity)
        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                Log.w(TAG, "onCreate: ")
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                    activity
                ) { loadAndShowError ->
                    // Consent gathering failed.
                    Log.w(
                        TAG, String.format(
                            "%s: %s",
                            loadAndShowError?.errorCode,
                            loadAndShowError?.message
                        )
                    )

                    if (consentInformation.canRequestAds()) {
                        initializeMobileAdsSdk(config.consentCallback)
                    } else config.consentCallback(ConsentResult.CAN_NOT_REQUEST_ADS)
                }
            },
            { requestConsentError ->
                // Consent gathering failed.
                Log.w(
                    TAG, String.format(
                        "%s: %s",
                        requestConsentError.errorCode,
                        requestConsentError.message
                    )
                )

                config.consentCallback(ConsentResult.CONSENT_FAILED)
            })

        if (consentInformation.canRequestAds()) {
            initializeMobileAdsSdk(config.consentCallback)
        }
    }

    private fun initializeMobileAdsSdk(callback: (consentResult: ConsentResult) -> Unit) {
        if (isMobileAdsInitializeCalled.get()) {
            return
        }
        isMobileAdsInitializeCalled.set(true)

        callback.invoke(ConsentResult.CAN_REQUEST_ADS)
    }

    // Ext
    fun requestAdsIfCan(block: () -> Unit) {
        if(canRequestAds) {
            block.invoke()
        }
    }

    class Config internal constructor(
    ) {
        var isDebug: Boolean = false
        var testDeviceHashId: String? = null

        /**
         * Only working in debug mode
         *
         * `0 for do nothing`
         *
         * `1 for ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_DISABLED`
         *
         * `2 for ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA`
         *
         * `3 for ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_NOT_EEA`
         */
        @IntRange(from = 0, to = 3)
        var debugGeography: Int = 0

        var consentCallback: (consentResult: ConsentResult) -> Unit = {}
    }
    enum class ConsentResult {
        CAN_REQUEST_ADS,
        CAN_NOT_REQUEST_ADS,
        CONSENT_FAILED
    }
}
