package com.noclone.ghostpizzamapgenerator.Map;

import com.noclone.ghostpizzamapgenerator.Map.TileType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MapGenerator {
    public static Tile[][] generateMap() {
        int rows = 7;
        int cols = 7;
        Tile[][] gameMap = initializeMap(rows, cols);

        List<int[]> players = new ArrayList<>();
        players.add(new int[]{1, 1});
        players.add(new int[]{5, 5});
        players.add(new int[]{1, 5});
        players.add(new int[]{5, 1});

        placePlayers(gameMap, players);
        placeBaseBlocks(gameMap, players);
        placePizzaAndHouse(gameMap, players);
        placeFences(gameMap);
        placeTeleporters(gameMap);
        placeGhosts(gameMap);
        clearBlockedTiles(gameMap);

        return gameMap;
    }

    private static Tile[][] initializeMap(int rows, int cols) {
        Tile[][] gameMap = new Tile[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                gameMap[i][j] = new Tile(TileType.EMPTY);
            }
        }
        return gameMap;
    }

    private static void printMap(String[][] gameMap) {
        for (String[] row : gameMap) {
            System.out.println(String.join(" ", row));
        }
    }

    private static void placePlayers(String[][] gameMap, List<int[]> players) {
        for (int i = 0; i < players.size(); i++) {
            int[] position = players.get(i);
            gameMap[position[0]][position[1]] = "J" + (i + 1);
        }
    }

    private static void blockTile(String[][] gameMap, int i, int j) {
        if (i >= 0 && i < gameMap.length && j >= 0 && j < gameMap[0].length) {
            gameMap[i][j] = BLOCK;
        }
    }

    private static void placeBaseBlocks(String[][] gameMap, List<int[]> players) {
        for (int[] position : players) {
            blockTile(gameMap, position[0] - 1, position[1]);
            blockTile(gameMap, position[0] + 1, position[1]);
            blockTile(gameMap, position[0], position[1] - 1);
            blockTile(gameMap, position[0], position[1] + 1);
        }
    }

    private static boolean checkDistanceFromPlayers(List<int[]> players, int i, int j, int minDistance) {
        for (int[] player : players) {
            int row = player[0];
            int col = player[1];
            if (Math.abs(i - row) + Math.abs(j - col) < minDistance) {
                return false;
            }
        }
        return true;
    }

    private static void clearPizzasAndHouses(String[][] gameMap, List<int[]> pizzaPositions, List<int[]> housePositions) {
        for (int[] pizzaPosition : pizzaPositions) {
            gameMap[pizzaPosition[0]][pizzaPosition[1]] = EMPTY;
        }
        for (int[] housePosition : housePositions) {
            gameMap[housePosition[0]][housePosition[1]] = EMPTY;
        }
    }

    private static boolean pizzaClose(int[] player, List<int[]> pizzaPositions, int playerPizzaDistance) {
        for (int[] pizzaPosition : pizzaPositions) {
            if (Math.abs(player[0] - pizzaPosition[0]) + Math.abs(player[1] - pizzaPosition[1]) == playerPizzaDistance) {
                return true;
            }
        }
        return false;
    }

    private static void placePizzaAndHouse(String[][] gameMap, List<int[]> players) {
        List<int[]> pizzaPositions = new ArrayList<>();
        List<int[]> housePositions = new ArrayList<>();

        int playerPizzaMinDistance = new Random().nextInt(3) + 3;
        int playerPizzaDistance = playerPizzaMinDistance;
        int pizzaHouseDistance = new Random().nextInt(3) + 3;

        for (int i = 0; i < players.size(); i++) {
            List<int[]> possiblePizzaPositions = new ArrayList<>();
            for (int row = 0; row < gameMap.length; row++) {
                for (int col = 0; col < gameMap[0].length; col++) {
                    if (gameMap[row][col].equals(EMPTY) && checkDistanceFromPlayers(players, row, col, playerPizzaMinDistance) &&
                            ((playerPizzaDistance == -1 || Math.abs(row - players.get(i)[0]) + Math.abs(col - players.get(i)[1]) == playerPizzaDistance) ||
                                    pizzaClose(players.get(i), pizzaPositions, playerPizzaDistance))) {
                        possiblePizzaPositions.add(new int[]{row, col});
                    }
                }
            }
            Collections.shuffle(possiblePizzaPositions);
            int[] pizzaPosition = possiblePizzaPositions.get(0);
            pizzaPositions.add(pizzaPosition);

            List<int[]> possibleHousePositions = new ArrayList<>();
            for (int row = 0; row < gameMap.length; row++) {
                for (int col = 0; col < gameMap[0].length; col++) {
                    if (gameMap[row][col].equals(EMPTY) && Math.abs(row - pizzaPosition[0]) + Math.abs(col - pizzaPosition[1]) == pizzaHouseDistance) {
                        possibleHousePositions.add(new int[]{row, col});
                    }
                }
            }
            Collections.shuffle(possibleHousePositions);

            if (possibleHousePositions.isEmpty()) {
                clearPizzasAndHouses(gameMap, pizzaPositions, housePositions);
                placePizzaAndHouse(gameMap, players);
                return;
            }

            int[] housePosition = possibleHousePositions.get(0);
            housePositions.add(housePosition);

            gameMap[pizzaPosition[0]][pizzaPosition[1]] = "P" + (i + 1);
            gameMap[housePosition[0]][housePosition[1]] = "H" + (i + 1);
        }
    }

    private static void placeFences(String[][] gameMap) {
        int i = 4;
        Random random = new Random();
        while (i != 0) {
            int row = random.nextInt(gameMap.length);
            int col = random.nextInt(gameMap[0].length);
            if (gameMap[row][col].equals(EMPTY)) {
                gameMap[row][col] = FENCE;
                i -= 1;
                if (!isFenceValid(gameMap, row, col)) {
                    gameMap[row][col] = EMPTY;
                    i += 1;
                }
            }
        }
    }

    private static boolean isCorner(int row, int col, int i, int j) {
        return List.of(new int[]{i - 1, j - 1}, new int[]{i - 1, j + 1}, new int[]{i + 1, j - 1}, new int[]{i + 1, j + 1})
                .contains(new int[]{row, col});
    }

    private static int findFenceWayToBorder(String[][] gameMap, int i, int j, boolean[][] visited, boolean detach) {
        visited[i][j] = true;
        int borderFences = 0;
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        for (int[] direction : directions) {
            int row = i + direction[0];
            int col = j + direction[1];
            if (row >= 0 && row < gameMap.length && col >= 0 && col < gameMap[0].length && gameMap[row][col].equals(FENCE) && !visited[row][col]) {
                if (row == 0 || row == gameMap.length - 1 || col == 0 || col == gameMap[0].length - 1) {
                    if (detach || isCorner(row, col, i, j)) {
                        borderFences += 1;
                    }
                    borderFences += findFenceWayToBorder(gameMap, row, col, visited, false);
                } else {
                    borderFences += findFenceWayToBorder(gameMap, row, col, visited, true);
                }
            }
        }
        return borderFences;
    }

    private static boolean isFenceValid(String[][] gameMap, int i, int j) {
        boolean[][] visited = new boolean[gameMap.length][gameMap[0].length];
        if (i == 0 || i == gameMap.length - 1 || j == 0 || j == gameMap[0].length - 1) {
            int borderFences = findFenceWayToBorder(gameMap, i, j, visited, false);
            return borderFences == 0;
        }
        int borderFences = findFenceWayToBorder(gameMap, i, j, visited, true);
        return borderFences < 2;
    }

    private static void placeTeleporters(String[][] gameMap) {
        Random random = new Random();
        for (int count = 0; count < 3; count++) {
            while (true) {
                int row = random.nextInt(gameMap.length);
                int col = random.nextInt(gameMap[0].length);
                if (gameMap[row][col].equals(EMPTY)) {
                    gameMap[row][col] = TELEPORTER;
                    break;
                }
            }
        }
    }

    private static void placeGhosts(String[][] gameMap) {
        Random random = new Random();
        for (int count = 0; count < 6; count++) {
            while (true) {
                int row = random.nextInt(gameMap.length);
                int col = random.nextInt(gameMap[0].length);
                if (gameMap[row][col].equals(EMPTY)) {
                    gameMap[row][col] = GHOST;
                    break;
                }
            }
        }
    }

    private static void clearBlockedTiles(String[][] gameMap) {
        for (int i = 0; i < gameMap.length; i++) {
            for (int j = 0; j < gameMap[0].length; j++) {
                if (gameMap[i][j].equals(BLOCK)) {
                    gameMap[i][j] = EMPTY;
                }
            }
        }
    }
}

