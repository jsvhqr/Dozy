/*
 * Copyright (C) 2016 Swedish Institute of Computer Science (SICS) Copyright (C)
 * 2016 Royal Institute of Technology (KTH)
 *
 * Dozy is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package se.sics.dozy.vod.util;

import org.javatuples.Pair;
import se.sics.dozy.DozyResult;
import se.sics.gvod.mngr.event.LibraryContentsEvent;
import se.sics.gvod.mngr.event.LibraryElementEvent;
import se.sics.gvod.mngr.event.TorrentDownloadEvent;
import se.sics.gvod.mngr.event.TorrentStopEvent;
import se.sics.gvod.mngr.event.TorrentUploadEvent;

/**
 * @author Alex Ormenisan <aaor@kth.se>
 */
public class ResponseStatusMapper {

    public static Pair<javax.ws.rs.core.Response.Status, String> resolveLibraryContents(DozyResult<LibraryContentsEvent.Response> result) {
        return resolve(result.status, (result.hasValue() ? result.getValue().result.status : null));
    }

    public static Pair<javax.ws.rs.core.Response.Status, String> resolveLibraryElement(DozyResult<LibraryElementEvent.Response> result) {
        return resolve(result.status, (result.hasValue() ? result.getValue().result.status : null));
    }

    public static Pair<javax.ws.rs.core.Response.Status, String> resolveTorrentUpload(DozyResult<TorrentUploadEvent.Response> result) {
        return resolve(result.status, (result.hasValue() ? result.getValue().result.status : null));
    }

    public static Pair<javax.ws.rs.core.Response.Status, String> resolveTorrentDownload(DozyResult<TorrentDownloadEvent.Response> result) {
        return resolve(result.status, (result.hasValue() ? result.getValue().result.status : null));
    }

    public static Pair<javax.ws.rs.core.Response.Status, String> resolveTorrentStop(DozyResult<TorrentStopEvent.Response> result) {
        return resolve(result.status, (result.hasValue() ? result.getValue().result.status : null));
    }

    public static Pair<javax.ws.rs.core.Response.Status, String> resolve(se.sics.dozy.DozyResult.Status dozyStatus, se.sics.gvod.mngr.util.Result.Status vodStatus) {
        switch (dozyStatus) {
            case OK:
                break; // continue and check vod response status
            case INTERNAL_ERROR:
                return Pair.with(javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR, "dozy problem");
            case OTHER:
                return Pair.with(javax.ws.rs.core.Response.Status.SEE_OTHER, "dozy problem");
            case TIMEOUT:
                return Pair.with(javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE, "vod problem");
            default:
                return Pair.with(javax.ws.rs.core.Response.Status.SEE_OTHER, "dozy problem");
        }
        
        if(vodStatus == null) {
            return Pair.with(javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR, "dozy status logic problem");
        }
        switch (vodStatus) {
            case SUCCESS:
                return Pair.with(javax.ws.rs.core.Response.Status.OK, "ok");
            case TIMEOUT:
                return Pair.with(javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE, "vod problem");
            case INTERNAL_FAILURE:
                return Pair.with(javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR, "vod problem");
            case BAD_REQUEST:
                return Pair.with(javax.ws.rs.core.Response.Status.BAD_REQUEST, "app problem");
            default:
                return Pair.with(javax.ws.rs.core.Response.Status.SEE_OTHER, "vod problem");
        }
    }
}
