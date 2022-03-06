package com.example.appandroid.ui.elencoListeAltroUtente;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.appandroid.listViewClass.listaPersonalizzata.AdapterListaPersonalizzata;
import com.example.appandroid.listViewClass.listaPersonalizzata.ListaPersonalizzata;

import org.json.JSONException;

import java.util.List;
import java.util.concurrent.TimeoutException;

public class ListeAltroUtenteFragment extends Fragment implements DefaultMethodsFragment {

    private ListeAltroUtenteViewModel viewModel;
    private View root;
    private List<ListaPersonalizzata> elencoListe;

    private String emailAltroUtente;
    private ListView listView ;
    private AdapterListaPersonalizzata adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_tutte_le_liste_altro_utente, container, false);
        getBundleArguments();

        viewModel = new ViewModelProvider(getViewModelStore(), new ListeAltroUtenteViewModelFactory(emailAltroUtente)).get(ListeAltroUtenteViewModel.class);

        flowInitFragment();

        initListView();

        return  root;
    }


    public void getBundleArguments(){
        if (getArguments() != null) {
            emailAltroUtente = getArguments().getString("email");
        }
        else{
            emailAltroUtente = "Nessuna" ;
        }
    }



    @Override
    public void initViewId(){
        listView = root.findViewById(R.id.listViewElencoListe);
    }

    @Override
    public void initViewListener(){

    }

    @Override
    public void initObserver() {
        final Observer<List<ListaPersonalizzata>> observer = new Observer<List<ListaPersonalizzata>>() {
            @Override
            public void onChanged(@Nullable final List<ListaPersonalizzata> listaAggiornata) {
                // Update the UI, in this case, a TextView.
                Log.d("AGGIORNAMENTO UI ","AGGIORNO !! Elenco liste Altro utente");
                adapter.notifyDataSetChanged();
            }
        };

        viewModel.getElencoListeAltroUtente().observe(getViewLifecycleOwner(), observer);
    }

    @Override
    public void fetchData() {
        new Thread(()->{
            try {
                viewModel.recuperaListe();
            } catch (JSONException | TimeoutException e) {
                e.printStackTrace();
                UtilsToast.stampaToast(requireActivity(),e.getMessage(), Toast.LENGTH_SHORT);
            }
        }).start();
    }

    @Override
    public void initEmptyDateVM() {
        viewModel.init();

    }


    public void initListView(){
        adapter = new AdapterListaPersonalizzata(getContext(),viewModel.getElencoListeAltroUtente().getValue(),null ,AdapterListaPersonalizzata.LISTA_ALTRO_UTENTE);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            //Logica dopo selezione di una lista dall'elenco
            Bundle bundle = new Bundle();
            ListaPersonalizzata listaPersonalizzata = viewModel.getElencoListeAltroUtente().getValue().get(position);
            bundle.putInt("idLista", listaPersonalizzata.getIdLista());
            bundle.putString("descrizione", listaPersonalizzata.getDescrizione());
            bundle.putString("nome", listaPersonalizzata.getNome());
            bundle.putBoolean("censored",listaPersonalizzata.isCensored());
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_listeAltroUtenteFragment_to_contenutoListaAltroUtente,bundle);
        });


    }




}


