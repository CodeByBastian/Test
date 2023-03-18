import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class MovingCircle extends JPanel implements ActionListener {

	private double x = 1.0;
	private double y = 0.0;
	private double vx = 0.0;
	private double vy = 1.2;
	private double dt = 0.5;
	private double M = 1;
	private double G = 11.79156655;
	private double r = 1.0;
	private Timer timer;
	private Image earthImage;
	private Image sunImage;
	private ArrayList<Point> trail = new ArrayList<>();

	public MovingCircle() {
		timer = new Timer(10, this);
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
		int moveXY = 500;
		int mP = 300;
		int diameter = 40;
		int diameterSun = 100;
		double V = Math.sqrt(vx * vx + vy * vy);
		int yPixel = (int) (y * mP) + moveXY - diameter / 2;
		int xPixel = (int) (x * mP) + moveXY - diameter / 2;

		g.setColor(Color.black);
		g.drawString("Distance: " + Double.toString(r * 149.5978707) + "Gm", 100, 100);
		g.drawString("Velocity: " + Double.toString(V * 8.683120322) + "km/s", 100, 120);
		try {
			sunImage = ImageIO.read(new File("img/sun.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		g.drawImage(sunImage, moveXY - diameterSun / 2, moveXY - diameterSun / 2, diameterSun, diameterSun, null);
		trail.add(new Point(xPixel + diameter / 2, yPixel + diameter / 2));
		g2d.setColor(Color.black);
		for (int i = 1; i < trail.size(); i++) {

			Point p1 = trail.get(i - 1);
			Point p2 = trail.get(i);
			g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
		}
		g.drawImage(earthImage, xPixel, yPixel, diameter, diameter, null);
	}

	public void actionPerformed(ActionEvent e) {
		r = Math.sqrt(x * x + y * y);
		vx = vx - ((G * M * x) / Math.pow(r, 3)) * dt;
		vy = vy - ((G * M * y) / Math.pow(r, 3)) * dt;
		x = x + vx * dt;
		y = y + vy * dt;
		repaint();

	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("SunEarthOrbiting");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1000, 1000);
		frame.add(new MovingCircle());
		frame.setVisible(true);
	}
}