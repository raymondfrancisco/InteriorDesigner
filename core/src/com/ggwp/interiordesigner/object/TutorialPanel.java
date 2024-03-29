package com.ggwp.interiordesigner.object;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ggwp.interiordesigner.Main;
import com.ggwp.interiordesigner.MenuScreen;
import com.ggwp.interiordesigner.manager.SkinManager;
import com.ggwp.utils.Tweener.ImageButtonAccessor;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Cubic;

public class TutorialPanel extends AppScreen {

    protected Stage stage;

    private TweenManager manager;
    private Image a, b;
    private int curPos = 0;
    private TextButton backButton,nextButton,returnButton;
    private List<FileHandle> slides = null;
    private boolean doneAnimating = true;

    public TutorialPanel() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        slides = Arrays.asList(Gdx.files.internal("Tutorial").list());

        Collections.sort(slides, new Comparator<FileHandle>() {
            @Override
            public int compare(FileHandle fa, FileHandle fb) {
                String nameA = fa.name().replace(".png", "");
                String nameB = fb.name().replace(".png", "");
                return Integer.valueOf(nameA).compareTo(Integer.valueOf(nameB));
            }
        });

        if (slides.isEmpty()) {
            return;
        }

        a = new Image(new SpriteDrawable(new Sprite(new Texture(slides.get(0)))));
        a.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        stage.addActor(a);
        if (slides.size() == 1) {
            return;
        }

        b = new Image(new SpriteDrawable(new Sprite(new Texture(slides.get(1)))));
        b.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        stage.addActor(b);
        a.toFront();
        cur = a;
        manager = new TweenManager();

        Tween.registerAccessor(Image.class, new ImageButtonAccessor());

        a.addListener(new ActorGestureListener() {
            @Override
            public void fling(InputEvent event, float velocityX, float velocityY, int button) {
                super.fling(event, velocityX, velocityY, button);
                processSlide(velocityX,velocityY,b);
            }
        });

        b.addListener(new ActorGestureListener() {
            @Override
            public void fling(InputEvent event, float velocityX, float velocityY, int button) {
                super.fling(event, velocityX, velocityY, button);
                processSlide(velocityX, velocityY, a);
            }
        });


        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = SkinManager.getDefaultSkin().newDrawable("defaultButton");
        textButtonStyle.down = SkinManager.getDefaultSkin().newDrawable("defaultButton");
        textButtonStyle.font = SkinManager.getDefaultSkin().getFont("defaultFont");
        textButtonStyle.fontColor= Color.BLACK;
        returnButton = new TextButton("RETURN", textButtonStyle);
        returnButton.setBounds(5f, Gdx.graphics.getHeight()-45f, 80f, 40f);

        TextButton.TextButtonStyle nextButtonStyle = new TextButton.TextButtonStyle();
        nextButtonStyle .up = SkinManager.getDefaultSkin().newDrawable("greenButton");
        nextButtonStyle .down = SkinManager.getDefaultSkin().newDrawable("greenButton");
        nextButtonStyle .font = SkinManager.getDefaultSkin().getFont("defaultFont");
        nextButtonStyle .fontColor= Color.WHITE;
        nextButton = new TextButton("NEXT", nextButtonStyle);
        nextButton.setBounds(Gdx.graphics.getWidth()-75f, 5f, 70f, 40f);

        final TextButton.TextButtonStyle backButtonStyle = new TextButton.TextButtonStyle();
        backButtonStyle  .up = SkinManager.getDefaultSkin().newDrawable("redButton");
        backButtonStyle  .down = SkinManager.getDefaultSkin().newDrawable("redButton");
        backButtonStyle  .font = SkinManager.getDefaultSkin().getFont("defaultFont");
        backButtonStyle  .fontColor= Color.WHITE;
        backButton = new TextButton("BACK", backButtonStyle);
        backButton.setBounds(5f, 5f, 70f, 40f);

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                cur = (cur==a)?b:a;
                processSlide(10, 0, cur);
            }
        });

        nextButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                cur = (cur==b)?a:b;
                processSlide(-10, 0, cur);

            }
        });

        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Main.getInstance().setScreen(new MenuScreen());
                dispose();
            }
        });

        stage.addActor(backButton);
        stage.addActor(nextButton);
        stage.addActor(returnButton);

    }

    Image cur;

    public boolean processSlide( float velocityX, float velocityY, Image a){
        if (doneAnimating) {
            if (Math.abs(velocityX) > Math.abs(velocityY)) {
                if (velocityX < 0) {
                    if (curPos >= slides.size() - 1){
                        return false;
                    }
                    a.setDrawable(new SpriteDrawable(new Sprite(new Texture(slides.get(++curPos)))));

                    a.toFront();
                    backButton.toFront();
                    nextButton.toFront();
                    returnButton.toFront();
                    Tween.from(a, ImageButtonAccessor.POSITION_X, 1f)
                            .target(Gdx.graphics.getWidth(), 0)
                            .ease(Cubic.INOUT)
                            .start(manager).setCallback(new TweenCallback() {
                        @Override
                        public void onEvent(int type, BaseTween<?> source) {
                            doneAnimating = true;
                        }
                    });
                    doneAnimating = false;
                } else if (velocityX > 0) {
                    if (curPos <= 0){
                        return false;
                    }

                    a.setDrawable(new SpriteDrawable(new Sprite(new Texture(slides.get(--curPos)))));
                    a.toFront();
                    backButton.toFront();
                    nextButton.toFront();
                    returnButton.toFront();
                    Tween.from(a, ImageButtonAccessor.POSITION_X, 1f)
                            .target(-Gdx.graphics.getWidth(), 0)
                            .ease(Cubic.INOUT)
                            .start(manager).setCallback(new TweenCallback() {
                        @Override
                        public void onEvent(int type, BaseTween<?> source) {
                            doneAnimating = true;
                        }
                    });
                    doneAnimating=false;
                }
                return true;
            }
        }

        return false;
    }
    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));

        manager.update(delta);
        stage.getBatch().begin();
        stage.getBatch().end();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

}
