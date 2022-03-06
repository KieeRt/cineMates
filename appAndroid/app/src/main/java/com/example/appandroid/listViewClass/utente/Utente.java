package com.example.appandroid.listViewClass.utente;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.amplifyframework.core.Amplify;
import com.example.appandroid.notifiche.NotificheService;
import com.example.appandroid.repository.Repository;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.Serializable;
import java.util.Objects;

public class Utente implements Serializable {
	private String username;
	private String email ;
	private Bitmap bitmapImmagine;
	private static Utente utente;
	private int CURRENT_STATE;
	public static final int UTENTE_AMICO = 0 ;
	public static final int UTENTE_NONAMICO = 1 ;
	public static final int UTENTE_RICHIEDE_AMICIZIA = 2 ;
	public static final int UTENTE_AMICIZIA_INVIATA = 3 ;


	// TODO: creare un Utente di tipo altri utenti ?

	public Utente(String username,  String email){
		this(username,email,1);
	}

	public Utente(String username, String email, int CURRENT_STATE){
		this.username = username;
		this.email = email;
		this.CURRENT_STATE = CURRENT_STATE;
	}

	public static void initUtente(String username, String email){
		if(utente == null){
			Log.v("initUtente", "Inizializzo nuovo utente locale");
			utente = new Utente(username, email);
		}
	}


	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Bitmap getBitmapImmagine() {
		return bitmapImmagine;
	}


	public int getCURRENT_STATE() {
		return CURRENT_STATE;
	}
	public void setCURRENT_STATE(int state) {
		 this.CURRENT_STATE = state;
	}



	public static Utente getUtente() {
		return utente;
	}

	public static void stampaUtenteLocale(){
		stampaUtente(Utente.getUtente());
	}
	public static void stampaUtente(Utente utente){
		if(utente!=null){

			System.out.println("Username: " + utente.getUsername());
			System.out.println("Email: " + utente.getEmail());

		}
		else{
			System.out.println("UTENTE = NULL");
		}
	}

	@Override
	public boolean equals(@Nullable @org.jetbrains.annotations.Nullable Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj.getClass() != this.getClass()) {
			return false;
		}

		final Utente other = (Utente) obj;

		return Objects.equals(this.username, other.username);
	}

	@Override
	public int hashCode() {
		return Objects.hash(username, email);
	}

	/**UTENTE**/

	public static void logout(Context context){
		NotificheService.cancellaNotifiche(context);

		if( Profile.getCurrentProfile() != null){
			LoginManager.getInstance().logOut();
			utente = null;
			Log.v("logout", "Logout con Facebook effettuato, Profile: " + Profile.getCurrentProfile());
		}

		if(	GoogleSignIn.getLastSignedInAccount(context) != null){
			GoogleSignInOptions googleSignInOption = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
					.requestEmail()
					.build();
			GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(context, googleSignInOption);

			googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
				@Override
				public void onComplete(@NonNull Task<Void> task) {
					utente = null;
					Log.v("logout", "Logout con Google effettuato, Profile: " + GoogleSignIn.getLastSignedInAccount(context));
				}
			});
		}

		if( Amplify.Auth.getCurrentUser() != null){

			Amplify.Auth.signOut(
				() -> {
					Log.v("logout", "Logout con Cognito effettuato, Profile: ");
					utente = null;
					},
				error -> Log.e("logout", error.toString())
			);
		}

	}


	public void setBitmapImmagine(Bitmap bitmapImmagine) {
		this.bitmapImmagine = bitmapImmagine;
	}
}
