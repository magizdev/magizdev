package com.magizdev.dayplan.util;

import java.util.Calendar;
import java.util.List;

import android.content.Context;

import com.magizdev.dayplan.PieChartBuilder.PieChartData;
import com.magizdev.dayplan.viewmodel.DayTaskTimeInfo;

public class WeekNavigate implements INavigate {
	private int currentWeekOfYear;
	private int currentWeekStartDay;
	private int nowWeekOfYear;
	private int today;
	private DayTaskTimeUtil util;

	private int currentWeekEndDay() {
		if (currentWeekOfYear == nowWeekOfYear) {
			return today + 1;
		} else {
			return currentWeekStartDay + 7;
		}
	}

	public WeekNavigate(Context context) {
		util = new DayTaskTimeUtil(context);
		today = DayUtil.Today();
		Calendar cToday = DayUtil.toCalendar(today);
		nowWeekOfYear = cToday.get(Calendar.WEEK_OF_YEAR);
		currentWeekOfYear = nowWeekOfYear;
		cToday.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		currentWeekStartDay = DayUtil.toDate(cToday.getTime());
	}

	@Override
	public void Backword() {
		currentWeekOfYear--;
		currentWeekStartDay -= 7;
	}

	@Override
	public void Forward() {
		if (!IsLast()) {
			currentWeekOfYear++;
			currentWeekStartDay += 7;
		}

	}

	@Override
	public String CurrentTitle() {
		Calendar startDay = DayUtil.toCalendar(currentWeekStartDay);
		Calendar endDay = DayUtil.toCalendar(currentWeekEndDay() - 1);
		return startDay.get(Calendar.YEAR) + "-" + startDay.get(Calendar.MONTH)
				+ "-" + startDay.get(Calendar.DAY_OF_MONTH) + " -- "
				+ endDay.get(Calendar.YEAR) + "-" + endDay.get(Calendar.MONTH)
				+ "-" + endDay.get(Calendar.DAY_OF_MONTH);
	}

	@Override
	public boolean IsLast() {
		return currentWeekOfYear == nowWeekOfYear;
	}

	@Override
	public List<PieChartData> GetPieChartData() {
		List<DayTaskTimeInfo> data = util.GetByDateRange(currentWeekStartDay,
				currentWeekEndDay());
		return DayTaskTimeUtil.compute(data);
	}

}