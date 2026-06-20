/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.nature.files.filelist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.commit
import com.nature.files.app.AppActivity
import com.nature.files.util.args
import com.nature.files.util.putArgs

class OpenFileAsDialogActivity : AppActivity() {
    private val args by args<OpenFileAsDialogFragment.Args>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Calls ensureSubDecor().
        findViewById<View>(android.R.id.content)
        if (savedInstanceState == null) {
            val fragment = OpenFileAsDialogFragment().putArgs(args)
            supportFragmentManager.commit {
                add(fragment, OpenFileAsDialogFragment::class.java.name)
            }
        }
    }
}
