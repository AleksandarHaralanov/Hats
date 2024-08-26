package io.github.aleksandarharalanov.hats.handler;

public enum LightRadius {
    NARROW(3),
    WIDE(5);

    private final int radius;

    LightRadius(int radius) {
        this.radius = radius;
    }

    public int getRadius() {
        return radius;
    }

    public static int fromValue(Object value) {
        if (value instanceof String) {
            String stringValue = (String) value;
            for (LightRadius radius : LightRadius.values()) {
                if (radius.name().equalsIgnoreCase(stringValue)) {
                    return radius.getRadius();
                }
            }
        }
        return NARROW.getRadius();
    }
}
