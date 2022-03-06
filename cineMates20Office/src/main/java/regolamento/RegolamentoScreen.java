package regolamento;


import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextPane;

public class RegolamentoScreen extends JFrame {

	private JPanel contentPane;
	private RegolamentoViewModel viewModel ;
	JTextPane fieldRegolamento ;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RegolamentoScreen frame = new RegolamentoScreen();
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
	public RegolamentoScreen() {
		viewModel = new RegolamentoViewModel();
		viewModel.init();

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 1000, 715);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Regolamento");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 25));
		lblNewLabel.setBounds(29, 24, 175, 58);
		contentPane.add(lblNewLabel);

		fieldRegolamento = new JTextPane();
		fieldRegolamento.setEditable(false);
		fieldRegolamento.setBounds(29, 78, 919, 561);
		contentPane.add(fieldRegolamento);

		visualizzaRegolamento();
	}

	public void visualizzaRegolamento(){
		String regolamento = viewModel.recuperaRegolamento();
		fieldRegolamento.setText(regolamento);
	}
}
