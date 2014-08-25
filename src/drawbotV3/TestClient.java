package drawbotV3;

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

import drawbotV3.BotController;
import drawbotV3.DrawBot;
import drawbotV3.KeyHandling;
import drawbotV3.MotorInstructions;
import drawbotV3.TestClient;

public class TestClient extends JFrame implements KeyListener, KeyHandling, ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DrawBot drawBot = new DrawBot();
	private BotController botController;
	private boolean autoDraw = false;
	
	public TestClient() {
//		Settings.TimeSlice_US *= 16;
//		Settings.ShouldInterpolate = false;
		this.setSize(200,100);
		this.setUndecorated(true);
		this.setVisible(true);
		this.setResizable(false);
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
	
	public static void main (String[] a) {		
		TestClient tc = new TestClient();
	}
	
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
	}
	private void doNextPoint() {
		MotorInstructions mIs = botController.nextMotorInstructions();
		mIs.assertLimits();
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
