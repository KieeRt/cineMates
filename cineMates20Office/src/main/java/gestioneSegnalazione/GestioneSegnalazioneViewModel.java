package gestioneSegnalazione;


import model.Notifica;
import model.Segnalazione;
import repository.RepositoryFactory;
import repository.RepositoryService;
import software.amazon.awssdk.regions.Region;

import java.io.IOException;

public class GestioneSegnalazioneViewModel {
	private Segnalazione segnalazione;

	private RepositoryService repositoryService;

	public void init(){
	    repositoryService = RepositoryFactory.getRepository();
	}

    public boolean oscuraLista() throws IOException {
		if(segnalazione!=null){
			boolean esitoCensura = repositoryService.setListaCensored(segnalazione.getIdListaSegnalata());
			if(esitoCensura) {
				Notifica notificaSegnalato = new Notifica("Admin", Notifica.Build_message.LISTA_OSCURATA_PER_DESTINATARIO,segnalazione.getMotivazione(),segnalazione.getTitoloLista());
				repositoryService.addNotifica(notificaSegnalato,segnalazione.getEmailUtenteSegnalato());
				Notifica notificaSegnalatore = new Notifica("Admin", Notifica.Build_message.LISTA_OSCURATA_PER_MITTENTE,segnalazione.getMotivazione(),segnalazione.getTitoloLista());
				repositoryService.addNotifica(notificaSegnalatore,segnalazione.getEmailUtenteSegnalatore());
				return repositoryService.deleteSegnalazione(segnalazione.getIdsegnalazione());
			}
		}
		return false ;
	}
    public boolean cancellaLista() throws IOException {
		if(segnalazione!=null){
			boolean esitoCensura = repositoryService.deleteLista(segnalazione.getIdListaSegnalata());
			if(esitoCensura){

				Notifica notificaSegnalato = new Notifica("Admin", Notifica.Build_message.LISTA_ELIMINATA_PER_DESTINATARIO,segnalazione.getMotivazione(),segnalazione.getTitoloLista());
				repositoryService.addNotifica(notificaSegnalato,segnalazione.getEmailUtenteSegnalato());
				Notifica notificaSegnalatore = new Notifica("Admin", Notifica.Build_message.LISTA_ELIMINATA_PER_MITTENTE,segnalazione.getMotivazione(),segnalazione.getTitoloLista());
				repositoryService.addNotifica(notificaSegnalatore,segnalazione.getEmailUtenteSegnalatore());

				return repositoryService.deleteSegnalazione(segnalazione.getIdsegnalazione());
			}
		}
		return false;
	}

    public boolean respingiSegnalazione() throws IOException {
		boolean esitoCancellazione = false;
		if(segnalazione!=null){
			esitoCancellazione = repositoryService.deleteSegnalazione(segnalazione.getIdsegnalazione());
			if(esitoCancellazione) {
				Notifica notificaSegnalatore = new Notifica("Admin", Notifica.Build_message.SEGNALAZIO_RESPINTA_PER_MITTENTE,segnalazione.getMotivazione(),segnalazione.getTitoloLista());
				repositoryService.addNotifica(notificaSegnalatore,segnalazione.getEmailUtenteSegnalatore());
			}
		}
		return esitoCancellazione;
	}

    public Segnalazione getSegnalazione(int idSegnalazione) throws IOException {
		segnalazione =  repositoryService.getSegnalazione(idSegnalazione);
		return segnalazione;
    }


	
	

}
