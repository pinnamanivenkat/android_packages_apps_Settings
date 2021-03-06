/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.settings.wifi;

import android.content.Context;
import android.support.v7.preference.PreferenceScreen;

import com.android.settings.core.PreferenceController;
import com.android.settings.core.instrumentation.MetricsFeatureProvider;
import com.android.settings.core.lifecycle.LifecycleObserver;
import com.android.settings.core.lifecycle.events.OnPause;
import com.android.settings.core.lifecycle.events.OnResume;
import com.android.settings.core.lifecycle.events.OnStart;
import com.android.settings.core.lifecycle.events.OnStop;
import com.android.settings.widget.SummaryUpdater;
import com.android.settings.widget.MasterSwitchPreference;
import com.android.settings.widget.MasterSwitchController;

public class WifiMasterSwitchPreferenceController extends PreferenceController
        implements SummaryUpdater.OnSummaryChangeListener,
        LifecycleObserver, OnResume, OnPause, OnStart, OnStop {

    public static final String KEY_TOGGLE_WIFI = "toggle_wifi";

    private MasterSwitchPreference mWifiPreference;
    private WifiEnabler mWifiEnabler;
    private final WifiSummaryUpdater mSummaryHelper;
    private final MetricsFeatureProvider mMetricsFeatureProvider;

    public WifiMasterSwitchPreferenceController(Context context,
            MetricsFeatureProvider metricsFeatureProvider) {
        super(context);
        mMetricsFeatureProvider = metricsFeatureProvider;
        mSummaryHelper = new WifiSummaryUpdater(mContext, this);
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        mWifiPreference = (MasterSwitchPreference) screen.findPreference(KEY_TOGGLE_WIFI);
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String getPreferenceKey() {
        return KEY_TOGGLE_WIFI;
    }

    @Override
    public void onResume() {
        mSummaryHelper.register(true);
        if (mWifiEnabler != null) {
            mWifiEnabler.resume(mContext);
        }
    }

    @Override
    public void onPause() {
        if (mWifiEnabler != null) {
            mWifiEnabler.pause();
        }
        mSummaryHelper.register(false);
    }

    @Override
    public void onStart() {
        mWifiEnabler = new WifiEnabler(mContext, new MasterSwitchController(mWifiPreference),
            mMetricsFeatureProvider);
    }

    @Override
    public void onStop() {
        if (mWifiEnabler != null) {
            mWifiEnabler.teardownSwitchController();
        }
    }

    @Override
    public void onSummaryChanged(String summary) {
        if (mWifiPreference != null) {
            mWifiPreference.setSummary(summary);
        }
    }

}
