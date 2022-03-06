package com.example.appandroid.globalUtils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.appandroid.R;
import com.example.appandroid.listViewClass.film.Film;
import com.example.appandroid.listViewClass.listaPersonalizzata.AdapterTendinaSalvaFilmInLista;
import com.example.appandroid.listViewClass.listaPersonalizzata.ListaPersonalizzata;
import com.example.appandroid.listViewClass.segnalazione.Segnalazione;
import com.example.appandroid.listViewClass.utente.Utente;
import com.example.appandroid.repository.RepositoryFactory;
import com.example.appandroid.repository.RepositoryService;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class DisplayDialog {
	private final FragmentActivity fragmentActivity;
	private final Context context;
	private final RepositoryService repository;
	private MutableLiveData<List<ListaPersonalizzata>> elencoListe;
	private AdapterTendinaSalvaFilmInLista adapter;

	public DisplayDialog(Context context, FragmentActivity fragmentActivity) {
		this.context = context;
		this.fragmentActivity = fragmentActivity;
		repository= RepositoryFactory.getRepositoryConcrete();


		elencoListe = new MutableLiveData<>(new ArrayList<>());
		try {
			elencoListe.setValue(repository.getListe(Utente.getUtente().getEmail()));

		} catch (JSONException | TimeoutException e) {
			e.printStackTrace();
			UtilsToast.stampaToast(fragmentActivity, e.getMessage(), Toast.LENGTH_SHORT);
		}


	}

	//QUESTO METODO E' DA ORDINARE
	public void mostraTendinaSalvaFilmInLista(Film film) {


		AlertDialogUtils costruttoreDialog = new AlertDialogUtils(context, R.layout.tendina_aggiungi_film_in_lista);
		View layoutDialog = costruttoreDialog.getLayout();
		List<ListaPersonalizzata> listeChecked = new ArrayList<>();
		List<ListaPersonalizzata> listeUnchecked = new ArrayList<>();


		TextView linkCreaNuovaLista = layoutDialog.findViewById(R.id.linkCreaNuovaListaTendinaSalvaFilmInLista);
		linkCreaNuovaLista.setOnClickListener(this::mostraAlertDialogCreaNuovaLista);

		CompoundButton.OnCheckedChangeListener listener = (buttonView, isChecked) -> {
			int posizioneLista = (int) buttonView.getTag();
			ListaPersonalizzata listaInteressata = elencoListe.getValue().get(posizioneLista);
			if (buttonView.isChecked()) {
				listeChecked.add(listaInteressata);
				listeUnchecked.remove(listaInteressata);
			} else {
				listeChecked.remove(listaInteressata);
				listeUnchecked.add(listaInteressata);
			}
		};

		ListView listViewListe = layoutDialog.findViewById(R.id.ListViewListeTendinaSalvaFilmInLista);
		adapter = new AdapterTendinaSalvaFilmInLista(context, elencoListe.getValue(), film, listener);


		final Observer<List<ListaPersonalizzata>> observer = new Observer<List<ListaPersonalizzata>>() {
			@Override
			public void onChanged(@Nullable final List<ListaPersonalizzata> listaAggiornata) {
				// Update the UI, in this case, a TextView.
				Log.d("AGGIORNAMENTO UI ", "AGGIORNO !! Elenco liste proprie");

				adapter.notifyDataSetChanged();
			}
		};

		elencoListe.observe(fragmentActivity, observer);

		listViewListe.setAdapter(adapter);

		costruttoreDialog.initAlertButtonAction(R.id.buttonAnnullaTendinaSalvaFilmInLista, v1 -> {
			v1.startAnimation(AnimationUtils.loadAnimation(context, R.anim.click_on));
			costruttoreDialog.chiudiAlert();
		});

		costruttoreDialog.initAlertButtonAction(R.id.buttonFattoTendinaSalvaFilmInLista, v2 -> {
			v2.startAnimation(AnimationUtils.loadAnimation(context, R.anim.click_on));

			new Thread(() -> {
				try {
					aggiornaListeUtente(film, listeChecked, listeUnchecked);
					UtilsToast.stampaToast(fragmentActivity, "Cambiamenti salvati", Toast.LENGTH_SHORT);
				} catch (TimeoutException | JSONException e) {
					e.printStackTrace();
					UtilsToast.stampaToast(fragmentActivity, e.getMessage(), Toast.LENGTH_SHORT);
				}
			}).start();

			costruttoreDialog.chiudiAlert();
		});

		costruttoreDialog.mostraAlertDialog();

	}

	private void aggiornaListeUtente(Film filmInteressato, List<ListaPersonalizzata> listeChecked, List<ListaPersonalizzata> listeUnchecked) throws TimeoutException, JSONException {
		String idFilm = filmInteressato.getIdFilm();
		for (ListaPersonalizzata listaSpuntata : listeChecked) {
			int idLista = listaSpuntata.getIdLista();
			repository.addFilm(idFilm);
			repository.addFilmInLista(idLista, idFilm);
			repository.cambiaImmagineLista(idLista, filmInteressato.getImmagineCopertina());

		}
		for (ListaPersonalizzata listaNonSpuntata : listeUnchecked) {
			int idLista = listaNonSpuntata.getIdLista();
			repository.removeFilmInLista(idLista, idFilm);

			if (listaNonSpuntata.getImmagineCopertina().equals(filmInteressato.getImmagineCopertina())) {
				if (listaNonSpuntata.getListaDiFilm() != null && listaNonSpuntata.getListaDiFilm().size() > 0) {
					Film film = repository.getFilmById(listaNonSpuntata.getListaDiFilm().get(listaNonSpuntata.getListaDiFilm().size() - 1));
					System.out.println("NUOVA COPERTINA DELLA LISTA : " + listaNonSpuntata.getNome());
					System.out.println("COPERTINA : " + film.getNome());
					repository.cambiaImmagineLista(idLista, film.getImmagineCopertina());
				} else {
					repository.cambiaImmagineLista(idLista, "null");
				}
			}
		}
	}

	public void mostraTendinaCambioPassword() {
		// username del utente richiesto inteso da Cognito parametro usato per la registrazione, in questo caso e' email

		AlertDialogUtils alertDialogUtils = new AlertDialogUtils(context, R.layout.tendina_cambio_password);

		View layout = alertDialogUtils.getLayout();
		TextInputEditText viewVecchiaPassword = layout.findViewById(R.id.editTextVecchiaPasswordTendinaCambioPassword);
		TextInputEditText viewNuovaPassword = layout.findViewById(R.id.editTextNuovaPasswordTendinaCambioPassword);

		alertDialogUtils.initAlertButtonAction(R.id.buttonSalvaTendinaCambioPassword, v1 -> {
			v1.startAnimation(AnimationUtils.loadAnimation(context, R.anim.click_on));
			String oldPassword = viewVecchiaPassword.getText().toString();
			String newPassword = viewNuovaPassword.getText().toString();
			boolean risposta = repository.effettuaRichiestaCambioPassword(oldPassword,newPassword);

			if(risposta)
				UtilsToast.stampaToast(fragmentActivity, "Password cambiata con successo", Toast.LENGTH_SHORT);
			else
				UtilsToast.stampaToast(fragmentActivity, "Password non rispetta i requisiti", Toast.LENGTH_SHORT);

			alertDialogUtils.chiudiAlert();

		});


		alertDialogUtils.initAlertButtonAction(R.id.buttonAnnullaTendinaCambioPassword, v2 -> {
			v2.startAnimation(AnimationUtils.loadAnimation(context, R.anim.click_on));
			alertDialogUtils.chiudiAlert();
		});

		alertDialogUtils.mostraAlertDialog();
	}

	public void mostraAlertDialogCreaNuovaLista(View view) {
		AlertDialogUtils costruttore = new AlertDialogUtils(context, R.layout.tendina_crea_nuova_lista);

		View layout = costruttore.getLayout();

		TextInputEditText viewTitoloLista = layout.findViewById(R.id.titoloCreazioneLista);
		TextInputEditText viewDescrizioneLista = layout.findViewById(R.id.descirzioneCreazioneLista);
		TextView viewMessaggioErrore = layout.findViewById(R.id.messaggioErrore);


		costruttore.initAlertButtonAction(R.id.buttonAnnullaTendinaCreaNuovaLista, v -> {
			v.startAnimation(AnimationUtils.loadAnimation(context, R.anim.click_on));
			costruttore.chiudiAlert();
		});

		costruttore.initAlertButtonAction(R.id.buttonSalvaTendinaCreaNuovaLista, v -> {
			v.startAnimation(AnimationUtils.loadAnimation(context, R.anim.click_on));
			if (!viewTitoloLista.getText().toString().equals("")) {
				String nuovoTitolo = viewTitoloLista.getText().toString();
				String nuovaDescrizione = viewDescrizioneLista.getText().toString();

				new Thread(() -> {
					try {
						creaNuovaLista(nuovoTitolo, nuovaDescrizione);
						UtilsToast.stampaToast(fragmentActivity,"Lista "+nuovoTitolo+" è stata creata con successo",Toast.LENGTH_SHORT);
					} catch (TimeoutException | JSONException e) {
						UtilsToast.stampaToast(fragmentActivity, e.getMessage(), Toast.LENGTH_SHORT);
						e.printStackTrace();
					}
				}).start();

				costruttore.chiudiAlert();
			} else {
				viewMessaggioErrore.setText("Titolo mancante, riprova...");
				new Thread(() -> {
					fragmentActivity.runOnUiThread(() -> viewMessaggioErrore.setAlpha(1));
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					fragmentActivity.runOnUiThread(() -> viewMessaggioErrore.setAlpha(0));
				}).start();
			}


		});
		costruttore.mostraAlertDialog();
	}

	public void mostraTendinaSegnalazione(int idLista) {
		AlertDialogUtils costruttoreDialog = new AlertDialogUtils(context, R.layout.tendina_segnalazione);
		View rootDialog = costruttoreDialog.getLayout();
		RadioGroup radioGroup = rootDialog.findViewById(R.id.radioGroupPulsantiSegnalazione);

		costruttoreDialog.initAlertButtonAction(R.id.buttonAnnullaSegnalazione, v1 -> {
			v1.startAnimation(AnimationUtils.loadAnimation(context, R.anim.click_on));
			costruttoreDialog.chiudiAlert();
		});

		costruttoreDialog.initAlertButtonAction(R.id.buttonFattoSegnalazione, v2 -> {
			v2.startAnimation(AnimationUtils.loadAnimation(context, R.anim.click_on));
			int idRadioButtonChecked = radioGroup.getCheckedRadioButtonId();
			if (idRadioButtonChecked != -1) {
				String motivoSegnalazione = null;
				switch (idRadioButtonChecked) {
					case R.id.radioButtonSpoiler:
						motivoSegnalazione = "Spoiler";
						break;
					case R.id.radioButtonContenutiViolenti:
						motivoSegnalazione = "Contenuti violenti o ripugnanti";
						break;
					case R.id.radioButtonContenutiOffensivi:
						motivoSegnalazione = "Contenuti offensivi od oltraggiosi";
						break;
					default:
				}

				String finalMotivoSegnalazione = motivoSegnalazione;
				new Thread(() -> {
					try {
						creaNuovaSegnalazione(finalMotivoSegnalazione, idLista);
						UtilsToast.stampaToast(fragmentActivity, "Segnalazione avvenuta con successo", Toast.LENGTH_SHORT);
					} catch (TimeoutException | JSONException e) {
						e.printStackTrace();
						UtilsToast.stampaToast(fragmentActivity, e.getMessage(), Toast.LENGTH_SHORT);
					}
				}).start();

				costruttoreDialog.chiudiAlert();

			} else {
				UtilsToast.stampaToast(fragmentActivity, "Nessuna motivazione rilevata", Toast.LENGTH_SHORT);			}
		});

		costruttoreDialog.mostraAlertDialog();
	}

	public void creaNuovaSegnalazione(String motivo, int idLista) throws TimeoutException, JSONException {
		repository.addSegnalazione(new Segnalazione(motivo), idLista, Utente.getUtente().getEmail());
	}


	private void creaNuovaLista(String titolo, String descrizione) throws TimeoutException, JSONException {
		ListaPersonalizzata listaCreata = new ListaPersonalizzata(titolo, descrizione);
		repository.addLista(listaCreata, Utente.getUtente().getEmail());

		List<ListaPersonalizzata> elencoListeUtente = elencoListe.getValue();
		elencoListeUtente.add(listaCreata);
		elencoListe.postValue(elencoListeUtente);
	}


}
