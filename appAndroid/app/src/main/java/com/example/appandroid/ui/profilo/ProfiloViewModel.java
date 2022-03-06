package com.example.appandroid.ui.profilo;

import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.appandroid.repository.RepositoryFactory;
import com.example.appandroid.repository.RepositoryService;

public class ProfiloViewModel extends AndroidViewModel {


	private MutableLiveData<String> nomeUtente;
	private MutableLiveData<String> emailUtente;
	private MutableLiveData<Bitmap> immagineProfilo;
	private MutableLiveData<Boolean> inCaricamento;

	private RepositoryService repository;


	public ProfiloViewModel(@NonNull Application application) {
		super(application);

	}

	public void init(){
		if(nomeUtente!=null && emailUtente!=null && immagineProfilo!=null)
			return;

		repository= RepositoryFactory.getRepositoryConcrete();

		nomeUtente=new MutableLiveData<>(repository.getUser().getUsername());
		emailUtente=new MutableLiveData<>(repository.getUser().getEmail());
		inCaricamento=new MutableLiveData<>(false);
		immagineProfilo=new MutableLiveData<>();

	}

	public void recuperaBitmapImmagineUtente(){
		inCaricamento.postValue(true);
		Bitmap bitmap = repository.getBitmapImmagineUtente(getApplication().getApplicationContext());
		immagineProfilo.postValue(bitmap);
		inCaricamento.postValue(false);
	}

	public MutableLiveData<String> getNomeUtente() {
		if(nomeUtente==null)
			return new MutableLiveData<>();

		return nomeUtente;
	}

	public MutableLiveData<Boolean> isInCaricamento() {
		if(inCaricamento==null)
			return new MutableLiveData<>();

		return inCaricamento;
	}

	public MutableLiveData<String> getEmailUtente() {
		if(emailUtente==null)
			return new MutableLiveData<>();

		return emailUtente;
	}

	public MutableLiveData<Bitmap> getImmagineProfiloUtente() {
		if(immagineProfilo==null)
			return new MutableLiveData<>();

		return immagineProfilo;
	}


	public void aggiornaImmagineUtente(Intent data){
		inCaricamento.postValue(true);
		Uri imageUri = data.getData();
		Bitmap bitmap = repository.uplodadBitmapImmagineUtente(getApplication().getApplicationContext(),imageUri);
		immagineProfilo.postValue(bitmap);
		inCaricamento.postValue(false);

	}






}