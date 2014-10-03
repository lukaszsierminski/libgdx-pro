package com.luksie.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

/**
 * Created by Łukasz Siermiński on 2014-09-29.
 */
public class WorldRenderer implements InputProcessor {

    private static final float FRAME_DURATION_RUNNING = 0.09f;
    private static final float SCALE = 1/16f;
    private static final String MAP_FILE = "map.tmx";

    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;

    private Stage stage;
    private Player player;

    private Batch spriteBatch;

    private TextureRegion playerIdleLeft;
    private TextureRegion playerIdleRight;
    private TextureRegion playerJumpLeft;
    private TextureRegion playerJumpRight;
    private TextureRegion playerFrame;

    private Skin skin;
    private TextButton jumpButton;
    private TextButton moveRightButton;
    private TextButton moveLeftButton;
    private TextButton fireButton;

    private Animation walkLeftAnimation;
    private Animation walkRightAnimation;

    private boolean jumpingPressed;
    private long jumpPressedTime;

    ShapeRenderer debugRenderer = new ShapeRenderer();

    private Array<Rectangle> tiles;
    private float tileSize;

    private Pool<Rectangle> rectPool = new Pool<Rectangle>() {
        @Override
        protected Rectangle newObject () {
            return new Rectangle();
        }
    };

    public WorldRenderer() {
        stage = new Stage(MAP_FILE);
        tileSize = stage.getTileWidth() * SCALE;
        TiledMapTileLayer layer = (TiledMapTileLayer)stage.getMap().getLayers().get(1);
        player = new Player(rectPool, tiles, layer, tileSize);

        loadPlayerTextures();

        setupButtons();

        player.setPosition(new Vector2(3, 9));
        player.setWidth(SCALE * playerIdleRight.getRegionWidth());
        player.setHeight(SCALE * playerIdleRight.getRegionHeight());

        renderer = new OrthogonalTiledMapRenderer(stage.getMap(), SCALE);

        spriteBatch = renderer.getSpriteBatch();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 30, 20);
        camera.update();
    }

    private void setupButtons() {
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        jumpButton = new TextButton("Jump", skin);
        jumpButton.setWidth(100f);
        jumpButton.setHeight(45f);
        jumpButton.setPosition(Gdx.graphics.getWidth() / 2 - 100f, Gdx.graphics.getHeight() / 2 - 10f);
        jumpButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (player.isGrounded() && player.getState() != Player.PlayerState.FALLING) {
                    if (!player.getState().equals(Player.PlayerState.JUMPING)) {
                        jumpingPressed = true;
                        player.setGrounded(false);
                        jumpPressedTime = System.currentTimeMillis();
                        player.setState(Player.PlayerState.JUMPING);
                        player.getVelocity().y = Player.MAX_JUMP_SPEED;
                    } else {
                        if ((jumpingPressed && ((System.currentTimeMillis() - jumpPressedTime) >= Player.LONG_JUMP_PRESS))) {
                            jumpingPressed = false;
                        } else {
                            if (jumpingPressed) {
                                player.getVelocity().y = Player.MAX_JUMP_SPEED;
                            }
                        }
                    }
                }
            }
        });

        TextButton moveRightButton = new TextButton(">", skin);
        moveRightButton.setWidth(100f);
        moveRightButton.setHeight(45f);
        TextButton moveLeftButton = new TextButton("<", skin);
        moveLeftButton.setWidth(100f);
        moveLeftButton.setHeight(45f);
        TextButton fireButton = new TextButton("Fire", skin);
    }

    public void render (float delta) {
        debugRenderer.setProjectionMatrix(camera.combined);

        renderer.setView(camera);
        camera.update();

        renderer.render();

        drawPlayer();
        drawDebug();
        player.render(delta);
        player.update(delta);
    }

    public void loadPlayerTextures(){
        TextureAtlas movementAtlas = new TextureAtlas(Gdx.files.internal("player-movement.pack"));

		// Standing
        playerIdleLeft = movementAtlas.findRegion("4");

        playerIdleRight = new TextureRegion(playerIdleLeft);
        playerIdleRight.flip(true, false);

        TextureRegion[] walkLeftFrames = new TextureRegion[2];
        walkLeftFrames[0] =  movementAtlas.findRegion("2");
        walkLeftFrames[1] =  movementAtlas.findRegion("3");

        walkLeftAnimation = new Animation(FRAME_DURATION_RUNNING, walkLeftFrames);

        TextureAtlas jumpingAtlas = new TextureAtlas(Gdx.files.internal("player-jumping.pack"));
		// Jumping
        playerJumpLeft = jumpingAtlas.findRegion("3");

        TextureRegion[] walkRightFrames = new TextureRegion[2];
        walkRightFrames[0] = new TextureRegion(walkLeftFrames[0]);
        walkRightFrames[0].flip(true, false);
        walkRightFrames[1] = new TextureRegion(walkLeftFrames[1]);
        walkRightFrames[1].flip(true, false);

        walkRightAnimation = new Animation(FRAME_DURATION_RUNNING, walkRightFrames);

        playerJumpRight = new TextureRegion(playerJumpLeft);
        playerJumpRight.flip(true, false);
    }


    public void drawPlayer(){
        playerFrame = player.isFacingRight() ? playerIdleRight : playerIdleLeft;

        if(player.getState() == Player.PlayerState.WALKING) {
            playerFrame = player.isFacingRight() ? walkRightAnimation.getKeyFrame(player.getStateTime(), true) : walkLeftAnimation.getKeyFrame(player.getStateTime(), true);
        } else if (player.getState() == Player.PlayerState.JUMPING) {
            playerFrame = player.isFacingRight() ? playerJumpRight : playerJumpLeft;
        }else if (player.getState() == Player.PlayerState.FALLING){
            playerFrame = player.isFacingRight() ? playerJumpRight : playerJumpLeft;
        }

        spriteBatch.begin();
        spriteBatch.draw(playerFrame, player.getPosition().x, player.getPosition().y, player.getWidth(), player.getHeight());
        spriteBatch.end();
    }

    public void drawDebug(){
        debugRenderer.begin(ShapeRenderer.ShapeType.Line);

        Rectangle rect = new Rectangle(0, 0, player.getWidth(), player.getHeight());
        float x1 = player.getPosition().x + rect.x;
        float y1 = player.getPosition().y + rect.y;
        debugRenderer.setColor(new Color(0, 1, 0, 1));
        debugRenderer.rect(x1, y1, rect.width, rect.height);
        debugRenderer.end();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == jumpButton. && player.isGrounded() && player.getState() != Player.PlayerState.FALLING) {
            if (!player.getState().equals(Player.PlayerState.JUMPING)) {
                jumpingPressed = true;
                player.setGrounded(false);
                jumpPressedTime = System.currentTimeMillis();
                player.setState(Player.PlayerState.JUMPING);
                player.getVelocity().y = Player.MAX_JUMP_SPEED;
            } else {
                if ((jumpingPressed && ((System.currentTimeMillis() - jumpPressedTime) >= Player.LONG_JUMP_PRESS))) {
                    jumpingPressed = false;
                } else {
                    if (jumpingPressed) {
                        player.getVelocity().y = Player.MAX_JUMP_SPEED;
                    }
                }
            }
        }

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if(keycode ==  && player.getState() == Player.PlayerState.JUMPING){
            player.getVelocity().y -= Player.MAX_JUMP_SPEED/1.5f;
            player.setState(Player.PlayerState.FALLING);
            jumpingPressed = false;
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
