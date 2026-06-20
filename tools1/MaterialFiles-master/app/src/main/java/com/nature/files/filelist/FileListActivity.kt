/*
 * Copyright (c) 2018 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.nature.files.filelist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.KeyEvent
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java8.nio.file.Path
import java8.nio.file.Paths
import com.nature.files.R
import com.nature.files.app.AppActivity
import com.nature.files.app.HomeFragment
import com.nature.files.databinding.FileListActivityBinding
import com.nature.files.file.MimeType
import com.nature.files.util.createIntent
import com.nature.files.util.extraPath
import com.nature.files.util.putArgs

class FileListActivity : AppActivity() {
    private lateinit var binding: FileListActivityBinding
    private lateinit var adapter: TabAdapter

    private val tabs = mutableListOf<TabItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FileListActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tabs.add(TabItem.Home)
        if (intent.action != Intent.ACTION_MAIN) {
            tabs.add(TabItem.Files(intent))
        }

        adapter = TabAdapter(this)
        binding.viewPager.adapter = adapter
        binding.viewPager.isUserInputEnabled = false // Disable swiping between tabs

        val mediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            // Custom view will be set below
        }
        mediator.attach()

        // Set custom views for tabs
        for (i in 0 until binding.tabLayout.tabCount) {
            val tab = binding.tabLayout.getTabAt(i)
            tab?.let { setupCustomTabView(it, i) }
        }

        binding.addTab.setOnClickListener {
            addNewTab(FileListActivity.createViewIntent(Paths.get(Environment.getExternalStorageDirectory().absolutePath)))
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    binding.viewPager.currentItem = 0
                    true
                }
                R.id.navigation_files -> {
                    if (tabs.size == 1) {
                        addNewTab(FileListActivity.createViewIntent(Paths.get(Environment.getExternalStorageDirectory().absolutePath)))
                    } else {
                        if (binding.viewPager.currentItem == 0) {
                            binding.viewPager.currentItem = 1
                        }
                    }
                    true
                }
                else -> false
            }
        }

        if (tabs.size > 1) {
            binding.viewPager.setCurrentItem(tabs.size - 1, false)
            binding.bottomNavigation.selectedItemId = R.id.navigation_files
        }
    }

    private fun setupCustomTabView(tab: TabLayout.Tab, position: Int) {
        val view = layoutInflater.inflate(R.layout.tab_item, null)
        val titleText = view.findViewById<TextView>(R.id.tab_title)
        val closeButton = view.findViewById<ImageButton>(R.id.tab_close)

        val item = tabs[position]
        titleText.text = when (item) {
            is TabItem.Home -> getString(R.string.home)
            is TabItem.Files -> item.intent.extraPath?.fileName?.toString() ?: getString(R.string.file_list_title)
        }

        if (item is TabItem.Files) {
            closeButton.visibility = View.VISIBLE
            closeButton.setOnClickListener {
                removeTab(position)
            }
        } else {
            closeButton.visibility = View.GONE
        }

        tab.customView = view
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.action == Intent.ACTION_VIEW || intent.action == Intent.ACTION_OPEN_DOCUMENT || intent.action == Intent.ACTION_OPEN_DOCUMENT_TREE) {
            addNewTab(intent)
        }
    }

    private fun addNewTab(intent: Intent) {
        tabs.add(TabItem.Files(intent))
        adapter.notifyItemInserted(tabs.size - 1)

        // TabLayoutMediator doesn't automatically add custom view for new tabs after attach
        val newTabIndex = tabs.size - 1
        // We need to wait a bit or use a post to ensure TabLayout has the new tab
        binding.tabLayout.post {
            val tab = binding.tabLayout.getTabAt(newTabIndex)
            tab?.let { setupCustomTabView(it, newTabIndex) }
            binding.viewPager.setCurrentItem(newTabIndex, true)
            binding.bottomNavigation.selectedItemId = R.id.navigation_files
        }
    }

    private fun removeTab(position: Int) {
        if (position <= 0 || position >= tabs.size) return

        val currentItem = binding.viewPager.currentItem
        tabs.removeAt(position)
        adapter.notifyItemRemoved(position)

        // Re-setup custom views for all tabs after removal to update positions and close button listeners
        binding.tabLayout.post {
            for (i in 0 until binding.tabLayout.tabCount) {
                val tab = binding.tabLayout.getTabAt(i)
                tab?.let { setupCustomTabView(it, i) }
            }
            if (tabs.size == 1) {
                binding.bottomNavigation.selectedItemId = R.id.navigation_home
            }
        }
    }

    private inner class TabAdapter(activity: AppActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = tabs.size
        override fun createFragment(position: Int): Fragment {
            return when (val item = tabs[position]) {
                is TabItem.Home -> HomeFragment()
                is TabItem.Files -> FileListFragment().putArgs(FileListFragment.Args(item.intent))
            }
        }

        override fun getItemId(position: Int): Long = tabs[position].hashCode().toLong()
        override fun containsItem(itemId: Long): Boolean = tabs.any { it.hashCode().toLong() == itemId }
    }

    sealed class TabItem {
        object Home : TabItem()
        data class Files(val intent: Intent) : TabItem()
    }

    override fun onKeyShortcut(keyCode: Int, event: KeyEvent): Boolean {
        val fragment = supportFragmentManager.findFragmentByTag("f${binding.viewPager.currentItem}")
        if (fragment is FileListFragment && fragment.onKeyShortcut(keyCode, event)) {
            return true
        }
        return super.onKeyUp(keyCode, event)
    }

    companion object {
        fun createViewIntent(path: Path): Intent =
            FileListActivity::class.createIntent()
                .setAction(Intent.ACTION_VIEW)
                .apply { extraPath = path }
    }

    class OpenFileContract : ActivityResultContract<List<MimeType>, Path?>() {
        override fun createIntent(context: Context, input: List<MimeType>): Intent =
            FileListActivity::class.createIntent()
                .setAction(Intent.ACTION_OPEN_DOCUMENT)
                .setType(MimeType.ANY.value)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .putExtra(Intent.EXTRA_MIME_TYPES, input.map { it.value }.toTypedArray())

        override fun parseResult(resultCode: Int, intent: Intent?): Path? =
            if (resultCode == RESULT_OK) intent?.extraPath else null
    }

    class CreateFileContract : ActivityResultContract<Triple<MimeType, String?, Path?>, Path?>() {
        override fun createIntent(
            context: Context,
            input: Triple<MimeType, String?, Path?>
        ): Intent =
            FileListActivity::class.createIntent()
                .setAction(Intent.ACTION_CREATE_DOCUMENT)
                .setType(input.first.value)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .apply {
                    input.second?.let { putExtra(Intent.EXTRA_TITLE, it) }
                    input.third?.let { extraPath = it }
                }

        override fun parseResult(resultCode: Int, intent: Intent?): Path? =
            if (resultCode == RESULT_OK) intent?.extraPath else null
    }

    class OpenDirectoryContract : ActivityResultContract<Path?, Path?>() {
        override fun createIntent(context: Context, input: Path?): Intent =
            FileListActivity::class.createIntent()
                .setAction(Intent.ACTION_OPEN_DOCUMENT_TREE)
                .apply { input?.let { extraPath = it } }

        override fun parseResult(resultCode: Int, intent: Intent?): Path? =
            if (resultCode == RESULT_OK) intent?.extraPath else null
    }
}
