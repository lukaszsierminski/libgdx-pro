package com.luksie.game;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

/**
 * Created by Łukasz Siermiński on 2014-09-29.
 */
public class Stage {

    private TiledMap map;
    private float tileWidth;
    private float tileHeight;

    public Stage(String tilemapName){
        map = new TmxMapLoader().load(tilemapName);
        TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get(0);
        tileWidth = layer.getTileWidth();
        tileHeight = layer.getTileHeight();
    }

    public TiledMap getMap() {
        return map;
    }

    public float getTileHeight() {
        return tileHeight;
    }

    public float getTileWidth() {
        return tileWidth;
    }
}
