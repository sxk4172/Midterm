/*
 * cgCanvas.java
 * 
 * Version: $Id: cgCanvas.java, v 1.1 2015/24/03 16:47:43
 * 
 * Revisions: 
 * 		
 * Initial Revision
 * 
 */

/**
 * This is a simple canvas class for adding functionality for the
 * 2D portion of Computer Graphics.
 * 
 * @author Sanika Kulkarni
 */

import java.util.*;
import Jama.*;

public class cgCanvas extends simpleCanvas {

	private int numberOfPolygons = 0;
	private ArrayList<Poly> arrList = new ArrayList<Poly>();
	private Matrix normalizeM, viewM;
	float[] outputX = new float[50];
	float[] outputY = new float[50];
	Rasterizer R;
	clipper C = new clipper();
	private Poly windowClipped;

	/**
	 * Constructor
	 *
	 * @param w
	 *            width of canvas
	 * @param h
	 *            height of canvas
	 */

	cgCanvas(int w, int h) {
		super(w, h);
		R = new Rasterizer(h);
	}

	/**
	 * addPoly - Adds and stores a polygon to the canvas. Note that this method
	 * does not draw the polygon, but merely stores it for later draw. Drawing
	 * is initiated by the draw() method.
	 *
	 * Returns a unique integer id for the polygon.
	 *
	 * @param x
	 *            - Array of x coords of the vertices of the polygon to be added
	 * @param y
	 *            - Array of y coords of the vertices of the polygin to be added
	 * @param n
	 *            - Number of verticies in polygon
	 *
	 * @return a unique integer identifier for the polygon
	 */

	public int addPoly(float x[], float y[], int n) {
		arrList.add(new Poly(x, y, n, Matrix.identity(3, 3)));
		return numberOfPolygons++;
	}

	/**
	 * clearTransform - Set the current transformation to the identity matrix.
	 */

	public void clearTransform() {

		for (Poly i : arrList) {
			i.changesInPoly = Matrix.identity(3, 3);
		}
	}

	/**
	 * drawPoly - Draw the polygon with the given id. Should draw the polygon
	 * after applying the current transformation on the vertices of the polygon.
	 *
	 * @param polyID
	 *            - the ID of the polygon to be drawn
	 */

	public void drawPoly(int polyID) {

		float[][] polys = arrList.get(polyID).changesP();
		Matrix multiplicationM = viewM.times(normalizeM);

		float[][] nPolys = new Poly(polys[0], polys[1],
				arrList.get(polyID).numberOfP, multiplicationM).changesP();

		windowClipped.changesInPoly = multiplicationM;
		float[][] cPolys = windowClipped.changesP();

		int nVertices = C.clipPolygon(arrList.get(polyID).numberOfP, nPolys[0],
				nPolys[1], outputX, outputY, cPolys[0][0], cPolys[1][0],
				cPolys[0][2], cPolys[1][2]);

		int[] x = new int[nVertices];
		int[] y = new int[nVertices];

		int i = 0;
		while (i < nVertices) {
			x[i] = (int) outputX[i];
			y[i] = (int) outputY[i];
			i++;
		}

		R.drawPolygon(nVertices, x, y, this);
	}

	/**
	 * rotate - Add a rotation to the current transformation by pre-multiplying
	 * the appropriate rotation matrix to the current transformation matrix.
	 *
	 * @param degrees
	 *            - Amount of rotation in degrees
	 */

	public void rotate(float degrees) {

		double degreesToRadians = degrees * (3.14 / 180);

		for (Poly i : arrList) {
			i.changesInPoly = new Matrix(
					new double[][] {
							{ Math.cos(degreesToRadians),
									Math.sin((-1) * degreesToRadians), 0 },
							{ Math.sin(degreesToRadians),
									Math.cos(degreesToRadians), 0 },
							{ 0, 0, 1 } }).times(i.changesInPoly);
		}
	}

	/**
	 * scale - Add a scale to the current transformation by pre-multiplying the
	 * appropriate scaling matrix to the current transformation matrix.
	 *
	 * @param x
	 *            - Amount of scaling in x
	 * @param y
	 *            - Amount of scaling in y
	 */

	public void scale(float x, float y) {
		for (Poly i : arrList) {
			i.changesInPoly = new Matrix(new double[][] { { (double) x, 0, 0 },
					{ 0, (double) y, 0 }, { 0, 0, 1 } }).times(i.changesInPoly);
		}
	}

	/**
	 * setClipWindow - defines the clip window
	 *
	 * @param bottom
	 *            - y coord of bottom edge of clip window (in world coords)
	 * @param top
	 *            - y coord of top edge of clip window (in world coords)
	 * @param left
	 *            - x coord of left edge of clip window (in world coords)
	 * @param right
	 *            - x coord of right edge of clip window (in world coords)
	 */

	public void setClipWindow(float bottom, float top, float left, float right) {
		float rl = (right - left);
		float tb = (top - bottom);
		normalizeM = new Matrix(new double[][] {
				{ 2 / rl, 0, (((-2 * left) - rl) / rl) },
				{ 0, 2 / tb, (((-2 * bottom) - tb) / tb) }, { 0, 0, 1 } });
		float temp1[] = new float[] { left, right, right, left };
		float temp2[] = new float[] { bottom, bottom, top, top };
		windowClipped = new Poly(temp1, temp2, 4, Matrix.identity(3, 3));

	}

	/**
	 * setViewport - defines the viewport
	 *
	 * @param xmin
	 *            - x coord of lower left of view window (in screen coords)
	 * @param ymin
	 *            - y coord of lower left of view window (in screen coords)
	 * @param width
	 *            - width of view window (in world coords)
	 * @param height
	 *            - width of view window (in world coords)
	 */

	public void setViewport(int x, int y, int width, int height) {
		viewM = new Matrix(new double[][] {
				{ (width / 2), 0, ((width + 2 * x) / 2) },
				{ 0, (height / 2), ((height + 2 * y) / 2) }, { 0, 0, 1 } });

	}

	/**
	 * translate - Add a translation to the current transformation by
	 * pre-multiplying the appropriate translation matrix to the current
	 * transformation matrix.
	 *
	 * @param x
	 *            - Amount of translation in x
	 * @param y
	 *            - Amount of translation in y
	 */

	public void translate(float x, float y) {
		for (Poly i : arrList) {
			i.changesInPoly = new Matrix(new double[][] { { 0, 0, (double) x },
					{ 0, 0, (double) y }, { 0, 0, 0 } }).plus(i.changesInPoly);
		}
	}

	/**
	 * 
	 * @author Sanika Kulkarni
	 *
	 */

	public class Poly {
		private int numberOfP;
		private Matrix changesInPoly;
		private Matrix[] matricesOfVertices;

		/**
		 * 
		 * @param x
		 *            - x coordinate
		 * @param y
		 *            - y coordinate
		 * @param n
		 *            - number of polygons
		 * @param changesInPolygon
		 */

		public Poly(float[] x, float[] y, int n, Matrix changesInPolygon) {
			numberOfP = n;
			changesInPoly = changesInPolygon;
			matricesOfVertices = new Matrix[n];
			int i = 0;
			while (i < n) {
				matricesOfVertices[i] = new Matrix(new double[][] { { x[i] },
						{ y[i] }, { 1 } });
				i++;
			}
		}

		/**
		 * 
		 * @return temp - transformations in x and y
		 */

		public float[][] changesP() {
			float temp[][] = new float[2][numberOfP];
			int i = 0;
			while (i < numberOfP) {
				double[][] newVertex = changesInPoly.times(
						matricesOfVertices[i]).getArray();
				temp[0][i] = (float) newVertex[0][0];
				temp[1][i] = (float) newVertex[1][0];
				i++;
			}
			return temp;
		}

	}
}