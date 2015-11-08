
import java.awt.*;
import java.awt.image.BufferedImage;
import java.applet.*;
import java.util.Scanner;
import java.awt.event.*;

/* 
3D slicer
by Eric J.Parfitt (ejparfitt@gmail.com)

This program shows 2D slices of a 3D terrain that you can move around
in.  Eventually I plan on turning this into a battle tank style game. 

Version: 1.0 alpha
 */

public class CurveChange extends Applet implements Runnable
{
	//Variables
	private BufferedImage drawing;
	private Graphics2D g;
	private Thread myThread; 
	private boolean isLeft;
	private boolean isRight;
	private boolean isUp;
	private boolean isDown;
	private boolean isZ;
	private boolean isX;
	private double colorFactor=.6;
	//Keys
	public boolean keyDown(Event e, int key) {
		if (key == Event.LEFT) {
			isLeft = true;
		}
		if(key == Event.RIGHT) {
			isRight = true;
		}
		if(key == Event.UP) {
			isUp = true;
		}
		if(key == Event.DOWN) {
			isDown = true;
		}
		if(key == 122) {
			isZ = true;
		}
		if(key == 120) {
			isX = true;
		}
		return true;
	}

	public boolean keyUp(Event e, int key) {
		if (key == Event.LEFT) {
			isLeft = false;
		}
		if(key == Event.RIGHT) {
			isRight = false;
		}
		if(key == Event.UP) {
			isUp = false;
		}
		if(key == Event.DOWN) {
			isDown = false;
		}
		if(key == 122) {
			isZ = false;
		}
		if(key == 120) {
			isX = false;
		}
		return true;
	}
	public void keyTyped(KeyEvent e) {

	}
	Scanner scan = new Scanner(System.in);


	// More variables
	Color redColor;
	Color weirdColor;
	Color bgColor;
	Color whiteColor;
	Color blackColor;
	Color greenColor;
	Color yellowColor;
	Color orangeColor;
	Color brownColor;
	Color azureColor;
	Color purpleColor;
	Color blueColor;
	
	private int pointNumber = 10;
	private int tankNumber = 2;
	private double pointRange = 8;
	private double brightness = 1.1;
	private double mapZoom = .6;
	private double zoom = (15.0 / 500.0);
	private double xSize = 500;
	static double ySize = 250;
	private double xRange = xSize * zoom;
	private double yRange = ySize * zoom;
	private double maxRange;
	private double normalH = 3.0;
	private int numColors = 4;
	
	static double mapSize = 100;

	private double xLocation = 0;
	private double yChange = 0;
	private double pt1XStart = 3;
	private double pt1YStart = .5;
	private double pt2XStart = -1;
	private double pt2YStart = -1;
	private double pt1XCo =  pt1XStart;
	private double pt1YCo = pt1YStart;
	private double pt2XCo = pt2XStart;
	private double pt2YCo = pt2YStart;
	
	private MaxPoint pt1 = new MaxPoint(3, .5, 2);
	private MaxPoint pt2 = new MaxPoint(-1, -1, 0);
	private MaxPoint[] pointArray = {pt1, pt2};
	
	private Tank tank1 = new Tank(-2, -1.5);
	private Tank tank2 = new Tank(2, .5);
	//private Tank[] tankArray = {tank1, tank2};
	private Tank[] tankArray = new Tank[tankNumber];
	
	//private MaxPoint pt1 = new MaxPoint(Math.random() * 15 - 7.5, Math.random() * 15 - 7.5, 2);
	//private MaxPoint pt2 = new MaxPoint(Math.random() * 15 - 7.5, Math.random() * 15 - 7.5, 0);
	//To make random points
	//private MaxPoint[] pointArray = new MaxPoint[pointNumber];
	private double pt1Ang;
	private double pt2Ang;
	private double pt1Rad;
	private double pt2Rad;
	private double xPixelToCo = (double) xRange / xSize;
	private double yPixelToCo = (double) yRange / ySize;
	private double test;
	static double sliceXPos;
	static double sliceYPos;

	private double mapYPos;
	private double mapXPos;
	static double mapAngle;
	private double mapXMin;
	private double mapXMax;
	private double mapBottomIntc;
	private double mapTopIntc;
	static double mapPixelToCo;
	
	//private Grapher toGraph = new Grapher(xRange, yRange, xSize, ySize);
	private double[] y;
	private double[] mapY;
	private double[][] shading;
	private double[] graphArray = new double[(int) xSize];
	Point mapOrigin = new Point(0,0);
	Point mapForward = new Point(1,0);
	Point mapUp = new Point(0,1);
	//Point mapTest = new Point(-2, 1);

	public void init() 
	{

		// set up double buffering
		drawing = new BufferedImage((int) xSize, (int) ySize, BufferedImage.TYPE_4BYTE_ABGR);
		g = drawing.createGraphics();
		resize((int) xSize, (int) ySize);

		// Colors
		redColor = Color.red;
		weirdColor = new Color(60,60,122);
		//bgColor = Color.blue;
		whiteColor = Color.white;
		blackColor = Color.black;
		greenColor = Color.green;
		yellowColor = Color.yellow;
		orangeColor = Color.orange;
		blueColor = Color.blue;
		azureColor = new Color(0,127,255);
		brownColor = new Color (150,75,0);
		bgColor = azureColor;
		purpleColor = new Color(255, 0, 255);

		setBackground(bgColor);
		if (xRange < yRange) {
			maxRange = (int) Math.round((double) yRange * mapZoom);
		}
		else
		{
			maxRange = (int) Math.round((double) xRange * mapZoom);
		}
		mapPixelToCo = maxRange / mapSize;
		//Randomizer
		/*
		for (int i = 0; i < pointArray.length; i++) {
			//pointArray[i] = pt1;
			pointArray[i] = new MaxPoint(Math.random() * pointRange - pointRange / 2, Math.random() * pointRange - pointRange / 2, 2);
		}
		*/
		for (int i = 0; i < tankArray.length; i++) {
			tankArray[i] = new Tank(Math.random() * pointRange - pointRange / 2, Math.random() * pointRange - pointRange / 2);
		}
		
		shading = shadingArray();
		
	}

	public void stop()
	{
		myThread = null;
	}

	public void start() {
		if(myThread == null) {
			myThread = new Thread(this, "curvechange");
			myThread.start();
		}
	}
	//Gets keys and updates graphics
	public void run() {
		System.out.println("Running...");
		//System.out.println(mapPixelToCo);
		while(true) {
			try {Thread.sleep(30);}
			catch(Exception e) {}
			if (isLeft == true) {
				//Moves slice.
				sliceXPos -= xPixelToCo * Math.cos(mapAngle);
				sliceYPos -= xPixelToCo * Math.sin(mapAngle);
				//Moves maxpoints.
				/*
				for (int i = 0; i < pointArray.length; i++) {
					pointArray[i].incX(xPixelToCo);
				}
				*/
				//pt1XCo += xPixelToCo;
				//pt2XCo += xPixelToCo;
				mapXPos = sliceXPos / mapPixelToCo;
				mapYPos = sliceYPos / mapPixelToCo;
			}
			if(isRight == true) {
				sliceXPos += xPixelToCo * Math.cos(mapAngle);
				sliceYPos += xPixelToCo * Math.sin(mapAngle);
				/*
				for (int i = 0; i < pointArray.length; i++) {
					pointArray[i].decrX(xPixelToCo);
				}
				*/
				//pt1XCo -= xPixelToCo;
				//pt2XCo -= xPixelToCo;
				mapXPos = sliceXPos / mapPixelToCo;
				mapYPos = sliceYPos / mapPixelToCo;
			}
			if(isUp == true) {
				sliceXPos += yPixelToCo * Math.cos(mapAngle + Math.PI / 2);
				sliceYPos += yPixelToCo * Math.sin(mapAngle + Math.PI / 2);
				/*
				for (int i = 0; i < pointArray.length; i++) {
					pointArray[i].decrY(yPixelToCo);
				}
				*/
				//pt1YCo -= yPixelToCo;
				//pt2YCo -= yPixelToCo;
				mapXPos = sliceXPos / mapPixelToCo;
				mapYPos = sliceYPos / mapPixelToCo;
			}
			if(isDown == true) {
				sliceXPos -= yPixelToCo * Math.cos(mapAngle + Math.PI / 2);
				sliceYPos -= yPixelToCo * Math.sin(mapAngle + Math.PI / 2);
				/*
				for (int i = 0; i < pointArray.length; i++) {
					pointArray[i].incY(yPixelToCo);
				}
				*/
				//pt1YCo += yPixelToCo;
				//pt2YCo += yPixelToCo;
				mapXPos = sliceXPos / mapPixelToCo;
				mapYPos = sliceYPos / mapPixelToCo;
			}
			
			//For point rotation - calculates angle and radius of points relative to point coordinates.
			/*
			if(isZ == true || isX == true) {
				for (int i = 0; i < pointArray.length; i++) {
					if(pointArray[i].getX() - xLocation * xPixelToCo > 0) {
						pointArray[i].setAngle(Math.atan(pointArray[i].getY()/(pointArray[i].getX() - xLocation * xPixelToCo)));
					}
					else if (pointArray[i].getX() - xLocation * xPixelToCo < 0) {
						pointArray[i].setAngle(Math.atan(pointArray[i].getY()/(pointArray[i].getX() - xLocation * xPixelToCo)) + Math.PI);
					}
					else {
						pointArray[i].setAngle(Math.PI / 2);
					}
					pointArray[i].setRadius(Math.sqrt(Math.pow((pointArray[i].getX() - xLocation * xPixelToCo),2) + Math.pow(pointArray[i].getY(), 2)));
				}
			}
			*/
			
			if(isZ == true) {
				//Rotates points
				/*
				for (int i = 0; i < pointArray.length; i++) {
					pointArray[i].decrAngle((2 * Math.PI) / 360);
					pointArray[i].setX(pointArray[i].getRadius() * Math.cos(pointArray[i].getAngle()) + xLocation * xPixelToCo);
					pointArray[i].setY(pointArray[i].getRadius() * Math.sin(pointArray[i].getAngle()));
				}
				*/
				mapAngle += (2 * Math.PI) / 360;
				if(mapAngle > 2 * Math.PI) {
					mapAngle = mapAngle - 2 * Math.PI;
				}
				/*
				pt1Ang -= (2 * Math.PI) / 360;
				pt2Ang -= (2 * Math.PI) / 360;
				mapAngle += (2 * Math.PI) / 360;
				pt1XCo = pt1Rad * Math.cos(pt1Ang) + xLocation * xPixelToCo;
				pt1YCo = pt1Rad * Math.sin(pt1Ang);
				pt2XCo = pt2Rad * Math.cos(pt2Ang) + xLocation * xPixelToCo;
				pt2YCo = pt2Rad * Math.sin(pt2Ang);
				*/
			}
			if(isX == true) {
				/*
				for (int i = 0; i < pointArray.length; i++) {
					pointArray[i].incAngle((2 * Math.PI) / 360);
					pointArray[i].setX(pointArray[i].getRadius() * Math.cos(pointArray[i].getAngle()) + xLocation * xPixelToCo);
					pointArray[i].setY(pointArray[i].getRadius() * Math.sin(pointArray[i].getAngle()));
				}
				*/
				mapAngle -= (2 * Math.PI) / 360;
				if(mapAngle < 0) {
					mapAngle = mapAngle + 2 * Math.PI;
				}
				/*
				pt1Ang += (2 * Math.PI) / 360;
				pt2Ang += (2 * Math.PI) / 360;
				mapAngle -= (2 * Math.PI) / 360;
				pt1XCo = pt1Rad * Math.cos(pt1Ang) + xLocation * xPixelToCo;
				pt1YCo = pt1Rad * Math.sin(pt1Ang);
				pt2XCo = pt2Rad * Math.cos(pt2Ang) + xLocation * xPixelToCo;
				pt2YCo = pt2Rad * Math.sin(pt2Ang);
				*/
			}
			update(getGraphics());

		}
	}

	public void update(Graphics gr) {
		paint(gr);
	}

	// Draws stuff on screen
	public void paint(Graphics gr) 
	{
		// Creates a new Grapher object
		//toGraph.incrementExtra(0);
		
		//Fills background
		g.setColor(bgColor);
		g.fillRect(0, 0, (int) xSize, (int) ySize);
		
		//Map background
		for(int i = 0; i < mapSize; i++) {
			//System.out.println();
			for(int j = 0; j < mapSize; j++) {
				//System.out.println(shading[j][i]);
				int colorCode = (int)Math.round(shading[j][i] * 255.0 / brightness);
				int amtRed = (int) Math.round(150 + colorFactor * colorCode);
				int amtGreen = (int) Math.round(75 + colorFactor * colorCode);
				int amtBlue = (int) Math.round(colorFactor * colorCode);
				/*
				if (colorCode > 255) {
					colorCode = 255;
				}
				*/
				if (amtRed > 255) {
					amtRed = 255;
				}
				if (amtGreen > 255) {
					amtGreen = 255;
				}
				if (amtBlue > 255) {
					amtBlue = 255;
				}
				//System.out.print(colorCode + ",");
				Color mapColor = new Color(amtRed, amtGreen, amtBlue);
				g.setColor(mapColor);
				g.drawRect(i, (int) ySize - j, 1, 1);
			}
		}
		//g.drawRect()
		/*
		g.setColor(whiteColor);
		g.fillRect(0, 100, 100, 100);
		*/
		/*
		g.setColor(redColor);
		for (int i = 0; i < pointArray.length; i++) {
			g.fillRect(mapXToPixel(pointArray[i].getXStart()), (int) ySize - mapXToPixel(pointArray[i].getYStart()), 1, 1);
		}
		*/
		
		//Graphs the slice
		//System.out.println(mapAngle);
		if (mapAngle !=0) {
			mapBottomIntc = (-maxRange / 2 - mapPixelToCo * mapYPos) / Math.tan(mapAngle) + mapPixelToCo * mapXPos;
			mapTopIntc = (maxRange / 2 - mapPixelToCo * mapYPos) / Math.tan(mapAngle) + mapPixelToCo * mapXPos;
			if (mapBottomIntc <= mapTopIntc) {
				if (mapBottomIntc > -maxRange / 2) {
					mapXMin = mapBottomIntc;
				}
				else {
					mapXMin = -maxRange / 2;
				}
				if (mapTopIntc < maxRange / 2) {
					mapXMax = mapTopIntc;
				}
				else {
					mapXMax = maxRange / 2;
				}
			}
			else {
				if (mapTopIntc > -maxRange / 2) {
					mapXMin = mapTopIntc;
				}
				else {
					mapXMin = -maxRange / 2;
				}
				if (mapBottomIntc < maxRange / 2) {
					mapXMax = mapBottomIntc;
				}
				else {
					mapXMax = maxRange / 2;
				}
			}
		}
		else {
			mapXMin = -maxRange / 2;
			mapXMax = maxRange / 2;
		}
		//g.setColor(redColor);
		mapY = mapArray();
		if (mapBottomIntc != mapTopIntc || mapAngle == 0) {
			/*
			for(int i = mapXToPixel(mapXMin); i < mapXToPixel(mapXMax); i++) {
				int yCorner = mapYToPixel(mapY[i]);
				g.fillRect(i - 1, ySize - (yCorner + 1), 3, 3);
			}
			*/
			int pixelXMin = mapXToPixel(mapXMin);
			int pixelXMax = mapXToPixel(mapXMax);
			boolean drawLineA = false;
			boolean drawLineB = false;
			int pixelYMin = 0;
			int pixelYMax = 0;
			if (pixelXMin >=0 && pixelXMin < mapY.length) {
				pixelYMin = (int) ySize - mapYToPixel(mapY[pixelXMin]);
				drawLineA = true;
			}
			if (pixelXMax - 1 >=0 && pixelXMin - 1 < mapY.length) {
				pixelYMax = (int) ySize - mapYToPixel(mapY[pixelXMax - 1]);
				drawLineB = true;
			}
			g.setColor(blackColor);
			if (drawLineA == true && drawLineB == true) {
				g.drawLine(pixelXMin, pixelYMin, pixelXMax, pixelYMax);
			}
			/*
			g.drawLine(pixelXMin + 1, pixelYMin - 1, pixelXMax + 1, pixelYMax - 1);
			g.drawLine(pixelXMin + 1, pixelYMin + 1, pixelXMax + 1, pixelYMax + 1);
			g.drawLine(pixelXMin - 1, pixelYMin + 1, pixelXMax - 1, pixelYMax + 1);
			g.drawLine(pixelXMin + 1, pixelYMin - 1, pixelXMax + 1, pixelYMax - 1);
			g.drawLine(pixelXMin - 1, pixelYMin - 1, pixelXMax - 1, pixelYMax - 1);
			g.drawLine(pixelXMin + 1, pixelYMin, pixelXMax + 1, pixelYMax);
			g.drawLine(pixelXMin - 1, pixelYMin, pixelXMax - 1, pixelYMax);
			g.drawLine(pixelXMin, pixelYMin + 1, pixelXMax, pixelYMax + 1);
			g.drawLine(pixelXMin, pixelYMin - 1, pixelXMax, pixelYMax - 1);
			*/
			
		}
		else {
			g.fillRect((int) Math.round(mapSize / 2) - 1, (int) ySize - ((int) mapSize + 1), 3, (int) mapSize);
		}
		
		//Draws the map location
		g.setColor(redColor);
		Point testPoint = new Point(1, 0);
		//System.out.println(testPoint.getMapX());
		double testX = testPoint.getMapX() / mapPixelToCo;
		double testY = testPoint.getMapY() / mapPixelToCo;
		//g.drawOval((int) Math.round(mapXPos + mapSize / 2) - 3 - 1, (int) ySize - ((int) Math.round(mapYPos + mapSize / 2) + 2 + 1), 6, 6);
		//g.drawLine((int) Math.round(mapXPos + mapSize / 2) - 1, (int) ySize - ((int) Math.round(mapYPos + mapSize / 2)), (int) Math.round(testX + mapSize / 2) - 1 , (int) ySize - ((int) Math.round(testY + mapSize / 2)));
		g.drawLine(mapOrigin.xForMap(), mapOrigin.yForMap(), mapForward.xForMap() , mapForward.yForMap());
		g.drawLine(mapOrigin.xForMap(), mapOrigin.yForMap(), mapUp.xForMap() , mapUp.yForMap());
		
		//Draws points on map
		g.setColor(orangeColor);
		for (int i = 0; i < pointArray.length; i++) {
			g.fillRect(mapXToPixel(pointArray[i].getXStart()), (int) Math.round(ySize) - (mapYToPixel(pointArray[i].getYStart())), 3, 3);
			//g.fillRect(mapXToPixel(pt2XStart) - 1, ySize - (mapYToPixel(pt2YStart) + 1), 3, 3);
		}
		
		//Tank AI here
		for (int i = 0; i < tankArray.length; i++) {
			double angle = tankArray[i].angleToYou;
			if (angle != 0) {
				if (angle <= Math.PI) {
					tankArray[i].incAngle((2 * Math.PI) / 360);
				}
				else {
					tankArray[i].decrAngle((2 * Math.PI) / 360);
				}
			}
		}
		
		//Draws tanks on map
		g.setColor(purpleColor);
		for (int i = 0; i < tankArray.length; i++) {
			g.fillRect(mapXToPixel(tankArray[i].getXStart()), (int) Math.round(ySize) - (mapYToPixel(tankArray[i].getYStart())), 3, 3);
			//g.fillRect(mapXToPixel(pt2XStart) - 1, ySize - (mapYToPixel(pt2YStart) + 1), 3, 3);
		}
		
		//Graphs the equation
		double[][] xCo = new double[pointArray.length][(int) xSize];
		double[][] yCo = new double[pointArray.length][(int) xSize];
		double[][] distArray = new double[pointArray.length][(int) xSize];
		double[] graphArray = new double[(int) xSize];
		for (int j = 0; j < pointArray.length; j++) {
			g.setColor(greenColor);
			for(int i=0; i < xSize; i++) {
				double xChange = ((double) i) * (xRange / xSize) - xRange / 2;
				//System.out.println(xChange);
				//System.out.println(xChange);
				//System.out.println("x = " + pointArray[j].getX() + " y + " + pointArray[j].getY());
				//y[i] = ((double)myYSize/myYRange) * Math.sin(((double) i - myExtra) * ((double)myXRange / myXSize) - myXRange / 2);
				//graphArray[i] += ((5/(Math.sqrt(2 * 3.14) * Math.pow(2.72,(Math.pow((xChange - pointArray[j].getX()),2) + Math.pow((pointArray[j].getY() - yChange),2) + Math.pow((pointArray[j].getHeightFactor() / 2),2)) / 2))));
				//graphArray[i] = ((5/(Math.sqrt(2 * Math.PI) * Math.pow(Math.E,(Math.pow((xChange - pt1XCo),2) + Math.pow((pt1YCo-yChange),2) + Math.pow((2/ 2),2)) / 2)) + 5/(Math.sqrt(2 * Math.PI) * Math.pow(Math.E,(Math.pow((xChange - pt2XCo),2) + Math.pow((pt2YCo-yChange),2) + Math.pow((0 / 2),2)) / 2))));
				//Next line is the old working grapher where points rotate.
				//graphArray[i] += ((5/(Math.sqrt(2 * Math.PI) * Math.pow(Math.E,(Math.pow((xChange - pointArray[j].getX()),2) + Math.pow((pointArray[j].getY()-yChange),2) + Math.pow((pointArray[j].getHeightFactor()/ 2),2)) / 2))));
				//Newer grapher where slice rotates.
				xCo[j][i] = xChange * Math.cos(mapAngle) + sliceXPos;
				//yCo[j][i] = xChange * Math.sin(mapAngle) + pointArray[j].getY();
				distArray[j][i] = Math.sqrt(Math.pow((pointArray[j].getYStart() - Math.tan(mapAngle) * (xCo[j][i] - sliceXPos) - sliceYPos),2) + Math.pow((pointArray[j].getXStart() - xCo[j][i]),2)+ Math.pow((pointArray[j].getHeightFactor()/ 2),2));
				//System.out.println(xCo[1][20]);
				graphArray[i] += normalH / (Math.sqrt(2 * Math.PI) * Math.pow(Math.E,(Math.pow(distArray[j][i],2) / 2)));
			}			
		}
		//Draws stick guy
		g.setColor(yellowColor);
		g.fillRect((int) Math.round(xLocation + (double) xSize / 2) - 1, YToPixel(graphArray[(int) xLocation + (int) Math.round((double) xSize / 2)])-15 + 1, 3, 15);
		//Draws curve
		for(int i = 1; i < xSize; i++) {
			int yCorner = YToPixel(graphArray[i]);
			int colorCode = (int) Math.round(graphArray[i] * 255.0 / brightness);
			int amtRed = (int) Math.round(150 + colorFactor * colorCode);
			int amtGreen = (int) Math.round(75 + colorFactor * colorCode);
			int amtBlue = (int) Math.round(colorFactor * colorCode);
			/*
			if (colorCode > 255) {
				colorCode = 255;
			}
			*/
			if (amtRed > 255) {
				amtRed = 255;
			}
			if (amtGreen > 255) {
				amtGreen = 255;
			}
			if (amtBlue > 255) {
				amtBlue = 255;
			}
			//System.out.print(colorCode + ",");
			Color mapColor = new Color(amtRed, amtGreen, amtBlue);
			//System.out.print(colorCode + ",");
			//Color mapColor = new Color(colorCode, 255, colorCode);
			g.setColor(mapColor);
			g.fillRect(i, yCorner, 3, 3);
			//g.drawLine(i, yCorner, i, (int) Math.round(ySize / 2));
			g.fillRect(0, (int) Math.round(ySize) / 2, (int) Math.round(xSize), 3);
		}
		g.setColor(redColor);
		g.drawRect(0, (int) ySize - 100, 100, 99);
		
		//Draws tanks on curve
		double x;
		double y;
		g.setColor(purpleColor);
		for (int i = 0; i < tankArray.length; i++) {
			double a = sliceXPos;
			double b = sliceYPos;
			double c = tankArray[i].getX();
			double d = tankArray[i].getY();
			double m = Math.tan(mapAngle);
			//Point (perpX, perpY) closest to poit (c,d) on line y == m * x + b, m(x - a) + b == (1 / m) * (x - c) + d
			double perpX = (a * m * m + (d-b) * m + c) / (m * m + 1);
			double perpY = m * (perpX - a) + b;
			//lengthDist == distance from (a,b) to (perpX, perpY)
			double lengthDist = Math.sqrt((a - perpX) * (a - perpX) + (b - perpY) * (b - perpY));
			//widthDist == distance from (c,d) to (perpX, perpY)
			double widthDist = Math.sqrt((c - perpX) * (c - perpX) + (d - perpY) * (d - perpY));
			//y = (tankArray[i].getY() / yPixelToCo) + (double) ySize / 2 ;
			//x = (tankArray[i].getX() / xPixelToCo) + (double) xSize / 2 ;
			//Next few lines figure out whether perpX and perpY are positive or negative
			double tankAngle = Math.atan2((d - b), (c - a));
			if (d - b < 0) {
				tankAngle += 2 * Math.PI;
			}
			double tLAngleMax = tankAngle + Math.PI / 2;
			double tLAngleMin = tankAngle - Math.PI / 2;
			boolean lAngOverMax = false;
			boolean lAngUnderMin = false;
			if (tLAngleMax > 2 * Math.PI) {
				lAngOverMax = true;
				tLAngleMax = tLAngleMax - 2 * Math.PI;
			}
			if (tLAngleMin < 0) {
				lAngUnderMin = true;
				tLAngleMin = tLAngleMin + 2 * Math.PI;
			}
			if (lAngUnderMin == true || lAngOverMax == true) {
				if (! (mapAngle < tLAngleMax || mapAngle > tLAngleMin)){
					lengthDist = -lengthDist;
				}
			}
			else if (! (mapAngle < tLAngleMax && mapAngle > tLAngleMin)) {
				lengthDist = -lengthDist;
			}
			x = lengthDist / xPixelToCo + xSize / 2;
			
			double tWAngleMax = tankAngle + Math.PI;
			double tWAngleMin = tankAngle;
			boolean hAngOverMax = false;
			boolean hAngUnderMin = false;
			if(tWAngleMax > 2 * Math.PI) {
				hAngOverMax = true;
				tWAngleMax = tWAngleMax - 2 * Math.PI;
			}
			if(tLAngleMin < 0) {
				hAngUnderMin = true;
				tWAngleMin = tWAngleMin + 2 * Math.PI;
			}
			if (hAngUnderMin == true || hAngOverMax == true) {
				if ((mapAngle < tWAngleMax || mapAngle > tWAngleMin)){
					widthDist = -widthDist;
				}
			}
			else if ((mapAngle < tWAngleMax && mapAngle > tWAngleMin)) {
				widthDist = -widthDist;
			}
			//System.out.println(angleMin);
			//double angle = Math.atan((pointArray[j].getYStart() - sliceYPos) / (pointArray[j].getXStart() - sliceXPos));
			//if (mapAngle )
			/*
			if (!(Math.tan(mapAngle) < angle && angle <= Math.tan(mapAngle) - Math.PI)) {
				height2Pt = -height2Pt;
			}
			*/
			//System.out.println(mapAngle + " " + tLAngleMin);
			//System.out.println(widthDist);
			double colorF = 255 - 255 * (widthDist + maxRange / 2) / maxRange;
			//This stuff sets the tank color
			Color color;
			if (colorF / 255 > 1) {
				color = redColor;
			}
			else if (colorF / 255 > (double) (numColors - 1)/numColors) {
    			//gl.glColor3f(1.0f, numColors * (1.0f - (float) colorF), 0.0f);
    			color = new Color(255, (int) Math.round(numColors * (255 - colorF)), 0);
				//color = whiteColor;
    			//System.out.println((int) Math.round(numColors * (255 - colorF)));
    		}
    		else if (colorF / 255 > (double) (numColors - 2)/numColors) {
    			//gl.glColor3f(5.0f * ((float) colorF - (float) 1/5), 1.0f, 0.0f);
    			//gl.glColor3f(numColors * ((float) colorF - (float) (numColors - 1)/numColors) + 1f, 1.0f, 0.0f);
    			color = new Color ((int) Math.round(numColors * (colorF - 255 * (double) (numColors - 1) / numColors) + 255), 255, 0);
    			//System.out.println((int) Math.round(numColors * (colorF - 255 * (numColors - 1) / numColors) + 255));
    			//System.out.println(colorF);
    			//color = whiteColor;
    		}
    		else if (colorF / 255 > (double) (numColors - 3)/numColors) {
    			//gl.glColor3f(0.0f, 1.0f, numColors * (1.0f - (float) colorF) - 2.0f);
    			color = new Color(0, 255, (int) Math.round(numColors * (255 - colorF) - 2 * 255));
    		}
    		//else if (colorF > (double) (numColors - 4)/numColors) {
    		else if (colorF / 255 > 0) {
    			//gl.glColor3f(0.0f, numColors * ((float) colorF - (float) (numColors - 1)/numColors) + 3f, 1.0f);
    			color = new Color(0, (int) Math.round(numColors * (colorF - 255 * (double)(numColors - 1) / numColors) + 3 * 255), 255);
    			//System.out.println(numColors * (colorF - 255 * (numColors - 1) / numColors) + 3 * 255);
    			//System.out.println(4 * (63.75 - 255 * (double)(4-1) / 4) + 3 * 255);
    			//color = whiteColor;
    		}
    		else {
    			color = blueColor;
    		}
			double tankHeight = 0;
			for(int k = 0; k < pointArray.length; k++) {
				tankHeight += ((normalH / (Math.sqrt(2 * Math.PI) * Math.pow(Math.E,(Math.pow((tankArray[i].getX() - pointArray[k].getX()),2) + Math.pow((pointArray[k].getY() - tankArray[i].getY()),2) + Math.pow((pointArray[k].getHeightFactor()/ 2),2)) / 2))));
			//System.out.println(pointArray[k].getY());
			}
			g.setColor(color);
			g.fillRect((int)Math.round(x), (int)Math.round(ySize/2 - (tankHeight / yPixelToCo)), 3, 3);
			//System.out.println(tankHeight);
		}
		//Draws points on curve
		/*
		for(int j = 0; j < pointArray.length; j++) {
			g.setColor(redColor);
			double dispPointX = (Math.tan(mapAngle) * Math.tan(mapAngle) * sliceXPos - Math.tan(mapAngle) * (sliceYPos - pointArray[j].getYStart()) + pointArray[j].getXStart()) / (Math.tan(mapAngle) * Math.tan(mapAngle) + 1);
			double dispPointY = Math.tan(mapAngle) * (dispPointX - sliceXPos) + sliceYPos;
			double length2Pt = Math.sqrt(Math.pow(sliceXPos - dispPointX, 2) + Math.pow(sliceYPos - dispPointY, 2));
			double height2Pt = Math.sqrt(Math.pow(pointArray[j].getXStart() - dispPointX, 2) + Math.pow(pointArray[j].getYStart() - dispPointY, 2));
			/*if (pointArray[j].getYStart() <= dispPointY) {
				height2Pt = -height2Pt;
			}
			if (sliceXPos >= dispPointX) {
				length2Pt = -length2Pt;
			*
			System.out.println(mapAngle);
			double angle = Math.atan((pointArray[j].getYStart() - sliceYPos) / (pointArray[j].getXStart() - sliceXPos));
			//if (mapAngle )
			if (!(Math.tan(mapAngle) < angle && angle <= Math.tan(mapAngle) - Math.PI)) {
				height2Pt = -height2Pt;
			}
			if (sliceXPos >= dispPointX) {
				length2Pt = -length2Pt;
			}
			g.fillRect(XToPixel(length2Pt), YToPixel(height2Pt), 3, 3);
		}
		*/
		/*
		for(int i = 0; i >= -2; i --){
			g.drawLine(0, (int) Math.round((double) ySize/2 - i), (int) xSize, (int) Math.round((double) ySize/2 - i));
		}
		*/
		//y = returnArray();
		//draws points on screen
		/*
		g.fillRect(XToPixel(pt1XCo), YToPixel(pt1YCo), 3, 3);
		g.fillRect(XToPixel(pt2XCo), YToPixel(pt2YCo), 3, 3);
		*/
		/*
		for (int i = 0; i < pointArray.length; i++) {
			g.fillRect(XToPixel(pointArray[i].getX()), YToPixel(pointArray[i].getY()), 3, 3);
		}
		
		g.setColor(redColor);
		*/
		
		
		//Graphs the derivative
		/*
		double[] derivArray = new double[(int) xSize];
		for (int j = 0; j < pointArray.length; j++) {
			for(int i=0; i < xSize; i++) {
				double xChange = ((double) i) * (xRange / xSize) - xRange / 2;
				//System.out.println(xChange);
				//System.out.println(xChange);
				//System.out.println("x = " + pointArray[j].getX() + " y + " + pointArray[j].getY());
				//y[i] = ((double)myYSize/myYRange) * Math.sin(((double) i - myExtra) * ((double)myXRange / myXSize) - myXRange / 2);
				//graphArray[i] += ((5/(Math.sqrt(2 * 3.14) * Math.pow(2.72,(Math.pow((xChange - pointArray[j].getX()),2) + Math.pow((pointArray[j].getY() - yChange),2) + Math.pow((pointArray[j].getHeightFactor() / 2),2)) / 2))));
				//graphArray[i] = ((5/(Math.sqrt(2 * Math.PI) * Math.pow(Math.E,(Math.pow((xChange - pt1XCo),2) + Math.pow((pt1YCo-yChange),2) + Math.pow((2/ 2),2)) / 2)) + 5/(Math.sqrt(2 * Math.PI) * Math.pow(Math.E,(Math.pow((xChange - pt2XCo),2) + Math.pow((pt2YCo-yChange),2) + Math.pow((0 / 2),2)) / 2))));
				double y1 = Math.sqrt(Math.pow(xChange - pointArray[j].getX(), 2) + Math.pow(pointArray[j].getY() - yChange, 2) + Math.pow(pointArray[j].getHeightFactor() / 2, 2));
				double y1Prime = (1 / 2)* Math.pow((Math.pow(xChange - pointArray[j].getX(), 2) + Math.pow(pointArray[j].getY() - yChange, 2) + Math.pow(pointArray[j].getHeightFactor() / 2, 2)), - 1 / 2) * 2 * (xChange - pointArray[j].getX());
				derivArray[i] += -5 * Math.pow(Math.sqrt(2 * Math.PI) * Math.pow(Math.E, y1 * y1 / 2), -2) * (Math.sqrt(2 * Math.PI) * Math.pow(Math.E, y1 * y1 / 2) * ((1/2) * 2 * y1 * y1Prime));
			}
		}
		for(int i = 1; i < xSize; i++) {
			int yCorner = YToPixel(derivArray[i]);
			g.fillRect(i, yCorner, 3, 3);
			//g.drawLine(i, yCorner, i, (int) Math.round(ySize / 2));
		}
		*/
		gr.drawImage(drawing, 0,0, this);
		//System.out.println(YToPixel(y[150]) + " " + YToPixel(graphArray[150]));
		//}


		// reset the color to the standard color for the next time the applets paints
		// an applet is repainted when a part was'nt visible anymore
		// happens most often because of browser minimizing or scrolling. 

		//g.setColor(Color.black);

	}
	
	//Methods
	public int XToPixel(double x) {
		return (int) Math.round((double) (xSize/xRange) * x + (double) xSize / 2);	
	}
	public int YToPixel(double y) {
		return (int) Math.round(-(double) (ySize/yRange) * y + (double) ySize / 2);
	}
	/*
	public double[] returnArray () {
		double[] y = new double[(int) xSize];
		double x = 0;
		yChange = 0;
		for(int i = 0; i < xSize; i++) {
			x = ((double) i - (double) xSize / 2) * xPixelToCo;
			//y[i] = ((double)ySize/yRange) * Math.sin(((double) i - Grapher.myExtra) * ((double)xRange / xSize) - xRange / 2);
			y[i] = ((5/(Math.sqrt(2 * Math.PI) * Math.pow(Math.E,(Math.pow((x - pt1XCo),2) + Math.pow((pt1YCo-yChange),2) + Math.pow((2/ 2),2)) / 2)) + 5/(Math.sqrt(2 * Math.PI) * Math.pow(Math.E,(Math.pow((x - pt2XCo),2) + Math.pow((pt2YCo-yChange),2) + Math.pow((0 / 2),2)) / 2))));
		}
		return y;
	}
	*/
	
	public double[] mapArray() {
		double[] y = new double[(int) mapSize];
		double x = 0;
		double xMin = 0;
		double xMax = 0;
		double bottomIntc;
		double topIntc;
		if (mapAngle != 0) {
			bottomIntc = (-(double) maxRange / 2 - mapPixelToCo * mapYPos) / Math.tan(mapAngle) + mapPixelToCo * mapXPos;
			topIntc = (maxRange - mapPixelToCo * mapYPos) / Math.tan(mapAngle) + mapPixelToCo * mapXPos;
			if (bottomIntc <= topIntc) {
				if (bottomIntc > -(double) maxRange / 2) {
					xMin = bottomIntc;
				}
				else {
					xMin = -maxRange / 2;
				}
				if (topIntc < maxRange / 2) {
					xMax = topIntc;
				}
				else {
					xMax = maxRange / 2;
				}
			}
			else {
				if (topIntc > -maxRange / 2) {
					xMin = topIntc;
				}
				else {
					xMin = -maxRange / 2;
				}
				if (bottomIntc < maxRange / 2) {
					xMax = bottomIntc;
				}
				else {
					xMax = maxRange / 2;
				}
			}
			if (bottomIntc != topIntc && (!(topIntc < -maxRange / 2 && bottomIntc < -maxRange / 2) && !(topIntc > maxRange / 2 && bottomIntc > maxRange / 2))) {
				for (int i = 0; i < mapXToPixel(xMin); i++) {
					y[i] = maxRange - 1;
				}
				for(int i = mapXToPixel(xMin); i < mapXToPixel(xMax); i++) {
					x = ((double) i + 1 - mapSize / 2) * mapPixelToCo;
					y[i] = Math.tan(mapAngle)*(x - mapPixelToCo * mapXPos) + mapPixelToCo * mapYPos;
				}
				for (int i = mapXToPixel(xMax); i < mapSize; i++) {
					y[i] = maxRange - 1;
				}
			}
			else {
				for (int i = 0; i < mapSize; i++) {
					y[i] = maxRange - 1;
				}
			}
		}
		else {
			for (int i = 0; i < mapSize; i++) {
				y[i] = mapPixelToCo * mapYPos;
			}
		}
		return y;
	}
	
	public double[][] shadingArray() {
		double[][] myShading = new double[(int) mapSize][(int) mapSize];
		double x;
		for (int k = 0; k < pointArray.length; k++) {
			for (int i = 0; i < mapSize; i++) {
				yChange = (i - (double) mapSize / 2) * mapPixelToCo;
				for (int j = 0; j < mapSize; j++) {
					x = (j - (double) mapSize / 2) * mapPixelToCo;
					myShading[i][j] += ((normalH / (Math.sqrt(2 * Math.PI) * Math.pow(Math.E,(Math.pow((x - pointArray[k].getX()),2) + Math.pow((pointArray[k].getY() - yChange),2) + Math.pow((pointArray[k].getHeightFactor()/ 2),2)) / 2))));
				}
			}
		}
		return myShading;
	}
	
	public int mapXToPixel(double x) {
		return (int) Math.round(((double) mapSize/maxRange) * x + (double) mapSize / 2);
	}
	
	public int mapYToPixel(double y) {
		return (int) Math.round(((double) mapSize/maxRange) * y + (double) mapSize / 2);
	}
	
} 