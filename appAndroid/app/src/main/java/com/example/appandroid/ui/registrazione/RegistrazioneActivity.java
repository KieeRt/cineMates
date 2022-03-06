package com.example.appandroid.ui.registrazione;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.appandroid.R;
import com.example.appandroid.globalUtils.AlertDialogUtils;
import com.example.appandroid.globalUtils.UtilsToast;
import com.example.appandroid.ui.login.LoginActivity;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;

import java.util.concurrent.TimeoutException;

public class RegistrazioneActivity extends AppCompatActivity {

	private Button buttonAnnulla;
	private Button buttonRegistrati;

	private TextView linkCodice ;

	private EditText editTextUsername;
	private EditText editTextPassword;
	private EditText editTextConfermaPassword;
	private EditText editTextEmail;


	private String emailInserita;
	private String passwordInserita;
	private String confermaPassword;
	private String usernameInserito;

	private RegistrazioneViewModel viewModel ;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registrazione);
		viewModel = new ViewModelProvider(this).get(RegistrazioneViewModel.class);
		init();
	}

	public void init(){
		viewModel.init();
		initView();
		initButtonAction();
		initLinkAction();
	}

	public void initView(){
		buttonRegistrati = findViewById(R.id.registratiButton);
		buttonAnnulla = findViewById(R.id.annullaButton);
		editTextUsername = findViewById(R.id.editTextUsernameSignUpScreen);
		editTextEmail = findViewById(R.id.editTextEmailSignUpScreen);
		editTextConfermaPassword=findViewById(R.id.editTextConfermaPasswordSignUpScreen);
		editTextPassword = findViewById(R.id.editTextPasswordSignUpScreen);
		linkCodice = findViewById(R.id.linkHoGiaUnCodice);
	}

	public void initLinkAction(){
		linkCodice.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				apriTendinaHoGiaUnCodice();
			}
		});
	}

	public void initButtonAction(){
		buttonAnnulla.setOnClickListener(this::apriSchermataLogin);

		buttonRegistrati.setOnClickListener(v -> {
			v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.click_on));
			recuperaCampiInseriti();
			if(campiSonoValidi(emailInserita,passwordInserita,confermaPassword,usernameInserito))
				registrazioneUtenteCognito();

		});
	}

	/**
	 * Input : String  emailDaControllare
	 * 		   String passwordDaControllare
	 * 		   String confermaPasswordDaControllare
	 * 		   String usernameDaControllare
	 * Output TRUE SE E SOLO SE vengono verificate tutte le seguenti condizioni :
	 * -Tutti i campi non sono vuoti
	 * -La lunghezza della password compresa tra 8 e 30
	 * -Password e ConfermaPassword corrispondono
	 * -Email non registrata sul database
	 * -Username non registrato sul database
	 *
	 *  Altrimenti verrà restituito FALSE
	 *
	 *  Viene stampato un messaggio in funzione dei controlli superati/non superati.
	 * */

	public boolean campiSonoValidi(String emailDaControllare, String passwordDaControllare,String confermaPasswordDaControllare, String usernameDaControllare){
		String msgError = "";
		try {
			if(!isAvailableUsername(usernameDaControllare)){
				msgError = msgError.concat("Username non disponibile");
			}
			if(!isAvailableEmail(emailDaControllare)){
				if(!msgError.equals(""))
					msgError=msgError.concat("\n");
				msgError = msgError.concat("Email già utilizzata");
			}
			if(!checkConfermaPassoword(confermaPasswordDaControllare,passwordDaControllare)) {
				if(!msgError.equals(""))
					msgError=msgError.concat("\n");
				msgError = msgError.concat("Password e conferma password non corrispondono");
			}
			if(!checkCampiCompilati(emailDaControllare,passwordDaControllare,confermaPasswordDaControllare,usernameDaControllare)
					|| !checkValiditaPassword(passwordDaControllare)){
				if(!msgError.equals(""))
					msgError=msgError.concat("\n");
				msgError = msgError.concat("Campi mancanti o requisiti non rispettati");
			}
			if(!checkValiditaPassword(passwordDaControllare)){
				if(!msgError.equals(""))
					msgError=msgError.concat("\n");
				msgError = msgError.concat("Lunghezza password deve essere compresa tra 8 e 30 caratteri");
			}

			if(!msgError.equals("")) {
				UtilsToast.stampaToast(this, msgError, Toast.LENGTH_LONG);
				return false ;
			}
		} catch (TimeoutException | JSONException | NullPointerException e) {
			e.printStackTrace();
			UtilsToast.stampaToast(this, e.getMessage(), Toast.LENGTH_LONG);

			return false;
		}

		return true;
	}

	private boolean checkCampiCompilati(String emailDaControllare, String passwordDaControllare,String confermaPasswordDaControllare, String usernameDaControllare){
		return !emailDaControllare.equals("") &&
				!passwordDaControllare.equals("") &&
				!confermaPasswordDaControllare.equals("") &&
				!usernameDaControllare.equals("") ;
	}

	private boolean checkValiditaPassword(String password){
		return password.length()>7 && password.length()<31 ;
	}

	private boolean isAvailableEmail(String email) throws TimeoutException, JSONException {
		System.out.println("EMAIL PASSATA" +email);
		System.out.println("UTENTE RECUPERATO " + viewModel.getUtenteFromBD(email));
		return viewModel.getUtenteFromBD(email)==null ;
	}

	private boolean checkConfermaPassoword(String passwordConfirm,String password) {
		return password.equals(passwordConfirm);
	}

	private boolean isAvailableUsername(String username) throws TimeoutException, JSONException {
		return !viewModel.usernameAlreadyExists(username);
	}

	public void apriSchermataLogin(View view){
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
	}

	public void registrazioneUtenteCognito(){
		boolean risposta = viewModel.effettuaRegistrazione(emailInserita,usernameInserito,passwordInserita);
		if(risposta){
			this.runOnUiThread(() -> apriTendinaInserisciCodice(null));
		}
		else{
			UtilsToast.stampaToast(this, "Errore riscontrato nella registrazione", Toast.LENGTH_LONG);
		}
	}

	public void confermaCodiceRegistrazionePremuto(String codiceConferma, String emailDaConfermare, String usernameDaConfermare){
		new Thread(()->{
			boolean risposta = false;
			try {
				risposta = viewModel.confermaCodiceRegistrazione(emailDaConfermare,usernameDaConfermare,codiceConferma);
			} catch (TimeoutException | JSONException e) {
				runOnUiThread(()->{UtilsToast.stampaToast(this,e.getMessage(),Toast.LENGTH_SHORT);});
			}
			if(risposta){
				runOnUiThread(()->{	UtilsToast.stampaToast(this,"Registrazione confermata con successo",Toast.LENGTH_SHORT); });
				apriSchermataLogin(null);
			}
			else{
				runOnUiThread(()->{	UtilsToast.stampaToast(this,"Registrazione non confermata",Toast.LENGTH_SHORT);});
			}
		}).start();
	}

	public void recuperaCampiInseriti(){
		emailInserita = editTextEmail.getText().toString();
		passwordInserita = editTextPassword.getText().toString();
		usernameInserito = editTextUsername.getText().toString();
		confermaPassword = editTextConfermaPassword.getText().toString();
	}

	public void apriTendinaInserisciCodice(View v){
		AlertDialogUtils costruttoreDialog = new AlertDialogUtils(this,R.layout.tendina_conferma_registrazione);
		View layout = costruttoreDialog.getLayout();

		costruttoreDialog.initAlertButtonAction(R.id.buttonConfermaTendinaVerificaAccount,view -> {
			view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.click_on));
			TextInputEditText codice = layout.findViewById(R.id.editTextCodiceConferma);
			String codiceInserito = codice.getText().toString();
			confermaCodiceRegistrazionePremuto(codiceInserito,emailInserita,usernameInserito);
			costruttoreDialog.chiudiAlert();
		});

		costruttoreDialog.initAlertButtonAction(R.id.buttonAnnullaTendinaVerificaAccount,view -> {
			view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.click_on));
			costruttoreDialog.chiudiAlert();
		});

		costruttoreDialog.mostraAlertDialog();
	}

	public void apriTendinaHoGiaUnCodice() {
		AlertDialogUtils costruttoreDialog = new AlertDialogUtils(this, R.layout.tendina_ho_gia_un_codice);
		View layout = costruttoreDialog.getLayout();

		costruttoreDialog.initAlertButtonAction(R.id.buttonConfermaTendinaVerificaAccount, view -> {
			view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.click_on));

			TextInputEditText codiceField = layout.findViewById(R.id.editTextCodiceConferma);
			TextInputEditText usernameField = layout.findViewById(R.id.editTextUsername);
			TextInputEditText emailField = layout.findViewById(R.id.editTextEmail);

			String codiceInserito = codiceField.getText().toString();
			String emailInserita = emailField.getText().toString();
			String usernameInserita = usernameField.getText().toString();

			confermaCodiceRegistrazionePremuto(codiceInserito, emailInserita, usernameInserita);
			costruttoreDialog.chiudiAlert();
		});

		costruttoreDialog.initAlertButtonAction(R.id.buttonAnnullaTendinaVerificaAccount, view -> {
			view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.click_on));
			costruttoreDialog.chiudiAlert();
		});

		costruttoreDialog.mostraAlertDialog();
	}

}
