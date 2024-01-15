/*
 * Copyright (C) 2014-2024 Arpit Khurana <arpitkh96@gmail.com>, Vishal Nehra <vishalmeham2@gmail.com>,
 * Emmanuel Messulam<emmanuelbendavid@gmail.com>, Raymond Lai <airwave209gt at gmail.com> and Contributors.
 *
 * This file is part of Amaze File Manager.
 *
 * Amaze File Manager is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.amaze.filemanager.asynchronous.asynctasks.searchfilesystem

import android.database.Cursor
import android.provider.MediaStore
import com.amaze.filemanager.filesystem.RootHelper
import java.io.File

class IndexedSearch(private val cursor: Cursor) : FileSearch() {
    override suspend fun search(
        path: String,
        filter: SearchFilter,
        searchParameters: SearchParameters
    ) {
        if (cursor.count > 0 && cursor.moveToFirst()) {
            do {
                val nextPath =
                    cursor.getString(
                        cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
                    )

                if (nextPath != null &&
                    nextPath.contains(path) &&
                    filter.searchFilter(path)
                ) {
                    val hybridFileParcelable =
                        RootHelper.generateBaseFile(
                            File(nextPath),
                            SearchParameter.SHOW_HIDDEN_FILES in searchParameters
                        )

                    if (hybridFileParcelable != null) {
                        publishProgress(hybridFileParcelable)
                    }
                }
            } while (cursor.moveToNext())
        }

        cursor.close()
    }
}
