package com.luksie.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputListener;

/**
 * Created by Łukasz Siermiński on 2014-09-25.
 */
public class Player extends Actor {

    Texture texture = new Texture(Gdx.files.internal("player.png"));
    public final static float playerX = 100;
    public final static float playerY = 255;

    public Player() {
        setBounds(playerX, playerY, texture.getWidth(), texture.getHeight());
        addListener(new InputListener(){
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(texture, this.getX(), this.getY());
    }

    public void move() {
        setPosition(this.getX() + 20, this.getY());
    }
}
