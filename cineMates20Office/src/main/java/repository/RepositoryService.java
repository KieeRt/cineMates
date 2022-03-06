package repository;

import model.Notifica;
import model.Segnalazione;

import java.io.IOException;
import java.util.List;

public interface RepositoryService {

    boolean effettuaLogin(String email, String password);
    boolean effettuaLogout();
    List<Segnalazione> recuperaSegnalazioni() throws IOException;
    Segnalazione getSegnalazione(int idSegnalazione) throws IOException;
    boolean deleteSegnalazione(int idsegnalazione) throws IOException;
    boolean setListaCensored(int idlista) throws IOException;
    boolean deleteLista(int idlista) throws IOException;
    boolean addNotifica(Notifica notifica, String emailRicevente) throws IOException;
    String recuperaRegolamento();


}
