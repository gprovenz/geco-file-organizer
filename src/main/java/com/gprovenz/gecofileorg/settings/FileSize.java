package com.gprovenz.gecofileorg.settings;

public class FileSize {
    public enum Unit {B, KB, MB, GB, TB}
    private double size;
    private Unit unit;

    public double getSize() {
        return size;
    }

    public Unit getUnit() {
        return unit;
    }
}
