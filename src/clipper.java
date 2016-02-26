/*
 * clipper.java
 * 
 * Version: $Id: clipper.java, v 1.1 2015/24/03 16:47:12
 * 
 * Revisions: 
 * 		
 * Initial Revision
 * 
 */

/**
 * Object for performing clipping
 *
 * @author Sanika Kulkarni
 */

import java.util.ArrayList;

public class clipper {

	// declaring array lists to store edges and vertices of polygons
	ArrayList<float[]> listOfEdges = new ArrayList<float[]>();
	ArrayList<float[]> listOfVertices = new ArrayList<float[]>();

	/**
	 * checkLineInside
	 * 
	 * To check if the edge lies inside the clipping rectangle
	 * 
	 * @param vertex
	 *            - contains the list of vertices of the polygon
	 * @param edge
	 *            - the edge of polygon to be checked
	 * @param count
	 *            - to indicate the bottom top left or right of the clipping
	 *            window
	 * 
	 * @return boolean - value to indicate if the edge is inside or outside
	 * 
	 */
	public boolean checkLineInside(float[] vertex, float[] edge, int count) {

		if (count == 0) {
			return vertex[1] >= edge[1];
		} else if (count == 1) {
			return vertex[0] <= edge[0];
		} else if (count == 2) {
			return vertex[1] <= edge[1];
		} else if (count == 3) {
			return vertex[0] >= edge[0];
		}
		return false;

	}

	/**
	 * newIntersectedPoint
	 * 
	 * To find the new vertex of the edge of polygon with respect to the
	 * clipping window
	 * 
	 * @param vertex1
	 *            - contains the list of vertices of the polygon
	 * @param veretex2
	 *            - contains the list of vertices of the polygon
	 * @param edge
	 *            - the edge of polygon to be checked
	 * @param count
	 *            - to indicate the bottom top left or right of the clipping
	 *            window
	 * 
	 * @return newPoint - the new vertex with the point of intersection
	 * 
	 */

	public float[] newIntersectedPoint(int count, float[] vertex1,
			float[] vertex2, float[] edge) {
		float NumeratorX = ((vertex2[0] - vertex1[0])
				* (edge[0] * edge[3] - edge[2] * edge[1]) - (edge[2] - edge[0])
				* (vertex1[0] * vertex2[1] - vertex2[0] * vertex1[1]));
		float NumeratorY = ((edge[1] - edge[3])
				* (vertex1[0] * vertex2[1] - vertex2[0] * vertex1[1]) - (vertex1[1] - vertex2[1])
				* (edge[0] * edge[3] - edge[2] * edge[1]));
		float denominator = ((vertex1[0] - vertex2[0]) * (edge[1] - edge[3]) - (vertex1[1] - vertex2[1])
				* (edge[0] - edge[2]));
		float x = NumeratorX / denominator;
		float y = NumeratorY / denominator;

		float newPoint[] = { x, y };
		return newPoint;
	}

	/**
	 * edgesOfPolygon
	 * 
	 * To find the all the edges of the polygon
	 * 
	 * @param x0
	 *            - the lower left x coordinate
	 * @param y0
	 *            - the lower left y coordinate
	 * @param x1
	 *            - the upper right x coordinate
	 * @param y1
	 *            - the upper right y coordinate
	 * 
	 */
	public void edgesOfPolygon(float x0, float y0, float x1, float y1) {
		listOfEdges.clear();
		int n = 4;
		float clippingX[] = { x0, x1, x1, x0 };
		float clippingY[] = { y0, y0, y1, y1 };
		int i = 0;
		while (i < n) {
			int j = (i + 1) % n;
			float listOfVertex[] = { clippingX[i], clippingY[i], clippingX[j],
					clippingY[j] };
			listOfEdges.add(listOfVertex);
			i++;
		}
	}

	/**
	 * VertexList
	 * 
	 * To find the all the vertices of the polygon
	 * 
	 * @param xCoordinate
	 *            - the x coordinate
	 * @param yCoordinate
	 *            - the y coordinate
	 * @param numberOfVertices
	 *            - the number of vertices
	 * 
	 */

	public void VertexList(float[] xCoordinate, float[] yCoordinate,
			int numberOfVertices) {
		listOfVertices.clear();
		int i = 0;
		while (i < numberOfVertices) {
			float listOfVertex[] = { xCoordinate[i], yCoordinate[i] };
			listOfVertices.add(listOfVertex);
			i++;
		}
	}

	/**
	 * clipPolygon
	 *
	 * Clip the polygon with vertex count in and vertices inx/iny against the
	 * rectangular clipping region specified by lower-left corner (x0,y0) and
	 * upper-right corner (x1,y1). The resulting vertices are placed in
	 * outx/outy.
	 *
	 * The routine should return the the vertex count of the polygon resulting
	 * from the clipping.
	 *
	 * @param numberOfVertices
	 *            the number of vertices in the polygon to be clipped
	 * @param xCoordinate
	 *            - x coords of vertices of polygon to be clipped.
	 * @param yCoordinate
	 *            - y coords of vertices of polygon to be clipped.
	 * @param outx
	 *            - x coords of vertices of polygon resulting after clipping.
	 * @param outy
	 *            - y coords of vertices of polygon resulting after clipping.
	 * @param x0
	 *            - x coord of lower left of clipping rectangle.
	 * @param y0
	 *            - y coord of lower left of clipping rectangle.
	 * @param x1
	 *            - x coord of upper right of clipping rectangle.
	 * @param y1
	 *            - y coord of upper right of clipping rectangle.
	 *
	 * @return number of vertices in the polygon resulting after clipping
	 *
	 */
	public int clipPolygon(int numberOfVertices, float xCoordinate[],
			float yCoordinate[], float outx[], float outy[], float x0,
			float y0, float x1, float y1) {

		VertexList(xCoordinate, yCoordinate, numberOfVertices);
		edgesOfPolygon(x0, y0, x1, y1);

		int count = 0;

		for (float[] edge : listOfEdges) {
			ArrayList<float[]> tempList = new ArrayList<float[]>(listOfVertices);
			if (tempList.size() == 0) {
				return 0;
			}
			int temp = tempList.size() - 1;
			float v[] = tempList.get(temp);
			listOfVertices.clear();
			for (float p[] : tempList) {
				if (checkLineInside(p, edge, count)) {
					if (checkLineInside(v, edge, count)) {
						listOfVertices.add(p);

					} else {
						float i[] = newIntersectedPoint(count, v, p, edge);
						listOfVertices.add(i);
						listOfVertices.add(p);
					}
				} else {
					if (checkLineInside(v, edge, count)) {
						float i[] = newIntersectedPoint(count, v, p, edge);
						listOfVertices.add(i);
					}
				}
				v = p;
			}
			count++;
		}
		for (int j = 0; j < listOfVertices.size(); j++) {
			outx[j] = listOfVertices.get(j)[0];
			outy[j] = listOfVertices.get(j)[1];
		}
		return listOfVertices.size();
	}

}
