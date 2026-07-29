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

import com.example.parking.model.ParkingEvent;
import com.example.parking.repository.ParkingEventRepository;

public class ParkingControllerTest {

	@Mock
	private ParkingSlotRepository slotRepository;

	@Mock
	private ParkingView parkingView;
	
	@Mock
	private ParkingEventRepository eventRepository;

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
	
	@Test
	public void testMarkOccupiedWhenSlotExists() {
		ParkingSlot existingSlot = new ParkingSlot("1", false);
		when(slotRepository.findById("1")).thenReturn(existingSlot);
		ParkingSlot occupiedSlot = new ParkingSlot("1", true);
		ParkingEvent event = new ParkingEvent("1", true, "2026-01-01T10:00:00");
		parkingController.markOccupied("1", "2026-01-01T10:00:00");
		InOrder inOrder = inOrder(slotRepository, eventRepository, parkingView);
		inOrder.verify(slotRepository).delete("1");
		inOrder.verify(slotRepository).save(occupiedSlot);
		inOrder.verify(eventRepository).save(event);
		inOrder.verify(parkingView).slotUpdated(occupiedSlot);
	}

	@Test
	public void testMarkOccupiedWhenSlotDoesNotExist() {
		when(slotRepository.findById("1")).thenReturn(null);
		parkingController.markOccupied("1", "2026-01-01T10:00:00");
		verify(parkingView)
			.showError("No existing slot with id 1", null);
		verifyNoMoreInteractions(ignoreStubs(slotRepository));
	}
	
	@Test
	public void testMarkFreeWhenSlotExists() {
		ParkingSlot existingSlot = new ParkingSlot("1", true);
		when(slotRepository.findById("1")).thenReturn(existingSlot);
		ParkingSlot freeSlot = new ParkingSlot("1", false);
		ParkingEvent event = new ParkingEvent("1", false, "2026-01-01T10:00:00");
		parkingController.markFree("1", "2026-01-01T10:00:00");
		InOrder inOrder = inOrder(slotRepository, eventRepository, parkingView);
		inOrder.verify(slotRepository).delete("1");
		inOrder.verify(slotRepository).save(freeSlot);
		inOrder.verify(eventRepository).save(event);
		inOrder.verify(parkingView).slotUpdated(freeSlot);
	}

	@Test
	public void testMarkFreeWhenSlotDoesNotExist() {
		when(slotRepository.findById("1")).thenReturn(null);
		parkingController.markFree("1", "2026-01-01T10:00:00");
		verify(parkingView)
			.showError("No existing slot with id 1", null);
		verifyNoMoreInteractions(ignoreStubs(slotRepository));
	}
	
	@Test
	public void testAllSlots() {
		java.util.List<ParkingSlot> slots = asList(new ParkingSlot("1", false));
		when(slotRepository.findAll()).thenReturn(slots);
		parkingController.allSlots();
		verify(parkingView).showAllSlots(slots);
	}
	
	@Test
	public void testShowHistory() {
		java.util.List<ParkingEvent> events =
			asList(new ParkingEvent("1", true, "2026-01-01T10:00:00"));
		when(eventRepository.findAll()).thenReturn(events);
		parkingController.showHistory();
		verify(parkingView).showHistory(events);
	}
	
	@Test
	public void testCountSlots() {
		java.util.List<ParkingSlot> slots = asList(
			new ParkingSlot("1", false),
			new ParkingSlot("2", true),
			new ParkingSlot("3", false));
		when(slotRepository.findAll()).thenReturn(slots);
		parkingController.countSlots();
		verify(parkingView).showCounts(2, 1);
	}

}
