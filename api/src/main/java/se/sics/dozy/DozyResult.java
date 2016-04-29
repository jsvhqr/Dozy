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
package se.sics.dozy;

import com.google.common.base.Optional;

/**
 * @author Alex Ormenisan <aaor@kth.se>
 */
public class DozyResult<V extends Object> {

    public final Status status;
    private final Optional<String> details;
    private final Optional<V> value;

    public DozyResult(Status status, Optional<String> details, Optional<V> value) {
        this.status = status;
        this.details = details;
        this.value = value;
    }

    public DozyResult(Status status, Optional<String> details) {
        this.status = status;
        this.details = details;
        this.value = Optional.absent();
    }

    public DozyResult(V val) {
        this.status = Status.OK;
        this.details = Optional.absent();
        this.value = Optional.of(val);
    }

    public boolean ok() {
        return status.equals(Status.OK);
    }
    
    public String getDetails() {
        if(details.isPresent()) {
            return details.get();
        }
        return null;
    }
    
    public boolean hasValue() {
        return value.isPresent();
    }
    
    public V getValue() {
        return value.get();
    }
    
    public static enum Status {
        OK, TIMEOUT, INTERNAL_ERROR, OTHER
    }
    
    
    public static DozyResult ok(Object value) {
        return new DozyResult(Status.OK, Optional.absent(), Optional.fromNullable(value));
    }
    
    public static DozyResult failed(Status status, String reason) {
        return new DozyResult(status, Optional.fromNullable(reason), Optional.absent());
    }
    
    public static DozyResult internalError(String reason) {
        return new DozyResult(Status.INTERNAL_ERROR, Optional.fromNullable(reason));
    }
    
    public static DozyResult timeout() {
        return new DozyResult(Status.TIMEOUT, Optional.absent());
    }
}

