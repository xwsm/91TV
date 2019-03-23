package com.owen.player.event;


public class PlayerFinishEvent {

    private boolean isFinish;

    public PlayerFinishEvent(boolean isFinish) {
        this.isFinish = isFinish;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void setFinish(boolean finish) {
        isFinish = finish;
    }
}
