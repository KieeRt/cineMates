package gestioneSegnalazione;



import javax.swing.*;
import javax.swing.border.EmptyBorder;

import home.HomeScreen;
import model.Segnalazione;
import roundedBorder.*;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;

import static javax.swing.JOptionPane.showMessageDialog;

public class GestioneSegnalazioneScreen extends JFrame {

	private JPanel contentPane;
	private GestioneSegnalazioneViewModel viewModel;
	private TextField segnalato;
	private TextField segnalatore;
	private TextField orario;
	private JTextPane descrizione;
	private JButton pulsanteAnnulla ;
	private JButton pulsanteConferma ;
	JRadioButton radioButtonRespingi ;
	JRadioButton radioButtonCancellaContenuto ;
	JRadioButton radioButtonOscura ;
	ButtonGroup gruppoProvvedimenti;


	private int idSegnalazione;



	public GestioneSegnalazioneScreen(int idSegnalazione) {
		System.out.println("Costruttore 1 par");
		viewModel = new GestioneSegnalazioneViewModel();
		viewModel.init();
		this.idSegnalazione = idSegnalazione;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setPreferredSize(new Dimension(800,600));

		setContentPane(contentPane);

		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Gestisci segnalazione");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 25));
		lblNewLabel.setBounds(25, 27, 310, 56);
		contentPane.add(lblNewLabel);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(10, 90, 770, 2);
		contentPane.add(separator);
		
		JLabel lblNewLabel_1 = new JLabel("Utente segnalato");
		lblNewLabel_1.setBounds(25, 103, 115, 14);
		contentPane.add(lblNewLabel_1);
		
		segnalato = new TextField();
		segnalato.setEnabled(false);
		segnalato.setEditable(false);
		segnalato.setBounds(25, 123, 177, 22);
		contentPane.add(segnalato);
		
		JLabel lblNewLabel_1_1 = new JLabel("Utente segnalatore");
		lblNewLabel_1_1.setBounds(25, 155, 115, 14);
		contentPane.add(lblNewLabel_1_1);
		
		segnalatore = new TextField();
		segnalatore.setEnabled(false);
		segnalatore.setEditable(false);
		segnalatore.setBounds(25, 175, 177, 22);
		contentPane.add(segnalatore);
		
		JLabel lblNewLabel_1_2 = new JLabel("Orario segnalazione");
		lblNewLabel_1_2.setBounds(244, 103, 139, 14);
		contentPane.add(lblNewLabel_1_2);
		
		orario = new TextField();
		orario.setEnabled(false);
		orario.setEditable(false);
		orario.setBounds(244, 123, 177, 22);
		contentPane.add(orario);
		
		JLabel lblNewLabel_2 = new JLabel("Contenuto segnalato");
		lblNewLabel_2.setBounds(25, 245, 250, 20);
		contentPane.add(lblNewLabel_2);
		
		descrizione = new JTextPane();
		descrizione.setBackground(new Color(255, 255, 255));
		descrizione.setEditable(false);
		descrizione.setBounds(25, 277, 725, 149);
		contentPane.add(descrizione);
		
		radioButtonRespingi = new JRadioButton("Respingi");
		radioButtonRespingi.setBounds(25, 440, 109, 30);
		contentPane.add(radioButtonRespingi);
		
		radioButtonCancellaContenuto = new JRadioButton("Cancella contenuto");
		radioButtonCancellaContenuto.setBounds(25, 470, 147, 30);
		contentPane.add(radioButtonCancellaContenuto);
		
		radioButtonOscura = new JRadioButton("Oscura contenuto");
		radioButtonOscura.setBounds(25, 500, 139, 30);
		contentPane.add(radioButtonOscura);

		gruppoProvvedimenti = new ButtonGroup();
		gruppoProvvedimenti.add(radioButtonOscura);
		gruppoProvvedimenti.add(radioButtonCancellaContenuto);
		gruppoProvvedimenti.add(radioButtonRespingi);

		pulsanteAnnulla = new JButton("Annulla");
		pulsanteAnnulla.setBounds(550, 510, 90, 20);
		pulsanteAnnulla.setBorder(new RoundedBorder(10));
		contentPane.add(pulsanteAnnulla);
		
		pulsanteConferma = new JButton("Conferma");
		pulsanteConferma.setBounds(650, 510, 90, 20);
		pulsanteConferma.setBorder(new RoundedBorder(10));
		contentPane.add(pulsanteConferma);

		initViewListener();

		new Thread(()->{
			try {
				mostraSegnalazione();
			} catch (IOException e) {
				e.printStackTrace();
				showMessageDialog(null, "Non e' stato possibile caricare la segnalazione");
			}
		}).start();

	}

	public void initViewListener(){
		pulsanteAnnulla.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pulsanteAnnullaPremuto();
			}
		});

		pulsanteConferma.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					pulsanteConfermaPremuto();
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}
			}
		});
	}

	public void mostraSegnalazione() throws IOException {
			Segnalazione segnalazione = viewModel.getSegnalazione(idSegnalazione);
			segnalato.setText(segnalazione.getEmailUtenteSegnalato());
			segnalatore.setText(segnalazione.getEmailUtenteSegnalatore());
			orario.setText(segnalazione.getOrario());
			descrizione.setText("La lista di nome '" + segnalazione.getTitoloLista() + "' ha la seguente descrizione: " + segnalazione.getDescrizioneLista());
	}

	public String getProvvedimentoPreso(){
		String provvedimento = "nessuno";
		if(radioButtonOscura.isSelected()){
			provvedimento="oscura";
		}
		else if(radioButtonCancellaContenuto.isSelected()){
			provvedimento="cancellaLista";
		}
		else if(radioButtonRespingi.isSelected()){
			provvedimento="respingiSegnalazione";
		}

		return provvedimento;
	}



	public void apriSchermataHome(){
		HomeScreen homeScreen = new HomeScreen();
		homeScreen.setVisible(true);
	}

	public void pulsanteConfermaPremuto() throws IOException {
		String provvedimento = getProvvedimentoPreso();
		boolean esitoRichiesta = false;
		String msg ;

		if(provvedimento.equals("oscura")){
			esitoRichiesta = viewModel.oscuraLista();
		}
		else if(provvedimento.equals("cancellaLista")) {
			esitoRichiesta = viewModel.cancellaLista();
		}
		else if(provvedimento.equals("respingiSegnalazione")) {
			esitoRichiesta = viewModel.respingiSegnalazione();
		}

		if(esitoRichiesta){
			msg="Segnalazione gestita con successo";
			showMessageDialog(null,msg);
		}
		else{
			showMessageDialog(null,"Qualcosa è andato storto...");
		}

		this.dispose();
		apriSchermataHome();
	}

	public void pulsanteAnnullaPremuto(){
		this.dispose();
		apriSchermataHome();

	}


}
