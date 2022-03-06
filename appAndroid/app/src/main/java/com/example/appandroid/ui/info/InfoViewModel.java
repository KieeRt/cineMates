package com.example.appandroid.ui.info;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class InfoViewModel extends ViewModel {

	private MutableLiveData<String> mText;

	public InfoViewModel() {
		mText = new MutableLiveData<>();
		mText.setValue("Questa applicazione è stata realizzata a cura di:\n"+ "Dmytro Lozyak\n" + "Carmine Testa");
	}

	public LiveData<String> getText() {
		return mText;
	}
}