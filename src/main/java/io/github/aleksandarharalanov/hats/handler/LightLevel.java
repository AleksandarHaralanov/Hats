package io.github.aleksandarharalanov.hats.handler;

public enum LightLevel {
    LOW(7),
    MEDIUM(11),
    HIGH(15);

    private final int level;

    LightLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public static LightLevel getNextLevel(LightLevel currentLevel) {
        LightLevel[] levels = LightLevel.values();
        int nextIndex = (currentLevel.ordinal() + 1) % levels.length;
        return levels[nextIndex];
    }

    public static int fromValue(Object value) {
        if (value instanceof String) {
            String stringValue = (String) value;
            for (LightLevel level : LightLevel.values()) {
                if (level.name().equalsIgnoreCase(stringValue)) {
                    return level.getLevel();
                }
            }
        }
        return LOW.getLevel();
    }
}
