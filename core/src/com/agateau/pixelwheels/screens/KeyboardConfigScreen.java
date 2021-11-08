/*
 * Copyright 2019 Aurélien Gâteau <mail@agateau.com>
 *
 * This file is part of Pixel Wheels.
 *
 * Pixel Wheels is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.agateau.pixelwheels.screens;

import static com.agateau.translations.Translator.tr;

import com.agateau.pixelwheels.PwGame;
import com.agateau.pixelwheels.PwRefreshHelper;
import com.agateau.pixelwheels.gameinput.KeyboardInputHandler;
import com.agateau.ui.KeyMapper;
import com.agateau.ui.VirtualKey;
import com.agateau.ui.anchor.AnchorGroup;
import com.agateau.ui.menu.LabelMenuItem;
import com.agateau.ui.menu.Menu;
import com.agateau.ui.uibuilder.UiBuilder;
import com.agateau.utils.Assert;
import com.agateau.utils.FileUtils;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/** Configure a keyboard input device */
public class KeyboardConfigScreen extends PwStageScreen {
    private final PwGame mGame;
    private final int mPlayerIdx;
    private final KeyMapper mKeyMapper;

    KeyboardConfigScreen(PwGame game, int playerIdx) {
        super(game.getAssets().ui);
        mGame = game;
        mPlayerIdx = playerIdx;
        new PwRefreshHelper(mGame, getStage()) {
            @Override
            protected void refresh() {
                mGame.replaceScreen(new KeyboardConfigScreen(mGame, mPlayerIdx));
            }
        };

        KeyboardInputHandler handler =
                (KeyboardInputHandler) mGame.getConfig().getPlayerInputHandler(mPlayerIdx);
        Assert.check(handler != null, "input handler is not a KeyboardInputHandler");
        mKeyMapper = (KeyMapper) handler.getInputMapper();
        Assert.check(mKeyMapper != null, "input mapper is not a KeyMapper");

        setupUi();
    }

    private void setupUi() {
        UiBuilder builder = new UiBuilder(mGame.getAssets().atlas, mGame.getAssets().ui.skin);

        AnchorGroup root =
                (AnchorGroup) builder.build(FileUtils.assets("screens/keyboardconfig.gdxui"));
        root.setFillParent(true);
        getStage().addActor(root);

        Menu menu = builder.getActor("menu");

        createKeyItem(menu, tr("Left:"), VirtualKey.LEFT);
        createKeyItem(menu, tr("Right:"), VirtualKey.RIGHT);
        createKeyItem(menu, tr("Up:"), VirtualKey.UP);
        createKeyItem(menu, tr("Down / Brake:"), VirtualKey.DOWN);
        createKeyItem(menu, tr("Trigger:"), VirtualKey.TRIGGER);

        builder.getActor("backButton")
                .addListener(
                        new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                onBackPressed();
                            }
                        });
    }

    private void createKeyItem(Menu menu, String text, VirtualKey virtualKey) {
        Integer[] keys = mKeyMapper.getKeys(virtualKey);
        Assert.check(keys.length >= 1, "No keys defined");
        String keyText = Input.Keys.toString(keys[0]);

        menu.addItemWithLabel(text, new LabelMenuItem(keyText, menu.getSkin()));
    }

    @Override
    public void onBackPressed() {
        mGame.popScreen();
    }
}
