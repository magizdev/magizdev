/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.magizdev.babyoneday.profilewizard;

import java.util.ArrayList;

import android.support.v4.app.Fragment;

import com.example.android.wizardpager.wizard.model.ModelCallbacks;
import com.example.android.wizardpager.wizard.model.Page;
import com.example.android.wizardpager.wizard.model.ReviewItem;
import com.magizdev.babyoneday.util.Profile;

/**
 * A page asking for a name and an email.
 */
public class BirthDataPage extends Page {
	public BirthDataPage(ModelCallbacks callbacks, String title) {
		super(callbacks, title);
	}

	@Override
	public Fragment createFragment() {
		return BirthDataFragment.create(getKey());
	}

	@Override
	public void getReviewItems(ArrayList<ReviewItem> dest) {
	}

	@Override
	public boolean isCompleted() {
		boolean isBirthydaySet = mData.getInt(Profile.BIRTHDAY) > 0;
		boolean isHeightSet = mData.getFloat(Profile.HEIGHT) > 0;
		boolean isWeightSet = mData.getFloat(Profile.WEIGHT) > 0;
		return isBirthydaySet && isHeightSet && isWeightSet;
	}
}
