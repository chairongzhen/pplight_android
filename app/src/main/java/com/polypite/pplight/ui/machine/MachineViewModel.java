package com.polypite.pplight.ui.machine;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MachineViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public MachineViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is machine page");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
