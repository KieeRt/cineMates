package model;
import java.util.Random;

public class Notifica {
    private String messaggio;
    private final int idNotifica ;



    /**
     * Utilizzare nel caso di costruzione standart del messaggio, fa riferimento a {@link Build_message}
     * @param username_soggetto il soggeto della frase che compie azione
     * @param tipo_notifica addatta il testo in base al tipo di notifica, controllare  {@link Build_message}
     */
    public Notifica(String username_soggetto, int tipo_notifica, String motivo, String titoloLista) {
        // TODO: risolvere il dilema con immagine notifica
        this.messaggio = new Build_message(tipo_notifica, username_soggetto, motivo, titoloLista).getMessaggio();
        this.idNotifica = (int)( System.nanoTime() ^ new Random().nextInt() );
    }
    /**
     *Utilizzare quando il messaggio e' stato gia' costruito
     *
     */
    public Notifica(int idNotifica, String messaggio) {
        // TODO: risolvere il dilema con immagine notifica
        this.messaggio = messaggio;
        this.idNotifica = idNotifica;
    }



    public int getIdNotifica() {
        return idNotifica;
    }

    public String getMessaggio() {
        return messaggio;
    }

    public void setMessaggio(String messaggio){
        this.messaggio = messaggio;
    }




    public static class Build_message {
        public int tipo_notifica;
        public String soggetto;
        public String motivo;
        public String titolo;
        public String descrizione;
        public static final int LISTA_ELIMINATA_PER_MITTENTE = 3;
        public static final int LISTA_OSCURATA_PER_MITTENTE = 4;
        public static final int SEGNALAZIO_RESPINTA_PER_MITTENTE = 5;
        public static final int LISTA_ELIMINATA_PER_DESTINATARIO = 6;
        public static final int LISTA_OSCURATA_PER_DESTINATARIO = 7;




        public String messaggio;

        public Build_message(int tipo_notifica, String username_soggetto, String motivo, String titolo) throws IllegalArgumentException {
            if(tipo_notifica < 3 || tipo_notifica > 7){
                throw new IllegalArgumentException();
            }
            this.tipo_notifica = tipo_notifica;
            this.soggetto = username_soggetto;
            this.motivo = motivo;
            this.titolo = titolo;
            set_messaggio();
        }

        private void set_messaggio(){
            if(tipo_notifica == LISTA_ELIMINATA_PER_MITTENTE){
                messaggio =  "Una tua segnalazione ha portato all'eliminazione della lista, grazie per la collaborazione.";
            }
            if(tipo_notifica == LISTA_OSCURATA_PER_MITTENTE){
                messaggio = "Una tua segnalazione ha portato all'oscuramento della lista, grazie per la collaborazione.";
            }
            if(tipo_notifica == SEGNALAZIO_RESPINTA_PER_MITTENTE){
                messaggio = "La tua segnalazione sulla lista ' " + titolo + " ' è stata rispinta.";
            }
            if(tipo_notifica == LISTA_ELIMINATA_PER_DESTINATARIO){
                messaggio =  "Una tua lista è stata segnalata per i motivi '" +motivo+ "' e a seguito del controllo è stata eliminata";
            }
            if( tipo_notifica == LISTA_OSCURATA_PER_DESTINATARIO){
                messaggio = "Una tua lista è stata segnalata per i motivi '" +motivo+ "' e a seguito del controllo è stata oscurata";

            }
        }

        public String getMessaggio() {
            return messaggio;
        }
    }


}
