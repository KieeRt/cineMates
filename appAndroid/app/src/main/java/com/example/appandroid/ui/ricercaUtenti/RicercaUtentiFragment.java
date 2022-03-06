package com.example.appandroid.ui.ricercaUtenti;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.appandroid.R;
import com.example.appandroid.globalUtils.KeyboardUtils;
import com.example.appandroid.globalUtils.DefaultMethodsFragment;
import com.example.appandroid.globalUtils.UtilsToast;
import com.example.appandroid.listViewClass.utente.AdapterUtente;
import com.example.appandroid.listViewClass.utente.Utente;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;

import java.util.List;
import java.util.concurrent.TimeoutException;

public class RicercaUtentiFragment extends Fragment implements DefaultMethodsFragment {
    private View root;
    private RicercaUtentiViewModel viewModel;
    private TextInputEditText textInputEditText;
    private TextInputLayout textInputLayout;
    private ListView listView;
    private NavController navController;
    private AdapterUtente adapterUtente;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_ricerca_utenti, container, false);
        viewModel = new ViewModelProvider(this, new RicercaUtentiViewModelFactory(requireActivity().getApplication())).get(RicercaUtentiViewModel.class);

        flowInitFragment();

        initListView();

        return root;
    }




    private final View.OnClickListener listenerUtenteGenerico = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.click_on));
            String pulstanteOperazione =  (String) v.getTag(R.string.operazione);
            Integer pulsantePosition = (Integer) v.getTag(R.string.posizione);

            List<Utente> listaUtenti = viewModel.getRisultatiRicerca().getValue();
            String emailUtenteInLista = listaUtenti.get(pulsantePosition).getEmail();
            String usernameUtenteInLista = listaUtenti.get(pulsantePosition).getUsername();

            switch (pulstanteOperazione){
                case "remove_friend":
                    new Thread(()->{
                        try {
                            viewModel.rimuoviAmicizia(emailUtenteInLista);
                            UtilsToast.stampaToast(requireActivity(),getResources().getString(R.string.conferma_amicizia_rimossa), Toast.LENGTH_SHORT);
                        } catch (TimeoutException | JSONException e) {
                            e.printStackTrace();
                            UtilsToast.stampaToast(requireActivity(),e.getMessage(), Toast.LENGTH_SHORT);


                        }
                    }).start();
                    System.out.println("RICHIESTA RIMOZIONE AMICO:" + usernameUtenteInLista);
                    break;
                case "add_request":
                    new Thread(()->{
                        try {
                            viewModel.addRichiestaAmicizia(emailUtenteInLista);
                            UtilsToast.stampaToast(requireActivity(),getResources().getString(R.string.conferma_amicizia_inviata), Toast.LENGTH_SHORT);

                        } catch (TimeoutException | JSONException e) {
                            e.printStackTrace();
                            UtilsToast.stampaToast(requireActivity(),e.getMessage(), Toast.LENGTH_SHORT);

                        }
                    }).start();
                    System.out.println("RICHIESTA AMICIZIA INVIATA ALL'UTENTE:" + usernameUtenteInLista);
                    break;
                case "accept_request":
                    new Thread(()->{
                        try {
                            viewModel.accettaRichiestaAmicizia(emailUtenteInLista);
                            UtilsToast.stampaToast(requireActivity(),getResources().getString(R.string.conferma_amicizia_accettata), Toast.LENGTH_SHORT);

                        } catch (TimeoutException | JSONException e) {
                            e.printStackTrace();
                            UtilsToast.stampaToast(requireActivity(),e.getMessage(), Toast.LENGTH_SHORT);

                        }
                    }).start();
                    System.out.println("RICHIESTA MICIZIA ACCETTATA DALL'UTENTE:" + usernameUtenteInLista);
                    break;
                case "refuse_request":
                    new Thread(()->{
                        try {
                            viewModel.rifiutaRichiestaAmicizia(emailUtenteInLista);
                            UtilsToast.stampaToast(requireActivity(),getResources().getString(R.string.conferma_amicizia_rifiutata), Toast.LENGTH_SHORT);

                        } catch (TimeoutException | JSONException e) {
                            e.printStackTrace();
                            UtilsToast.stampaToast(requireActivity(),e.getMessage(), Toast.LENGTH_SHORT);

                        }
                    }).start();
                    System.out.println("RICHIESTA MICIZIA RIFIUTATA ALL'UTENTE:" + usernameUtenteInLista);
                    break;
                case "cancel_request":
                    new Thread(()->{
                        try {
                            viewModel.cancellaRichiestaAmicizia(emailUtenteInLista);
                            UtilsToast.stampaToast(requireActivity(),getResources().getString(R.string.conferma_richiesta_amicizia_rimossa), Toast.LENGTH_SHORT);
                        } catch (TimeoutException | JSONException e) {
                            e.printStackTrace();
                            UtilsToast.stampaToast(requireActivity(),e.getMessage(), Toast.LENGTH_SHORT);
                        }
                    }).start();
                    System.out.println("RICHIESTA MICIZIA CANCELLATA ALL'UTENTE:" + usernameUtenteInLista);
                    break;
            }
        }
    };




    @Override
    public void initViewId(){
        textInputLayout = root.findViewById(R.id.TextInputLaout_ricercaUtenti);
        textInputEditText = root.findViewById(R.id.TextInputEditText_ricercaUtenti);
        listView = root.findViewById(R.id.listView_risultati_ricerca_utenti);
    }


    @Override
    public void initObserver() {
        final Observer<List<Utente>> observer = new Observer<List<Utente>>() {
            @Override
            public void onChanged(List<Utente> utentiCercati) {
                adapterUtente.notifyDataSetChanged();
            }
        };

        final Observer<Boolean> observerRicercaFinita = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean finito) {
                if(finito){
                    if(viewModel.getRisultatiRicerca().getValue().isEmpty())
                        UtilsToast.stampaToast(requireActivity(), "Nessun risultato trovato", Toast.LENGTH_SHORT);
                }
            }
        };

        viewModel.getRisultatiRicerca().observe(getViewLifecycleOwner(), observer);
        viewModel.getRicercaFinita().observe(getViewLifecycleOwner(), observerRicercaFinita);
    }

    @Override
    public void fetchData() {
    }

    @Override
    public void initEmptyDateVM() {
        viewModel.init();
    }

    @Override
    public void initViewListener(){
        KeyboardUtils.HideKeyboardOnFocusChangeListener(textInputEditText, getActivity());
        textInputLayout.setEndIconOnClickListener(this::effettuaRicercaPremuto);
    }

    public void initListView(){
        adapterUtente = new AdapterUtente(getContext(),viewModel.getRisultatiRicerca().getValue(), listenerUtenteGenerico);
        listView.setAdapter(adapterUtente);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO: logica per aprire la schermata
                Utente utente = viewModel.getRisultatiRicerca().getValue().get(position);
                Bundle bundle = new Bundle();
                bundle.putString("email", utente.getEmail());
                navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_ricercaUtentiFragment_to_altroUtente, bundle);

                System.out.println("Cliccato sul utente:" + utente.getUsername() + " Attualmente rel amicizia: " + utente.getCURRENT_STATE());
            }
        });

    }
    private void effettuaRicercaPremuto(View view) {
        new Thread(()->{
            KeyboardUtils.hideKeyboard(getActivity());
            try {
                viewModel.effettuaRicerca(textInputEditText.getText().toString());
            } catch (TimeoutException | JSONException e) {
                UtilsToast.stampaToast(requireActivity(),e.getMessage(),Toast.LENGTH_SHORT);
                e.printStackTrace();
            }
        }).start();
    }




}