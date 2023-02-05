package com.mfy.distancetracker.util

object Constants {
    const val PERMISSION_LOCATION_REQUEST_CODE = 1
    const val BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE = 2

    const val ACTION_START_SERVICE = "ACTION START SERVICE"
    const val ACTION_STOP_SERVICE =  "ACTION_STOP_SERVICE"
    const val ACTION_NAVIGATE_TO_MAPS_FRAGMENT = "ACTION_NAVIGATE_TO_MAPS_FRAGMENT"

    const val NOTIFICATION_CHANNEL_ID = "tracker_notification_id"
    const val NOTIFICATION_CHANNEL_NAME = "tracker_notification"
    const val NOTIFICATION_ID = 3
    const val PENDING_INTENT_REQUEST_CODE = 99

    const val LOCATION_UPDATE_INTERVAL = 4000L // 4 seconds
    const val LOCATION_FASTEST_UPDATE_INTERVAL = 2000L // 2 Second


}