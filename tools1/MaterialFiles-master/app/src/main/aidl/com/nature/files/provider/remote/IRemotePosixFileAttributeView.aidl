package com.nature.files.provider.remote;

import com.nature.files.provider.common.ParcelableFileTime;
import com.nature.files.provider.common.ParcelablePosixFileMode;
import com.nature.files.provider.common.PosixGroup;
import com.nature.files.provider.common.PosixUser;
import com.nature.files.provider.remote.ParcelableException;
import com.nature.files.provider.remote.ParcelableObject;

interface IRemotePosixFileAttributeView {
    ParcelableObject readAttributes(out ParcelableException exception);

    void setTimes(
        in ParcelableFileTime lastModifiedTime,
        in ParcelableFileTime lastAccessTime,
        in ParcelableFileTime createTime,
        out ParcelableException exception
    );

    void setOwner(in PosixUser owner, out ParcelableException exception);

    void setGroup(in PosixGroup group, out ParcelableException exception);

    void setMode(in ParcelablePosixFileMode mode, out ParcelableException exception);

    void setSeLinuxContext(in ParcelableObject context, out ParcelableException exception);

    void restoreSeLinuxContext(out ParcelableException exception);
}
