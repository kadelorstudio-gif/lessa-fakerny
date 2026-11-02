package app.lessafakerny.android.notifications

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.ui.graphics.vector.ImageVector
import app.lessafakerny.android.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class RingtoneOption(
    val key: String,
    val nameAr: String,
    val nameEn: String,
    val descriptionAr: String,
    val descriptionEn: String,
    val rawResId: Int?, // null for SILENT
    val icon: ImageVector
)

object RingtoneCatalog {
    val options = listOf(
        RingtoneOption(
            key = "DEFAULT",
            nameAr = "الافتراضية",
            nameEn = "Default Chime",
            descriptionAr = "نغمة متوازنة وواضحة لجميع التنبيهات اليومية",
            descriptionEn = "Balanced dual chime for everyday academic reminders",
            rawResId = R.raw.tone_default,
            icon = Icons.Default.NotificationsActive
        ),
        RingtoneOption(
            key = "CALM",
            nameAr = "الهادئة",
            nameEn = "Calm Melodic",
            descriptionAr = "لحن ثلاثي دافئ ومريح للأعصاب",
            descriptionEn = "Warm ambient marimba chord, soothing and peaceful",
            rawResId = R.raw.tone_calm,
            icon = Icons.Default.SelfImprovement
        ),
        RingtoneOption(
            key = "SHORT",
            nameAr = "التنبيه القصير",
            nameEn = "Short Alert",
            descriptionAr = "تنبيه إلكتروني فوري وموجز لا يشتت الانتباه",
            descriptionEn = "Crisp, concise electronic pulse for instant awareness",
            rawResId = R.raw.tone_short,
            icon = Icons.Default.ElectricBolt
        ),
        RingtoneOption(
            key = "DIGITAL",
            nameAr = "الرقمية",
            nameEn = "Futuristic Digital",
            descriptionAr = "ألحان رقمية مستقبلية متناسقة مع هوية النيون",
            descriptionEn = "Futuristic sci-fi arpeggio blips in sync with neon UI",
            rawResId = R.raw.tone_digital,
            icon = Icons.Default.GraphicEq
        ),
        RingtoneOption(
            key = "CLASSIC",
            nameAr = "الكلاسيكية",
            nameEn = "Classic Bell",
            descriptionAr = "رنين أجراس كلاسيكية عذبة ومألوفة",
            descriptionEn = "Timeless resonant bells reminiscent of campus clocks",
            rawResId = R.raw.tone_classic,
            icon = Icons.Default.MusicNote
        ),
        RingtoneOption(
            key = "ACADEMIC",
            nameAr = "الأكاديمية",
            nameEn = "Academic Fanfare",
            descriptionAr = "نغمات تفوق وتخرج تحفزك لحضور المحاضرة",
            descriptionEn = "Uplifting graduation motif inspiring study focus",
            rawResId = R.raw.tone_academic,
            icon = Icons.Default.School
        ),
        RingtoneOption(
            key = "CHIME",
            nameAr = "الرنين الواضح",
            nameEn = "Clear Crystal Chime",
            descriptionAr = "رنين كريستالي حاد ومسموع حتى في الأماكن المزدحمة",
            descriptionEn = "High-clarity crystal ping audible in noisy halls",
            rawResId = R.raw.tone_clear_chime,
            icon = Icons.Default.VolumeUp
        ),
        RingtoneOption(
            key = "GENTLE",
            nameAr = "التنبيه اللطيف",
            nameEn = "Gentle Pluck",
            descriptionAr = "عزف ناعم وهادئ يناسب قاعات المذاكرة والمكتبة",
            descriptionEn = "Soft acoustic pluck designed for study rooms & libraries",
            rawResId = R.raw.tone_gentle,
            icon = Icons.Default.AutoAwesome
        ),
        RingtoneOption(
            key = "DISTINCTIVE",
            nameAr = "النغمة المميزة",
            nameEn = "Signature «لسه فاكرني؟»",
            descriptionAr = "لحن التطبيق الحصري والفريد المقتبس من الهوية البصرية",
            descriptionEn = "Exclusive signature app theme crafted for the brand",
            rawResId = R.raw.tone_distinctive,
            icon = Icons.Default.WorkspacePremium
        ),
        RingtoneOption(
            key = "SILENT",
            nameAr = "صامت",
            nameEn = "Silent (Visual Only)",
            descriptionAr = "إشعار مرئي على الشاشة فقط دون إطلاق أي صوت",
            descriptionEn = "Visual notification popup and banner without playing audio",
            rawResId = null,
            icon = Icons.Default.VolumeMute
        )
    )

    fun getOptionByKey(key: String): RingtoneOption {
        return options.find { it.key.equals(key, ignoreCase = true) } ?: options[0]
    }

    fun getSoundUri(context: Context, key: String): Uri? {
        val option = getOptionByKey(key)
        val resId = option.rawResId ?: return null
        return Uri.parse("android.resource://${context.packageName}/$resId")
    }
}

/**
 * Singleton player to guarantee that only one preview tone plays at a time
 * and previous playback is automatically stopped.
 */
object RingtonePreviewPlayer {
    private const val TAG = "RingtonePreviewPlayer"
    private var mediaPlayer: MediaPlayer? = null

    private val _currentlyPlayingKey = MutableStateFlow<String?>(null)
    val currentlyPlayingKey: StateFlow<String?> = _currentlyPlayingKey.asStateFlow()

    fun play(context: Context, key: String) {
        stop()

        if (key.equals("SILENT", ignoreCase = true)) {
            _currentlyPlayingKey.value = null
            return
        }

        val soundUri = RingtoneCatalog.getSoundUri(context, key)
        if (soundUri == null) {
            _currentlyPlayingKey.value = null
            return
        }

        try {
            val mp = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                        .build()
                )
                setDataSource(context, soundUri)
                setOnCompletionListener {
                    stop()
                }
                setOnErrorListener { _, what, extra ->
                    Log.e(TAG, "MediaPlayer error: what=$what, extra=$extra")
                    stop()
                    true
                }
                prepare()
                start()
            }
            mediaPlayer = mp
            _currentlyPlayingKey.value = key
        } catch (e: Exception) {
            Log.e(TAG, "Failed to play preview tone $key", e)
            stop()
        }
    }

    fun stop() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.reset()
                it.release()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing MediaPlayer", e)
        } finally {
            mediaPlayer = null
            _currentlyPlayingKey.value = null
        }
    }
}
