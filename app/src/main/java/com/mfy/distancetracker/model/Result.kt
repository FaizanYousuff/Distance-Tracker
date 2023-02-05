package com.mfy.distancetracker.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize // this required so that this can be passed as safeargs during navigation and hence this will be visible as arguments
data class Result (
    val distance : String,
    val time : String
) : Parcelable