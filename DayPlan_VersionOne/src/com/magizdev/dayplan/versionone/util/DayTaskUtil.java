package com.magizdev.dayplan.versionone.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.R.bool;
import android.content.Context;

import com.magizdev.dayplan.versionone.store.DayPlanMetaData.DayTaskTable;
import com.magizdev.dayplan.versionone.store.DayPlanMetaData.DayTaskTimeTable;
import com.magizdev.dayplan.versionone.viewmodel.BacklogItemInfo;
import com.magizdev.dayplan.versionone.viewmodel.DayTaskInfo;
import com.magizdev.dayplan.versionone.viewmodel.DayTaskInfo.TaskState;
import com.magizdev.dayplan.versionone.viewmodel.DayTaskTimeInfo;
import com.magizdev.dayplan.versionone.viewmodel.StorageUtil;

public class DayTaskUtil {
	private StorageUtil<DayTaskInfo> storage;
	private StorageUtil<DayTaskTimeInfo> timeStorageUtil;
	private StorageUtil<BacklogItemInfo> backlogStorage;
	private long currentRunningTask;

	public DayTaskUtil(Context context) {
		storage = new StorageUtil<DayTaskInfo>(context, new DayTaskInfo());
		timeStorageUtil = new StorageUtil<DayTaskTimeInfo>(context,
				new DayTaskTimeInfo());
		backlogStorage = new StorageUtil<BacklogItemInfo>(context,
				new BacklogItemInfo());
		currentRunningTask = -1;
		List<Long> todaysTasks = GetTasksByDate(DayUtil.Today());
		for (Long id : todaysTasks) {
			if (IsTaskWaitingForStop(id)) {
				currentRunningTask = id;
				break;
			}
		}
	}

	public void AddTask(long backlogId) {
		DayTaskInfo dayTask = new DayTaskInfo(-1, DayUtil.Today(), backlogId,
				null, TaskState.Stop, 0, -1);
		storage.add(dayTask);
	}

	public float GetTaskRemainEstimate(long backlogId, boolean excludToday) {
		if (excludToday) {
			List<DayTaskInfo> tasks = storage.getCollection("("
					+ DayTaskTable.DATE + "<" + DayUtil.Today() + " and "
					+ DayTaskTable.BIID + " = " + backlogId + ")",
					DayTaskTable.DATE + " DESC");
			if (tasks.size() > 0) {
				return tasks.get(0).RemainEffort;
			}

			return backlogStorage.getSingle(backlogId).Estimate;
		} else {
			List<DayTaskInfo> tasks = storage.getCollection("("
					+ DayTaskTable.DATE + "<=" + DayUtil.Today() + " and "
					+ DayTaskTable.BIID + " = " + backlogId + ")",
					DayTaskTable.DATE + " DESC");
			if (tasks.size() > 0 && tasks.get(0).RemainEffort != -1) {
				return tasks.get(0).RemainEffort;
			} else if (tasks.size() > 1 && tasks.get(0).RemainEffort == -1) {
				return tasks.get(1).RemainEffort;
			}

			return backlogStorage.getSingle(backlogId).Estimate;
		}
	}

	public List<Long> GetTasksByDate(int date) {
		List<DayTaskInfo> tasks = storage.getCollection("(" + DayTaskTable.DATE
				+ "=" + date + ")", null);
		List<Long> biIds = new ArrayList<Long>();
		for (DayTaskInfo task : tasks) {
			biIds.add(task.BIID);
		}
		return biIds;
	}

	public void CloseDate(int date) {
		if (date < DayUtil.Today()) {
			List<Long> biids = GetTasksByDate(date);

		}
	}

	public void ClearTasksByDate(int date) {
		List<DayTaskInfo> tasks = storage.getCollection("(" + DayTaskTable.DATE
				+ "=" + date + ")", null);
		for (DayTaskInfo task : tasks) {
			storage.delete(task.ID);
		}
	}

	public void StartTask(long backlogId) {
		if (currentRunningTask > 0) {
			StopTask(currentRunningTask);
		}
		currentRunningTask = backlogId;
		Date now = new Date();
		DayTaskTimeInfo timeInfo = new DayTaskTimeInfo(-1, DayUtil.Today(),
				backlogId, null, DayUtil.msOfDay(now), 0);
		timeStorageUtil.add(timeInfo);
	}

	public void StopTask(long backlogId) {
		Date now = new Date();
		List<DayTaskTimeInfo> times = timeStorageUtil.getCollection("("
				+ DayTaskTimeTable.DATE + "=" + DayUtil.Today() + " and "
				+ DayTaskTimeTable.BIID + "=" + backlogId + ")", null);
		DayTaskTimeInfo timeInfo = times.get(0);
		timeInfo.EndTime = DayUtil.msOfDay(now);

		timeStorageUtil.update(timeInfo.ID, timeInfo);
	}

	public boolean IsTaskWaitingForStop(long backlogId) {
		List<DayTaskTimeInfo> times = timeStorageUtil.getCollection("("
				+ DayTaskTimeTable.DATE + "=" + DayUtil.Today() + " and "
				+ DayTaskTimeTable.BIID + "=" + backlogId + ")", null);
		if (times.size() == 0) {
			return false;
		} else {
			DayTaskTimeInfo timeInfo = times.get(0);
			return timeInfo.EndTime == 0;
		}
	}
}