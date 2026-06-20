package com.nature.files.app

import android.os.Bundle
import android.os.Environment
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.nature.files.R
import com.nature.files.databinding.HomeFragmentBinding
import com.nature.files.file.MimeType
import com.nature.files.filelist.FileListActivity
import com.nature.files.ftpserver.FtpServerActivity
import com.nature.files.util.createIntent
import com.nature.files.util.startActivitySafe
import java8.nio.file.Paths
import java.io.File

class HomeFragment : Fragment() {
    private lateinit var binding: HomeFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setTitle(R.string.app_name)
        binding.toolFtp.setOnClickListener {
            startActivitySafe(FtpServerActivity::class.createIntent())
        }

        setupStorageStats()
        setupCategories()
    }

    private fun setupStorageStats() {
        val externalStorage = Environment.getExternalStorageDirectory()
        val totalSpace = externalStorage.totalSpace
        val freeSpace = externalStorage.freeSpace
        val usedSpace = totalSpace - freeSpace

        val totalSpaceString = Formatter.formatFileSize(requireContext(), totalSpace)
        val freeSpaceString = Formatter.formatFileSize(requireContext(), freeSpace)

        binding.storageStatsText.text = getString(
            R.string.navigation_storage_subtitle_format,
            freeSpaceString,
            totalSpaceString
        )

        if (totalSpace > 0) {
            val progress = (usedSpace.toDouble() / totalSpace * 100).toInt()
            binding.storageProgress.progress = progress
        }
    }

    private fun setupCategories() {
        binding.categoryImages.setOnClickListener {
            openCategory(MimeType.IMAGE_ANY)
        }
        binding.categoryVideos.setOnClickListener {
            openCategory("video/*".asMimeType())
        }
        binding.categoryAudio.setOnClickListener {
            openCategory("audio/*".asMimeType())
        }
        binding.categoryDocs.setOnClickListener {
            openCategory(MimeType.PDF)
        }
        binding.categoryArchives.setOnClickListener {
            openCategory("application/zip".asMimeType())
        }
        binding.categoryApks.setOnClickListener {
            openCategory(MimeType.APK)
        }
    }

    private fun openCategory(mimeType: MimeType) {
        val path = Paths.get(Environment.getExternalStorageDirectory().absolutePath)
        val intent = FileListActivity.createViewIntent(path)
            .setType(mimeType.value)
        startActivitySafe(intent)
    }

    private fun String.asMimeType() = MimeType(this)
}
