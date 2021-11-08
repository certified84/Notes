package com.certified.notes.util

import androidx.fragment.app.Fragment
import com.shashank.sony.fancytoastlib.FancyToast

object Extensions {
    fun Fragment.showToast(message: String) =
        FancyToast.makeText(
            requireContext(),
            message,
            FancyToast.LENGTH_LONG
        ).show()
}