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

}