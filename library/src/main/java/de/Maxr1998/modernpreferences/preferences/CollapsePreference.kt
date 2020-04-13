/*
 * Copyright (C) 2018 Max Rumpf alias Maxr1998
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

package de.Maxr1998.modernpreferences.preferences

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import de.Maxr1998.modernpreferences.Preference
import de.Maxr1998.modernpreferences.PreferenceScreen
import de.Maxr1998.modernpreferences.PreferencesAdapter
import de.Maxr1998.modernpreferences.R

class CollapsePreference(internal val screen: PreferenceScreen.Builder, key: String) : Preference(key), PreferenceScreen.Appendable {
    private val preferences = ArrayList<Preference>()

    init {
        titleRes = R.string.pref_advanced_block
        iconRes = R.drawable.map_ic_expand_24dp
    }

    override fun addPreferenceItem(p: Preference) {
        screen.addPreferenceItem(p)
        preferences.add(p.apply { visible = false })
    }

    /*
     This makes sure we don't get recycled for any other preference.
     Hopefully, no one else uses this constant for the exact same purpose…
     But that will probably not be our problem.
     */
    @SuppressLint("ResourceType")
    override fun getWidgetLayoutResource() = -10

    override fun bindViews(holder: PreferencesAdapter.ViewHolder) {
        if (visible) buildSummary(holder.itemView.context)
        super.bindViews(holder)
        holder.summary?.apply {
            setSingleLine()
            ellipsize = TextUtils.TruncateAt.END
        }
    }

    private fun buildSummary(context: Context) {
        if (summaryRes != -1 || summary != null)
            return

        summary = preferences.asSequence().filter(Preference::includeInCollapseSummary).take(5).joinToString { p ->
            when {
                p.titleRes != -1 -> context.getString(p.titleRes)
                else -> p.title
            }
        }
    }

    override fun onClick(holder: PreferencesAdapter.ViewHolder) {
        visible = false
        for (i in preferences.indices)
            preferences[i].visible = true
        parent?.requestRebind(screenPosition, 1 + preferences.size)
    }

    fun reset() {
        visible = true
        for (i in preferences.indices)
            preferences[i].visible = false
        parent?.requestRebind(screenPosition, 1 + preferences.size)
    }

    // Utility method
    @Suppress("NOTHING_TO_INLINE")
    private inline operator fun StringBuilder.plusAssign(string: String) {
        append(string)
    }
}