package com.example.appandroid.ui.amici;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.motion.widget.MotionScene;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.appandroid.R;
import com.example.appandroid.globalUtils.KeyboardUtils;
import com.example.appandroid.globalUtils.UtilsToast;
import com.example.appandroid.globalUtils.DefaultMethodsFragment;
import com.example.appandroid.listViewClass.utente.AdapterUtente;
import com.example.appandroid.listViewClass.utente.AdapterUtenteListaAmici;
import com.example.appandroid.listViewClass.utente.Utente;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import org.json.JSONException;

import java.util.List;
import java.util.concurrent.TimeoutException;

import static java.util.Objects.requireNonNull;

public class AmiciFragment extends Fragment implements DefaultMethodsFragment {
    private View root;
    private AmiciViewModel amiciViewModel;
    private MotionLayout motionLayout;
    private TextInputEditText textInputEditText;
    private TextInputLayout textInputLayout;
    private FloatingActionButton imageViewIcona_aggiungi_amico;
    private TextView textViewTutteRichiesteAmicizia;
    private ListView listViewListaAmici;
    private ListView listViewRichiesteAmicizia;
    private AdapterUtenteListaAmici adapterAmici;
    private AdapterUtente adapterRichiesteAmicizia;


    private  NavController navController;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_amici, container, false);
        amiciViewModel =  new ViewModelProvider(this, new AmiciViewModelFactory(requireActivity().getApplication())).get(AmiciViewModel.class);

        flowInitFragment();

        initListViewUtentiAmici();
        initListViewUtentiRichiesteAmicizia();

        return root;
    }




    View.OnClickListener listenerOpzioniAmici = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.click_on));
            String pulstanteOperazione =  (String) v.getTag(R.string.operazione);
            Integer pulsantePosition = (Integer) v.getTag(R.string.posizione);
            String usernameTarget = (String) v.getTag(R.string.username_tag);
            Utente utente = amiciViewModel.getAmici().getValue().stream()
                    .filter(o -> o.getUsername().equals(usernameTarget))
                    .findAny()
                    .orElse(null);

            if(utente == null){
                UtilsToast.stampaToast(requireActivity(),"Operazione non riuscita, aggiorna la pagina",Toast.LENGTH_SHORT);
                return;
            }
            if(pulstanteOperazione.equals("remove_friend")){
                new Thread(() -> {
                    try {
                        amiciViewModel.rimuoviAmicizia(utente);
                        UtilsToast.stampaToast(requireActivity(),getResources().getString(R.string.conferma_amicizia_rimossa), Toast.LENGTH_SHORT);
                    } catch (TimeoutException | JSONException e) {
                        UtilsToast.stampaToast(requireActivity(),e.getMessage(),Toast.LENGTH_SHORT);
                        e.printStackTrace();
                    }
                }).start();

            }
        }
    };

    View.OnClickListener listenerOpzioniRichiesteAmicizia = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.click_on));
            String pulstanteOperazione = (String) v.getTag(R.string.operazione);
            Integer pulsantePosition = (Integer) v.getTag(R.string.posizione);
            String usernameTarget = (String) v.getTag(R.string.username_tag);

            Utente utente = amiciViewModel.getRichiesteAmicizia().getValue().stream()
                    .filter(o -> o.getUsername().equals(usernameTarget))
                    .findAny()
                    .orElse(null);

            if(utente == null){
                UtilsToast.stampaToast(requireActivity(),"Operazione non riconosciuta, riprovare",Toast.LENGTH_SHORT);

                return;
            }

            switch (pulstanteOperazione){
                case "accept_request":
                    System.out.println("RICHIESTA AMICIZIA ACCETTATA DALL'UTENTE:" + utente.getUsername());
                    new Thread(() -> {
                        try {
                            amiciViewModel.flowAccettazioneRichiestaAmicizia(utente);
                            amiciViewModel.inviaNotificaAccettazioneRichiestaAmicizia(utente );
                            UtilsToast.stampaToast(requireActivity(),getResources().getString(R.string.conferma_amicizia_accettata), Toast.LENGTH_SHORT);

                        } catch (TimeoutException | JSONException e) {
                            e.printStackTrace();
                            UtilsToast.stampaToast(requireActivity(),e.getMessage(),Toast.LENGTH_SHORT);

                        }
                    }).start();
                    break;
                case "refuse_request":
                    new Thread(() -> {
                        try {
                            amiciViewModel.removeRichiestaAmicizia(utente);
                            amiciViewModel.inviaNotificaRifiutoRichiestaAmicizia(utente);
                            UtilsToast.stampaToast(requireActivity(),getResources().getString(R.string.conferma_amicizia_rifiutata), Toast.LENGTH_SHORT);
                        } catch (TimeoutException | JSONException e) {
                            UtilsToast.stampaToast(requireActivity(),e.getMessage(),Toast.LENGTH_SHORT);
                            e.printStackTrace();
                        }
                    }).start();


                    break;
            }

        }
    };



    @Override
    public void initViewId(){
        motionLayout = root.findViewById(R.id.my_motionLayout);
        MotionScene.Transition transition = motionLayout.getTransition(R.id.transition_restore_default_scene_amici);
        textInputEditText = root.findViewById(R.id.TextInputEditText_ricercaAmici);
        textInputLayout = root.findViewById(R.id.textInputLayout_ricercaAmici);
        imageViewIcona_aggiungi_amico = root.findViewById(R.id.icona_aggiungi_amico);
        textViewTutteRichiesteAmicizia = root.findViewById(R.id.tutte_richieste_di_amicizia);
        listViewListaAmici = root.findViewById(R.id.listView_lista_amici);
        listViewRichiesteAmicizia = root.findViewById(R.id.ListView_richieste_amicizia);

    }




    @Override
    public void initViewListener(){
        // Testo
        textInputEditText.setOnFocusChangeListener(this::transizioneOnFocusBarraRicercaAmici);
        // Casella esterna, sul icona premuta metto un listener
        textInputLayout.setEndIconOnClickListener(this::transizioneOnClickBarraRicercaAmici);
       // imageViewIcona_aggiungi_amico.setOnClickListener(this::apriSchermataRicercaUtenti);
        imageViewIcona_aggiungi_amico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apriSchermataRicercaUtenti(null);
            }
        });

        textViewTutteRichiesteAmicizia.setOnClickListener(this::apriSchermataTutteLeRichiesteAmicizia);
        textInputEditText.addTextChangedListener(textWatchernew);




    }

    @Override
    public void initObserver() {
        Observer<List<Utente>> observer_amici = utenteList -> {
            adapterAmici.saveUnfiltredList();
            adapterAmici.notifyDataSetChanged();

        };
        Observer<List<Utente>> observer_richieste = utenteList ->{
            adapterRichiesteAmicizia.notifyDataSetChanged();
        };


        amiciViewModel.getAmici().observe(getViewLifecycleOwner(),observer_amici);
        amiciViewModel.getRichiesteAmicizia().observe(getViewLifecycleOwner(), observer_richieste);
    }

    @Override
    public void fetchData() {
        new Thread(() -> {
            try {
                amiciViewModel.recuperaAmici();
            } catch (TimeoutException | JSONException e) {
                UtilsToast.stampaToast(requireActivity(),e.getMessage(),Toast.LENGTH_SHORT);
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                amiciViewModel.recuperaRichiesteAmicizia();
            } catch (TimeoutException | JSONException e) {
                e.printStackTrace();
            }
        }).start();


    }

    @Override
    public void initEmptyDateVM() {
        amiciViewModel.init();
    }




    public void initListViewUtentiAmici(){
        adapterAmici = new AdapterUtenteListaAmici(getContext(),amiciViewModel.getAmici().getValue(), listenerOpzioniAmici);
        listViewListaAmici.setAdapter(adapterAmici);
        listViewListaAmici.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                // Prendo username per utente
                bundle.putString("email",amiciViewModel.getAmici().getValue().get(position).getEmail());
                navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_amiciFragment_to_altroUtente, bundle);
            }
        });




    }
    public void initListViewUtentiRichiesteAmicizia(){
        adapterRichiesteAmicizia = new AdapterUtente(getContext(), amiciViewModel.getRichiesteAmicizia().getValue(), listenerOpzioniRichiesteAmicizia);
        listViewRichiesteAmicizia.setAdapter(adapterRichiesteAmicizia);
        listViewRichiesteAmicizia.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("email", amiciViewModel.getRichiesteAmicizia().getValue().get(position).getEmail());
                navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_amiciFragment_to_altroUtente, bundle);
            }
        });

    }

    public void transizioneOnFocusBarraRicercaAmici(View v, boolean hasFocus){

       if(motionLayout.getCurrentState() != motionLayout.getEndState()){
           motionLayout.transitionToEnd();
           textInputLayout.setEndIconDrawable(R.drawable.ic_baseline_close_24);
       }
       KeyboardUtils.hideKeyboard(getActivity());

    }


   public void transizioneOnClickBarraRicercaAmici(View v) {

       if(motionLayout.getCurrentState() == motionLayout.getStartState()){
           textInputLayout.setEndIconDrawable(R.drawable.ic_baseline_close_24);
           motionLayout.transitionToEnd();
           KeyboardUtils.showKeyboard(getActivity(), root);
           return;
       }

       if(motionLayout.getCurrentState() == motionLayout.getEndState()){
           textInputLayout.setEndIconDrawable(R.drawable.ic_baseline_search_24);
           textInputLayout.clearFocus();
           motionLayout.transitionToStart();
           textInputLayout.getEditText().setText("");
           KeyboardUtils.hideKeyboard(getActivity());
           return;
       }
    }

    private void apriSchermataTutteLeRichiesteAmicizia(View view) {
        navController = Navigation.findNavController(root);
        navController.navigate(R.id.action_amiciFragment_to_richiesteAmiciziaFragment);
    }


    private void apriSchermataRicercaUtenti(View view) {
        navController = Navigation.findNavController(root);
        navController.navigate(R.id.action_amiciFragment_to_ricercaUtentiFragment);

    }





    TextWatcher textWatchernew = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            adapterAmici.getFilter().filter(s);
        }
        @Override
        public void afterTextChanged(Editable s) {
        }
    };



    @Override
    public void onResume() {
        super.onResume();
        // perche' cambiando paggina e ritornando indietro, editText si ricorda il testo precedente ma non viene applicato il filtro
        textInputEditText.setText("");
    }





}

