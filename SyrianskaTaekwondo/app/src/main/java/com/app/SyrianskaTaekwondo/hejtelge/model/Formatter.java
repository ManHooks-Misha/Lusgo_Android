package com.app.SyrianskaTaekwondo.hejtelge.model;

import org.joda.time.LocalDate;

public interface Formatter {

    String getDayName(LocalDate date);

    String getHeaderText(int type, LocalDate from, LocalDate to);

}
