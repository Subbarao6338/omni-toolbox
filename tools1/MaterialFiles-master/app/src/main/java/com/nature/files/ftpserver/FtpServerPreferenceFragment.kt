/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.nature.files.ftpserver

import android.os.Bundle
import com.nature.files.R
import com.nature.files.ui.PreferenceFragmentCompat

class FtpServerPreferenceFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferencesFix(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.ftp_server)
    }
}
