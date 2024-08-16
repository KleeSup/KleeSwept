package com.github.kleesup.kleeswept.util;

/**
 * Utility class that holds two bytes.
 * @author KleeSup
 * @since 1.1
 * @version 1.0
 */
public class BytePair {

    public byte x, y;
    public BytePair(byte x, byte y) {
        this.x = x;
        this.y = y;
    }
    public BytePair() {}

    public BytePair set(byte x, byte y){
        this.x = x;
        this.y = y;
        return this;
    }

    public BytePair setZero(){
        this.x = 0;
        this.y = 0;
        return this;
    }

    public byte getX() {
        return x;
    }

    public BytePair setX(byte x) {
        this.x = x;
        return this;
    }

    public byte getY() {
        return y;
    }

    public BytePair setY(byte y) {
        this.y = y;
        return this;
    }
}
