/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package com.nature.files.provider.common

import java.io.Closeable

interface CloseableIterator<T> : Iterator<T>, Closeable
