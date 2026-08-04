package com.example.parking.view.swing;

import java.awt.EventQueue;
import java.util.List;

import javax.swing.JFrame;

import com.example.parking.model.ParkingEvent;
import com.example.parking.model.ParkingSlot;
import com.example.parking.view.ParkingView;

public class ParkingSwingView extends JFrame implements ParkingView {

	private static final long serialVersionUID = 1L;

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

	/**
	 * Create the frame.
	 */
	public ParkingSwingView() {
		setBounds(100, 100, 450, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
