package app.lessafakerny.android.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object AppLinks {
    const val OFFICIAL_WEBSITE_URL = "https://kadelorstudio-gif.github.io/lessa-fakerny/"
    const val PRIVACY_POLICY_URL = "https://kadelorstudio-gif.github.io/lessa-fakerny/privacy-policy.html"
    const val TERMS_CONDITIONS_URL = "https://kadelorstudio-gif.github.io/lessa-fakerny/terms.html"
    const val DEVELOPER_EMAIL = "Kadelor-studio@gmail.com"

    fun openUrl(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, url, Toast.LENGTH_LONG).show()
        }
    }

    fun openWebsite(context: Context) = openUrl(context, OFFICIAL_WEBSITE_URL)
    fun openPrivacyPolicy(context: Context) = openUrl(context, PRIVACY_POLICY_URL)
    fun openTerms(context: Context) = openUrl(context, TERMS_CONDITIONS_URL)

    fun sendEmail(context: Context, subject: String = "Lessa Fakerny Inquiry") {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$DEVELOPER_EMAIL")
                putExtra(Intent.EXTRA_SUBJECT, subject)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, DEVELOPER_EMAIL, Toast.LENGTH_LONG).show()
        }
    }
}
