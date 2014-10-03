package com.luksie.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Created by Łukasz Siermiński on 2014-09-25.
 */
public class World implements Screen{

    public static final int JUMP_BUTTON_POSITION_X = 70;
    public static final int JUMP_BUTTON_POSITION_Y = 70;
    public static final int JUMP_BUTTON_HEIGHT = 50;
    public static final int JUMP_BUTTON_WIDTH = 80;

    public static final int FIRE_BUTTON_POSITION_X = 170;
    public static final int FIRE_BUTTON_POSITION_Y = 70;
    public static final int FIRE_BUTTON_HEIGHT = 50;
    public static final int FIRE_BUTTON_WIDTH = 80;

    public static final int CAM_HEIGHT = 432;
    public static final int CAM_WIDTH = 576;

    public static final String MAP_TMX = "map.tmx";

    SpriteBatch spriteBatch;
    Skin uiSkin;
    OrthographicCamera camera;
    Player player;
    Stage level;
    TextButton jumpButton;
    TextButton fireButton;
    TiledMapRenderer tiledMapRenderer;
    TiledMap tiledMap;

    public World() {
        spriteBatch = new SpriteBatch();
        camera = new OrthographicCamera();
        player = new Player();
        level = new Stage();

        tiledMap = new TmxMapLoader().load(MAP_TMX);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, CAM_WIDTH, CAM_HEIGHT);
        camera.update();

        uiSkin = new Skin(Gdx.files.internal("uiskin.json"));

        jumpButton = new TextButton("Jump", uiSkin, "default");
        jumpButton.setPosition(JUMP_BUTTON_POSITION_X, JUMP_BUTTON_POSITION_Y);
        jumpButton.setSize(JUMP_BUTTON_WIDTH, JUMP_BUTTON_HEIGHT);
        jumpButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                jumpButton.setText("jumpd");
            }
        });

        fireButton = new TextButton("Fire", uiSkin, "default");
        fireButton.setPosition(FIRE_BUTTON_POSITION_X, FIRE_BUTTON_POSITION_Y);
        fireButton.setSize(FIRE_BUTTON_WIDTH, FIRE_BUTTON_HEIGHT);
        fireButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                fireButton.setText("fired");
            }
        });

        level.addActor(jumpButton);
        level.addActor(fireButton);
//        level.addActor(player);
        Gdx.input.setInputProcessor(level);
    }

    public void moveCamera(float x) {
        camera.position.set(x + 20, camera.position.y , 0);
        camera.update();
    }

    public void moveCamera() {
        camera.position.set(camera.position.x + 20, camera.position.y , 0);
        camera.update();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(176/255f, 224/255f, 248/255f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
//        player.draw(spriteBatch, delta);
        level.draw();
        spriteBatch.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
    }
}
