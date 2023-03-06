package graph;

import java.util.HashMap;
import java.util.Random;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Grid {

	public class Coord {
		private int x;
		private int y;
		
		public Coord() {
			this.x = 0;
			this.y = 0;
		}
		
		public Coord(int x ,int y) {
			this.x = x;
			this.y = y;
		}
		
		public int getX() {
			return x;
		}
		
		public int getY() {
			return y;
		}
	}
	
	public class Cell {
		boolean isEmpty;
		boolean isStart;
		boolean isDest;			
		boolean isVisited;
		boolean isFound;
		boolean isVoid;
		boolean isPath;
		int key;
		int x;
		int y;
				
		private BooleanProperty isEmptyProperty = new SimpleBooleanProperty(isEmpty);
		private BooleanProperty isVisitedProperty = new SimpleBooleanProperty(isVisited);
		private ObjectProperty<Cell> objectProperty = new SimpleObjectProperty<Cell>(this);
		
		public final BooleanProperty getIsVisitedProperty() { return isVisitedProperty; }
		public final BooleanProperty getIsEmptyProperty() { return isEmptyProperty; }
		public final ObjectProperty<Cell> getObjectProperty() { return objectProperty; }
		
		public Cell() {
			this.isEmpty = true;
		}
		
		public void setEmpty(boolean isEmpty) {
			if (!isStart && !isDest) {
				this.isEmpty = isEmpty; 
			}
		}
		
		public void setIsStart(boolean isStart) {
			if (isEmpty) {
				this.isStart = isStart; 
			}
		}
		
		public void setIsDest(boolean isDest) {
			if (isEmpty) {
				this.isDest = isDest; 
			}
		}
		
		public void setIsVisited(boolean isVisited) {
			this.isVisited = isVisited; 
		}
		
		public void visit() {
			if (!isVisited && isEmpty) {
				this.isVisited = true; 
			}
		}
		
		public void traverse() {
			if (isVisited) {
				this.isPath = true; 
			}
		}
		
		public void reset() {
			this.isEmpty = true; 
			this.isStart = false; 
			this.isDest = false; 
			this.isFound = false; 
			this.isPath = false; 
			this.isVisited = false; 
			this.isPath = false; 
		}
	}
	
	public Cell[][] grid;
	public int width;
	public int height;
	
	java.util.Map<Integer, Grid.Cell> cells = new HashMap<Integer, Grid.Cell>();
	
	public Grid() {
		this(10, 10);
	}
	
	public Grid(int width, int height) {
		this.width = width;
		this.height = height;
		
		grid = new Cell[width][height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				grid[x][y] = new Cell();
				grid[x][y].x = x;
				grid[x][y].y = y;
				cells.put(y * width + x, grid[x][y]);
			}
		}
	}
	
	public void clear() {		
		grid = new Cell[width][height];
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				grid[x][y] = new Cell();
				grid[x][y].x = x;
				grid[x][y].y = y;
				cells.put(y * width + x, grid[x][y]);
			}
		}
	}
	
	public void empty() {				
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				grid[x][y].setEmpty(true);				
			}
		}
	}
	
	public void fill() {				
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				grid[x][y].setEmpty(false);				
			}
		}
	}

		
}
