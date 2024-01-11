package com.noclone.ghostpizzamapgenerator.Map;

import static com.noclone.ghostpizzamapgenerator.Map.PlayerStartPositions.fourPlayerStartPositions;
import static com.noclone.ghostpizzamapgenerator.Map.PlayerStartPositions.threePlayerStartPositions;
import static com.noclone.ghostpizzamapgenerator.Map.PlayerStartPositions.twoPlayerStartPositions;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MapGenerator {

    public static Tile[][] generateMap(int nbPlayers) {
        int rows = 7;
        int cols = 7;
        Tile[][] gameMap = initializeMap(rows, cols);

        List<int[]> players = getPlayerStartPositions(nbPlayers);

        assert players != null;
        placePlayers(gameMap, players);
        placeBaseBlocks(gameMap, players);
        placePizzaAndHouse(gameMap, players);
        placeFences(gameMap);
        placeTeleporters(gameMap);
        placeGhosts(gameMap);
        clearBlockedTiles(gameMap);

        return gameMap;
    }

    private static List<int[]> getPlayerStartPositions(int nbPlayers) {
        if (nbPlayers == 2) {
            return twoPlayerStartPositions.get(new Random().nextInt(twoPlayerStartPositions.size()));
        } else if (nbPlayers == 3) {
            return threePlayerStartPositions.get(new Random().nextInt(threePlayerStartPositions.size()));
        } else if (nbPlayers == 4) {
            return fourPlayerStartPositions.get(new Random().nextInt(fourPlayerStartPositions.size()));
        }
        return null;
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

    public static void printMap(Tile[][] gameMap) {
        String map = "";
        for (Tile[] row : gameMap) {
            for (Tile tile : row) {
                String symbol = "";
                switch (tile.getType()){
                    case EMPTY:
                        symbol = "--";
                        break;
                    case BLOCKED:
                        symbol = "XX";
                        break;
                    case PLAYER:
                        symbol = "J" + tile.getValue();
                        break;
                    case PIZZA:
                        symbol = "P" + tile.getValue();
                        break;
                    case HOUSE:
                        symbol = "H" + tile.getValue();
                        break;
                    case FENCE:
                        symbol = "FF";
                        break;
                    case TELEPORTER:
                        symbol = "TT";
                        break;
                    case GHOST:
                        symbol = "GG";
                        break;
                }
                map += symbol + " ";
            }
            map += "\n";
        }
        Log.d("GAME_MAP", map);
    }

    private static void placePlayers(Tile[][] gameMap, List<int[]> players) {
        for (int i = 0; i < players.size(); i++) {
            int[] position = players.get(i);
            gameMap[position[0]][position[1]] = new Tile(TileType.PLAYER, i+1);
        }
    }

    private static void blockTile(Tile[][] gameMap, int i, int j) {
        if (i >= 0 && i < gameMap.length && j >= 0 && j < gameMap[0].length) {
            gameMap[i][j] = new Tile(TileType.BLOCKED);
        }
    }

    private static void placeBaseBlocks(Tile[][] gameMap, List<int[]> players) {
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

    private static void clearPizzasAndHouses(Tile[][] gameMap, List<int[]> pizzaPositions, List<int[]> housePositions) {
        for (int[] pizzaPosition : pizzaPositions) {
            gameMap[pizzaPosition[0]][pizzaPosition[1]] = new Tile(TileType.EMPTY);
        }
        for (int[] housePosition : housePositions) {
            gameMap[housePosition[0]][housePosition[1]] = new Tile(TileType.EMPTY);
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

    private static void placePizzaAndHouse(Tile[][] gameMap, List<int[]> players) {
        List<int[]> pizzaPositions = new ArrayList<>();
        List<int[]> housePositions = new ArrayList<>();

        int playerPizzaMinDistance = 3 + new Random().nextInt(5 - players.size());
        int pizzaHouseDistance = 3 + new Random().nextInt(3);

        for (int i = 0; i < players.size(); i++) {
            List<int[]> possiblePizzaPositions = new ArrayList<>();
            for (int row = 0; row < gameMap.length; row++) {
                for (int col = 0; col < gameMap[0].length; col++) {
                    if (gameMap[row][col].getType() == TileType.EMPTY && checkDistanceFromPlayers(players, row, col, playerPizzaMinDistance) &&
                            ((Math.abs(row - players.get(i)[0]) + Math.abs(col - players.get(i)[1]) == playerPizzaMinDistance) ||
                                    pizzaClose(players.get(i), pizzaPositions, playerPizzaMinDistance))) {
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
                    if (gameMap[row][col].getType() == TileType.EMPTY && Math.abs(row - pizzaPosition[0]) + Math.abs(col - pizzaPosition[1]) == pizzaHouseDistance) {
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

            gameMap[pizzaPosition[0]][pizzaPosition[1]] = new Tile(TileType.PIZZA, i + 1);
            gameMap[housePosition[0]][housePosition[1]] = new Tile(TileType.HOUSE, i + 1);
        }
    }

    private static void placeFences(Tile[][] gameMap) {
        int i = 4;
        Random random = new Random();
        while (i != 0) {
            int row = random.nextInt(gameMap.length);
            int col = random.nextInt(gameMap[0].length);
            if (gameMap[row][col].getType() == TileType.EMPTY) {
                gameMap[row][col] = new Tile(TileType.FENCE);
                i -= 1;
                if (!isFenceValid(gameMap, row, col)) {
                    gameMap[row][col] = new Tile(TileType.EMPTY);
                    i += 1;
                }
            }
        }
    }

    private static boolean isCorner(int row, int col, int i, int j) {
        List<int[]> corners = new ArrayList<int[]>(){{
            add(new int[]{i - 1, j - 1});
            add(new int[]{i - 1, j + 1});
            add(new int[]{i + 1, j - 1});
            add(new int[]{i + 1, j + 1});
        }};
        return corners.contains(new int[]{row, col});
    }

    private static int findFenceWayToBorder(Tile[][] gameMap, int i, int j, boolean[][] visited, boolean detach) {
        visited[i][j] = true;
        int borderFences = 0;
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        for (int[] direction : directions) {
            int row = i + direction[0];
            int col = j + direction[1];
            if (row >= 0 && row < gameMap.length && col >= 0 && col < gameMap[0].length && gameMap[row][col].getType() == TileType.FENCE && !visited[row][col]) {
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

    private static boolean isFenceValid(Tile[][] gameMap, int i, int j) {
        boolean[][] visited = new boolean[gameMap.length][gameMap[0].length];
        if (i == 0 || i == gameMap.length - 1 || j == 0 || j == gameMap[0].length - 1) {
            int borderFences = findFenceWayToBorder(gameMap, i, j, visited, false);
            return borderFences == 0;
        }
        int borderFences = findFenceWayToBorder(gameMap, i, j, visited, true);
        return borderFences < 2;
    }

    private static void placeTeleporters(Tile[][] gameMap) {
        Random random = new Random();
        for (int count = 1; count <= 3; count++) {
            while (true) {
                int row = random.nextInt(gameMap.length);
                int col = random.nextInt(gameMap[0].length);
                if (gameMap[row][col].getType() == TileType.EMPTY) {
                    gameMap[row][col] = new Tile(TileType.TELEPORTER, count);
                    break;
                }
            }
        }
    }

    private static void placeGhosts(Tile[][] gameMap) {
        Random random = new Random();
        for (int count = 0; count < 6; count++) {
            while (true) {
                int row = random.nextInt(gameMap.length);
                int col = random.nextInt(gameMap[0].length);
                if (gameMap[row][col].getType() == TileType.EMPTY) {
                    gameMap[row][col] = new Tile(TileType.GHOST);
                    break;
                }
            }
        }
    }

    private static void clearBlockedTiles(Tile[][] gameMap) {
        for (int i = 0; i < gameMap.length; i++) {
            for (int j = 0; j < gameMap[0].length; j++) {
                if (gameMap[i][j].getType() == TileType.BLOCKED) {
                    gameMap[i][j] = new Tile(TileType.EMPTY);
                }
            }
        }
    }
}

