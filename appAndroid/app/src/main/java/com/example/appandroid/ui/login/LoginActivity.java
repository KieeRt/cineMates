package com.example.appandroid.ui.login;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.ViewModelProvider;

import com.amplifyframework.auth.AuthUser;
import com.amplifyframework.core.Amplify;
import com.example.appandroid.R;
import com.example.appandroid.globalUtils.AlertDialogUtils;
import com.example.appandroid.globalUtils.UtilsToast;
import com.example.appandroid.listViewClass.utente.Utente;
import com.example.appandroid.ui.main.MainActivity;
import com.example.appandroid.ui.registrazione.RegistrazioneActivity;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.concurrent.TimeoutException;


public class LoginActivity extends AppCompatActivity {

	// TODO: gestire prova login in assenza di connessione internet
	private static final String TAG = "handlerSingInGoogle";
	// per le notifiche
	public static final String CHANNEL_ID = "NOTIFICHE_CHANNEL";
	public static final int CHANNEL_ID_int = 1122;
	public static NotificationCompat.Builder builder;

	private static final int RC_SIGN_IN_GOOGLE = 1;
	private Button pulsanteLogin;
	private final Context context = this ;
	private TextInputEditText password;
	private TextInputEditText username;
	private TextView linkPasswordDimenticata;
	private TextView linkRegistrati;
	private LoginViewModel loginViewModel;

	//Facebook
	private CallbackManager callbackManager;


	// Google
	private SignInButton signInWithGoogleButton;
	private GoogleSignInClient googleSignInClient;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		loginViewModel = new ViewModelProvider(this,new LoginViewMovelFactory(this.getApplication())).get(LoginViewModel.class);
		checkHashCodeFacebook();
		initViewID();
		initViewListner();
		loginViewModel.init();

		Amplify.Auth.fetchAuthSession(
				result -> Log.i("fetchAuthSession", result.toString()),
				error -> Log.e("fetchAuthSession", error.toString())
		);

		System.out.println("-------------------------------------------------------------");


		// Registrazione e attivazione app di Facebook, tracciamento dei dati
		AppEventsLogger.activateApp(getApplication());

		Profile.fetchProfileForCurrentAccessToken();

		if(utenteHaGiaLoggato()){
			String email = recuperaEmailUtenteLoggato();
			try {
				if(isFirstLogin(email)){
					effettuaPrimaRegistrazioneConProvider(email);
				}
				else{
					loginViewModel.initUtenteLocale(email);
					UtilsToast.stampaToast(this,"Accesso completato !",Toast.LENGTH_LONG);
					apriSchermataMain();
				}
			} catch (TimeoutException | JSONException e) {
				e.printStackTrace();
				UtilsToast.stampaToast(this,e.getMessage(),Toast.LENGTH_SHORT);
			}
		}

	}

	/**
	 * Controlla hashCode di Facebook, id identificativo dell'app che il dispositivo attualmente utilizza.
	 */
	private void checkHashCodeFacebook(){
		try {
			PackageInfo info = getPackageManager().getPackageInfo(
					"com.example.appandroid",
					PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				String hash = Base64.encodeToString(md.digest(), Base64.DEFAULT);
				Log.d("KeyHash:", hash);
			}
		} catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
				e.printStackTrace();
		}
	}


	public void effettuaPrimaRegistrazioneConProvider(String email){
		AlertDialogUtils alertDialogUtils = new AlertDialogUtils(this,R.layout.tendina_scegli_username);
		View v = alertDialogUtils.getLayout();
		TextInputEditText usernameText = v.findViewById(R.id.editTextUsername);
		TextView testoAlert = v.findViewById(R.id.alertUsernameSbagliato);

		alertDialogUtils.initAlertButtonAction(R.id.buttonAnnullaTendinaInserisciUsername, v1 -> {
			//loginViewModel.logout(getApplicationContext());
			loginViewModel.logout();
			alertDialogUtils.chiudiAlert();

		});

		alertDialogUtils.initAlertButtonAction(R.id.buttonConfermaTendinaInserisciUsername, v12 -> {
			try {
				String username = usernameText.getText().toString();
				boolean esiste = loginViewModel.usernameAlreadyExists(username);
				if(!username.equals("") && !esiste) {
					loginViewModel.configureNewUtente(username,email);
					UtilsToast.stampaToast(context,"Registrazione effettuata con successo",Toast.LENGTH_SHORT);
					apriSchermataMain();
					alertDialogUtils.chiudiAlert();
				}
				else if(esiste){
					UtilsToast.stampaToast(context,"Username già esistente",Toast.LENGTH_SHORT);
				}
				else if(username.equals("")){
					UtilsToast.stampaToast(context,"Campo username vuoto",Toast.LENGTH_SHORT);
				}
			} catch (JSONException | TimeoutException e) {
				e.printStackTrace();
				loginViewModel.logout();
				//loginViewModel.logout();
				UtilsToast.stampaToast(context,e.getMessage(),Toast.LENGTH_SHORT);
				alertDialogUtils.chiudiAlert();
			}
		});
		alertDialogUtils.mostraAlertDialog();
	}

	public boolean isFirstLogin(String email) throws TimeoutException, JSONException {
		if(loginViewModel.getUtenteFromBD(email)!=null){
			System.out.println("PRIMO LOGIN : FALSE");
			return false;
		}
		else{
			System.out.println("PRIMO LOGIN : TRUE");
			return true;
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		// updateUI(account);
        /*
        If GoogleSignIn.getLastSignedInAccount returns a GoogleSignInAccount object (rather than null), the user has already signed in to your app with Google. Update your UI accordingly—that is, hide the sign-in button, launch your main activity, or whatever is appropriate for your app.
        If GoogleSignIn.getLastSignedInAccount returns null, the user has not yet signed in to your app with Google. Update your UI to display the Google Sign-in button.
        */
	}

	@Override
	protected void onResume(){
		super.onResume();
		Log.d("onResume", "Login Risveglio");

	}

	@Override
	protected void onPause() {
		super.onPause();
		System.out.println("Login Messo in pausa");
	}

	public void initViewListner() {
		initButtonAction();
		initLinkAction();
		configureSignInGoogleButton();
		initLoginConFacebook();
	}

	public void initButtonAction() {
		pulsanteLogin.setOnClickListener(this::loginPremuto);

	}

	public void initLinkAction() {
		linkPasswordDimenticata.setOnClickListener(this::linkPasswordDimenticataPremuto);
		linkRegistrati.setOnClickListener(this::linkRegistratiPremuto);
	}

	public void loginPremuto(View view) {
		view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.click_on));

		new Thread(()->{
			boolean esito = false;
			try {
				esito = loginViewModel.effettuaLoginStandard(username.getText().toString(),password.getText().toString());
				if(esito){
					runOnUiThread(()->{UtilsToast.stampaToast(this,"Login effettuato con successo",Toast.LENGTH_LONG);});
					apriSchermataMain();
				}
				else {
					loginViewModel.logout();
					runOnUiThread(()->{UtilsToast.stampaToast(this,"Email o Password non validi",Toast.LENGTH_LONG);});
				}
			} catch (TimeoutException | JSONException e) {
				e.printStackTrace();
				loginViewModel.logout();
				runOnUiThread(()->{UtilsToast.stampaToast(this,e.getMessage(),Toast.LENGTH_LONG);});
			}

		}).start();

		//effettuaLoginStandard(username.getText().toString(),password.getText().toString());
	}

	public void linkPasswordDimenticataPremuto(View v) {
		mostraTendinaRecuperoPass();
	}

	public void linkRegistratiPremuto(View v) {
		apriSchermataRegistrazione();
	}

	public void initViewID() {
		pulsanteLogin = findViewById(R.id.loginButton);
		password = findViewById(R.id.editTextPasswordLoginScreen);
		username = findViewById(R.id.editTextUsernameLoginScreen);
		linkPasswordDimenticata = findViewById(R.id.linkPasswordDimenticataLoginScreen);
		linkRegistrati = findViewById(R.id.linkRegistrazioneLoginScreen);
		signInWithGoogleButton = findViewById(R.id.buttonLoginWithGG);
	}

	public void configureSignInGoogleButton() {
		// Configure sign-in to request the user's ID, email address, and basic
		// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
		GoogleSignInOptions googleSignInOption = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestEmail()
				.build();

		googleSignInClient = GoogleSignIn.getClient(this, googleSignInOption);

		signInWithGoogleButton = findViewById(R.id.buttonLoginWithGG);

		signInWithGoogleButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent signInIntent = googleSignInClient.getSignInIntent();
				startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE);
			}
		});
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		System.out.println("Sono nell' on activity RESULT");

		// Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
		if (requestCode == RC_SIGN_IN_GOOGLE) {
			// The Task returned from this call is always completed, no need to attach
			// a listener.
			System.out.println("Sono nell' IF di on activity RESULT PER GOOGLE");
			Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

			handleSignInResult(task);

		}

		// FACEBOOK:
		else {
			System.out.println("Sono nell' on activity RESULT PER FACEBOOK");
			boolean risultatoGestioneFacebook = callbackManager.onActivityResult(requestCode, resultCode, data);
			System.out.println("Gestito dal menager di Facebook = " + risultatoGestioneFacebook);
		}


	}


	private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
		try {
			GoogleSignInAccount accountGoogle = completedTask.getResult(ApiException.class);

			if (accountGoogle != null){
				String email = recuperaEmailUtenteLoggatoConGoogle();
				System.out.println("EMAIL RECUPERATA DA GOOGLE "+email);

				try {
					if (isFirstLogin(email)) {
						runOnUiThread(()->effettuaPrimaRegistrazioneConProvider(email));
					} else {
						loginViewModel.initUtenteLocale(email);
						UtilsToast.stampaToast(this,"Accesso con Google in corso...",Toast.LENGTH_LONG);

						apriSchermataMain();
					}
				} catch (TimeoutException | JSONException e) {
					loginViewModel.logout();
					e.printStackTrace();
					UtilsToast.stampaToast(this,e.getMessage(),Toast.LENGTH_SHORT);
				}
			}
			else{
				UtilsToast.stampaToast(this,"Nessun utente google rilevato",Toast.LENGTH_SHORT);
			}
			// Signed in successfully, show authenticated UI.
			// updateUI(account);
		} catch (ApiException e) {
			// The ApiException status code indicates the detailed failure reason.
			// Please refer to the GoogleSignInStatusCodes class reference for more information.
			Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
			UtilsToast.stampaToast(this,"Errore login",Toast.LENGTH_SHORT);
			// updateUI(null);
		}
	}



	public void initLoginConFacebook() {
		callbackManager = CallbackManager.Factory.create();

		LoginButton signInWithFacebookButton = (LoginButton) findViewById(R.id.buttonLoginWithFB);

		// Permesso per accedere alla email dell'account
		signInWithFacebookButton.setReadPermissions("email");

		// Callback registration
		signInWithFacebookButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
			@Override
			public void onSuccess(LoginResult loginResult) {
				Thread thread = new Thread(()-> {
					final AccessToken accessToken = loginResult.getAccessToken();
					GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
						@Override
						public void onCompleted(JSONObject user, GraphResponse graphResponse) {
							if (graphResponse.getError() != null) {
								Profile.fetchProfileForCurrentAccessToken();
								AccessToken accessToken = AccessToken.getCurrentAccessToken();
								System.out.println("ACCESS TOKEN: " + accessToken.getToken());
								Log.e("ERRORE GRAPH", "Erorre nell 'on completed di accesso con fb");
								Log.e("ERRORE GRAPH", graphResponse.getRawResponse());

							} else {
								Profile.fetchProfileForCurrentAccessToken();
								AccessToken accessToken = AccessToken.getCurrentAccessToken();
								System.out.println("ACCESS TOKEN: " + accessToken.getToken());

								String email = user.optString("email");
								System.out.println("EMAIL PRESA DA ON COMPLETED" + email);

								try {
									if (isFirstLogin(email)) {
										runOnUiThread(()->effettuaPrimaRegistrazioneConProvider(email));
									} else {
										runOnUiThread(()->UtilsToast.stampaToast(context,"Accesso con Facebook riuscito...",Toast.LENGTH_LONG));

										loginViewModel.initUtenteLocale(email);
										apriSchermataMain();
									}
								} catch (TimeoutException | JSONException e) {
									loginViewModel.logout();
									e.printStackTrace();
									runOnUiThread(()->UtilsToast.stampaToast(context,e.getMessage(),Toast.LENGTH_LONG));
								}
							}
						}
					});
					Bundle parameters = new Bundle();
					parameters.putString("fields", "id,name,email");
					request.setParameters(parameters);
					request.executeAndWait();
				});

				thread.start();

				try{
					thread.join();
				}
				catch(InterruptedException i){
					Log.e("JOIN",i.getMessage());
				}

			}



			@Override
			public void onCancel() {
				System.out.println("DENTRO onCancel");
				// App code
			}

			@Override
			public void onError(FacebookException exception) {
				System.out.println("DENTRO OnError");
				runOnUiThread(()->UtilsToast.stampaToast(context,"Errore login Facebook",Toast.LENGTH_LONG));
				// App code
			}
		});
	}

	public boolean verificaAcessoEffettuato() {
		AccessToken accessToken = AccessToken.getCurrentAccessToken();
		return accessToken != null && !accessToken.isExpired();
	}

	public void apriSchermataMain() {
		try {
			if(loginViewModel.checkMovieKey()){
				Intent intent = new Intent(this, MainActivity.class);
				String messaggio = getIntent().getStringExtra("notifica");
				if(messaggio != null && messaggio.equals("new"))
					intent.putExtra("notifica", "new");

				startActivity(intent);

			}
			else {
				UtilsToast.stampaToast(this,"Problema con database film...",Toast.LENGTH_SHORT);
				loginViewModel.logout();
			}
		} catch (TimeoutException | JSONException e) {
			UtilsToast.stampaToast(this,"Problema con internet",Toast.LENGTH_SHORT);
			e.printStackTrace();
		}

	}

	public void apriSchermataRegistrazione() {
		Intent intent = new Intent(this, RegistrazioneActivity.class);
		startActivity(intent);
	}

	public void mostraTendinaRecuperoPass(){
		int buttonOk = R.id.buttonInviaCodiceTendinaRecuperoPassword;
		int buttonAnnulla = R.id.buttonAnnullaTendinaRecuperoPasswordMail;

		AlertDialogUtils costruttoreDialog = new AlertDialogUtils(this, R.layout.tendina_recupero_password);
		// Invia codice di conferma sull'email. N.B username in questo caso e' email


		costruttoreDialog.initAlertButtonAction(buttonOk, v -> {
			costruttoreDialog.mostraAlertDialog();
			View layout = costruttoreDialog.getLayout();
			EditText email = layout.findViewById(R.id.editTextEmailAddressTendinaRecuperoPass);
			String emilInserita = email.getText().toString();


			new Thread(()->{
				boolean esito = loginViewModel.effettuaRichiestaSendCodiceResetPassword(emilInserita);
				System.out.println("ESITO : "+esito);
				if(esito){
					runOnUiThread(this::mostraTendinaRecuperoPasswordCodice);
				}
				else{
					runOnUiThread(()->UtilsToast.stampaToast(context,"Campi mancanti o requisiti non rispettati",Toast.LENGTH_SHORT));
				}
			}).start();

			costruttoreDialog.chiudiAlert();

		});

		costruttoreDialog.initAlertButtonAction(buttonAnnulla, v -> {
			costruttoreDialog.chiudiAlert();
		});

		costruttoreDialog.mostraAlertDialog();
	}

	public void mostraTendinaRecuperoPasswordCodice(){
		AlertDialogUtils costruttoreDialog = new AlertDialogUtils(this, R.layout.tendina_recupero_password_cambiofinale);
		TextInputEditText codiceConferma = costruttoreDialog.getLayout().findViewById(R.id.editTextCodiceConferma);
		TextInputEditText nuovaPassword =  costruttoreDialog.getLayout().findViewById(R.id.editTextNuovaPasswordTendinaCambioPasswordRecupero);


		costruttoreDialog.initAlertButtonAction(R.id.buttonAnnullaTendinaCambioPasswordRecupero, v -> costruttoreDialog.chiudiAlert());

		costruttoreDialog.initAlertButtonAction(R.id.buttonSalvaTendinaCambioPasswordRecupero, v -> {
			System.out.println("SONO IN RESET PASSWORD CONFIRM CLICK ATTIVO");
			//Recuperato il codice e la nuova password.
			// Principale errore e' riservarto alla password non conforme: InvalidPasswordException
			// more info: https://docs.aws.amazon.com/cognito-user-identity-pools/latest/APIReference/API_Operations.html
			String password = nuovaPassword.getText().toString() ;
			String codice = codiceConferma.getText().toString();

			new Thread(()->{
				int esito = loginViewModel.effettuaRichiestaChangePasswordResetPassword(codice,password);
				int OK = 0;
				int CODICE_ERRATO = 1;
				int MANCANTI_NON_RISPETTATI = 2;
				String msg = "";

				if(esito==OK)
					msg="Password cambiata con successo";
				else if(esito==CODICE_ERRATO)
					msg="Codice errato";
				else if(esito==MANCANTI_NON_RISPETTATI)
					msg="Campi mancanti o requisiti non rispettati";

				String finalMsg = msg;
				runOnUiThread(()->{UtilsToast.stampaToast(context, finalMsg,Toast.LENGTH_SHORT);});
			}).start();

			costruttoreDialog.chiudiAlert();

		});

		costruttoreDialog.mostraAlertDialog();
	}

	@Deprecated
	public void effettuaLoginStandard(String username, String password) {
		Amplify.Auth.signIn(username, password,
				result -> {
					if (result.isSignInComplete()) {
						try {
							if (isFirstLogin(username)) {
								runOnUiThread(()->effettuaPrimaRegistrazioneConProvider(username));
							} else {
								loginViewModel.initUtenteLocale(username);
								UtilsToast.stampaToast(this,"Login effettuato con successo",Toast.LENGTH_LONG);
								apriSchermataMain();
							}
						} catch (TimeoutException | JSONException e) {
							loginViewModel.logout();
							UtilsToast.stampaToast(this,e.getMessage(),Toast.LENGTH_SHORT);
							e.printStackTrace();
						}
					} else {
						System.out.println("Errore AWS COGNITO LOGIN");
					}
				},
				error -> {
					Log.e("AuthQuickstart", error.toString());
					UtilsToast.stampaToast(this,error.getMessage(),Toast.LENGTH_SHORT);
				}
		);
	}


	public boolean utenteHaGiaLoggato(){
		if(Profile.getCurrentProfile() != null || GoogleSignIn.getLastSignedInAccount(this) != null || Amplify.Auth.getCurrentUser() != null){
			System.out.println("utenteHaGiaLoggato : TRUE");
			return true;
		}
		else
			System.out.println("utenteHaGiaLoggato : FALSE");
			return false;
	}


	public String recuperaEmailUtenteLoggato(){
		String email = null;
		//Utente locale è già inizializzato
		Utente utente = loginViewModel.getUser();
		if(utente != null){
			Log.v("recuperaUtenteLoggato", "Utente locale gia' esiste");
			Utente.stampaUtenteLocale();
			email=utente.getEmail();
		}
		else{
			if(Profile.getCurrentProfile() != null){
				email=recuperaEmailUtenteLoggatoDaFacebook();
				Log.v("recuperaUtenteLoggato", "recupero utente facebook "+ email);

			}
			if(GoogleSignIn.getLastSignedInAccount(this) != null){
				email=recuperaEmailUtenteLoggatoConGoogle();
				Log.v("recuperaUtenteLoggato", "recupero utente google "+ email);

			}
			if( Amplify.Auth.getCurrentUser() != null){
				email=recuperaEmailUtenteLoggatoDaCognito();
				Log.v("recuperaUtenteLoggato", "recupero utente Cognito "+ email);
			}
		}



		return email;
	}


	public String recuperaEmailUtenteLoggatoDaFacebook(){
		Profile.fetchProfileForCurrentAccessToken();

		final AccessToken accessToken = AccessToken.getCurrentAccessToken();
		final String[] email = new String[1];
		Thread thread = new Thread(()->{
			GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
				@Override
				public void onCompleted(JSONObject user, GraphResponse graphResponse) {
					if (graphResponse.getError() != null) {
						Log.e("ERRORE GRAPH","Erorre nell 'on completed di accesso con fb 2");
						Log.e("ERRORE GRAPH", graphResponse.getError().getErrorMessage());
					} else {

						email[0] = user.optString("email");
						System.out.println("EMAIL RECUPERATA "+email[0]);
					}
				}

			});

			Bundle parameters = new Bundle();
			parameters.putString("fields", "id,name,email");
			request.setParameters(parameters);
			request.executeAndWait();

		});
		thread.start();

		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("STRING EMAIL "+email[0]);

		return email[0];
	}

	public String recuperaEmailUtenteLoggatoConGoogle(){
		GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
		if(account!=null)
			return account.getEmail();
		else{
			System.out.println("ERRORE ACCOUNT GOOGLE == NULL");
			return null;
		}
	}

	public String recuperaEmailUtenteLoggatoDaCognito(){
		AuthUser account =	Amplify.Auth.getCurrentUser();
		return account.getUsername();
	}









}
