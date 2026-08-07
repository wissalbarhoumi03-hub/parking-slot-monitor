package com.example.parking.view.swing;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.fixture.JButtonFixture;
import com.example.parking.model.ParkingSlot;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.example.parking.controller.ParkingController;
import java.util.Arrays;
import static org.assertj.core.api.Assertions.assertThat;
import javax.swing.DefaultListModel;
import com.example.parking.model.ParkingEvent;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.timeout;

@RunWith(GUITestRunner.class)
public class ParkingSwingViewTest extends AssertJSwingJUnitTestCase {

	private FrameFixture window;

	private ParkingSwingView parkingSwingView;
	
	@Mock
	private ParkingController parkingController;
	
	private AutoCloseable closeable;
	
	private static final int TIMEOUT = 5000;
	

	@Override
	protected void onSetUp() {
		closeable = MockitoAnnotations.openMocks(this);
		GuiActionRunner.execute(() -> {
			parkingSwingView = new ParkingSwingView();
			parkingSwingView.setParkingController(parkingController);
			return parkingSwingView;
		});
		window = new FrameFixture(robot(), parkingSwingView);
		window.show();
	}
	
	@Override
	protected void onTearDown() throws Exception {
		closeable.close();
	}

	@Test @GUITest
	public void testControlsInitialStates() {
		window.label(JLabelMatcher.withText("id"));
		window.textBox("idTextBox").requireEnabled();
		window.button(JButtonMatcher.withText("Add")).requireDisabled();
		window.list("slotList");
		window.button(JButtonMatcher.withText("Mark Occupied")).requireDisabled();
		window.button(JButtonMatcher.withText("Mark Free")).requireDisabled();
		window.label("errorMessageLabel").requireText(" ");
	}
	
	@Test
	public void testWhenIdIsNonEmptyThenAddButtonShouldBeEnabled() {
		window.textBox("idTextBox").enterText("1");
		window.button(JButtonMatcher.withText("Add")).requireEnabled();
	}
	
	@Test
	public void testWhenIdIsBlankThenAddButtonShouldBeDisabled() {
		window.textBox("idTextBox").enterText(" ");
		window.button(JButtonMatcher.withText("Add")).requireDisabled();
	}

	@Test
	public void testMarkButtonsShouldBeEnabledOnlyWhenASlotIsSelected() {
		GuiActionRunner.execute(() ->
			parkingSwingView.getListSlotsModel().addElement(new ParkingSlot("1", false)));
		window.list("slotList").selectItem(0);
		JButtonFixture markOccupiedButton =
			window.button(JButtonMatcher.withText("Mark Occupied"));
		JButtonFixture markFreeButton =
			window.button(JButtonMatcher.withText("Mark Free"));
		markOccupiedButton.requireEnabled();
		markFreeButton.requireEnabled();
		window.list("slotList").clearSelection();
		markOccupiedButton.requireDisabled();
		markFreeButton.requireDisabled();
	}
	
	@Test
	public void testShowAllSlotsShouldAddSlotsToTheList() {
		ParkingSlot slot1 = new ParkingSlot("1", false);
		ParkingSlot slot2 = new ParkingSlot("2", true);
		GuiActionRunner.execute(
			() -> parkingSwingView.showAllSlots(Arrays.asList(slot1, slot2))
		);
		String[] listContents = window.list("slotList").contents();
		assertThat(listContents)
			.containsExactly(slot1.toString(), slot2.toString());
	}
	
	@Test
	public void testShowErrorShouldShowTheMessageInTheErrorLabel() {
		ParkingSlot slot = new ParkingSlot("1", false);
		GuiActionRunner.execute(
			() -> parkingSwingView.showError("error message", slot)
		);
		window.label("errorMessageLabel")
			.requireText("error message: " + slot);
	}
	
	@Test
	public void testSlotAddedShouldAddTheSlotToTheListAndResetTheErrorLabel() {
		ParkingSlot slot = new ParkingSlot("1", false);
		parkingSwingView.slotAdded(slot);
		String[] listContents = window.list("slotList").contents();
		assertThat(listContents).containsExactly(slot.toString());
		window.label("errorMessageLabel").requireText(" ");
	}
	
	@Test
	public void testSlotUpdatedShouldReplaceTheSlotInTheListAndResetTheErrorLabel() {
		ParkingSlot slot = new ParkingSlot("1", false);
		GuiActionRunner.execute(() -> {
			DefaultListModel<ParkingSlot> model = parkingSwingView.getListSlotsModel();
			model.addElement(slot);
		});
		ParkingSlot updatedSlot = new ParkingSlot("1", true);
		GuiActionRunner.execute(
			() -> parkingSwingView.slotUpdated(updatedSlot)
		);
		String[] listContents = window.list("slotList").contents();
		assertThat(listContents).containsExactly(updatedSlot.toString());
		window.label("errorMessageLabel").requireText(" ");
	}
	
	@Test
	public void testShowCountsShouldDisplayFreeAndOccupiedCounts() {
		GuiActionRunner.execute(
			() -> parkingSwingView.showCounts(3, 2)
		);
		window.label("countsLabel")
			.requireText("Free: 3 - Occupied: 2");
	}
	
	@Test
	public void testShowHistoryShouldAddEventsToTheHistoryList() {
		ParkingEvent event1 = new ParkingEvent("1", true, "2026-01-01T10:00");
		ParkingEvent event2 = new ParkingEvent("2", false, "2026-01-01T11:00");
		GuiActionRunner.execute(
			() -> parkingSwingView.showHistory(Arrays.asList(event1, event2))
		);
		String[] listContents = window.list("historyList").contents();
		assertThat(listContents)
			.containsExactly(event1.toString(), event2.toString());
	}
	
	@Test
	public void testAddButtonShouldDelegateToControllerAddSlot() {
		window.textBox("idTextBox").enterText("1");
		window.button(JButtonMatcher.withText("Add")).click();
		verify(parkingController, timeout(TIMEOUT)).addSlot(new ParkingSlot("1", false));
	}
	
	@Test
	public void testMarkOccupiedButtonShouldDelegateToControllerMarkOccupied() {
		ParkingSlot slot = new ParkingSlot("1", false);
		GuiActionRunner.execute(
			() -> parkingSwingView.getListSlotsModel().addElement(slot));
		window.list("slotList").selectItem(0);
		window.button(JButtonMatcher.withText("Mark Occupied")).click();
		verify(parkingController, timeout(TIMEOUT)).markOccupied(eq("1"), anyString());
	}
	
	@Test
	public void testMarkFreeButtonShouldDelegateToControllerMarkFree() {
		ParkingSlot slot = new ParkingSlot("1", true);
		GuiActionRunner.execute(
			() -> parkingSwingView.getListSlotsModel().addElement(slot));
		window.list("slotList").selectItem(0);
		window.button(JButtonMatcher.withText("Mark Free")).click();
		verify(parkingController, timeout(TIMEOUT)).markFree(eq("1"), anyString());
	}
	
	@Test
	public void testSlotUpdatedShouldNotChangeSlotsWithDifferentId() {
		ParkingSlot slot = new ParkingSlot("1", false);
		GuiActionRunner.execute(() -> {
			DefaultListModel<ParkingSlot> model = parkingSwingView.getListSlotsModel();
			model.addElement(slot);
		});
		ParkingSlot otherSlot = new ParkingSlot("2", true);
		GuiActionRunner.execute(
			() -> parkingSwingView.slotUpdated(otherSlot)
		);
		String[] listContents = window.list("slotList").contents();
		assertThat(listContents).containsExactly(slot.toString());
	}
	
}