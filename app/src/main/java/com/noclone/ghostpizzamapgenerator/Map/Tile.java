package com.noclone.ghostpizzamapgenerator.Map;

public class Tile {
    private final TileType type;
    private final int value;

    public Tile(TileType type) {
        this.type = type;
        this.value = 0;
    }
    public Tile(TileType type, int value) {
        this.type = type;
        this.value = value;
    }

    public TileType getType() {
        return type;
    }

    public int getValue() {
        return value;
    }
}
