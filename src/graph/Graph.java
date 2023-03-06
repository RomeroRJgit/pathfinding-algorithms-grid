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

				int[] nesw = { (y - 1) * grid.width + x, // N
						y * grid.width + (x + 1), // E
						(y + 1) * grid.width + x, // S
						y * grid.width + (x - 1), // W
				};

				Grid.Cell cell = grid.grid[x][y];

				if (adjList[y * grid.width + x] == null || !cell.isEmpty) {

					continue;
				}

				int key = y * grid.width + x;

				if (y > 0) {
					Grid.Cell n = grid.grid[x][y - 1];

					if (n.isEmpty) {
						adjList[key].add(0, new Node(nesw[0]));
					}
				}

				if (x < grid.width - 1) {
					Grid.Cell e = grid.grid[x + 1][y];

					if (e.isEmpty) {
						adjList[key].add(0, new Node(nesw[1]));
					}
				}

				if (y < grid.height - 1) {
					Grid.Cell s = grid.grid[x][y + 1];

					if (s.isEmpty) {
						adjList[key].add(0, new Node(nesw[2]));
					}
				}

				if (x > 0) {
					Grid.Cell w = grid.grid[x - 1][y];

					if (w.isEmpty) {
						adjList[key].add(0, new Node(nesw[3]));
					}
				}

			}
		}
	}

	public void shuffle(Map map) {
		for (int i = 0; i < 10; i++) {
			try {
				Thread.sleep(Configuration.simulationRate * i / 2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			map.randomize(new Random().nextInt());

			map.updateCells();
		}

		gridToAdjList(map.grid);
		map.updateCells();

	}

	public void maze(Map map) {
		map.grid.empty();
		gridToAdjList(map.grid);
		map.grid.fill();
		map.updateCells();

		Random rng = new Random();

		Stack<Integer> stack = new Stack<Integer>();

		int start = map.start.y * map.grid.width + map.start.x;
		int dest = map.destination.y * map.grid.width + map.destination.x;
		int current = start;

		ArrayList<Integer> visited = new ArrayList<Integer>();
		int[] parents = new int[map.grid.width + map.destination.x];

		stack.push(start);
		visited.add(start);

		int maxSearch = adjList.length;
		int searchCount = 0;
		while (searchCount <= maxSearch) {
			try {
				Thread.sleep(Configuration.getSimulationRate());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (stack.isEmpty()) {
				System.out.println("No path to " + dest + " found!");
				map.grid.cells.get(dest).isVoid = true;
				map.updateCell(map.grid.cells.get(dest));
				break;
			}

			int last = current;
			current = stack.pop();
			visited.add(current);

			map.grid.cells.get(current).isEmpty = false;
			map.grid.cells.get(current).isPath = true;
			map.updateCell(map.grid.cells.get(current));

			List<Integer> attempts = new ArrayList<Integer>();
			while (attempts.size() < adjList[current].size() - 1) {

				int id = (int) adjList[current].get(rng.nextInt(adjList[current].size())).v;
				System.out.println(current + " | " + adjList[current].size() + " | " + id + " |  " + attempts.size());

				if (!attempts.contains(id)) {
					attempts.add(id);
				}

				if (!visited.contains(id)) {
					stack.push(id);
				}
			}
			searchCount++;
		}

		for (int y = 0; y < map.grid.height; y++) {
			for (int x = 0; x < map.grid.width; x++) {
				if (map.grid.cells.get(y * map.grid.width + x).isPath) {
					map.grid.cells.get(y * map.grid.width + x).isPath = false;
					map.grid.cells.get(y * map.grid.width + x).isEmpty = true;
					map.updateCell(map.grid.cells.get(y * map.grid.width + x));
				}
			}
		}
		map.updateCells();
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

			if (adjList[guess].size() == 0 || adjList[guess] == null) {
				continue;
			}

			int subguess = rng.nextInt(adjList[guess].size());

			if (subguess < 0 || guess < 0) {
				continue;
			}

			int id = (int) adjList[guess].get(subguess).v;
			Grid.Cell cell = map.grid.cells.get(id);

			if (!cell.isVisited && rng.nextInt(5) == 0) {
				cell.visit();
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

	public void takePath(Map map, List<Integer> path) {
		try {
			Thread.sleep(Configuration.getSimulationRate() * 2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < path.size(); i++) {
			try {
				Thread.sleep(Configuration.getSimulationRate());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			map.grid.cells.get(i).traverse();
			map.updateCell(map.grid.cells.get(i));
		}
	}

	public void breathFirstSearch(Map map) {
		gridToAdjList(map.grid);

		Queue<Integer> queue = new ArrayDeque<Integer>();

		int start = map.start.y * map.grid.width + map.start.x;
		int dest = map.destination.y * map.grid.width + map.destination.x;
		int current = start;

		ArrayList<Integer> visited = new ArrayList<Integer>();
		List<Integer> parents = new ArrayList<Integer>();

		for (int i = 0; i < adjList.length; i++) {
			parents.add(null);
		}

		queue.add(start);
		visited.add(start);

		int maxSearch = adjList.length;
		int searchCount = 0;
		while (searchCount <= maxSearch) {
			try {
				Thread.sleep(Configuration.getSimulationRate());
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
			map.grid.cells.get(current).visit();
			map.updateCell(map.grid.cells.get(current));

			for (int i = 0; i < adjList[current].size(); i++) {
				int id = (int) adjList[current].get(i).v;

				if (!visited.contains(id)) {
					queue.add(id);
					visited.add(id);
					parents.add(id, current);
				}
			}
			searchCount++;

			if (current == dest) {
				System.out.println("Found path to " + dest + "!");
				map.grid.cells.get(current).isFound = true;
				map.updateCell(map.grid.cells.get(current));

				List<Integer> path = new ArrayList<Integer>();
				int last = dest;

				for (int i = 0; i < parents.size(); i++) {
					System.out.println(last);

					last = path.get(last);

					path.add(last);

					if (parents.get(i) == start) {
						path = parents;
					}
				}

				takePath(map, path);
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
		int[] parents = new int[map.grid.width + map.destination.x];

		stack.push(start);
		visited.add(start);

		int maxSearch = adjList.length;
		int searchCount = 0;
		while (searchCount <= maxSearch) {
			try {
				Thread.sleep(Configuration.getSimulationRate());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (stack.isEmpty()) {
				System.out.println("No path to " + dest + " found!");
				map.grid.cells.get(dest).isVoid = true;
				map.updateCell(map.grid.cells.get(dest));
				break;
			}

			int last = current;
			current = stack.pop();
			visited.add(current);

			map.grid.cells.get(current).visit();
			map.updateCell(map.grid.cells.get(current));

			for (int i = 0; i < adjList[current].size(); i++) {
				int id = (int) adjList[current].get(i).v;

				if (!visited.contains(id)) {
					stack.push(id);
				}
			}
			searchCount++;

			if (current == dest) {
				System.out.println("Found path to " + dest + "!");
				map.grid.cells.get(current).isFound = true;
				map.updateCell(map.grid.cells.get(current));

				List<Integer> path = new ArrayList<Integer>();

				for (int i = 0; i < parents.length; i++) {

				}

				// takePath(map, path);
				break;
			}

		}
	}

	private void getGridDistance() {
		int x;
		int y;
	}

	public void greedy(Map map) {
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
				Thread.sleep(Configuration.getSimulationRate());
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
			map.grid.cells.get(current).visit();
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
