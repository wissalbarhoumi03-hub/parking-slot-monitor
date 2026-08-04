package com.example.parking.view.swing;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

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

	@Test
	public void test() {
		// just to check the setup works
	}

}