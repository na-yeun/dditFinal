package kr.or.ddit.commons.util;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DateRange {
    // 주간 데이터를 계산하는 메서드
    public List<LocalDate> getWeeklyDates(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> weeks = new ArrayList<>();
        LocalDate current = startDate;

        while (!current.isAfter(endDate)) {
            weeks.add(current);
            current = current.plusWeeks(1); // 1주 단위로 증가
        }

        return weeks;
    }
    
 // 월간 데이터를 계산하는 메서드
    public List<LocalDate> getMonthlyDates(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> months = new ArrayList<>();
        LocalDate current = startDate.withDayOfMonth(1); // 해당 월의 첫날로 설정
        endDate = endDate.withDayOfMonth(1); // 종료일도 첫날로 설정

        while (!current.isAfter(endDate)) {
            months.add(current);
            current = current.plusMonths(1); // 1달 단위로 증가
        }

        return months;
    }
    
}
