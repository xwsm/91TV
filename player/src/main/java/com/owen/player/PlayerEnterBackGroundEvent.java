package com.owen.player;


public class PlayerEnterBackGroundEvent {
    private boolean isBackGround;

    public PlayerEnterBackGroundEvent(boolean isBackGround) {
        this.isBackGround = isBackGround;
    }

    public boolean isBackGround() {
        return isBackGround;
    }

    public void setBackGround(boolean backGround) {
        isBackGround = backGround;
    }
}
