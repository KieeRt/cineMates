## Descrizione concentuale
CineMates20 è una piattaforma per gli appassionati di cinema, la soluzione è composta da due client, client mobile (Android) destinato agli utenti e client Desktop destinato agli amministatori.
#### Le principali funzionalità del client mobile (Android):
* Registrarsi/entrate con email, account Facebook e account Google.
* Creare liste personalizzate di film, con il titolo e la descrizione.
* Cercare/visualizzare/aggiungere alle proprie liste i film.
* Cercare/visualizzare/aggiungere altri utenti, con le loro liste ed eventuali film in comune.
* Segnalare le liste, in caso questi violano le normative della community.

#### Le principali funzionalità del client Desktop:
* Entrare nel sistema con credenziali fornite dal tecnico.
* Visualizzare e gestire le segnalazioni effettuati dai utenti.


## Descrizione tecnica
Servizio viene fornito sviluppando due client in Java, e come una altrernativa al server, viene implementata una soluzione serverless grazie al utilizzo di 
questi sistemi cloud (per documentazione dettagliata consultate file "documentazione.pdf"):
* AWS cloud: Cognito, API Gateway, Lambda (utilizzando Python 3.7), Simple Storage Service (S3), Relational Database Postgress (RDB) e CloudWatch.
* Altri: Google sing-in, Facebook sing-in e OMDb API.

Di seguito viene fornita una panoramica dell'architettura esterna:
![image](https://user-images.githubusercontent.com/44137092/156940647-4d389681-07fd-4b84-a9ac-06c7905ca461.png)

Internamente per entrambi client viene utilizzato il pattern MVVM (Model-View-ViewModel):
![image](https://user-images.githubusercontent.com/44137092/156940733-8f4d72d5-3fb4-4479-a571-f1e910ebdef1.png)

## Screenshot
Applicazione mobile:
![image](https://user-images.githubusercontent.com/44137092/156940793-dfcf4641-1a4f-444e-8bfe-41b1394cc1e4.png)

Applicazione desktop:
![image](https://user-images.githubusercontent.com/44137092/156940810-5fc31a91-69c0-4656-be57-1f5ae07ea8b8.png)
