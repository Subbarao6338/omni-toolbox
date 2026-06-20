package com.nature.files.utils

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap

/**
 * Provides path-based mutual exclusion for file operations.
 * This prevents race conditions when multiple operations (like move or delete)
 * touch the same file or directory concurrently.
 * Uses Mutex for non-blocking coroutine safety.
 */
object FileOperationLock {
    private val locks = ConcurrentHashMap<String, Mutex>()

    /**
     * Executes the given [action] while holding a lock for the specified [path].
     */
    suspend fun <T> withLock(path: String, action: suspend () -> T): T {
        val mutex = locks.computeIfAbsent(path) { Mutex() }
        return mutex.withLock {
            try {
                action()
            } finally {
                // To avoid memory leak, we could remove the mutex if no one else is waiting.
                // However, 'on the fly' removal is risky with computeIfAbsent.
                // For this implementation, we rely on the fact that file paths are finite
                // and Mutex objects are lightweight.
                // A more advanced version would use a ReferenceQueue or periodic cleanup.
                if (!mutex.isLocked) {
                    // locks.remove(path) // Risky race condition
                }
            }
        }
    }

    /**
     * Executes the given [action] while holding locks for all specified [paths].
     * Sorts paths to prevent deadlocks.
     */
    suspend fun <T> withLocks(paths: List<String>, action: suspend () -> T): T {
        val sortedPaths = paths.distinct().sorted()
        return withLocksRecursive(sortedPaths, 0, action)
    }

    private suspend fun <T> withLocksRecursive(paths: List<String>, index: Int, action: suspend () -> T): T {
        if (index >= paths.size) {
            return action()
        }
        val mutex = locks.computeIfAbsent(paths[index]) { Mutex() }
        return mutex.withLock {
            withLocksRecursive(paths, index + 1, action)
        }
    }
}
