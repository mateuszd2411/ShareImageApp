package com.example.shareimageapp.PhotoEditor.Interface;

public interface EditImageFragmentListener {
    void onBrightnessChanged(int brightness);
    void onSaturationChanged(int saturation);
    void onContrastChanged(int contrast);
    void onEditStarted();
    void onEditCompleted();
}
