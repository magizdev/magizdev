package com.magizdev.gobbler;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class DashboardUtil {
	SharedPreferences prefs;
	Editor editor;
	
	public DashboardUtil(Context context){
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	public List<Integer> getHighScores(){
		List<Integer> highScores = new ArrayList<Integer>();
		highScores.add(prefs.getInt(DashboardActivity.HIGH_SCORE_1, 0));
		highScores.add(prefs.getInt(DashboardActivity.HIGH_SCORE_2, 0));
		highScores.add(prefs.getInt(DashboardActivity.HIGH_SCORE_3, 0));
		highScores.add(prefs.getInt(DashboardActivity.HIGH_SCORE_4, 0));
		highScores.add(prefs.getInt(DashboardActivity.HIGH_SCORE_5, 0));
		
		return highScores;
	}
	
	public int getStars(){
		return prefs.getInt(DashboardActivity.STAR_ACHIEVEMENT, 0);
	}
	
	public void insertHighScore(int highScore){
		int temp = prefs.getInt(DashboardActivity.HIGH_SCORE_5, 0);
		if(highScore <= temp){
			return;
		}
		editor = prefs.edit();
		temp = insert(DashboardActivity.HIGH_SCORE_1, highScore);
		temp = insert(DashboardActivity.HIGH_SCORE_2, temp);
		temp = insert(DashboardActivity.HIGH_SCORE_3, temp);
		temp = insert(DashboardActivity.HIGH_SCORE_4, temp);
		temp = insert(DashboardActivity.HIGH_SCORE_5, temp);
		editor.commit();
	}
	
	public void addStar(int star){
		int temp = prefs.getInt(DashboardActivity.STAR_ACHIEVEMENT, 0);
		editor=prefs.edit();
		editor.putInt(DashboardActivity.STAR_ACHIEVEMENT, temp + star);
		editor.commit();
	}

	public int insert(String tag, int highScore){
		int temp = prefs.getInt(tag, 0);
		if(highScore > temp){
			editor.putInt(tag, highScore);
			return temp;
		}else {
			return highScore;
		}
	}
}
