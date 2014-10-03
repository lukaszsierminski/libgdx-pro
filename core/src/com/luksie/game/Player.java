package com.luksie.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

/**
 * Created by Łukasz Siermiński on 2014-09-25.
 */
public class Player {

    public enum PlayerState {
        STANDING,
        WALKING,
        JUMPING,
        FALLING;
    }


    public static final float MAX_VELOCITY = 5f;
    public static final float JUMP_VELOCITY = 20f;
    public static final float GRAVITY = -22.0f;
    public static final float MAX_JUMP_SPEED   = 9f;
    public static final long LONG_JUMP_PRESS 	= 150l;
    public static final float DECAY = 0.87f;

    private PlayerState state;
    private boolean facingRight;
    private boolean grounded;
    private float stateTime;
    private Vector2 position;
    private Vector2 velocity;
    private Vector2 acceleration;

    private float width;
    private float height;

    private Pool<Rectangle> rectPool;
    private Array<Rectangle> tiles;
    private TiledMapTileLayer layer;
    private float tileSize;

    public Player(Pool<Rectangle> rectPool, Array<Rectangle> tiles, TiledMapTileLayer layer, float width) {
        position = new Vector2();
        velocity = new Vector2();
        state = PlayerState.STANDING;
        facingRight = true;
        stateTime = 0;
        grounded = false;
        this.rectPool = rectPool;
        this.tiles = tiles;
        this.layer = layer;
        tileSize = width;
    }

    public void render(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            velocity.x = -Player.MAX_VELOCITY;
            if (grounded) {
                state = PlayerState.WALKING;
            }
            facingRight = false;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            velocity.x = Player.MAX_VELOCITY;
            if (grounded) {
                state = PlayerState.WALKING;
            }
            facingRight = true;
        }

        if(state != PlayerState.FALLING){
            if(velocity.y < 0){
                state = PlayerState.FALLING;
                grounded = false;
            }
        }

        acceleration.y = Player.GRAVITY;
        acceleration.scl(delta);
        velocity.add(acceleration.x, acceleration.y);

        // clamp the velocity to the maximum, x-axis only
        if (Math.abs(velocity.x) > Player.MAX_VELOCITY) {
            velocity.x = Math.signum(velocity.x) * Player.MAX_VELOCITY;
        }

        // clamp the velocity to 0 if it's < 1, and set the state to standing
        if (Math.abs(velocity.x) < 1) {
            velocity.x = 0;
            if (grounded) {
                state = PlayerState.STANDING;
            }
        }

        velocity.scl(delta);

        Rectangle playerRect = rectPool.obtain();
        playerRect.set(position.x, position.y + height*0.1f, width, height);

        int startX, startY, endX, endY;
        if (velocity.x > 0) {
            startX = endX = (int)(position.x + width + velocity.x);
        } else {
            startX = endX = (int)(position.x + velocity.x);
        }

        startY = (int)(position.y);
        endY = (int)(position.y + height);
        getTiles(startX, startY, endX, endY, tiles);

        playerRect.x += velocity.x;

        for (Rectangle tile : tiles) {
            if (playerRect.overlaps(tile)) {
                if(velocity.x > 0){
                    position.x = tile.x - tileSize - tileSize * 0.40f;
                }else if(velocity.x < 0){
                    position.x = tile.x + tileSize + tileSize * 0.05f;
                }
                velocity.x = 0;
                break;
            }
        }

        playerRect.set(position.x, position.y, width, height);

        // if the koala is moving upwards, check the tiles to the top of it's
        // top bounding box edge, otherwise check the ones to the bottom
        if (velocity.y > 0) {
            startY = endY = (int)(position.y + height + velocity.y);
        } else {
            startY = endY = (int)(position.y + velocity.y);
        }

        startX = (int)(position.x);
        endX = (int)(position.x + width);
        getTiles(startX, startY, endX, endY, tiles);
        playerRect.y += velocity.y;
        for (Rectangle tile : tiles) {
            if (playerRect.overlaps(tile)) {
                // we actually reset the koala y-position here
                // so it is just below/above the tile we collided with
                // this removes bouncing :)
                if (velocity.y > 0) {
                    velocity.y = tile.y - height;
                    // we hit a block jumping upwards, let's destroy it!
                    //					TiledMapTileLayer layer = (TiledMapTileLayer)level.getMap().getLayers().get(1);
                    //					layer.setCell((int)tile.x, (int)tile.y, null);
                } else {
                    position.y = tile.y + tile.height;
                    // if we hit the ground, mark us as grounded so we can jump
                    grounded = true;
                }
                velocity.y = 0;
                break;
            }
        }
        rectPool.free(playerRect);


        // unscale the velocity by the inverse delta time and set
        // the latest position
        position.add(velocity);
        velocity.scl(1 / delta);

        velocity.x *= Player.DECAY;
    }

    private void getTiles (int startX, int startY, int endX, int endY, Array<Rectangle> tiles) {
//        TiledMapTileLayer layer = (TiledMapTileLayer)level.getMap().getLayers().get(1);
        rectPool.freeAll(tiles);
        tiles.clear();
        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                if (cell != null) {
                    Rectangle rect = rectPool.obtain();
                    rect.set(x, y, 1, 1);
                    tiles.add(rect);
                }
            }
        }
    }

    public void update(float delta){
        stateTime += delta;
        position.add(velocity.cpy().scl(delta));
    }

    public PlayerState getState() {
        return state;
    }

    public void setState(PlayerState state) {
        this.state = state;
    }

    public boolean isFacingRight() {
        return facingRight;
    }

    public void setFacingRight(boolean facingRight) {
        this.facingRight = facingRight;
    }

    public boolean isGrounded() {
        return grounded;
    }

    public void setGrounded(boolean grounded) {
        this.grounded = grounded;
    }

    public float getStateTime() {
        return stateTime;
    }

    public void setStateTime(float stateTime) {
        this.stateTime = stateTime;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }
}
