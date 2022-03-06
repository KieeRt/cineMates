package com.example.appandroid.ui.elencoListePropie;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.appandroid.R;
import com.example.appandroid.globalUtils.AlertDialogUtils;
import com.example.appandroid.globalUtils.DefaultMethodsFragment;
import com.example.appandroid.globalUtils.UtilsToast;
import com.example.appandroid.listViewClass.listaPersonalizzata.AdapterListaPersonalizzata;
import com.example.appandroid.listViewClass.listaPersonalizzata.ListaPersonalizzata;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;

import java.util.List;
import java.util.concurrent.TimeoutException;

public class ElencoListeProprieFragment extends Fragment implements DefaultMethodsFragment {

    private View root ;
    private ElencoListeProprieViewModel viewModel;


    private ListView listView ;
    private FloatingActionButton floatingActionButton;
    private AdapterListaPersonalizzata adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_elenco_liste_proprie, container, false);
        viewModel = new ViewModelProvider(this).get(ElencoListeProprieViewModel.class);

        flowInitFragment();

        initListView();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void initViewId(){
        listView = root.findViewById(R.id.listViewSchermataTutteListe);
        floatingActionButton = root.findViewById(R.id.pulsanteAddInLista);

    }

    @Override
    public void initViewListener(){
        floatingActionButton.setOnClickListener(this::mostraAlertDialogCreaNuovaLista);
    }

    @Override
    public void initObserver() {
        final Observer<List<ListaPersonalizzata>> observer = new Observer<List<ListaPersonalizzata>>() {
            @Override
            public void onChanged(@Nullable final List<ListaPersonalizzata> listaAggiornata) {
                // Update the UI, in this case, a TextView.
                Log.d("AGGIORNAMENTO UI ","AGGIORNO !! Elenco liste proprie");

                adapter.notifyDataSetChanged();
            }
        };


        viewModel.getListe().observe(getViewLifecycleOwner(), observer);
    }

    @Override
    public void fetchData() {
        new Thread(()->{
            try {
                viewModel.recuperaElencoListe();
            } catch (JSONException | TimeoutException e) {
                e.printStackTrace();
                UtilsToast.stampaToast(requireActivity(),e.getMessage(),Toast.LENGTH_SHORT);
            }
        }).start();
    }

    @Override
    public void initEmptyDateVM() {
        viewModel.init();
    }


    public void initListView(){
        View.OnClickListener listner = generaListenerPerIconaMenu();

        adapter = new AdapterListaPersonalizzata(getContext(), viewModel.getListe().getValue(), listner,AdapterListaPersonalizzata.LISTA_PROPRIA);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Logica dopo selezione di una lista dall'elenco
                Bundle bundle = new Bundle();
                ListaPersonalizzata listaPersonalizzata = viewModel.getListe().getValue().get(position);
                bundle.putInt("idLista", listaPersonalizzata.getIdLista());
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.moveToContenutoLista,bundle);
            }
        });


    }


    public View.OnClickListener generaListenerPerIconaMenu(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getContext()!=null) {
                    PopupMenu popup = new PopupMenu(getContext(), v);
                    popup.inflate(R.menu.menu_opzioni_lista_propria);
                    popup.show();
                    popup.setOnMenuItemClickListener(generaListenerPerPopup(v));
                }
            }
        };

    }


    public PopupMenu.OnMenuItemClickListener generaListenerPerPopup(View listaAssociataAlPopup){
        return new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemCliccato = item.getItemId();
                if(itemCliccato == R.id.menuEliminaLista ){
                    //Todo : logica eliminazione lista
                    //Dovrebbe eliminare la lista e mostrare conferma

                    //L'oggetto View contiene la posizione della lista nell'elenco
                    int pos = (int)listaAssociataAlPopup.getTag();

                    //Per recuperare la lista da eliminare
                    ListaPersonalizzata listaInteressata = viewModel.getListe().getValue().get(pos);

                    new Thread(()->{
                        try {
                            viewModel.removeLista(listaInteressata.getIdLista());
                            UtilsToast.stampaToast(requireActivity(),"La Lista "+listaInteressata.getNome()+" è stata eliminata con successo",Toast.LENGTH_SHORT);
                        } catch (TimeoutException | JSONException e) {
                            e.printStackTrace();
                            UtilsToast.stampaToast(requireActivity(),e.getMessage(),Toast.LENGTH_SHORT);
                        }
                    }).start();

                }
                return false;
            }
        };
    }


    public void mostraAlertDialogCreaNuovaLista(View view){
        AlertDialogUtils costruttore;
        if(getContext()!=null)
             costruttore = new AlertDialogUtils(getContext(),R.layout.tendina_crea_nuova_lista);
        else{
            return;
        }
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
                        viewModel.addLista(nuovoTitolo,nuovaDescrizione);
                        UtilsToast.stampaToast(requireActivity(),"Lista "+nuovoTitolo+" è stata creata con successo",Toast.LENGTH_SHORT); }
                    catch (TimeoutException | JSONException e) {
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

}