package com.example.parking.controller;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.ignoreStubs;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.parking.model.ParkingSlot;
import com.example.parking.repository.ParkingSlotRepository;
import com.example.parking.view.ParkingView;

public class ParkingControllerTest {

	@Mock
	private ParkingSlotRepository slotRepository;

	@Mock
	private ParkingView parkingView;

	@InjectMocks
	private ParkingController parkingController;

	private AutoCloseable closeable;

	@Before
	public void setup() {
		closeable = MockitoAnnotations.openMocks(this);
	}

	@After
	public void releaseMocks() throws Exception {
		closeable.close();
	}

	@Test
	public void testAddSlotWhenSlotDoesNotAlreadyExist() {
		ParkingSlot slot = new ParkingSlot("1", false);
		when(slotRepository.findById("1")).thenReturn(null);
		parkingController.addSlot(slot);
		InOrder inOrder = inOrder(slotRepository, parkingView);
		inOrder.verify(slotRepository).save(slot);
		inOrder.verify(parkingView).slotAdded(slot);
	}

	@Test
	public void testAddSlotWhenSlotAlreadyExists() {
		ParkingSlot slotToAdd = new ParkingSlot("1", false);
		ParkingSlot existingSlot = new ParkingSlot("1", true);
		when(slotRepository.findById("1")).thenReturn(existingSlot);
		parkingController.addSlot(slotToAdd);
		verify(parkingView)
			.showError("Already existing slot with id 1", existingSlot);
		verifyNoMoreInteractions(ignoreStubs(slotRepository));
	}

}
