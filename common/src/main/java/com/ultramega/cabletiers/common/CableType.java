package com.ultramega.cabletiers.common;

import java.util.Locale;

public enum CableType {
    IMPORTER,
    EXPORTER,
    DESTRUCTOR,
    CONSTRUCTOR,
    DISK_INTERFACE,
    AUTOCRAFTER,
    INTERFACE;

    public String getLowercaseName() {
        return this.toString().toLowerCase(Locale.ROOT);
    }
}
