import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class MovingCircle extends JPanel implements MouseWheelListener, KeyListener, ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double x = 1;
	private double y = 0.0;
	private double vx = 0.0;
	private double vy = 1.2; //3.43066
	private double dt = 0.005;
	private double M = 1;
	private double G = 11.79156655;
	private double r = 1.0;
	private double dMax = 0;
	private double dMin = 10000000;
	private double vMax = 0;
	private double vMin = 10000000;
	private double rMax = 0;
	private double rMin = 10000000;
	private int xPix = 0;
	private int yPix = 0;
	private long loop = 0;
	private boolean direction = true;
	private Timer timer;
	private Image earthImage;
	private Image sunImage;
	private ArrayList<Point> trail = new ArrayList<>();
	Scanner scan = new Scanner(System.in);
	int UmoveX = 500;
	int UmoveY = 500;
	int moveX = 500;
	int moveY = 500;
	int UmP = 300;
	int Udiameter = (int) (40 * (UmP / 300d));
	int UdiameterSun = (int) (100 * (UmP / 300d));
	float scale = 1;
	int premx = UmoveY;
	int premy = UmoveY;
	int timerPace = 0;
	boolean locked = false;

	private Point startPoint;
	private Point endPoint;

	public MovingCircle() {
		addMouseWheelListener(this);
		addKeyListener(this);
		setFocusable(true);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				startPoint = e.getPoint();
				premx = moveX;
				premy = moveY;
			}

			@Override
			public void mouseReleased(MouseEvent e) {

			}

		});
		addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				endPoint = e.getPoint();
				int dx = endPoint.x - startPoint.x;
				int dy = endPoint.y - startPoint.y;
				moveX = premx + dx;
				moveY = premy + dy;
			}
		});
		addKeyListener(this);

		timer = new Timer(timerPace, this);
		timer.start();
		try {
			earthImage = ImageIO.read(new File("img/realEarth.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		int mP = (int) (UmP * scale);
		int diameter = (int) (40 * (mP / 300d));
		int diameterSun = (int) (100 * (mP / 300d));
		int xString = 10;
		double V = Math.sqrt(vx * vx + vy * vy);

		int yPixel = (int) (y * mP) + moveY - diameter / 2;
		int xPixel = (int) (x * mP) + moveX - diameter / 2;
		g.setColor(Color.black);
		g.drawString("Distance: " + Double.toString(r * 149.5978707) + "Gm", xString, 20);
		g.drawString("Velocity: " + Double.toString(V * 8.683120322) + "km/s", xString, 40);
		g.drawString("Loops: " + Long.toString(loop), xString, 60);
		g.drawString("Max Velocity: " + Double.toString(vMax * 8.683120322) + "km/s", xString, 80);
		g.drawString("Min Velocity: " + Double.toString(vMin * 8.683120322) + "km/s", xString, 100);
		g.drawString("Max Distance: " + Double.toString(rMax * 149.5978707) + "Gm", xString, 120);
		g.drawString("Min Distance: " + Double.toString(rMin * 149.5978707) + "Gm", xString, 140);
		g.drawString("Scale: " + scale + "x", xString, 160);
		try {
			sunImage = ImageIO.read(new File("img/sun.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		g.drawImage(sunImage, moveX - diameterSun / 2, moveY - diameterSun / 2, diameterSun, diameterSun, null);
		int PyPixel = (int) (y * UmP) + UmoveY;
		int PxPixel = (int) (x * UmP) + UmoveX;
		trail.add(new Point(PxPixel, PyPixel));
		try {
			Point tmpp1 = trail.get(trail.size() - 1);
			Point tmpp2 = trail.get(trail.size() - 2);
			double tmpD = Math.sqrt((Math.pow(tmpp2.y - tmpp1.y, 2)) + Math.pow(tmpp2.x - tmpp1.x, 2));
			if (tmpD > dMax) {
				dMax = tmpD;
			}
			if (tmpD < dMin) {
				dMin = tmpD;
			}
		} catch (Exception e) {
		}
		if (V > vMax) {
			vMax = V;
		}
		if (V < vMin) {
			vMin = V;
		}
		if (r > rMax) {
			rMax = r;
		}
		if (r < rMin) {
			rMin = r;
		}

		g2d.setStroke(new BasicStroke(3 * scale));
		for (int i = 1; i < trail.size(); i++) {

			Point p1 = trail.get(i - 1);
			Point p2 = trail.get(i);
			double distance = Math.sqrt((Math.pow(p2.y - p1.y, 2)) + Math.pow(p2.x - p1.x, 2));
			int cValue = (int) map(distance, dMin, dMax, 360, 60);
			float hue = cValue / 360f;
			Color color = new Color(Color.HSBtoRGB(hue, 1f, 1f));

			g2d.setColor(color);
			double p1x = ((((double) p1.x - UmoveX) / UmP) * mP) + moveX;
			double p1y = ((((double) p1.y - UmoveY) / UmP) * mP) + moveY;
			double p2x = ((((double) p2.x - UmoveX) / UmP) * mP) + moveX;
			double p2y = ((((double) p2.y - UmoveY) / UmP) * mP) + moveY;
			g2d.drawLine((int) p1x, (int) p1y, (int) p2x, (int) p2y);
		}
		g.drawImage(earthImage, xPixel, yPixel, diameter, diameter, null);
		xPix = moveX - xPixel + this.getWidth() / 2 - diameter / 2;
		yPix = moveY - yPixel + this.getHeight() / 2 - diameter / 2;
		if (locked) {
			moveX = xPix;
			moveY = yPix;
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		double tmp = Math.sqrt(vx * vx + vy * vy);
		boolean btmp = direction;
		r = Math.sqrt(x * x + y * y);
		vx = vx - ((G * M * x) / Math.pow(r, 3)) * dt;
		vy = vy - ((G * M * y) / Math.pow(r, 3)) * dt;
		x = x + vx * dt;
		y = y + vy * dt;
		if (Math.sqrt(vx * vx + vy * vy) > tmp) {
			direction = true;
		} else {
			direction = false;
		}
		if (direction != btmp && direction == false) {
			loop++;
		}
		repaint();

	}

	public static double map(double value, double fromLow, double fromHigh, double toLow, double toHigh) {
		return (value - fromLow) * (toHigh - toLow) / (fromHigh - fromLow) + toLow;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (e.getWheelRotation() >= 0 && scale > 0.04) {
			scale *= 0.9;

		} else if (e.getWheelRotation() <= 0) {
			scale *= 1.1;
		}
	}

	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
		char keyCode = e.getKeyChar();
		if (keyCode == 'r' || keyCode == 'R') {
			moveX = this.getWidth() / 2;
			moveY = this.getHeight() / 2;
		}
		if (keyCode == 'e' || keyCode == 'E') {
			locked = true;
		}
		if (keyCode == 'a' || keyCode == 'A') {
			locked = false;
		}

	}

	public void keyTyped(KeyEvent e) {
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("SunEarthOrbiting");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1000, 1000);
		frame.add(new MovingCircle());
		frame.setVisible(true);
	}
}