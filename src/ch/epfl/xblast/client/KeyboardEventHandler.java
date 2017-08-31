package ch.epfl.xblast.client;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import ch.epfl.xblast.PlayerAction;

/**
 * Handles the events sent the keyboard
 * 
 * @author Gabriel Hayat (246239)
 * @author Adrian Guerra Alejos (246225)
 */
public final class KeyboardEventHandler extends KeyAdapter {
    private final Map<Integer, PlayerAction> map;
    private final Consumer<PlayerAction> c;

    public KeyboardEventHandler(Map<Integer, PlayerAction> map,
            Consumer<PlayerAction> c) {
        this.map = new HashMap<>(map);
        this.c = c;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (map.containsKey(e.getKeyCode()))
            c.accept(map.get(e.getKeyCode()));
    }
}
