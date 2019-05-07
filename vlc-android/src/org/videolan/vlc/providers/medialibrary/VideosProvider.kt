/*****************************************************************************
 * VideosProvider.kt
 *****************************************************************************
 * Copyright © 2019 VLC authors and VideoLAN
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 *****************************************************************************/

package org.videolan.vlc.providers.medialibrary

import android.content.Context
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.videolan.medialibrary.media.Folder
import org.videolan.medialibrary.media.MediaWrapper
import org.videolan.vlc.media.getAll
import org.videolan.vlc.viewmodels.paged.MLPagedModel


@ExperimentalCoroutinesApi
class VideosProvider(val folder : Folder?, context: Context, scope: MLPagedModel<MediaWrapper>) : MedialibraryProvider<MediaWrapper>(context, scope){

    override fun canSortByFileNameName() = true
    override fun canSortByDuration() = true
    override fun canSortByLastModified() = folder == null

    override fun getTotalCount() = if (scope.filterQuery == null) when {
        folder !== null -> folder.mediaCount(Folder.TYPE_FOLDER_VIDEO)
        else -> medialibrary.videoCount
    } else when {
        folder !== null -> folder.searchTracksCount(scope.filterQuery, Folder.TYPE_FOLDER_VIDEO)
        else -> medialibrary.getVideoCount(scope.filterQuery)
    }

    override fun getPage(loadSize: Int, startposition: Int): Array<MediaWrapper> {
        val list = if (scope.filterQuery == null) when {
            folder !== null -> folder.media(Folder.TYPE_FOLDER_VIDEO, scope.sort, scope.desc, loadSize, startposition)
            else -> medialibrary.getPagedVideos(scope.sort, scope.desc, loadSize, startposition)
        } else when {
            folder !== null -> folder.searchTracks(scope.filterQuery, Folder.TYPE_FOLDER_VIDEO, scope.sort, scope.desc, loadSize, startposition)
            else -> medialibrary.searchVideo(scope.filterQuery, scope.sort, scope.desc, loadSize, startposition)
        }
        return list.also { completeHeaders(it, startposition) }
    }

    override fun getAll(): Array<MediaWrapper> = when {
        folder !== null -> folder.getAll(Folder.TYPE_FOLDER_VIDEO, scope.sort, scope.desc).toTypedArray()
        else -> medialibrary.videos
    }
}