package graph;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Random;

import graph.Graph.Node;
import graph.Grid.Cell;
import javafx.animation.Animation;
import javafx.animation.FillTransition;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

public class Map {

	public Grid grid;
	public Graph graph;

	public int size = 48;
	public int padding = 2;

	public Grid.Cell start;
	public Grid.Cell destination;

	Dictionary<Rectangle, Grid.Cell> rectCells = new Hashtable<Rectangle, Grid.Cell>();
	Dictionary<Grid.Cell, ArrayList<Object>> mapCells = new Hashtable<Grid.Cell, ArrayList<Object>>();

	int width;
	int height;

	public Map() {
		this(10, 10);
		graph = new Graph(grid);
	}

	public Map(int w, int h) {
		width = w;
		height = h;
		grid = new Grid(w, h);

		graph = new Graph(grid);
	}

	public Map(int w, int h, int seed) {
		width = w;
		height = h;
		grid = new Grid(w, h);
		randomize(seed);

		graph = new Graph(grid);
	}

	public Map(Grid grid) {
		this.grid = grid;
	}

	public void randomize(int seed) {
		reset();

		Random rng = new Random(seed);
		int x = rng.nextInt(width);
		int y = rng.nextInt(height);

		start = grid.grid[x][y];
		while (!start.isEmpty) {
			x = rng.nextInt(width);
			y = rng.nextInt(height);
			start = grid.grid[x][y];
		}
		grid.grid[x][y].isStart = true;

		destination = grid.grid[x][y];
		while (!destination.isEmpty || destination == start) {
			x = rng.nextInt(width);
			y = rng.nextInt(height);
			destination = grid.grid[x][y];
		}
		grid.grid[x][y].isDest = true;
		destination.isDest = true;

		for (int i = 0; i < width * height / 4; i++) {
			x = rng.nextInt(width);
			y = rng.nextInt(height);
			if (grid.grid[x][y].isEmpty && !grid.grid[x][y].isStart && !grid.grid[x][y].isDest) {
				grid.grid[x][y].setEmpty(rng.nextInt() == 4);
			}
		}

	}
	
	public double clamp(double val, double min, double max) {
		if (val > max) {
			return max;
		} else if (val < min) {
			return min;
		} else {
			return val;
		}
	}

	public double lerp(double start, double end, double step) {
		// total work x percentage of work done + initial work done
		// ((distance) * normalized displacement) + start
		// ((end - start) * step) + start
		
		double distance = end - start; // total work
		return clamp(distance * step + start, 0, 1);
		
	}
	
	public double boundingLerp(double start, double end, double step) {
		// total work x percentage of work done + initial work done
		// ((distance) * normalized displacement) + start
		// ((end - start) * step) + start
		// Calculate a lerp that overshoots with a sin function before returning to 1

		double boundingStep = (Math.sin(4.7195 * step - Math.PI/2) + 1);
		
		if ((step + 0.001) >= 1) {
			boundingStep = 1;
		}
			
		double distance = end - start; // total work
		return distance * boundingStep + start;

	}

	public void blendColor(Shape rect, String hex) {
		blendColor(rect, hex, 1);
	}
	
	public void blendColor(Shape rect, String hex, double timeScale) {
		if (Configuration.getCanShowAnimation()) {
			fadeAnimation(rect, Color.valueOf(hex),
					(Configuration.simulationRate * 5) / ((timeScale > 0) ? timeScale : 0));
		}
	}
	
	public void bouceColor(Shape rect, String hex) {
		bouceColor(rect, hex, 1);
	}

	public void bouceColor(Shape rect, String hex, double timeScale) {
		if (Configuration.getCanShowAnimation()) {
			overFadeAnimation(rect, Color.valueOf(hex),
					(Configuration.simulationRate * 5) / ((timeScale > 0) ? timeScale : 0));
		}
	}

	public void fadeAnimation(Shape shape, Color finalColor, double duration) {
		Animation fade = new Transition() {
			Color initColor;
			{
				setCycleDuration(Duration.millis(duration));
				initColor = Color.valueOf(shape.getFill().toString());
			}
			
			@Override
			protected void interpolate(double step) {
				Color color = Color.color(clamp(lerp(initColor.getRed(), finalColor.getRed(), step), 0, 1),
						clamp(lerp(initColor.getGreen(), finalColor.getGreen(), step), 0, 1),
						clamp(lerp(initColor.getBlue(), finalColor.getBlue(), step), 0, 1));
				
				shape.setFill(color);
			}
			
		};
		fade.play();
	}
	
	public void overFadeAnimation(Shape shape, Color finalColor, double duration) {
		Animation fade = new Transition() {
			Color initColor;
			{
				setCycleDuration(Duration.millis(duration));
				initColor = Color.valueOf(shape.getFill().toString());
			}

			@Override
			protected void interpolate(double step) {
				Color color = Color.color(clamp(boundingLerp(initColor.getRed(), finalColor.getRed(), step), 0, 1),
						clamp(boundingLerp(initColor.getGreen(), finalColor.getGreen(), step), 0, 1),
						clamp(boundingLerp(initColor.getBlue(), finalColor.getBlue(), step), 0, 1));

				shape.setFill(color);
			}

		};
		fade.play();
	}

	public void reset() {
		for (int y = 0; y < grid.height; y++) {
			for (int x = 0; x < grid.width; x++) {
				grid.grid[x][y].reset();
			}
		}

		updateCells();
	}

	public void updateCells() {
		for (int y = 0; y < grid.height; y++) {
			for (int x = 0; x < grid.width; x++) {
				updateCell(grid.grid[x][y]);
			}
		}
	}

	public void updateCell(Grid.Cell cell) {
		Platform.runLater(new Runnable() {
			public void run() {
				Rectangle rect = (Rectangle) mapCells.get(cell).get(0);
				Label val = (Label) mapCells.get(cell).get(1);

				if (cell.isFound) {
					bouceColor(rect, "#efe498");
					val.setText(":)");
					return;
				}
				if (cell.isVoid) {
					blendColor(rect, "#222222");
					val.setText(":(");
					return;
				}
				
				if (cell.isPath) {
					System.out.println("Path");
					bouceColor(rect, "#e58c51");
					val.setText("");
					return;
				}

				if (!cell.isDest && !cell.isStart) {
					configureEmpty(rect, val, cell);
					configureVisited(rect, val, cell);
				} else {
					configureGoals(rect, val, cell);
				}

				if (!Configuration.getCanShowNumbers()) {
					val.setText("");
				}
			}

		});

	}

	public void configureEmpty(Rectangle rect, Label val, Cell cell) {
		if (cell.isEmpty) {
			blendColor(rect, "#dedede");
			val.setText("");

			val.setText("" + (cell.y * grid.width + cell.x));
			val.setStyle("-fx-font-weight: 300;");
		} else {
			blendColor(rect, "#77777f");
			val.setText("-");
		}
	}

	public boolean configureGoals(Rectangle rect, Label val, Cell cell) {
		if (cell.isStart) {
			blendColor(rect, "#b0de9d");
			val.setText("S");
			return true;
		} else if (cell.isDest) {
			blendColor(rect, "#cf8278");
			val.setText("D");
			return true;
		}
		return false;
	}

	public void configureVisited(Rectangle rect, Label val, Cell cell) {
		if (cell.isVisited) {
			bouceColor(rect, "#c2e3f3", 0.8);
		}
	}
}
