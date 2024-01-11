package com.noclone.ghostpizzamapgenerator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.noclone.ghostpizzamapgenerator.Map.MapGenerator;
import com.noclone.ghostpizzamapgenerator.Map.Tile;

public class MainActivity extends AppCompatActivity {

    GridLayout gridLayout;
    MaterialButton generateButton;
    MaterialButtonToggleGroup numPlayersToggleGroup;

    int playerCount = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridLayout = findViewById(R.id.gridLayout);
        generateButton = findViewById(R.id.generateButton);
        generateButton.setOnClickListener(v -> genMap(playerCount));

        numPlayersToggleGroup = findViewById(R.id.numPlayersToggleGroup);
        numPlayersToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                int position = numPlayersToggleGroup.indexOfChild(group.findViewById(checkedId));
                playerCount = position + 2;
                genMap(playerCount);
            }
        });
        numPlayersToggleGroup.setSelectionRequired(true);
        numPlayersToggleGroup.check(numPlayersToggleGroup.getChildAt(playerCount - 2).getId());

        genMap(playerCount);
    }

    private void genMap(int numPlayers) {
        Tile[][] game_map = MapGenerator.generateMap(numPlayers);

        gridLayout.removeAllViews();
        for (Tile[] tiles : game_map) {
            for (Tile tile : tiles) {
                gridLayout.addView(createImageView(tile.getTileId()));
            }
        }
    }

    private ImageView createImageView(int drawableId) {
        ImageView imageView = new ImageView(this);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = 0;
        params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(drawableId);
        return imageView;
    }

}