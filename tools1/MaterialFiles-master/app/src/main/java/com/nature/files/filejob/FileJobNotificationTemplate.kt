/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.nature.files.filejob

import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.nature.files.R
import com.nature.files.util.NotificationChannelTemplate
import com.nature.files.util.NotificationTemplate

val fileJobNotificationTemplate =
    NotificationTemplate(
        NotificationChannelTemplate(
            "file_job",
            R.string.notification_channel_file_job_name,
            NotificationManagerCompat.IMPORTANCE_LOW,
            descriptionRes = R.string.notification_channel_file_job_description,
            showBadge = false
        ),
        colorRes = R.color.color_primary,
        smallIcon = R.drawable.notification_icon,
        ongoing = true,
        onlyAlertOnce = true,
        category = NotificationCompat.CATEGORY_PROGRESS,
        priority = NotificationCompat.PRIORITY_LOW
    )
