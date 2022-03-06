package model;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;
import java.util.Objects;

public class Segnalazione {

    private String motivazione;
    private int idsegnalazione;
    @SerializedName("idlista")
    private int idListaSegnalata;
    @SerializedName("segnalatore")
    private String emailUtenteSegnalatore;
    @SerializedName("segnalato")
    private String emailUtenteSegnalato;
    @SerializedName("titolo")
    private String titoloLista;
    @SerializedName("descrizione")
    private String descrizioneLista;



    @SerializedName("orario")
    private String orario;

    public Segnalazione(String motivazione, int idsegnalazione, int idListaSegnalata, String emailUtenteSegnalatore, String emailUtenteSegnalato, String orario, String titolo, String descrizione){
        this.motivazione = motivazione;
        this.idsegnalazione = idsegnalazione;
        this.idListaSegnalata = idListaSegnalata;
        this.emailUtenteSegnalatore = emailUtenteSegnalatore;
        this.emailUtenteSegnalato = emailUtenteSegnalato;
        this.orario = orario;
        this.titoloLista = titolo;
        this.descrizioneLista = descrizione;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Segnalazione)) return false;
        Segnalazione that = (Segnalazione) o;
        return idsegnalazione == that.idsegnalazione;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idsegnalazione);
    }

    public String getMotivazione() {
        return motivazione;
    }

    public int getIdsegnalazione() {
        return idsegnalazione;
    }

    public int getIdListaSegnalata() {
        return idListaSegnalata;
    }

    public String getEmailUtenteSegnalatore() {
        return emailUtenteSegnalatore;
    }

    public String getEmailUtenteSegnalato() {
        return emailUtenteSegnalato;
    }

    public String getOrario() {
        return orario;
    }

    public String getTitoloLista() {
        return titoloLista;
    }

    public String getDescrizioneLista() {
        return descrizioneLista;
    }

}
