package tgo1014.instabox.common.managers

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.google.ads.consent.*
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import tgo1014.instabox.BuildConfig
import tgo1014.instabox.R
import timber.log.Timber
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AdManagerImpl(val activity: Activity) : AdManager {

    private var rewardedAd: RewardedAd? = null

    override fun preloadRewardAd() {
        when (verifyConsent()) {
            ConsentStatus.NON_PERSONALIZED -> preloadRewardAd(activity, false)
            ConsentStatus.PERSONALIZED -> preloadRewardAd(activity, true)
            else -> showConsentDialog { success ->
                if (success) {
                    preloadRewardAd()
                }
            }
        }
    }

    private fun preloadRewardAd(context: Context, personalized: Boolean) {
        val extras = Bundle()
        if (!personalized) {
            extras.putString("npa", "1")
        }
        val request = AdRequest.Builder()
            .addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
            .build()
        rewardedAd = RewardedAd(context, context.getString(R.string.ad_unit_id))
        rewardedAd?.loadAd(request, null)
    }

    override fun showRewardAd(onReward: () -> Unit, onFail: () -> Unit) {
        if (rewardedAd?.isLoaded == false) {
            onReward()
            return
        }
        rewardedAd?.show(activity, object : RewardedAdCallback() {
            override fun onUserEarnedReward(reward: RewardItem) {
                onReward()
            }

            override fun onRewardedAdFailedToShow(errorCode: Int) {
                super.onRewardedAdFailedToShow(errorCode)
                Timber.d("Failed to load ad: $errorCode")
                onFail()
            }
        })
    }

    override suspend fun isEu(): Boolean {
        suspendCoroutine<Unit> {
            updateConsent(
                onSuccess = { it.resume(Unit) },
                onFail = { it.resume(Unit) }
            )
        }
        return ConsentInformation.getInstance(activity).isRequestLocationInEeaOrUnknown
    }

    private fun updateConsent(onFail: (() -> Unit)? = null, onSuccess: () -> Unit) {
        ConsentInformation.getInstance(activity)
            .requestConsentInfoUpdate(
                arrayOf(activity.getString(R.string.publisher_id)),
                object : ConsentInfoUpdateListener {
                    override fun onFailedToUpdateConsentInfo(reason: String?) {
                        Timber.d("onFailedToUpdateConsentInfo: $reason")
                        onFail?.invoke()
                    }

                    override fun onConsentInfoUpdated(consentStatus: ConsentStatus?) {
                        onSuccess()
                    }
                })
    }

    private fun verifyConsent(): ConsentStatus? {
        return ConsentInformation.getInstance(activity).consentStatus
    }


    private fun showConsentDialog(onComplete: ((success: Boolean) -> Unit)?) {
        var consentForm: ConsentForm? = null

        if (BuildConfig.DEBUG) {
            with(ConsentInformation.getInstance(activity)) {
                addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                debugGeography = DebugGeography.DEBUG_GEOGRAPHY_EEA
            }
        }

        updateConsent {

            // If the user in out of EU, don't show the dialog and show personalized ads
            if (!ConsentInformation.getInstance(activity).isRequestLocationInEeaOrUnknown) {
                ConsentInformation.getInstance(activity).consentStatus = ConsentStatus.PERSONALIZED
            }

            try {
                consentForm =
                    ConsentForm.Builder(activity, URL(activity.getString(R.string.privacy_url)))
                        .withListener(object : ConsentFormListener() {
                            override fun onConsentFormOpened() {
                                super.onConsentFormOpened()
                                Timber.d("Requesting Consent: onConsentFormOpened")
                            }

                            override fun onConsentFormLoaded() {
                                super.onConsentFormLoaded()
                                Timber.d("Requesting Consent: onConsentFormLoaded")
                                if (!activity.isFinishing) {
                                    consentForm?.show()
                                }
                            }

                            override fun onConsentFormError(reason: String?) {
                                super.onConsentFormError(reason)
                                ConsentInformation.getInstance(activity).consentStatus =
                                    ConsentStatus.PERSONALIZED
                                Timber.d("Requesting Consent: onConsentFormError. $reason")
                                onComplete?.invoke(false)
                            }

                            override fun onConsentFormClosed(
                                consentStatus: ConsentStatus?,
                                userPrefersAdFree: Boolean?
                            ) {
                                super.onConsentFormClosed(consentStatus, userPrefersAdFree)
                                Timber.d("Requesting Consent: onConsentFormClosed. Consent Status = $consentStatus")
                                ConsentInformation.getInstance(activity).consentStatus =
                                    consentStatus
                                        ?: ConsentStatus.UNKNOWN
                                onComplete?.invoke(true)
                            }
                        })
                        .withPersonalizedAdsOption()
                        .withNonPersonalizedAdsOption()
                        .build()

                consentForm?.load()
            } catch (e: Exception) {
                Timber.d(e)
                onComplete?.invoke(false)
            }

        }
    }
}