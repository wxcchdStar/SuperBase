package co.tton.android.lib.imagepicker.model;

import java.util.ArrayList;
import java.util.List;

import co.tton.android.lib.imagepicker.utils.FileUtils;

public class ImageFolder {
    public static final String ID_ALL_FOLDER = "#all#";

    public static final String PATH_TAKE_PHOTO = "#take_photo#";

    public String mId;
    public String mCoverPath;
    public String mName;
    public List<String> mPhotoList = new ArrayList<>();

    public void addPhoto(String path) {
        if (PATH_TAKE_PHOTO.equals(path) || FileUtils.isExist(path)) {
            mPhotoList.add(path);
        }
    }

    public boolean isAllFolder() {
        return ID_ALL_FOLDER.equals(mId);
    }
}
