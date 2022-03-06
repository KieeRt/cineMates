package login;

import java.awt.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import apiGateway.CineMatesClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import  home.HomeScreen;
import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Header;
import utils.ResizeImage;


import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static javax.swing.JOptionPane.showMessageDialog;

public class LoginScreen extends JFrame {

	private JPanel contentPane;
	private JTextField emailField;
	private JPasswordField passwordField;
	private JLabel lblNewLabel;
	private JLabel lblNewLabel_1;
	private JButton pulstanteLogin;
	private static LoginScreen frame;
	LoginViewModel viewModel;


	



	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				frame = new LoginScreen();
				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		//foo();
	}
	
	

	
	/**
	 * Create the frame.
	 */
	public LoginScreen() {
		viewModel = new LoginViewModel();
		viewModel.init();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1003, 703);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);


		try {
			BufferedImage myPicture = ImageIO.read(new File("src/main/resources/logo.png"));
			BufferedImage resizePicture = ResizeImage.resize(myPicture,myPicture.getWidth()/3,myPicture.getHeight()/3);

			JLabel picLabel = new JLabel(new ImageIcon(resizePicture));

			picLabel.setBounds(400, 100, resizePicture.getWidth(), resizePicture.getHeight());
			add(picLabel);
		} catch (IOException e) {
			e.printStackTrace();
		}


		lblNewLabel = new JLabel("Codice spaciale");
		lblNewLabel.setBounds(350, 270, 120, 30);
		contentPane.add(lblNewLabel);
		
		emailField = new JTextField();
		emailField.setBounds(350, 300, 300, 40);
		contentPane.add(emailField);
		emailField.setColumns(10);
		
		lblNewLabel_1 = new JLabel("Password");
		lblNewLabel_1.setBounds(350, 350, 133, 30);
		contentPane.add(lblNewLabel_1);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(350, 375, 300, 40);
		contentPane.add(passwordField);

		pulstanteLogin = new JButton("Login");
		pulstanteLogin.setBounds(425, 450, 150, 50);
		contentPane.add(pulstanteLogin);

		initClickListener();
	}




	public void initClickListener(){
		pulstanteLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pulsanteLoginPremuto();
			}
		});

	}

	public void pulsanteLoginPremuto(){
		if(viewModel.checkCampiValidi(emailField.getText(), new String(passwordField.getPassword()))) {
			if (viewModel.efettuaLogin(emailField.getText(), new String(passwordField.getPassword()))) {
				this.dispose();
				apriSchermataHome();
			} else {
				showMessageDialog(null, "Codice Identificativo o Password non sono corretti");
			}
		}
		else{
			showMessageDialog(null, "Codice Identificativo o Password non sono corretti ");
		}
	}

	public void apriSchermataHome(){
		HomeScreen homeScreen = new HomeScreen();
		homeScreen.setVisible(true);
	}




}
