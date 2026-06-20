package com.nature.files.provider.remote;

import com.nature.files.provider.remote.ParcelableException;

interface IRemoteFileSystem {
    void close(out ParcelableException exception);
}
