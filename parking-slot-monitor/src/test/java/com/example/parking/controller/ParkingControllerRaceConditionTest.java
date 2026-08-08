package com.example.parking.controller;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.parking.model.ParkingSlot;
import com.example.parking.repository.ParkingSlotRepository;
import com.example.parking.repository.ParkingEventRepository;
import com.example.parking.view.ParkingView;

public class ParkingControllerRaceConditionTest {

	@Mock
	private ParkingSlotRepository slotRepository;

	@Mock
	private ParkingEventRepository eventRepository;

	@Mock
	private ParkingView parkingView;

	@InjectMocks
	private ParkingController parkingController;

	private AutoCloseable closeable;

	@Before
	public void setUp() {
		closeable = MockitoAnnotations.openMocks(this);
	}

	@After
	public void releaseMocks() throws Exception {
		closeable.close();
	}

	@Test
	public void testAddSlotConcurrent() {
		List<ParkingSlot> slots = new ArrayList<>();
		ParkingSlot slot = new ParkingSlot("1", false);
		// stub the ParkingSlotRepository
		when(slotRepository.findById(anyString()))
			.thenAnswer(invocation -> slots.stream()
				.findFirst().orElse(null));
		doAnswer(invocation -> {
			slots.add(slot);
			return null;
		}).when(slotRepository).save(any(ParkingSlot.class));
		// start the threads calling addSlot concurrently
		List<Thread> threads = IntStream.range(0, 10)
			.mapToObj(i -> new Thread(() -> parkingController.addSlot(slot)))
			.peek(t -> t.start())
			.collect(Collectors.toList());
		// wait for all the threads to finish
		await().atMost(10, SECONDS)
			.until(() -> threads.stream().noneMatch(t -> t.isAlive()));
		// there should be a single element in the list
		assertThat(slots)
			.containsExactly(slot);
	}
}