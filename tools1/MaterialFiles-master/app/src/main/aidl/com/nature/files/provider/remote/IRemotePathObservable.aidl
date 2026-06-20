package com.nature.files.provider.remote;

import com.nature.files.provider.remote.ParcelableException;
import com.nature.files.util.RemoteCallback;

interface IRemotePathObservable {
    void addObserver(in RemoteCallback observer);

    void close(out ParcelableException exception);
}
