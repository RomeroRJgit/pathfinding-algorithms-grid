package graph;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Random;

import graph.Grid.Cell;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;

public class Map {

	public Grid grid;
	public Graph graph;
	
	public int size = 48;
	public int padding = 2;
	
	public Grid.Cell start;
	public Grid.Cell destination;
	
	Dictionary<Rectangle, Grid.Cell> rectCells = new Hashtable<Rectangle, Grid.Cell>();
	Dictionary<Grid.Cell, ArrayList<Object>> mapCells = new Hashtable<Grid.Cell, ArrayList<Object>>();
	
	
	public Map() {
		this(10, 10);
		graph = new Graph(grid);	
	}
	
	public Map(int w, int h) {
		grid = new Grid(w, h);
		
		Random rng = new Random();		
		int x =rng.nextInt(w);
		int y =rng.nextInt(h);	
		
		start = grid.grid[x][y];
		while (!start.isEmpty) {
			x =rng.nextInt(w);
			y =rng.nextInt(h);			
			start = grid.grid[x][y];			
		}
		grid.grid[x][y].isStart = true;
			
		destination = grid.grid[x][y];
		while (!destination.isEmpty || destination == start) {
			x =rng.nextInt(w);
			y =rng.nextInt(h);				
			destination = grid.grid[x][y];			
		}
		grid.grid[x][y].isDest = true;
		destination.isDest = true;

		graph = new Graph(grid);		
	}
	
	public Map(Grid grid) {
		this.grid = grid;
	}
		
	public void updateCell(Grid.Cell cell) {
		Platform.runLater(new Runnable() {
			public void run() {
				Rectangle rect = (Rectangle)mapCells.get(cell).get(0);
				Label val = (Label)mapCells.get(cell).get(1);
											
				//System.out.println("> " + cell.x + ", " + cell.y);
				
				if (cell.isFound) {
					rect.setStyle("-fx-fill: #f6eab6;");
					val.setText(":)");
					return;
				}
				if (cell.isVoid) {
					rect.setStyle("-fx-fill: #222222;");
					val.setText(":(");
					return;
				}
				
				if (!cell.isDest && !cell.isStart) {
					configureEmpty(rect, val, cell);	
					configureVisited(rect, val, cell);	
				}
			}

		});
			
	}

	public void configureEmpty(Rectangle rect, Label val, Cell cell) {
    	if (cell.isEmpty) {
    		rect.setStyle("-fx-fill: #dedede;");
    		val.setText(" ");	
    		val.setText("" + (cell.y * grid.width + cell.x));	
    		val.setStyle("-fx-font-weight: 300;");
    	} else {
    		rect.setStyle("-fx-fill: #666666"
    				+ ";");
    		val.setText("-");
    	}
    }
    
	public boolean configureGoals(Rectangle rect, Label val, Cell cell) {
		if (cell.isStart) {
			System.out.println("START");
			rect.setStyle("-fx-fill: #d6ead6;");
			val.setText("S");	
			return true;
		} else if (cell.isDest) {
			rect.setStyle("-fx-fill: #ead6d6;"); 
			val.setText("D");	
			return true;
		}
		return false;
	}
	
	public void configureVisited(Rectangle rect, Label val, Cell cell) {
    	if (cell.isVisited) {
    		rect.setStyle("-fx-fill: #deeaef;");
			val.setText("");	
    	}
    }
}
