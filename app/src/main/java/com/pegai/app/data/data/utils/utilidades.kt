package com.pegai.app.data.data.utils

<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/data/data/utils/utilidades.kt
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

fun formatarTempo(timestamp: Timestamp?): String {
    if (timestamp == null) return ""

    val diff = System.currentTimeMillis() - timestamp.toDate().time
=======
import java.util.concurrent.TimeUnit

fun formatarTempo(timestamp: com.google.firebase.Timestamp?): String {
    if (timestamp == null) return ""

    val diff = System.currentTimeMillis() - timestamp.toDate().time

>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/data/data/utils/utilidades.kt
    val dias = TimeUnit.MILLISECONDS.toDays(diff)

    return when {
        dias < 1 -> "Hoje"
        dias < 7 -> "$dias dias atrás"
        dias < 30 -> "${dias / 7} semanas atrás"
        dias < 365 -> "${dias / 30} meses atrás"
        else -> "${dias / 365} anos atrás"
    }
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/data/data/utils/utilidades.kt
}
=======
}

>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/data/data/utils/utilidades.kt
