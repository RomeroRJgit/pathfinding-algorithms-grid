package graph;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.function.Function;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
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

	Map map;
	
	final String[] algorithms = {"Breadth First Search", "Depth First Search", "Dijkstras Shortest Path", "A*"};
	
	String algorithm = "Breadth First Search";

	public void initialize() {
		subheader.setText("Breadth First Search");
		
	}

	public void generate(ActionEvent event) {	
		map = new Map(10, 10);

		gridMap.setHgap(4);
		gridMap.setVgap(4);

		for (int y = 0; y < map.grid.height; y++) {
			for (int x = 0; x < map.grid.width; x++) {

				Rectangle rect = new Rectangle(map.size, map.size);

				rect.getStyleClass().clear();
				rect.getStyleClass().add("gridCell");

				Label val = new Label();
				val.getStyleClass().clear();
				val.getStyleClass().add("gridVal");
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
						Label val = (Label) map.mapCells.get(cell).get(1);

						if (!map.rectCells.get(rect).isStart && !map.rectCells.get(rect).isDest) {
							map.rectCells.get(rect).isEmpty = !map.rectCells.get(rect).isEmpty;
						}

						map.updateCell(cell);
						map.graph.gridToAdjList(map.grid);
					}
				});
			}
			
			playAlgorithm(() -> {
				map.graph.shuffle(map);
				return this;
			});
		}	
	}
	
	public void chooseAlgoritm(ActionEvent event) {
		algorithm = ((MenuItem) event.getSource()).getText();
		subheader.setText(algorithm);
	}

	public void search(ActionEvent event) {
		Callable<?> searchAlgorithm = () -> null;
		
		switch (algorithm)
		{
			case "Random Guess":
			searchAlgorithm = () -> {
				map.graph.randomSearch(map);
				return this;
			};
			break;
			case "Breadth First Search":
				searchAlgorithm = () -> {
					map.graph.breathFirstSearch(map);
					return this;
				};
				break;
			case "Depth First Search":
				searchAlgorithm = () -> {
					map.graph.depthFirstSearch(map);
					return this;
				};
				break;
			case "Dijkstras Shortest Path":
				searchAlgorithm = () -> "" ;
				break;
			case "A*":
				searchAlgorithm = () -> "" ;
				break;
			default:
				break;			
		}
		
		playAlgorithm(searchAlgorithm);		
	}
	
	private void playAlgorithm(Callable<?> alg) {
		
		Thread searchThread = new Thread(() -> {
			System.out.println(alg);
			
			try {
				alg.call();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		searchThread.setDaemon(true);
		searchThread.start();
	}
}