package com.noclone.ghostpizzamapgenerator.Map;

import com.noclone.ghostpizzamapgenerator.R;

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

    public int getTileId() {
        switch (type){
            case EMPTY:
                return R.drawable.empty;
            case FENCE:
                return R.drawable.fence;
            case TELEPORTER:
                if (value == 1)
                    return R.drawable.tp1;
                else if (value == 2)
                    return R.drawable.tp2;
                else if (value == 3)
                    return R.drawable.tp3;
            case GHOST:
                return R.drawable.ghost;
            case PLAYER:
                if (value == 1)
                    return R.drawable.player1;
                else if (value == 2)
                    return R.drawable.player2;
                else if (value == 3)
                    return R.drawable.player3;
                else if (value == 4)
                    return R.drawable.player4;
            case PIZZA:
                if (value == 1)
                    return R.drawable.pizza1;
                else if (value == 2)
                    return R.drawable.pizza2;
                else if (value == 3)
                    return R.drawable.pizza3;
                else if (value == 4)
                    return R.drawable.pizza4;
            case HOUSE:
                if (value == 1)
                    return R.drawable.house1;
                else if (value == 2)
                    return R.drawable.house2;
                else if (value == 3)
                    return R.drawable.house3;
                else if (value == 4)
                    return R.drawable.house4;
            default:
                return R.drawable.empty;
        }
    }
}
