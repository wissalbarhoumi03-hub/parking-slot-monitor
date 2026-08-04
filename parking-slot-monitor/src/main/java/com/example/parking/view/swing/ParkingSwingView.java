package com.example.parking.view.swing;

import java.awt.EventQueue;
import java.util.List;

import javax.swing.JFrame;

import com.example.parking.model.ParkingEvent;
import com.example.parking.model.ParkingSlot;
import com.example.parking.view.ParkingView;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import java.awt.Insets;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JList;
import javax.swing.DefaultListModel;

public class ParkingSwingView extends JFrame implements ParkingView {

	private static final long serialVersionUID = 1L;
	private JTextField textField;
	private final JButton btnNewButton = new JButton("Add");
	private final JButton btnMarkOccupied = new JButton("Mark Occupied");
	private final JButton btnNewButton_1 = new JButton("Mark Free");
	private final JLabel lblNewLabel_1 = new JLabel(" ");
	private JList<ParkingSlot> listSlots;
	private DefaultListModel<ParkingSlot> listSlotsModel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ParkingSwingView frame = new ParkingSwingView();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	DefaultListModel<ParkingSlot> getListSlotsModel() {
		return listSlotsModel;
	}

	/**
	 * Create the frame.
	 */
	public ParkingSwingView() {
		setBounds(100, 100, 450, 313);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);

		JLabel lblNewLabel = new JLabel("id");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		getContentPane().add(lblNewLabel, gbc_lblNewLabel);

		textField = new JTextField();
		textField.setName("idTextBox");
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 0);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 0;
		getContentPane().add(textField, gbc_textField);
		textField.setColumns(10);
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.anchor = GridBagConstraints.WEST;
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton.gridx = 1;
		gbc_btnNewButton.gridy = 1;
		btnNewButton.setEnabled(false);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		getContentPane().add(btnNewButton, gbc_btnNewButton);
		GridBagConstraints gbc_btnMarkOccupied = new GridBagConstraints();
		gbc_btnMarkOccupied.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnMarkOccupied.insets = new Insets(0, 0, 5, 0);
		gbc_btnMarkOccupied.gridx = 1;
		gbc_btnMarkOccupied.gridy = 2;
		btnMarkOccupied.setEnabled(false);
		btnMarkOccupied.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		getContentPane().add(btnMarkOccupied, gbc_btnMarkOccupied);

		GridBagConstraints gbc_btnNewButton_1 = new GridBagConstraints();
		gbc_btnNewButton_1.anchor = GridBagConstraints.WEST;
		gbc_btnNewButton_1.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton_1.gridx = 1;
		gbc_btnNewButton_1.gridy = 3;
		btnNewButton_1.setEnabled(false);
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		getContentPane().add(btnNewButton_1, gbc_btnNewButton_1);

		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel_1.gridx = 1;
		gbc_lblNewLabel_1.gridy = 4;
		lblNewLabel_1.setName("errorMessageLabel");
		getContentPane().add(lblNewLabel_1, gbc_lblNewLabel_1);

		listSlotsModel = new DefaultListModel<>();
		listSlots = new JList<>(listSlotsModel);
		listSlots.setName("slotList");
		GridBagConstraints gbc_list = new GridBagConstraints();
		gbc_list.fill = GridBagConstraints.BOTH;
		gbc_list.gridx = 1;
		gbc_list.gridy = 5;
		getContentPane().add(listSlots, gbc_list);

	}

	@Override
	public void showAllSlots(List<ParkingSlot> slots) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showHistory(List<ParkingEvent> events) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showCounts(int free, int occupied) {
		// TODO Auto-generated method stub

	}

	@Override
	public void slotAdded(ParkingSlot slot) {
		// TODO Auto-generated method stub

	}

	@Override
	public void slotUpdated(ParkingSlot slot) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showError(String message, ParkingSlot slot) {
		// TODO Auto-generated method stub

	}

}