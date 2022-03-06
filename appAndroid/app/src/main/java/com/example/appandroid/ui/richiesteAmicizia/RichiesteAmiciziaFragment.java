package com.example.appandroid.ui.richiesteAmicizia;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.appandroid.R;
import com.example.appandroid.globalUtils.DefaultMethodsFragment;
import com.example.appandroid.globalUtils.UtilsToast;
import com.example.appandroid.listViewClass.utente.AdapterUtente;
import com.example.appandroid.listViewClass.utente.Utente;

import org.json.JSONException;

import java.util.List;
import java.util.concurrent.TimeoutException;

public class RichiesteAmiciziaFragment extends Fragment  implements DefaultMethodsFragment {

    private RichiesteAmiciziaViewModel viewModel;
    private View root;
    private NavController navController;
    private ListView ListView_richieste_amicizia;
    private AdapterUtente adapterUtentiRichiesteAmicizia;

    View.OnClickListener listenerRichiesteAmicizia = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.click_on));
            String pulstanteOperazione = (String) v.getTag(R.string.operazione);
            Integer pulsantePosition = (Integer) v.getTag(R.string.posizione);


            Utente utente = viewModel.getRichiesteAmicizia().getValue().get(pulsantePosition);
            switch (pulstanteOperazione){
                case "accept_request":
                    new Thread(() -> {
                        try {
                            viewModel.accettaRichiestaAmicizia(utente);
                            viewModel.inviaNotificaAccettazioneRichiestaAmicizia(utente);
                            UtilsToast.stampaToast(requireActivity(),"Amicizia accettata",Toast.LENGTH_SHORT);

                        } catch (TimeoutException | JSONException e) {
                            e.printStackTrace();
                            UtilsToast.stampaToast(requireActivity(),e.getMessage(),Toast.LENGTH_SHORT);

                        }
                    }).start();

                    break;
                case "refuse_request":
                    new Thread(() -> {
                        try {
                            viewModel.rifiutaRichiestaAmicizia(utente);
                            viewModel.inviaNotificaRifiutoRichiestaAmicizia(utente);
                            UtilsToast.stampaToast(requireActivity(),"Amicizia rifiutata",Toast.LENGTH_SHORT);

                        } catch (TimeoutException | JSONException e) {
                            e.printStackTrace();
                            UtilsToast.stampaToast(requireActivity(),e.getMessage(),Toast.LENGTH_SHORT);

                        }

                    }).start();
                    break;
            }

        }
    };


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_tutte_le_richieste_amicizia, container, false);
        viewModel = new ViewModelProvider(this, new RichiesteAmiciziaViewModelFactory(requireActivity().getApplication())).get(RichiesteAmiciziaViewModel.class);

        flowInitFragment();
        initListViewUtentiRichiesteAmicizia();

        return root;
    }


    public void initViewId(){
        ListView_richieste_amicizia = root.findViewById(R.id.listView_tutte_richieste_amicizia);
    }

    public void initViewListener(){

    }

    @Override
    public void initObserver() {
        Observer<List<Utente>> observer_richieste = utenteList ->{
            System.out.println("Observer triggerato");
            adapterUtentiRichiesteAmicizia.notifyDataSetChanged();
        };

        viewModel.getRichiesteAmicizia().observe(getViewLifecycleOwner(),observer_richieste);
    }

    @Override
    public void fetchData() {
        new Thread(()->{
            try {
                viewModel.recuperaRichiesteAmicizia();
            } catch (TimeoutException | JSONException e) {
                e.printStackTrace();
                UtilsToast.stampaToast(requireActivity(),e.getMessage(),Toast.LENGTH_SHORT);
            }
        }).start();
    }

    @Override
    public void initEmptyDateVM() {
        viewModel.init();
    }

    public void initListViewUtentiRichiesteAmicizia(){
        // Lista di utenti che richiedono amicizia

        // Adapter per le richieste
        adapterUtentiRichiesteAmicizia = new AdapterUtente(getContext(), viewModel.getRichiesteAmicizia().getValue(), listenerRichiesteAmicizia);
        ListView_richieste_amicizia.setAdapter(adapterUtentiRichiesteAmicizia);

        ListView_richieste_amicizia.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("email", viewModel.getRichiesteAmicizia().getValue().get(position).getEmail());
                navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_richiesteAmiciziaFragment_to_altroUtente, bundle);

            }
        });

    }




}