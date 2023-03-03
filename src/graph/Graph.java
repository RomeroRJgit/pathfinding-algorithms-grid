package graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;
import java.util.function.Predicate;
import java.util.stream.Stream;

import graph.Grid.Coord;
import javafx.concurrent.Task;

/*
 * Nodes: w * h
 * Edges: 0-4
 * Undirected/directed
 */

public class Graph {

	public class Node {
		public Object v;
		public int x;
		public int y;

		public Node(int v) {
			this.v = v;
		}

		public Node(int v, int x, int y) {
			this.x = x;
			this.y = y;
			this.v = v;
		}

		public Node(Object v) {
			this.v = v;
		}

		public void next() {

		}
	}

	private ArrayList<Node>[] adjList;

	public Graph() {
		adjList = new ArrayList[1];
	}

	int simulationRate = 25;

	@SuppressWarnings("unchecked")
	public Graph(Grid grid) {
		gridToAdjList(grid);
		print();
	}

	public Graph(int width, int height) {

	}

	void gridToAdjList(Grid grid) {
		adjList = new ArrayList[grid.width * grid.height];
		for (int y = 0; y < grid.height; y++) {
			for (int x = 0; x < grid.width; x++) {
				adjList[y * grid.width + x] = new ArrayList<Node>();
			}
		}
		
		for (int y = 0; y < grid.height; y++) {
			for (int x = 0; x < grid.width; x++) {

				int[] nswe = { (y - 1) * grid.width + x, // N
						(y + 1) * grid.width + x, // S
						y * grid.width + (x + 1), // E
						y * grid.width + (x - 1), // W
				};

				Grid.Cell cell = grid.grid[x][y];

				if (adjList[y * grid.width + x] == null || !cell.isEmpty) {
					System.out.println(y * grid.width + x);
					System.out.println(cell != null ? cell.isStart || cell.isDest : null);
					continue;
				}

				int key = y * grid.width + x;

				if (y > 0) {
					Grid.Cell n = grid.grid[x][y - 1];

					if (n.isEmpty) {
						adjList[key].add(0, new Node(nswe[0]));
					}
				}

				if (y < grid.height - 1) {
					Grid.Cell s = grid.grid[x][y + 1];

					if (s.isEmpty) {
						adjList[key].add(0, new Node(nswe[1]));
					}
				}

				if (x < grid.width - 1) {
					Grid.Cell w = grid.grid[x + 1][y];

					if (w.isEmpty) {
						adjList[key].add(0, new Node(nswe[2]));
					}
				}

				if (x > 0) {
					Grid.Cell e = grid.grid[x - 1][y];

					if (e.isEmpty) {
						adjList[key].add(0, new Node(nswe[3]));
					}
				}
			}
		}
	}

	public void shuffle(Map map) {
		Random rng = new Random();

		for (int y = 0; y < map.grid.height / 3; y++) {
			for (int x = 0; x < map.grid.width / 3; x++) {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				map.updateCell(map.grid.grid[y][x]);

				Grid.Cell cell = map.grid.grid[rng.nextInt(map.grid.width)][rng.nextInt(map.grid.height)];
				cell.isEmpty = (!cell.isStart || !cell.isDest) ? rng.nextInt(5) != 0 : true;

				map.updateCell(cell);
			}
		}
		
		gridToAdjList(map.grid);
	}

	public void randomSearch(Map map) {
		Random rng = new Random();

		int start = map.start.y * map.grid.width + map.start.x;
		int dest = map.destination.y * map.grid.width + map.destination.x;
		int guess = start;

		int maxSearch = adjList.length * 4;
		int searchCount = 0;
		
		while (searchCount <= maxSearch) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			guess = rng.nextInt(map.grid.width * map.grid.height);	
						
			if (adjList[guess].size() == 0 || adjList[guess] == null ) {
				continue;
			}
			
			int subguess = rng.nextInt(adjList[guess].size());
			
			if (subguess < 0 || guess < 0) {
				continue;
			}
			
			System.out.println(guess);
			int id = (int) adjList[guess].get(subguess).v;
			Grid.Cell cell = map.grid.cells.get(id);
			
			if (!cell.isVisited) {
				cell.isVisited = rng.nextInt(5) == 0;
			}

			map.updateCell(cell);

			if (guess == dest) {
				System.out.println("Found path to " + dest + "!");
				map.grid.cells.get(guess).isFound = true;
				map.updateCell(map.grid.cells.get(guess));
				break;
			}
		}

	}

	public void breathFirstSearch(Map map) {
		gridToAdjList(map.grid);

		Queue<Integer> queue = new ArrayDeque<Integer>();

		int start = map.start.y * map.grid.width + map.start.x;
		int dest = map.destination.y * map.grid.width + map.destination.x;
		int current = start;

		ArrayList<Integer> visited = new ArrayList<Integer>();

		queue.add(start);

		int maxSearch = adjList.length;
		int searchCount = 0;
		while (searchCount <= maxSearch) {
			try {
				Thread.sleep(simulationRate);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (queue.isEmpty()) {
				System.out.println("No path to " + dest + " found!");
				map.grid.cells.get(dest).isVoid = true;
				map.updateCell(map.grid.cells.get(dest));
				break;
			}

			current = queue.remove();
			map.grid.cells.get(current).isVisited = true;
			map.updateCell(map.grid.cells.get(current));

			for (int i = 0; i < adjList[current].size(); i++) {
				int id = (int) adjList[current].get(i).v;

				if (!visited.contains(id)) {
					queue.add(id);
					visited.add(id);

					System.out.println("Adj: " + id);
				}
			}
			searchCount++;

			if (current == dest) {
				System.out.println("Found path to " + dest + "!");
				map.grid.cells.get(current).isFound = true;
				map.updateCell(map.grid.cells.get(current));
				break;
			}

		}
	}

	public void depthFirstSearch(Map map) {
		gridToAdjList(map.grid);

		Stack<Integer> stack = new Stack<Integer>();

		int start = map.start.y * map.grid.width + map.start.x;
		int dest = map.destination.y * map.grid.width + map.destination.x;
		int current = start;

		ArrayList<Integer> visited = new ArrayList<Integer>();

		stack.push(start);

		int maxSearch = adjList.length;
		int searchCount = 0;
		while (searchCount <= maxSearch) {
			try {
				Thread.sleep(simulationRate);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (stack.isEmpty()) {
				System.out.println("No path to " + dest + " found!");
				map.grid.cells.get(dest).isVoid = true;
				map.updateCell(map.grid.cells.get(dest));
				break;
			}

			current = stack.pop();
			map.grid.cells.get(current).isVisited = true;
			map.updateCell(map.grid.cells.get(current));

			for (int i = 0; i < adjList[current].size(); i++) {
				int id = (int) adjList[current].get(i).v;

				if (!visited.contains(id)) {
					stack.push(id);
					visited.add(id);

					System.out.println("Adj: " + id);
				}
			}
			searchCount++;

			if (current == dest) {
				System.out.println("Found path to " + dest + "!");
				map.grid.cells.get(current).isFound = true;
				map.updateCell(map.grid.cells.get(current));
				break;
			}

		}
	}

	public void print() {
		System.out.println();

		for (int i = 0; i < adjList.length; i++) {
			System.out.print("Cell: " + i + " [" + adjList[i] + "]");

//			Node parent = adjList.get(i);
//
//			while (parent.next != null) {
//				System.out.print(",  [" + parent.next + "]");
//
//				parent = parent.next;
//			}

			System.out.println();
			System.out.println();
			System.out.println("----------------------------------------------------");
			System.out.println();

		}
	}

}
