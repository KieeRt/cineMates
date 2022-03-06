package com.example.appandroid.ui.registrazione;

import androidx.lifecycle.ViewModel;

import com.example.appandroid.listViewClass.utente.Utente;
import com.example.appandroid.repository.RepositoryFactory;
import com.example.appandroid.repository.RepositoryService;

import org.json.JSONException;

import java.util.concurrent.TimeoutException;

public class RegistrazioneViewModel extends ViewModel {
	private RepositoryService repository;


	public void init(){
		repository= RepositoryFactory.getRepositoryConcrete();

	}

	public void insertUtente(Utente utente) throws TimeoutException, JSONException {
		repository.insertUtente(utente);
	}

	public boolean usernameAlreadyExists(String username) throws TimeoutException, JSONException {
		return repository.isUsernameExist(username);
	}

	public Utente getUtenteFromBD(String email) throws TimeoutException, JSONException {
		return  repository.getUtenteSenzaImmagine(email);
	}

	public boolean effettuaRegistrazione(String email,String username, String password) {
		return repository.effettuaRegistrazioneCognito(email, username, password);
	}

	public boolean confermaCodiceRegistrazione(String email,String username, String codice) throws TimeoutException, JSONException {
		boolean esito = repository.confermaCodiceRegistrazione(email,codice);
		if(esito){
			insertUtente(new Utente(username,email));
		}
	return esito;
	}
}
