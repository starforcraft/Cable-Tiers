package com.ultramega.cabletiers.common.autocrafting.autocrafter;

public enum ImportMode {
    DONT_IMPORT,
    IMPORT_EVERYTHING,
    IMPORT_PATTERN_OUTPUTS,
    IMPORT_REQUESTED_RESOURCES;

    ImportMode toggle() {
        return switch (this) {
            case DONT_IMPORT -> IMPORT_EVERYTHING;
            case IMPORT_EVERYTHING -> IMPORT_PATTERN_OUTPUTS;
            case IMPORT_PATTERN_OUTPUTS -> IMPORT_REQUESTED_RESOURCES;
            case IMPORT_REQUESTED_RESOURCES -> DONT_IMPORT;
        };
    }
}
