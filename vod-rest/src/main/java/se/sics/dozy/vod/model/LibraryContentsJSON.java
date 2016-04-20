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

import java.util.HashMap;
import java.util.Map;
import org.javatuples.Pair;
import se.sics.gvod.mngr.event.LibraryContentsEvent;
import se.sics.gvod.mngr.util.TorrentStatus;
import se.sics.gvod.mngr.util.FileInfo;
import se.sics.gvod.mngr.util.TorrentInfo;
import se.sics.ktoolbox.util.identifiable.Identifier;
import se.sics.ktoolbox.util.identifiable.basic.IntIdentifier;

/**
 * @author Alex Ormenisan <aaor@kth.se>
 */
public class LibraryContentsJSON {
    private  Map<Integer, Pair<String, TorrentStatus>> contents;
    
    public LibraryContentsJSON( Map<Integer, Pair<String, TorrentStatus>> contents) {
        this.contents = contents;
    }
    
    public LibraryContentsJSON() {}

    public  Map<Integer, Pair<String, TorrentStatus>> getContents() {
        return contents;
    }

    public void setContents( Map<Integer, Pair<String, TorrentStatus>> contents) {
        this.contents = contents;
    }
    
    public static LibraryContentsJSON resolve(LibraryContentsEvent.Response vodResp) {
        Map<Integer, Pair<String, TorrentStatus>> contents = new HashMap<>();
        for(Map.Entry<Identifier, Pair<FileInfo, TorrentInfo>> e : vodResp.content.entrySet()) {
            contents.put(((IntIdentifier)e.getKey()).id, Pair.with(e.getValue().getValue0().name, e.getValue().getValue1().status));
        }
        return new LibraryContentsJSON(contents);
    }
}
