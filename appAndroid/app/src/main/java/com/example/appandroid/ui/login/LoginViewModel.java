package com.example.appandroid.ui.login;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

import com.example.appandroid.listViewClass.utente.Utente;
import com.example.appandroid.repository.RepositoryFactory;
import com.example.appandroid.repository.RepositoryService;

import org.json.JSONException;

import java.util.concurrent.TimeoutException;

public class LoginViewModel extends AndroidViewModel {

	private RepositoryService repository ;

	public LoginViewModel(Application application){
		super(application);
	}

	public void init(){
		repository= RepositoryFactory.getRepositoryConcrete();
	}

	public void configureNewUtente(String username, String email) throws TimeoutException, JSONException {
		repository.initUtenteLocaleSenzaApi(email,username);
		repository.insertUtente(new Utente(username, email));
	}

	public boolean usernameAlreadyExists(String username) throws TimeoutException, JSONException {
		return repository.isUsernameExist(username);
	}

	public Utente getUtenteFromBD(String email) throws TimeoutException, JSONException {
		return  repository.getUtenteSenzaImmagine(email);
	}

	public boolean checkMovieKey() throws TimeoutException, JSONException {
		return repository.checkKey();
	}

	public Utente getUser(){
		return repository.getUser();
	}

	public boolean effettuaLoginStandard(String email, String password) throws TimeoutException, JSONException {
		boolean response = repository.effettuaLogin(email,password);
		if(response){
			repository.initUtenteLocaleConApiGateway(email);
		}
		return response;
	}

	public void initUtenteLocale(String email) throws TimeoutException, JSONException {
		repository.initUtenteLocaleConApiGateway(email);
	}

	public boolean effettuaRichiestaSendCodiceResetPassword(String email){
		return repository.resetPasswordPrimoStep(email);
	}

	public int effettuaRichiestaChangePasswordResetPassword(String codice,String newPassword){
		return repository.resetPasswordSecondoStep(codice,newPassword);
	}

	public void logout(){
		repository.logout(getApplication().getApplicationContext());
	}


}
