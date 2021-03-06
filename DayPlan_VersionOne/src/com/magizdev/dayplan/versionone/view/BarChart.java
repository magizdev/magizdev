package com.magizdev.dayplan.versionone.view;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;

import com.magizdev.dayplan.versionone.model.ChartData;
import com.magizdev.dayplan.versionone.util.DayUtil;
import com.magizdev.dayplan.versionone.util.INavigate;

public class BarChart extends BaseChart {

	public BarChart(Context context) {
		super(context);
	}

	private int seriesCount;
	private HashMap<Integer, List<ChartData>> chartData;
	private int maxY;
	private int startDate;
	private int endDate;
	private INavigate navigate;
	private XYMultipleSeriesDataset mBarDataset;
	private XYMultipleSeriesRenderer mBarRenderer;

	@Override
	public GraphicalView GetChart(INavigate navigate) {
		this.navigate = navigate;
		mBarDataset = buildBarDataset();
		mBarRenderer = buildBarRenderer();
		return ChartFactory.getBarChartView(this.context, mBarDataset,
				mBarRenderer, org.achartengine.chart.BarChart.Type.DEFAULT);
	}

	private XYMultipleSeriesDataset buildBarDataset() {
		seriesCount = 0;
		chartData = navigate.GetBarChartData();

		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		HashMap<Long, XYSeries> categoryMap = new HashMap<Long, XYSeries>();
		maxY = 0;

		startDate = DayUtil.toDate(new Date());
		endDate = DayUtil.toDate(new Date(0));

		if (chartData.size() == 1) {
			seriesCount = 1;
			int i = 1;
			XYSeries series = new XYSeries("");
			categoryMap.put(1L, series);
			for (Integer j : chartData.keySet()) {
				for (ChartData data : chartData.get(j)) {
					series.add(i++, data.data);
					maxY = data.data > maxY ? data.data : maxY;
				}
			}

		} else {
			for (Integer date : chartData.keySet()) {
				if (startDate > date) {
					startDate = date;
				}
				if (endDate < date) {
					endDate = date;
				}
			}

			for (Integer date = startDate; date <= endDate; date++) {
				if (chartData.containsKey(date)) {
					for (ChartData data : chartData.get(date)) {
						if (categoryMap.containsKey(data.biid)) {
							categoryMap.get(data.biid).add(
									date - startDate + 1, data.data);
						} else {
							XYSeries series = new XYSeries(data.backlogName);
							series.add(date - startDate + 1, data.data);
							categoryMap.put(data.biid, series);
							seriesCount++;
						}

						maxY = data.data > maxY ? data.data : maxY;
					}
				}
			}
			maxY *= 1.2;
		}

		for (XYSeries series : categoryMap.values()) {
			dataset.addSeries(series);
		}

		return dataset;

	}

	private XYMultipleSeriesRenderer buildBarRenderer() {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		renderer.setAxisTitleTextSize(16);
		renderer.setLabelsTextSize(15);
		renderer.setBarWidth(20);
		renderer.setApplyBackgroundColor(true);
		renderer.setBackgroundColor(0x00FAFAFA);
		renderer.setMarginsColor(0x00FAFAFA);
		renderer.setLabelsColor(Color.BLUE);
		renderer.setAxesColor(Color.BLACK);
		renderer.setXLabelsColor(Color.BLACK);
		renderer.setYLabelsColor(0, Color.BLACK);
		renderer.setLegendTextSize(15);
		int length = COLORS.length;
		for (int i = 0; i < seriesCount; i++) {
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(COLORS[i % length]);
			r.setDisplayChartValues(true);
			if (seriesCount == 1) {
				r.setShowLegendItem(false);
			}
			renderer.addSeriesRenderer(r);
		}

		renderer.setXLabelsAlign(Align.LEFT);
		renderer.setYLabelsAlign(Align.LEFT);
		renderer.setPanEnabled(false, false);
		renderer.setYAxisMin(0);
		renderer.setYAxisMax(maxY);
		renderer.setXLabels(0);
		renderer.setXAxisMin(0);
		if (chartData.size() == 1) {
			for (Integer i : chartData.keySet()) {
				List<ChartData> datas = chartData.get(i);
				renderer.setXAxisMax(datas.size() + 2);

				for (int j = 1; j < datas.size() + 1; j++) {
					renderer.removeXTextLabel(j);
					renderer.addXTextLabel(j, datas.get(j - 1).backlogName);
				}
			}
		} else {
			renderer.setXAxisMax(endDate - startDate + 2);

			for (int i = 1; i < endDate - startDate + 2; i++) {
				Calendar calendar = DayUtil.toCalendar(startDate + i - 1);
				String title = (calendar.get(Calendar.MONTH) + 1) + "/"
						+ calendar.get(Calendar.DAY_OF_MONTH);
				renderer.addXTextLabel(i, title);
			}
		}

		renderer.setZoomEnabled(false);
		renderer.setZoomRate(1f);
		renderer.setBarSpacing(1f);
		return renderer;
	}

}
