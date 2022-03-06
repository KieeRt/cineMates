package com.example.appandroid.ui.notifiche;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.appandroid.listViewClass.notifica.Notifica;
import com.example.appandroid.repository.RepositoryFactory;
import com.example.appandroid.repository.RepositoryService;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class NotificheViewModel extends ViewModel {
	private MutableLiveData<List<Notifica>> notificheUtente;
	private RepositoryService repository;


	public void init(){
		if(notificheUtente!=null)
			return;

		notificheUtente=new MutableLiveData<>(new ArrayList<>());
		repository= RepositoryFactory.getRepositoryConcrete();
	}

	public void recuperaNotificheUtente() throws TimeoutException, JSONException {
		List<Notifica> notificheLocali = notificheUtente.getValue();
		notificheLocali.clear();
		notificheLocali.addAll(repository.getNotifiche(repository.getUser().getEmail()));
		notificheUtente.postValue(notificheLocali);
	}


	public MutableLiveData<List<Notifica>> getNotificheUtente() {
		if(notificheUtente==null)
			return new MutableLiveData<>(new ArrayList<>());

		return notificheUtente;
	}
}
