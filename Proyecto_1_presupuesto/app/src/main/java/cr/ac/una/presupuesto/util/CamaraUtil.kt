package cr.ac.una.presupuesto.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File


fun crearUriImage(
    context: Context
): Uri {

    var archivo = File.createTempFile(
        "movimiento",
        ".jpg",
        context.externalCacheDir
    )
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        archivo
    )
}