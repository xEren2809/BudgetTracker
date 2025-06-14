package de.hawhamburg.budgettracker.ui.planer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PlanerViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public PlanerViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is planer fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}