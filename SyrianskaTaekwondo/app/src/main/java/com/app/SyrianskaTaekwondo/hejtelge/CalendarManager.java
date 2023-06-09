package com.app.SyrianskaTaekwondo.hejtelge;

import com.app.SyrianskaTaekwondo.hejtelge.model.CalendarUnit;
import com.app.SyrianskaTaekwondo.hejtelge.model.CollapsibleCalendarEvent;
import com.app.SyrianskaTaekwondo.hejtelge.model.CollapsibleState;
import com.app.SyrianskaTaekwondo.hejtelge.model.Day;
import com.app.SyrianskaTaekwondo.hejtelge.model.DefaultFormatter;
import com.app.SyrianskaTaekwondo.hejtelge.model.Formatter;
import com.app.SyrianskaTaekwondo.hejtelge.model.Month;
import com.app.SyrianskaTaekwondo.hejtelge.model.RangeUnit;
import com.app.SyrianskaTaekwondo.hejtelge.model.Week;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class CalendarManager<T extends CollapsibleCalendarEvent> {

    private final DateTimeFormatter mMapEventKeyFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");

    private final LocalDate mToday;
    private CollapsibleState mState;
    private RangeUnit mUnit;
    private LocalDate mSelected;
    private LocalDate mMinDate;
    private LocalDate mMaxDate;
    private Formatter formatter;

    private LocalDate mActiveMonth;
    private Map<String, List<T>> mEventsMap = new HashMap<String, List<T>>();

    CalendarManager(LocalDate selected,
                    CollapsibleState state,
                    LocalDate minDate,
                    LocalDate maxDate) {
        this(selected, state, minDate, maxDate, null);
    }

    private CalendarManager(LocalDate selected,
                            CollapsibleState state,
                            LocalDate minDate,
                            LocalDate maxDate,
                            Formatter formatter) {
        mToday = LocalDate.now();
        mState = state;

        setFormatter(formatter);

        init(selected, minDate, maxDate);
    }

    void setEvents(List<T> events) {
        mEventsMap.clear();
        for (int i=0; i<events.size(); i++) {
            addEvent(events.get(i));
        }
    }

    void addEvents(List<T> events) {
        for (int i=0; i<events.size(); i++) {
            addEvent(events.get(i));
        }
    }

    void addEvent(T event) {
        String key = mMapEventKeyFormatter.print(event.getCollapsibleEventLocalDate());
        List<T> events;
        if (mEventsMap.containsKey(key)) {
            mEventsMap.get(key).add(event);
        } else {
            events = new ArrayList<T>();
            events.add(event);
            mEventsMap.put(key, events);
        }
    }

    void removeEvent(T event) {
        String key = mMapEventKeyFormatter.print(event.getCollapsibleEventLocalDate());
        if (mEventsMap.containsKey(key)) {
            mEventsMap.get(key).remove(event);
        }
    }

    List<T> getEventsForDate(LocalDate date) {
        String key = mMapEventKeyFormatter.print(date);
        if (mEventsMap.containsKey(key)) {
            return mEventsMap.get(key);
        }
        return new ArrayList<T>();
    }

    boolean selectPeriod(LocalDate date) {
        if (!mUnit.isIn(date) && mUnit.setPeriod(date)) {
            mUnit.select(mSelected);
            setActiveMonth(mUnit.getFrom());
            return true;
        } else {
            return false;
        }
    }

    boolean selectDay(LocalDate date) {
        if (!mSelected.isEqual(date) && mUnit.hasDate(date)) {
            mUnit.deselect(mSelected);
            mSelected = date;
            mUnit.select(mSelected);

            if (mState == CollapsibleState.WEEK) {
                setActiveMonth(date);
            }
            return true;
        } else {
            return false;
        }
    }

    LocalDate getSelectedDay() {
        return mSelected;
    }

    String getHeaderText() {
        return formatter.getHeaderText(mUnit.getType(), mUnit.getFrom(), mUnit.getTo());
    }

    boolean hasNext() {
        return mUnit.hasNext();
    }

    boolean hasPrev() {
        return mUnit.hasPrev();
    }

    boolean next() {
        if (mUnit.next()) {
            mUnit.select(mSelected);
            setActiveMonth(mUnit.getFrom());
            return true;
        } else {
            return false;
        }
    }

    public boolean prev() {
        if (mUnit.prev()) {
            mUnit.select(mSelected);
            setActiveMonth(mUnit.getTo());
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return index of month to focus to
     */
    void toggleView() {
        if (mState == CollapsibleState.MONTH) {
            toggleFromMonth();
        } else {
            toggleFromWeek();
        }
    }

    public CollapsibleState getState() {
        return mState;
    }

    CalendarUnit getUnits() {
        return mUnit;
    }

    LocalDate getActiveMonth() {
        return mActiveMonth;
    }

    private void setActiveMonth(LocalDate activeMonth) {
        mActiveMonth = activeMonth.withDayOfMonth(1);
    }

    private void toggleFromMonth() {
        // if same month as selected
        if (mUnit.isInView(mSelected)) {
            toggleFromMonth(mSelected);

            setActiveMonth(mSelected);
        } else {
            setActiveMonth(mUnit.getFrom());
            toggleFromMonth(mUnit.getFirstDateOfCurrentMonth(mActiveMonth));
        }
    }

    void toggleToWeek(int weekInMonth) {
        LocalDate date = mUnit.getFrom().plusDays(weekInMonth * 7);
        toggleFromMonth(date);
    }

    private void toggleFromMonth(LocalDate date) {
        setUnit(new Week(date, mToday, mMinDate, mMaxDate));
        mUnit.select(mSelected);
        mState = CollapsibleState.WEEK;
    }

    private void toggleFromWeek() {
        setUnit(new Month(mActiveMonth, mToday, mMinDate, mMaxDate));
        mUnit.select(mSelected);

        mState = CollapsibleState.MONTH;
    }

    private void init() {
        if (mState == CollapsibleState.MONTH) {
            setUnit(new Month(mSelected, mToday, mMinDate, mMaxDate));
        } else {
            setUnit(new Week(mSelected, mToday, mMinDate, mMaxDate));
        }
        mUnit.select(mSelected);
    }

    private void setUnit(RangeUnit unit) {
        if (unit != null) {
            mUnit = unit;
        }
    }

    int getWeekOfMonth() {
        if (mUnit.isInView(mSelected)) {
            if (mUnit.isIn(mSelected)) {
                return mUnit.getWeekInMonth(mSelected);
            } else if (mUnit.getFrom().isAfter(mSelected)) {
                return mUnit.getWeekInMonth(mUnit.getFrom());
            } else {
                return mUnit.getWeekInMonth(mUnit.getTo());
            }
        } else {
            return mUnit.getFirstWeek(mUnit.getFirstDateOfCurrentMonth(mActiveMonth)); // if not in this month first week should be selected
        }
    }

    private void init(LocalDate date, LocalDate minDate, LocalDate maxDate) {
        mSelected = date;
        setActiveMonth(date);
        mMinDate = minDate;
        mMaxDate = maxDate;

        init();
    }

    boolean dayHasEvent(Day day) {
        return mEventsMap.containsKey(mMapEventKeyFormatter.print(day.getDate()));
    }

    LocalDate getMinDate() {
        return mMinDate;
    }

    void setMinDate(LocalDate minDate) {
        mMinDate = minDate;
    }

    LocalDate getMaxDate() {
        return mMaxDate;
    }

    void setMaxDate(LocalDate maxDate) {
        mMaxDate = maxDate;
    }

    Formatter getFormatter() {
        return formatter;
    }

    void setFormatter(Formatter formatter) {
        if (formatter == null) {
            this.formatter = new DefaultFormatter();
        } else {
            this.formatter = formatter;
        }
    }
}
