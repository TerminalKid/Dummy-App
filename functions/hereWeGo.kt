package functions

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

inline fun <T> sdk29AndUp(onSdk29:() -> T )

  = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        onSdk29()

    else
        null