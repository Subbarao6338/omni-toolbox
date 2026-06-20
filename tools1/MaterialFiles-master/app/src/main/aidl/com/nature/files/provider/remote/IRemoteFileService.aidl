package com.nature.files.provider.remote;

import com.nature.files.provider.remote.IRemoteFileSystem;
import com.nature.files.provider.remote.IRemoteFileSystemProvider;
import com.nature.files.provider.remote.IRemotePosixFileAttributeView;
import com.nature.files.provider.remote.IRemotePosixFileStore;
import com.nature.files.provider.remote.ParcelableObject;

interface IRemoteFileService {
    IRemoteFileSystemProvider getRemoteFileSystemProviderInterface(String scheme);

    IRemoteFileSystem getRemoteFileSystemInterface(in ParcelableObject fileSystem);

    IRemotePosixFileStore getRemotePosixFileStoreInterface(in ParcelableObject fileStore);

    IRemotePosixFileAttributeView getRemotePosixFileAttributeViewInterface(
        in ParcelableObject attributeView
    );
}
