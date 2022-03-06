package home;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import  gestioneSegnalazione.GestioneSegnalazioneScreen;
import login.LoginScreen;
import model.Segnalazione;
import regolamento.RegolamentoScreen;
import roundedBorder.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import utils.ResizeImage;
import static javax.swing.JOptionPane.showMessageDialog;


public class HomeScreen extends JFrame {

	private JPanel contentPane;
	private JTable listaSegnalazioni;
	private JButton buttonLogout;
	private JButton buttonRegolamento;
	private Button buttonEffettuaRicercaSegnalazione;
	private TextField campoRicercaSegnalazioni ;
	HomeViewModel homeViewModel;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					HomeScreen frame = new HomeScreen();
					frame.setVisible(true);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public HomeScreen() {
		setResizable(false);
		setBackground(new Color(255, 255, 255));
		homeViewModel = new HomeViewModel();
		homeViewModel.init();

		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1000, 701);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(255, 255, 255));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		Panel panel = new Panel();
		panel.setBackground(new Color(245, 245, 245));
		panel.setBounds(0, 0, 112, 672);
		contentPane.add(panel);
		panel.setLayout(null);

		try {
			BufferedImage myPicture = ImageIO.read(new File("src/main/resources/logo.png"));
			BufferedImage resizePicture = ResizeImage.resize(myPicture,myPicture.getWidth()/6,myPicture.getHeight()/6);

			JLabel picLabel = new JLabel(new ImageIcon(resizePicture));

			picLabel.setBounds(0, 0, resizePicture.getWidth(), resizePicture.getHeight());
			panel.add(picLabel);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		buttonLogout = new JButton("Logout");

		buttonLogout.setBounds(10, 599, 89, 23);
		buttonLogout.setBorder(new RoundedBorder(10));
		panel.add(buttonLogout);
		
		Panel panel_1 = new Panel();
		panel_1.setBackground(new Color(245, 245, 245));
		panel_1.setBounds(110, 0, 884, 72);
		contentPane.add(panel_1);
		panel_1.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Gestione delle segnalazioni");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 40));
		lblNewLabel.setBounds(10, 5, 620, 50);
		panel_1.add(lblNewLabel);
		
		buttonRegolamento = new JButton("Consulta il regolamento");

		buttonRegolamento.setBounds(660, 21, 180, 25);
		buttonRegolamento.setBorder(new RoundedBorder(10));

	

		panel_1.add(buttonRegolamento);
		
		Panel panel_2 = new Panel();
		panel_2.setBackground(new Color(220, 220, 220));
		panel_2.setBounds(112, 69, 882, 42);
		contentPane.add(panel_2);
		panel_2.setLayout(null);
		
		buttonEffettuaRicercaSegnalazione = new Button("Cerca");
		buttonEffettuaRicercaSegnalazione.setBounds(798, 10, 60, 22);


		panel_2.add(buttonEffettuaRicercaSegnalazione);
		
		campoRicercaSegnalazioni = new TextField();
		campoRicercaSegnalazioni.setBounds(638, 10, 154, 22);

		panel_2.add(campoRicercaSegnalazioni);
		
		JLabel lblNewLabel_1 = new JLabel("Tutte le segnalazioni");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblNewLabel_1.setBounds(10, 0, 223, 37);
		panel_2.add(lblNewLabel_1);

		
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(113, 111, 872, 561);
		contentPane.add(scrollPane);
		
		listaSegnalazioni = new JTable();
		scrollPane.setViewportView(listaSegnalazioni);
		listaSegnalazioni.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listaSegnalazioni.setRowHeight(30);




		initViewListener();

		new Thread(()->{
			try {
				visualizzaSegnalazioni();
			} catch (IOException e) {
				popolaTabella(new ArrayList<>());
				showMessageDialog(null, "Non e' stato possibile caricare le segnalazioni");
				e.printStackTrace();
			}
		}).start();

	}

	public void visualizzaSegnalazioni() throws IOException {
		List<Segnalazione> listaSegnalazioni = homeViewModel.recuperaSegnalazioni();
		popolaTabella(listaSegnalazioni);
	}

	public void initViewListener(){


		buttonLogout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logoutPremuto();
			}
		});

		buttonRegolamento.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				regolamentoPremuto();
			}
		});

		listaSegnalazioni.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent event) {
				if(event.getValueIsAdjusting()){
					segnalazionePremuta();
				}
			}
		});

		buttonEffettuaRicercaSegnalazione.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				effettuaRicercaPremuto();
			}
		});


	}

	public void effettuaRicercaPremuto() {
		String query = getQueryRicerca();
		List<Segnalazione> segnalazioniFiltrate = homeViewModel.recuperaSegnalazioniConQuery(query);
		updateUI(segnalazioniFiltrate);
	}

	public void regolamentoPremuto() {
		apriSchermataRegolamento();
	}

	public void logoutPremuto() {
		homeViewModel.logout();
		this.dispose();
		apriSchermataLogin();
	}

	public void segnalazionePremuta() {
		this.dispose();
		apriSchermataGestioneSegnalazione();
	}


	public void updateUI(List<Segnalazione> listaAggiornata){
		if(listaAggiornata.isEmpty()){
			showMessageDialog(null, "Nessun risultato recuperato");
		}
		popolaTabella(listaAggiornata);
	}

	public String getQueryRicerca(){
		return campoRicercaSegnalazioni.getText();
	}



	public void apriSchermataGestioneSegnalazione(){
		System.out.println("trigger");
		// do some actions here, for example
		// print first column value from selected row
		int codiceSegnalazione = (int)listaSegnalazioni.getValueAt(listaSegnalazioni.getSelectedRow(), 5);
		GestioneSegnalazioneScreen gestioneSegnalazioneScreen = new GestioneSegnalazioneScreen(codiceSegnalazione);
		gestioneSegnalazioneScreen.setVisible(true);
	}

	public void apriSchermataRegolamento(){
		RegolamentoScreen regolamentoScreen = new RegolamentoScreen();
		regolamentoScreen.setVisible(true);
	}

	public void apriSchermataLogin(){

		LoginScreen loginScreen = new LoginScreen();
		loginScreen.setVisible(true);
	}

	public void popolaTabella(List<Segnalazione> list){
		Object[][] obj = new Object[list.size()][6];
		for(int i = 0; i < list.size(); i++){
			obj[i][0] = list.get(i).getMotivazione();
			obj[i][1] = list.get(i).getEmailUtenteSegnalatore();
			obj[i][2] = list.get(i).getEmailUtenteSegnalato();
			obj[i][3] = list.get(i).getIdListaSegnalata();
			obj[i][4] = list.get(i).getOrario();
			obj[i][5] = list.get(i).getIdsegnalazione();
		}
		DefaultTableModel defaultTableModel = new DefaultTableModel(
				obj,
				new String[] {
						"Contenuto", "Utente segnalatore","Utente segnalato","Lista segnalata", "Orario", "Codice segnalazione"
				}
		);

		listaSegnalazioni.setModel(defaultTableModel);
	}







}
