package drawbotV3_2;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.Timer;

import drawbotV3_2.BotController;
import drawbotV3_2.DrawBot;
import drawbotV3_2.DrawPointProvider.NudgeMode;
import drawbotV3_2.KeyHandling;
import drawbotV3_2.MotorInstructions;
import drawbotV3_2.Settings;
import drawbotV3_2.TestClient;

public class TestClient extends JFrame implements KeyListener, KeyHandling, ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final DrawBot drawBot = new DrawBot();
	private BotController botController;
	public boolean autoDraw = false;
	
	private static int UPDATE_SPEED = 50;
	
	public KeyHandling serialForKeyPresses;
	
	public TestClient() {
//		Settings.TimeSlice_US *= 16;
//		Settings.AbsMaxMotorStepsPerSecond *= 256f;
		Settings.StepMaxValue = Integer.MAX_VALUE;
		Settings.AssertLimits = false;
//		Settings.AbsMaxMotorStepsAccelPerSecond *= 200;
//		Settings.MinGondolaMoveDistance *= 40;
//		Settings.ONE_MILLION = 10;
		Settings.STEPS_PER_REV = 10;
//		Settings.ShouldInterpolate = false;
		this.setSize(200,100);
		this.setUndecorated(true);
		this.setVisible(true);
		this.setResizable(false);
		drawBot.isTestClient = true;
		drawBot.startSimulation();
		botController = drawBot;
		drawBot.serialForKeyPresses = this;
	}
	public void addNotify() {
		super.addNotify();
		addKeyListener(this);
		this.setFocusable(true);
		this.toFront();
		this.requestFocus();
		
		Timer t = new Timer(1, this);
		t.start();
	}
//	
//	public static void main (String[] a) {		
//		TestClient tc = new TestClient();
//	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		
	}
	@Override
	public void doKeyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		if (!autoDraw && code == KeyEvent.VK_N) {
			doNextPoint();
		}
		if (code == KeyEvent.VK_A){
			autoDraw = !autoDraw;
		}
		if (serialForKeyPresses != null) {
			serialForKeyPresses.doKeyPressed(e);
		}
		if (drawBot.drawPointProvider.nudgeMode.equals(NudgeMode.OFF)) {
			UPDATE_SPEED = 50;
		} else {
			UPDATE_SPEED = 1;
		}
	}
	public void doNextPoint() {
		for(int i=0; i < UPDATE_SPEED; ++i) {
			MotorInstructions mIs = botController.nextMotorInstructions();
			mIs.assertLimits();
		}
	}
	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
	}
	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (autoDraw) {
			doNextPoint();
		}
	}
}
