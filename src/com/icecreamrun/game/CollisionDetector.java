package com.icecreamrun.game;

import com.icecreamrun.model.MovingItem;
import com.icecreamrun.model.Player;

public class CollisionDetector {
    public boolean isColliding(Player player, MovingItem item, int groundY) {
        return player.getBounds(groundY).intersects(item.getBounds(groundY));
    }
}
