package com.ultramega.cabletiers.common.autocrafting.autocrafter;

public class ImportModeSettings {
    private static final int DONT_IMPORT = 0;
    private static final int IMPORT_EVERYTHING = 1;
    private static final int IMPORT_PATTERN_OUTPUTS = 2;
    private static final int IMPORT_REQUESTED_RESOURCES = 3;

    private ImportModeSettings() {
    }

    static ImportMode getImportMode(final int importMode) {
        return switch (importMode) {
            case IMPORT_EVERYTHING -> ImportMode.IMPORT_EVERYTHING;
            case IMPORT_PATTERN_OUTPUTS -> ImportMode.IMPORT_PATTERN_OUTPUTS;
            case IMPORT_REQUESTED_RESOURCES -> ImportMode.IMPORT_REQUESTED_RESOURCES;
            default -> ImportMode.DONT_IMPORT;
        };
    }

    static int getImportMode(final ImportMode importMode) {
        return switch (importMode) {
            case IMPORT_EVERYTHING -> IMPORT_EVERYTHING;
            case IMPORT_PATTERN_OUTPUTS -> IMPORT_PATTERN_OUTPUTS;
            case IMPORT_REQUESTED_RESOURCES -> IMPORT_REQUESTED_RESOURCES;
            default -> DONT_IMPORT;
        };
    }
}
