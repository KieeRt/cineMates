package com.example.appandroid.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.appandroid.R;
import com.example.appandroid.globalUtils.AlertDialogUtils;
import com.example.appandroid.globalUtils.Rotante;
import com.example.appandroid.globalUtils.DefaultMethodsFragment;
import com.example.appandroid.globalUtils.UtilsToast;
import com.example.appandroid.listViewClass.film.Film;
import com.example.appandroid.listViewClass.listaPersonalizzata.ListaPersonalizzata;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;

import java.util.List;
import java.util.concurrent.TimeoutException;


public class HomeFragment extends Fragment implements DefaultMethodsFragment {

	private View root;
	private HomeViewModel homeViewModel;

	private TextView linkListeProprie;
	private TextView linkFilmDaGuardare;
	private ExtendedFloatingActionButton pulsanteCreaLista ;

	private Rotante<Film> rot1;
	private Rotante<ListaPersonalizzata> rot2;

	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
		root = inflater.inflate(R.layout.fragment_home, container, false);

		disabilitaPulsanteIndietro();

		flowInitFragment();




		return root;
	}


	public void disabilitaPulsanteIndietro(){

		OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
			@Override
			public void handleOnBackPressed() {
				// Handle the back button event
			}
		};
		requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),callback);
	}


	public void mostraRotanteListeUtente(List<ListaPersonalizzata> listeAggiornata){
		if(getContext()!=null){
			rot2 = new Rotante<>(listeAggiornata, R.id.recyclerViewLista,root,getContext(),Rotante.ROTANTE_UTENTE);
			rot2.init();

		}
	}

	public void mostraRotanteFilmDaGuardare(List<Film> filmDaGuardareAggiornato){
		if(getContext()!=null){
			rot1 = new Rotante<>(filmDaGuardareAggiornato, R.id.recyclerViewFilm,root,getContext(),Rotante.ROTANTE_UTENTE);
			rot1.init();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}


	@Override
	public void initViewId(){
		pulsanteCreaLista = root.findViewById(R.id.pulsanteCreaListaPlus);
		linkListeProprie = root.findViewById(R.id.linkTutteLeListe);
		linkFilmDaGuardare = root.findViewById(R.id.linkFilmDaGuardare);
	}

	@Override
	public void initViewListener(){
		pulsanteCreaLista.setOnClickListener(this::mostraAlertDialogCreaNuovaLista);
		linkFilmDaGuardare.setOnClickListener(this::apriSchermataFilmDaGuardare);
		linkListeProprie.setOnClickListener(this::apriSchermataListeProprie);
	}

	@Override
	public void initObserver() {
		final Observer<List<ListaPersonalizzata>> observer = new Observer<List<ListaPersonalizzata>>() {
			@Override
			public void onChanged(@Nullable final List<ListaPersonalizzata> listeAggiornata) {
				// Update the UI
				Log.d("AGGIORNAMENTO UI HOME","AGGIORNO !! Liste in home page");
				mostraRotanteListeUtente(listeAggiornata);
			}
		};

		final Observer<List<Film>> observerFilmDaGuardare = new Observer<List<Film>>() {
			@Override
			public void onChanged(@Nullable final List<Film> filmDaGuardareAggiornato) {
				// Update the UI
				Log.d("AGGIORNAMENTO UI HOME","AGGIORNO !! Liste in home page");
				mostraRotanteFilmDaGuardare(filmDaGuardareAggiornato);
			}
		};



		homeViewModel.getListeUtenteMutable().observe(getViewLifecycleOwner(), observer);
		homeViewModel.getListFilmDaGuardare().observe(getViewLifecycleOwner(), observerFilmDaGuardare);
	}

	@Override
	public void fetchData() {
		new Thread(()->{
			try {
				homeViewModel.recuperaListeProprieEFilmDaGuardare();
			} catch (JSONException | TimeoutException e) {
				e.printStackTrace();
				UtilsToast.stampaToast(requireActivity(),e.getMessage(),Toast.LENGTH_SHORT);
			}
		}).start();
	}

	@Override
	public void initEmptyDateVM() {
		homeViewModel.init();
	}

	public void apriSchermataListeProprie(View view){
		NavController navController = Navigation.findNavController(view);
		navController.navigate(R.id.action_nav_home_to_elencoListeProprieFragment);
	}

	public void apriSchermataFilmDaGuardare(View view){
		//Logica pressione link liste proprie
		Bundle bundle = new Bundle();
		ListaPersonalizzata listaFilmDaGuardare = homeViewModel.getListeUtenteMutable().getValue().stream().filter(o->o.getNome().equals("Film da guardare")).findAny().orElse(null);

		if(listaFilmDaGuardare!=null)
			bundle.putInt("idLista",listaFilmDaGuardare.getIdLista());

		NavController navController = Navigation.findNavController(view);
		navController.navigate(R.id.moveToContenutoLista,bundle);
	}


	public void mostraAlertDialogCreaNuovaLista(View view){
		AlertDialogUtils costruttore = new AlertDialogUtils(getContext(),R.layout.tendina_crea_nuova_lista);

		View layout = costruttore.getLayout();

		TextInputEditText viewTitoloLista = layout.findViewById(R.id.titoloCreazioneLista);
		TextInputEditText viewDescrizioneLista = layout.findViewById(R.id.descirzioneCreazioneLista);
		TextView viewMessaggioErrore = layout.findViewById(R.id.messaggioErrore);


		costruttore.initAlertButtonAction(R.id.buttonAnnullaTendinaCreaNuovaLista,v -> {
			costruttore.chiudiAlert();
		});

		costruttore.initAlertButtonAction(R.id.buttonSalvaTendinaCreaNuovaLista,v -> {
			if(!viewTitoloLista.getText().toString().equals("")){
				String nuovoTitolo = viewTitoloLista.getText().toString();
				String nuovaDescrizione = viewDescrizioneLista.getText().toString();

				new Thread(()->{
					try {
						homeViewModel.addLista(nuovoTitolo,nuovaDescrizione);
						UtilsToast.stampaToast(requireActivity(),"Lista "+nuovoTitolo+" è stata creata con successo",Toast.LENGTH_SHORT);
					} catch (TimeoutException | JSONException e) {
						e.printStackTrace();
						UtilsToast.stampaToast(requireActivity(),e.getMessage(),Toast.LENGTH_SHORT);
					}
				}).start();


				costruttore.chiudiAlert();
			}
			else{
				viewMessaggioErrore.setText("Titolo mancante, riprova...");
				new Thread(()->{
					requireActivity().runOnUiThread(() -> viewMessaggioErrore.setAlpha(1));
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					requireActivity().runOnUiThread(() -> viewMessaggioErrore.setAlpha(0));
				}).start();
			}



		});
		costruttore.mostraAlertDialog();
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		System.out.println("HOME DISTRUTTA");
	}
}