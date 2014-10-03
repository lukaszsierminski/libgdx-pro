package com.luksie.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;

/**
 * Created by Łukasz Siermiński on 2014-09-29.
 */
public class GameScreen implements Screen {

    WorldRenderer renderer;

    public GameScreen(){
        renderer = new WorldRenderer();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(176/255f, 224/255f, 248/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render(delta);
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

    }
}
