package com.magizdev.dayplan;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.app.Activity;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.magizdev.dayplan.util.DayNavigate;
import com.magizdev.dayplan.util.DayUtil;
import com.magizdev.dayplan.util.INavigate;
import com.magizdev.dayplan.util.WeekNavigate;

public class PieChartBuilder extends Activity {

	private static int[] COLORS = new int[] { 0xffB2C938, 0xff3BA9B8,
			0xffFF9910, 0xffC74C47, 0xff5B1A69, 0xffA83AAE, 0xffF981C5 };
	private CategorySeries mPieSeries = new CategorySeries("");
	private DefaultRenderer mPieRenderer = new DefaultRenderer();

	private GraphicalView mPieChartView;
	private GraphicalView mBarChartView;
	private TextView chartTitle;
	private INavigate navigate;
	private int seriesCount;
	private XYMultipleSeriesDataset mBarDataset;
	private XYMultipleSeriesRenderer mBarRenderer;
	private LinearLayout barLayout;
	private HashMap<Integer, List<PieChartData>> chartData;
	private int maxY;
	private Integer startDate;
	private Integer endDate;

	@Override
	protected void onRestoreInstanceState(Bundle savedState) {
		super.onRestoreInstanceState(savedState);
		mPieSeries = (CategorySeries) savedState
				.getSerializable("current_series");
		mPieRenderer = (DefaultRenderer) savedState
				.getSerializable("current_renderer");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("current_series", mPieSeries);
		outState.putSerializable("current_renderer", mPieRenderer);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);
		ImageButton backButton = (ImageButton) findViewById(R.id.btnLeft);
		ImageButton forwardButton = (ImageButton) findViewById(R.id.btnRight);
		chartTitle = (TextView) findViewById(R.id.chartTitle);
		barLayout = (LinearLayout) findViewById(R.id.barChart);
		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				navigate.Backword();
				refresh();
			}
		});

		forwardButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!navigate.IsLast()) {
					navigate.Forward();
					refresh();
				}
			}
		});
		navigate = new DayNavigate(this);

		mPieRenderer.setStartAngle(180);
		mPieRenderer.setDisplayValues(true);
		mPieRenderer.setLegendTextSize(30);
		mPieRenderer.setLabelsTextSize(30);
		Spinner spinner = (Spinner) findViewById(R.id.spinner1);

		ArrayAdapter<CharSequence> mAdapter = ArrayAdapter.createFromResource(
				this, R.array.rangs,
				android.R.layout.simple_spinner_dropdown_item);

		spinner.setAdapter(mAdapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {
					navigate = new DayNavigate(PieChartBuilder.this);

					refresh();
				} else {
					navigate = new WeekNavigate(PieChartBuilder.this);

					refresh();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		refresh();
		if (mPieChartView == null) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.pieChart);
			mPieChartView = ChartFactory.getPieChartView(this, mPieSeries,
					mPieRenderer);
			// mRenderer.setClickEnabled(true);
//			mPieChartView.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					SeriesSelection seriesSelection = mPieChartView
//							.getCurrentSeriesAndPoint();
//					if (seriesSelection != null) {
//						for (int i = 0; i < mPieSeries.getItemCount(); i++) {
//							mPieRenderer.getSeriesRendererAt(i).setHighlighted(
//									i == seriesSelection.getPointIndex());
//						}
//						mPieChartView.repaint();
//						Toast.makeText(
//								PieChartBuilder.this,
//								mPieSeries.getCategory(seriesSelection
//										.getPointIndex())
//										+ ":"
//										+ seriesSelection.getValue(),
//								Toast.LENGTH_SHORT).show();
//					}
//				}
//			});
			layout.addView(mPieChartView, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		} else {
			mPieChartView.repaint();
		}
		if (mBarChartView == null) {

			mBarDataset = buildBarDataset();
			mBarRenderer = buildBarRenderer();
			mBarChartView = ChartFactory.getBarChartView(this, mBarDataset,
					mBarRenderer, BarChart.Type.STACKED);

			barLayout.addView(mBarChartView, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		} else {
			mBarChartView.repaint();
		}
	}

	private void refresh() {
		List<PieChartData> data = navigate.GetPieChartData();
		mPieSeries.clear();
		mPieRenderer.removeAllRenderers();
		for (PieChartData pieChartData : data) {
			mPieSeries.add(pieChartData.backlogName, pieChartData.data);
			SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
			renderer.setColor(COLORS[(mPieSeries.getItemCount() - 1)
					% COLORS.length]);
			renderer.setChartValuesTextSize(40);
			mPieRenderer.addSeriesRenderer(renderer);
		}
		chartTitle.setText(navigate.CurrentTitle());
		if (mPieChartView != null) {
			mPieChartView.repaint();
		}

		mBarDataset = buildBarDataset();
		mBarRenderer = buildBarRenderer();
		if (mBarChartView != null) {
			mBarDataset = buildBarDataset();
			mBarRenderer = buildBarRenderer();
			mBarChartView = ChartFactory.getBarChartView(this, mBarDataset,
					mBarRenderer, BarChart.Type.DEFAULT);
			barLayout.removeAllViews();
			barLayout.addView(mBarChartView, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		}
	}

	public static class PieChartData {
		public long biid;
		public String backlogName;
		public int data;

		public PieChartData(long biid, String backlogName, int data) {
			this.biid = biid;
			this.backlogName = backlogName;
			this.data = data;
		}
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
				for (PieChartData data : chartData.get(j)) {
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
			for (Integer date : chartData.keySet()) {
				for (PieChartData data : chartData.get(date)) {
					if (categoryMap.containsKey(data.biid)) {
						categoryMap.get(data.biid).add(date - startDate + 1,
								data.data);
					} else {
						XYSeries series = new XYSeries(data.backlogName);
						series.add(date - startDate + 1, data.data);
						categoryMap.put(data.biid, series);
						seriesCount++;
					}

					maxY = data.data > maxY ? data.data : maxY;
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
		renderer.setChartTitleTextSize(20);
		renderer.setLabelsTextSize(15);
		renderer.setBarWidth(20);
		renderer.setApplyBackgroundColor(true);
		renderer.setBackgroundColor(0xFFFAFAFA);
		renderer.setMarginsColor(0xFFFAFAFA);
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
				List<PieChartData> datas = chartData.get(i);
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
				String title = calendar.get(Calendar.MONTH) + "/"
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
