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

@RunWith(GUITestRunner.class)
public class ParkingSwingViewTest extends AssertJSwingJUnitTestCase {

	private FrameFixture window;

	private ParkingSwingView parkingSwingView;

	@Override
	protected void onSetUp() {
		GuiActionRunner.execute(() -> {
			parkingSwingView = new ParkingSwingView();
			return parkingSwingView;
		});
		window = new FrameFixture(robot(), parkingSwingView);
		window.show(); // shows the frame to test
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
	
}