package home;

import model.Segnalazione;
import repository.RepositoryFactory;
import repository.RepositoryService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomeViewModel {
    private List<Segnalazione> listaSegnalazioni;
    private RepositoryService repositoryService;

    public void init(){
        repositoryService = RepositoryFactory.getRepository();
    }

    public List<Segnalazione> recuperaSegnalazioni() throws IOException {
        listaSegnalazioni =  repositoryService.recuperaSegnalazioni();
        return listaSegnalazioni;
    }

    public List<Segnalazione> recuperaSegnalazioniConQuery(String query){
        if(listaSegnalazioni!=null && !listaSegnalazioni.isEmpty()){
            return filtraLista(query);
        }
        else return new ArrayList<>();
    }

    public List<Segnalazione> filtraLista(String query){
        List<Segnalazione> tmp = new ArrayList<>();
        for(Segnalazione segnalazione : listaSegnalazioni){
            String idSegnalazione = ""+segnalazione.getIdsegnalazione();
            if(idSegnalazione.contains(query)){
                tmp.add(segnalazione);
            }
        }
        return tmp;
    }

    public boolean logout(){
        return repositoryService.effettuaLogout();
    }

}
