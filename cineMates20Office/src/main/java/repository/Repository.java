package repository;


import CognitoApi.CognitoApi;
import apiGateway.ApiGateway;
import model.Notifica;
import model.Segnalazione;

import java.io.IOException;
import java.util.List;

public class Repository implements  RepositoryService{
	private static Repository repository;
	private final CognitoApi cognitoApi;
	private final ApiGateway apiGateway;


    private Repository() {
        cognitoApi = CognitoApi.getIstance();
        apiGateway = ApiGateway.getIstance();
    }

    public static Repository getIstance(){
        if( repository == null)
            repository = new Repository();
        return repository;
    }


    @Override
    public boolean effettuaLogin(String email, String password) {
        return cognitoApi.effettuaLogin(email, password);
    }

    @Override
    public boolean effettuaLogout() {
        return cognitoApi.logout();
    }

    @Override
    public List<Segnalazione> recuperaSegnalazioni() throws IOException {
        return apiGateway.recuperaSegnalazioni();
    }

    @Override
    public Segnalazione getSegnalazione(int idSegnalazione) throws IOException {
        return apiGateway.getSegnalazione(idSegnalazione);
    }

    @Override
    public boolean deleteSegnalazione(int idsegnalazione) throws IOException {
        return apiGateway.deleteSegnalazione(idsegnalazione);
    }

    @Override
    public boolean setListaCensored(int idlista) throws IOException {
        return apiGateway.setListaCensored(idlista);
    }

    @Override
    public boolean deleteLista(int idlista) throws IOException {
        return apiGateway.deleteLista(idlista);
    }

    @Override
    public boolean addNotifica(Notifica notifica, String emailRicevente) throws IOException {
        return apiGateway.addNotifica(notifica,emailRicevente);
    }




    @Override
    public String recuperaRegolamento() {
        return "Gli utenti possono effettuare delle segnalazione di liste, le cause della segnalazione sono esclusivamente legati ai contenuti Spoiler, violenti o ripugnanti e cotenuti offensivi od Oltraggiosi.\r\n"
                + "\r\n"
                + "Un amministratore deve gestire le segnalazione, selezionando tra le tre opzioni: Cancellare il contenuto segnalato, in questo caso la lista segnalata verra' eliminata, Oscurare il contenuto, in questo caso il Titolo e la descrizione della lista verranno oscurati in modo che non siano leggibili, Respingere la segnalazione, in questo caso la lista segnalata non sara' modificata in alcun modo.\r\n"
                + "\r\n"
                + "Al fine di rendere la minima l'intrepretazione, di seguito verranno fornite le spiegazioni e gli esempi per ogni tipo di segnalazione.\r\n"
                + "\r\n"
                + "Spoiler\r\n"
                + "Descrizione: Un contenuto e' considerato spoiler nel caso in cui contenga alcune informazioni riguardanti gli avvenimenti contenuti nel film.\r\n"
                + "Descrizione di una scena o descrizione del finale del film, anche sottoforma implicita.\r\n"
                + "Esempio: \"Nella scena in cui George ruba il porfoglio si vede che il portafoglio non e' del epoca.... \"\r\n"
                + "Come gestire: In genere Spoiler sono da oscurare ma in casi in cui e' evidente che la lista e' stata create ai fini di spoilerare si puo' anche eliminare la lista stessa.\r\n"
                + "\r\n"
                + "Contenuti violenti o ripugnanti\r\n"
                + "Descrizione: Incitamento di altri a commettere atti di violenza contro individui o un gruppo definito di persone.\r\n"
                + "Contenuti che riguardano incidenti stradali, disastri naturali, conseguenze di guerre o attacchi terroristici, risse di strada, attacchi fisici, violenze sessuali, sacrifici, torture, cadaveri, proteste o rivolte, rapine, procedure medicali o altri scenari simili, pubblicati allo scopo di sconvolgere o disgustare gli spettatori\r\n"
                + "Esempio: \"Nel giorno xx/xx/xxxx invito tutti ad uscire incontrarsi nel posto \"xxxx\" per vendicare accaduto \"xxxx\".\r\n"
                + "Come gestire: In genere questo tipo di contenuto deve essere cancellato\r\n"
                + "\r\n"
                + "Contenuti offensivi od oltraggiosi\r\n"
                + "Descrizione: Titoli o descrizioni che contengono le forme di offessa personale o diretta ad un gruppo di personne\r\n"
                + "Esempio: \" il popolo \"XXX\" non e' degno di essere rappresentato in questo film \"\r\n"
                + "Come gestire: In gere questo tpo di contenuto deve essere cancellato.\r\n"
                + "\r\n"
                + "Note\r\n"
                + "Si tenga presente che un utente puo' segnalare anche i contenuti che rispettano tutti i requisiti della community, quidi indipendentemente dal tipo di segnalazione che arriva amministratore deve analizzare il contenuto con estrema accuratezza al fine di prendere un giusta scelta.\r\n"
                + "";
    }


}
