package drawbotV3OffTrack;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import drawbotV3OffTrack.BotController;
import drawbotV3OffTrack.CoordVelocity;
import drawbotV3OffTrack.DrawBot;
import drawbotV3OffTrack.DrawPointProvider;
import drawbotV3OffTrack.KeyHandling;
import drawbotV3OffTrack.LineSprite;
import drawbotV3OffTrack.Machine;
import drawbotV3OffTrack.Motor;
import drawbotV3OffTrack.MotorInstructions;
import drawbotV3OffTrack.MotorSprite;
import drawbotV3OffTrack.Pointt;
import drawbotV3OffTrack.Settings;
import static java.lang.Math.*;

public class DrawBot extends JPanel implements ActionListener, KeyListener, Runnable, BotController {

	Machine machine = new Machine();
	Pointt curPoint = new Pointt();
	Pointt lastPoint = new Pointt();
	DrawPointProvider drawPointProvider = new DrawPointProvider(machine.startPoint());
	public static int CanvasHeight = (int) 533.4; // 21 inches in mm (unlike with width, height precision is not vital) 
	
	private static int lowerInfoHeight = 200;
	private static int lowerInfoWidth = 800 - Settings.MachineWidth;
	
	private Image canvas; /*off-screen image*/
	private Graphics cg; /*the graphics of the off-screen image*/
	
	private Color[] penColors = new Color[] {Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.PINK};
	private int penColorIndex = 0;
	
	private MotorSprite motorSpriteL;
	private MotorSprite motorSpriteR;
	
	private LineSprite lineSpriteL;
	private LineSprite lineSpriteR;
	
//	private Pointt _targetPoint = new Pointt();
	private CoordVelocity _targetCV = new CoordVelocity();
	
	private MotorInstructions currentMotorInstructions = null;
	
	private final static boolean FULL_SCREEN_MODE = false;
	private boolean autoAnimate = false;
	
	public KeyHandling serialForKeyPresses;
	
	private int CANVAS_SCALE = 1;
	
	public DrawBot()
	{
		this(false);
	}
	public DrawBot(boolean _autoAnimate) {
		autoAnimate = _autoAnimate;
	}
	
	public void addNotify() {
		super.addNotify();
		this.addKeyListener(this);
		this.setFocusable(true);
		setupCanvas();
		drawAllPoints((Graphics2D) cg);
		motorSpriteL = new MotorSprite(machine.getMotor(false), false);
		motorSpriteR = new MotorSprite(machine.getMotor(true), true);
		lineSpriteL = new LineSprite(machine.getMotor(false), false);
		lineSpriteR = new LineSprite(machine.getMotor(true),true);
		if (autoAnimate) {
			Timer t = new Timer(20, this);
			t.start();
		}
	}
	
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(canvas, 0, 0, null);
		
//		float canvasFitScale = 1f/(float)(CANVAS_SCALE);
//		float xoffset = -Settings.MachineWidth * (CANVAS_SCALE - 1f) *.5f;
//		float yoffset = -CanvasHeight * (CANVAS_SCALE - 1f) *.5f;
//		g2.drawImage(canvas, AffineTransform.getTranslateInstance(xoffset, yoffset), null);
		
//		drawMotorsAndChords(g2);
//		drawDebugSomePoints(g2);
		double percentDone = drawPointProvider.percentDone();
		String percDone = percentDone >= 1.0 ? "DONE" : "" + String.format("%.3f", percentDone);
		drawTextAt(g2,  new Pointt(Settings.MachineWidth - 40, CanvasHeight - 40), percDone ,0, Color.white);
		drawMotorsAndChords(g2);
		graphMotorVelocities(g2, currentMotorInstructions);
    }
	private void drawMotorsAndChords(Graphics2D g2) {
		Pointt leftPos = machine.motorLocationL;
		Pointt rightPos = machine.motorLocationR; 
		Motor left = machine.getMotor(false);
		Motor right = machine.getMotor(true);
		double leftAng = machine.testAngleForSide(false);
		double rightAng = machine.testAngleForSide(true);
		double yL = left.stringLength() * sin(leftAng);
		double xL = left.stringLength() * cos(leftAng);
		double yR = right.stringLength() * sin(rightAng);
		double xR = machine.machineWidth - right.stringLength() * cos(rightAng);
		lineSpriteL.drawLine(g2, leftPos, new Pointt(xL,yL) );
		lineSpriteR.drawLine(g2, rightPos, new Pointt(xR,yR) );
		motorSpriteL.drawMotor(g2, leftPos);
		motorSpriteR.drawMotor(g2, rightPos.minus(new Pointt(right.spoolDiameter,0)));
	}
	private void drawDebugSomePoints(Graphics2D g2) {
		drawPoint(g2, targetPoint(), Color.MAGENTA);
		drawPoint(g2, machine.departurePoint, Color.CYAN);
		drawPoint(g2, machine.cheatPoint, Color.PINK);
		drawPoint(g2, machine.cheatPointForPrev, Color.GREEN);
//		g2.draw(machine.departureToTargetLine().arrow());
	}
	private void drawPoint(Graphics2D g, Pointt p, Color c) {
		int w = 22, h = 8;
		Ellipse2D e = new Ellipse2D.Double(p.x - w/2.0, p.y - h/2.0, w,h);
		g.setPaint(c);
		g.fill(e);
		Ellipse2D el = new Ellipse2D.Double(p.x - h/2.0, p.y - w/2.0, h,w);
		g.draw(el);
	}
	private void drawTextAt(Graphics2D g, Pointt loc, String str, int pushDownY, Color col) {
		char[] dd = str.toCharArray();
		Pointt textO = loc.plus(new Pointt(0, pushDownY));
		textO.x = textO.x > 50 ? textO.x - 150 : textO.x;
		g.setPaint(Color.DARK_GRAY);
		Rectangle2D r = new Rectangle2D.Double(textO.x - 2, textO.y - 2, 100, 20);
		g.fill(r);
		g.setPaint(col);
		g.drawChars(dd, 0, dd.length, (int) textO.x,(int) textO.y);
	}

	@Override
	public void run() {
		doSetup(this);
	}
	public void startSimulation() {
		Thread thread = new Thread (this);
		thread.start();
	}
	public static void mainNOT(String[] args) {
		DrawBot db = new DrawBot(); 
		Thread thread = new Thread(db);
		thread.start();
	}
	private static void doSetup(DrawBot db) {
		JFrame fr = new JFrame();
		
		if (FULL_SCREEN_MODE) {
//			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
//	
//			//STATIC VARS
//			CanvasWidth = (int) dim.getWidth();
//			CanvasHeight = (int) dim.getHeight();
//
//			fr.setExtendedState(JFrame.MAXIMIZED_BOTH);
//			//get a 'device' object--whatever that is...
//			GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0];
//			device.setFullScreenWindow(fr);
		}
		
		fr.setSize(Settings.MachineWidth + lowerInfoWidth, CanvasHeight + lowerInfoHeight );
		fr.setUndecorated(true);
		db.setSize(Settings.MachineWidth + lowerInfoWidth, CanvasHeight + lowerInfoHeight);
		fr.add(db);
		fr.setVisible(true);
		fr.setResizable(false);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		updateState();
	}
	private void updateState() {
		updateState(false);
	}
	private void updateState(boolean goHome) {
		updateMachine();
		lastPoint = curPoint;
		curPoint = machine.penLocation();
		drawOnCanvas();
		repaint();
	}
	private void updateMachine() {
		updateMachine(false);
	}
	private Pointt targetPoint() { return _targetCV.getCoord(); }
	
	private void updateMachine(boolean goHome) {
		if (machine.readyForNextPoint()) {
			CoordVelocity nextCoordVelocity = null;
			while(true) {
				nextCoordVelocity = nextPoint();
				if (nextCoordVelocity == null) {
					Asserter.assertFalseAndDie("no null coord vels please");
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					break;
				}
			}
			 _targetCV = nextCoordVelocity;
			if (_targetCV == null) {
				_targetCV = new CoordVelocity(machine.startPoint(), new Pointt());
			}
			if (!goHome) currentMotorInstructions = machine.moveToPoint(_targetCV);
			else currentMotorInstructions = machine.moveToStartPoint();
			penColorIndex = (penColorIndex + 1) % penColors.length;
		}
	}
	
//	private Pointt nextPoint() {
//		return drawPointProvider.nextPoint();
//	}
	private CoordVelocity nextPoint() {
		return drawPointProvider.nextPoint();
	}
	
	private void drawOnCanvas() {
		Graphics2D cgg = (Graphics2D) cg;
//		if (machine.oneMotorArrivedFirst()) {
//			cgg.setPaint(Color.WHITE);
//		} else if (machine.sameDistanceToTarget()) {
//			cgg.setPaint(Color.LIGHT_GRAY);
//		} else {
//			cgg.setPaint(penColors[penColorIndex]);
//		}
		if (!lastPoint.equals(new Pointt())) {
			Line2D.Double nextLine = new Line2D.Double(lastPoint.point2D(), curPoint.point2D());
			cgg.draw(nextLine);
		}
//		Ellipse2D.Double curPointEll = new Ellipse2D.Double(curPoint.x, curPoint.y, 5, 5);
//		cgg.draw(curPointEll);
		
		cgg.setPaint(penColors[penColorIndex]);
		cgg.draw(machine.prevToCurrentLine().arrow());
	}
	private void drawAllPoints(Graphics2D g){
		ArrayList<CoordVelocity> cvs = drawPointProvider.getUninterpolatedPointts();
		Ellipse2D.Double el;
		g.setPaint(Color.GRAY);
		for (CoordVelocity cv : cvs) {
			Pointt lo = cv.getCoord();
			el = new Ellipse2D.Double(lo.x * CANVAS_SCALE, lo.y * CANVAS_SCALE, .3,.3);
			g.draw(el);
		}
		//want
	}

	private void setupCanvas()
	{
		if (canvas == null) {
			canvas = createImage(Settings.MachineWidth * CANVAS_SCALE,CanvasHeight * CANVAS_SCALE) ;
			cg = canvas.getGraphics();
		}
		cg.setColor(Color.BLACK);
		cg.fillRect(0, 0, (int)( Settings.MachineWidth * CANVAS_SCALE),(int)(CanvasHeight * CANVAS_SCALE ));
	}

	@Override
	public void keyPressed(KeyEvent e) {
//		machine.doKeyPressed(e);
		if (e.getKeyCode() == KeyEvent.VK_F) {
			ImageUtil.imageToDisk(canvas);
		}
		if (serialForKeyPresses != null)
			serialForKeyPresses.doKeyPressed(e);
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}
	@Override
	public void keyTyped(KeyEvent arg0) {
	}

	@Override
	public MotorInstructions nextMotorInstructions() {
		updateState();
		return currentMotorInstructions;
	}
	@Override
	public MotorInstructions goHomeInstructions() {
		updateState(true);
		return currentMotorInstructions;
	}
	
	private void graphMotorVelocities(Graphics2D g, MotorInstructions mi) {
		if (mi == null) return;
		int xx = (int)(drawPointProvider.percentDone() * (Settings.MachineWidth + lowerInfoWidth));

		double xaxisHeight = CanvasHeight + .5 * lowerInfoHeight;
		Rectangle2D c = new Rectangle2D.Double(xx, xaxisHeight, 1,1);
		g.setPaint(Color.RED);
		g.draw(c);
		
		double scaleY = 1d;
		double yyl = -1d * (scaleY *.5d * lowerInfoHeight * mi.leftSpeed / Settings.AbsMaxMotorStepsPerSecond);
		double yyr = -1d * (scaleY *.5d * lowerInfoHeight * mi.rightSpeed / Settings.AbsMaxMotorStepsPerSecond);
		yyl += xaxisHeight;
		yyr += xaxisHeight;
		
		Rectangle2D l = new Rectangle2D.Double(xx, yyl, 1,3);
		Rectangle2D r = new Rectangle2D.Double(xx, yyr, 1,2);
		g.setPaint(Color.ORANGE);
		g.draw(l);
		g.setPaint(Color.BLUE);
		g.draw(r);
		
		
		Rectangle2D coordPoint = new Rectangle2D.Double(mi.coVelDebug.getCoord().x, mi.coVelDebug.getCoord().y, 3,3); //Point on the machine (not part of graph)
		g.setPaint(Color.GREEN);
		g.draw(coordPoint);
		
		Rectangle2D velLength = new Rectangle2D.Double(xx, xaxisHeight + -1d * scaleY * mi.coVelDebug.getVelocity().distance(), 1,2);
		g.setPaint(Color.CYAN);
		g.draw(velLength);
	}
	private void fillInfoArea(Graphics2D g) {
		g.setColor(Color.WHITE);
		g.draw(new Rectangle2D.Double(0, CanvasHeight, Settings.MachineWidth, lowerInfoHeight ));
	}
	
	public static class ImageUtil 
	{
		public static void imageToDisk(Image im) {
			try {
				File outfile = new File("_drawBotSave.jpg");
				ImageIO.write((RenderedImage) im, "jpg", outfile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


}