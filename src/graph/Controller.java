package graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Function;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public class Controller {

	@FXML
	private Menu menu;

	@FXML
	private MenuBar menuBar;

	@FXML
	private GridPane gridMap;

	@FXML
	private Label subheader;

	@FXML
	private Button generateBtn;

	@FXML
	private Button searchBtn;

	@FXML
	private HBox footer;

	Map map;
	final java.util.Map<Integer, List<String>> algorithms = new HashMap<Integer, List<String>>() {
		{
			put(0, Arrays.asList("Random Guess", "RNG"));
			put(1, Arrays.asList("Breadth First Search", "BFS"));
			put(2, Arrays.asList("Depth First Search", "DFS"));
			put(3, Arrays.asList("Dikjstra Shortest Path", "Dikjstra"));
			put(3, Arrays.asList("A*", "A Star"));
		}
	};

	final java.util.Map<Integer, List<String>> generations = new HashMap<Integer, List<String>>() {
		{
			put(0, Arrays.asList("Shuffle", "Shuffle"));
			put(1, Arrays.asList("Random DFS Maze", "DFS Maze"));
		}
	};

	int algorithm = 1;
	int generation = 0;

	public void initialize() {
		chooseAlgoritm(algorithm);
		chooseMaze(generation);
		
		map = new Map(10, 10);
		generateMap(map);

		subheader.setText("Breadth First Search");
		// menuBar.setUseSystemMenuBar(true);

		footer.setOnMouseDragged(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent evt) {
				if ((int) evt.getY() > 0 && (int) evt.getY() < 30) {
					footer.setPrefHeight(footer.prefHeightProperty().get() + 2);
				} else if ((int) evt.getY() > 0 && (int) evt.getY() > 40) {
					footer.setPrefHeight(footer.prefHeightProperty().get() - 2);
				}

				evt.consume();
			}
		});

		footer.minHeightProperty().bind(footer.prefHeightProperty());
		footer.maxHeightProperty().bind(footer.prefHeightProperty());
		footer.setPrefHeight(footer.prefHeightProperty().get() + 1);
		footer.setPrefHeight(50);
	}

	public void generate(ActionEvent event) {
		map = new Map(10, 10, 666);
		generateMap(map);
		
		Callable<?> mazeAlgorithm = () -> null;

		switch (generation) {
		case 0:
			mazeAlgorithm = () -> {
				map.graph.shuffle(map);
				return this;
			};
			break;
		case 1:
			mazeAlgorithm = () -> {
				map.graph.maze(map);
				return this;
			};
			break;
		default:
			break;
		}

		playAlgorithm(mazeAlgorithm);
		
		map.updateCells();
	}

	public void generateMap(Map map) {
		gridMap.setHgap(4);
		gridMap.setVgap(4);

		for (int y = 0; y < map.grid.height; y++) {
			for (int x = 0; x < map.grid.width; x++) {

				Rectangle rect = new Rectangle(map.size, map.size);

				rect.getStyleClass().clear();
				rect.getStyleClass().add("grid-cell");

				Label val = new Label();
				val.getStyleClass().clear();
				val.getStyleClass().add("grid-val");
				val.setDisable(true);

				GridPane.setHalignment(val, HPos.CENTER);

				map.rectCells.put(rect, map.grid.grid[x][y]);

				map.mapCells.put(map.grid.grid[x][y], new ArrayList<Object>());

				map.mapCells.get(map.grid.grid[x][y]).add(rect);
				map.mapCells.get(map.grid.grid[x][y]).add(val);

				map.configureEmpty(rect, val, map.grid.grid[x][y]);
				map.configureGoals(rect, val, map.grid.grid[x][y]);

				gridMap.add(rect, x, y);
				gridMap.add(val, x, y);

				rect.setOnMousePressed(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent arg0) {
						Rectangle rect = (Rectangle) arg0.getSource();
						Grid.Cell cell = map.rectCells.get(rect);

						if (!map.rectCells.get(rect).isStart && !map.rectCells.get(rect).isDest) {
							map.rectCells.get(rect).isEmpty = !map.rectCells.get(rect).isEmpty;
						}

						map.updateCell(cell);
						map.graph.gridToAdjList(map.grid);
					}
				});
			}
		}
		map.updateCells();
	}
	
	public void playMaze() {
		playAlgorithm(() -> {
			map.graph.shuffle(map);
			return this;
		});
	}

	public void chooseAlgoritm(ActionEvent event) {
		int id = ((MenuItem) event.getSource()).getParentMenu().getItems().indexOf(((MenuItem) event.getSource()));
		chooseAlgoritm(id);
	}
	
	public void chooseMaze(ActionEvent event) {
		int id = ((MenuItem) event.getSource()).getParentMenu().getItems().indexOf(((MenuItem) event.getSource()));
		chooseMaze(id);
	}
	
	public void chooseAlgoritm(int id) {
		algorithm = id;
		subheader.setText(algorithms.get(algorithm).get(0));
		searchBtn.setText(algorithms.get(algorithm).get(1));
	}

	public void chooseMaze(int id) {
		generation = id;
		generateBtn.setText(generations.get(generation).get(1));
	}

	public void onClickSearch(ActionEvent event) {
		Callable<?> searchAlgorithm = () -> null;
		
		switch (algorithm) {
		case 0:
			searchAlgorithm = () -> {
				map.graph.randomSearch(map);
				return this;
			};
			break;
		case 1:
			searchAlgorithm = () -> {
				map.graph.breathFirstSearch(map);
				return this;
			};
			break;
		case 2:
			searchAlgorithm = () -> {
				map.graph.depthFirstSearch(map);
				return this;
			};
			break;
		case 3:
			searchAlgorithm = () -> "";
			break;
		case 4:
			searchAlgorithm = () -> "";
			break;
		default:
			break;
		}
		
		playAlgorithm(searchAlgorithm);
	}

	private void playAlgorithm(Callable<?> alg) {

		Thread algThread = new Thread(() -> {
			System.out.println(alg);

			try {
				alg.call();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		algThread.setDaemon(true);
		algThread.start();
	}

	public void toggleCanShowNumbers() {
		Configuration.setCanShowNumbers(!Configuration.getCanShowNumbers());
		map.updateCells();
	}

	public void toggleCanAnimate() {
		Configuration.setCanShowNumbers(!Configuration.getCanShowAnimation());
		map.updateCells();
	}
}