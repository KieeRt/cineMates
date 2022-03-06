package com.example.appandroid.ui.login;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class LoginViewMovelFactory implements ViewModelProvider.Factory {
	Application application;

	public LoginViewMovelFactory(Application application) {
		this.application = application;
	}

	@NonNull
	@Override
	public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
		return (T) new LoginViewModel(application);
	}
}
