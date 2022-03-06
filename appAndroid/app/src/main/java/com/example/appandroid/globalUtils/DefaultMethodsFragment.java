package com.example.appandroid.globalUtils;

public interface DefaultMethodsFragment {
    /**
     * inizializza i riferimenti agli oggetti view
     * */
    public void initViewId();
    /**
     * inizializza i set on click listener sugli oggetti della view
     */
    public void initViewListener();

    /**
     * inizializza observer sugli oggetti di View Model
     */
    public void initObserver();

    /**
     * recupera i dati
     */
    public void fetchData();

    /**
     * inizializza gli oggetti con valori di default
     */
    public void initEmptyDateVM();

    /**
     * specifica ordine di esecuzione delle operazioni
     */
    public default void flowInitFragment(){
        initViewId();
        initViewListener();
        initEmptyDateVM();
        initObserver();
        fetchData();
    }
}
