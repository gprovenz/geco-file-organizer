package com.gprovenz.gecofileorg.settings;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FileSize implements Comparable<Number>{
    public enum Unit {
        B(1),
        KB(1024),
        MB(1024 * 1024),
        GB(1024 * 1024 * 1024),
        TB(1024 * 1024 * 1024 * 1024);

        private final long mult;

        Unit(long mult) {
            this.mult = mult;
        }

        public long getMult() {
            return mult;
        }
    }
    private double size;

    @JsonProperty("unit")
    private Unit unit;

    @Override
    public int compareTo(Number value) {
        if (unit==null) {
            return new Double(size).compareTo(value.doubleValue());
        } else {
            return new Double(size * unit.mult).compareTo(value.doubleValue());
        }
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }
}
