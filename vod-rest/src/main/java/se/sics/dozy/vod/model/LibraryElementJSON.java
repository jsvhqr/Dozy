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
package se.sics.dozy.vod.model;

import se.sics.gvod.mngr.event.LibraryElementEvent;
import se.sics.gvod.mngr.util.TorrentStatus;
import se.sics.ktoolbox.util.identifiable.basic.IntIdentifier;

/**
 * @author Alex Ormenisan <aaor@kth.se>
 */
public class LibraryElementJSON {
    private int overlayId;
    private String fileName;
    private TorrentStatus status;
    
    public LibraryElementJSON(int overlayId, String fileName, TorrentStatus status) {
        this.overlayId = overlayId;
        this.fileName = fileName;
        this.status = status;
    }
    
    public LibraryElementJSON() {}

    public int getOverlayId() {
        return overlayId;
    }

    public void setOverlayId(int overlayId) {
        this.overlayId = overlayId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public TorrentStatus getStatus() {
        return status;
    }

    public void setStatus(TorrentStatus status) {
        this.status = status;
    }

    public static LibraryElementJSON resolve(LibraryElementEvent.Response vodResp) {
        return new LibraryElementJSON(((IntIdentifier)vodResp.req.overlayId).id, vodResp.req.fileName, vodResp.content.status);
    }
}
