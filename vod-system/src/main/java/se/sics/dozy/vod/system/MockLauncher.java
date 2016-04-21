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
package se.sics.dozy.vod.system;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sics.dozy.DozyResource;
import se.sics.dozy.DozySyncComp;
import se.sics.dozy.DozySyncI;
import se.sics.dozy.dropwizard.DropwizardDozy;
import se.sics.dozy.vod.DozyVoD;
import se.sics.dozy.vod.LibraryContentsREST;
import se.sics.dozy.vod.LibraryElementREST;
import se.sics.dozy.vod.mock.VoDMngrMockComp;
import se.sics.gvod.mngr.LibraryPort;
import se.sics.gvod.mngr.event.LibraryContentsEvent;
import se.sics.gvod.mngr.event.LibraryElementEvent;
import se.sics.kompics.Channel;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Init;
import se.sics.kompics.Kompics;
import se.sics.kompics.KompicsEvent;
import se.sics.kompics.Start;
import se.sics.kompics.config.ConfigException;
import se.sics.kompics.timer.Timer;
import se.sics.kompics.timer.java.JavaTimer;

/**
 * @author Alex Ormenisan <aaor@kth.se>
 */
public class MockLauncher extends ComponentDefinition {

    private Logger LOG = LoggerFactory.getLogger(MockLauncher.class);
    private String logPrefix = "";

    //****************************INTERNAL_STATE********************************
    private Component timerComp;
    private Component vodMockComp;
    private Component librarySyncIComp;
    private DropwizardDozy webserver;

    public MockLauncher() {
        LOG.info("{}starting...", logPrefix);

        subscribe(handleStart, control);
    }

    Handler handleStart = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            LOG.info("{}starting", logPrefix);

            timerComp = create(JavaTimer.class, Init.NONE);
            setVoDMock();
            setLibrarySyncI();
            setWebserver();

            trigger(Start.event, timerComp.control());
            trigger(Start.event, vodMockComp.control());
            trigger(Start.event, librarySyncIComp.control());
            startWebserver();
        }
    };

    private void setVoDMock() {
        vodMockComp = create(VoDMngrMockComp.class, new VoDMngrMockComp.Init());
    }

    private void setLibrarySyncI() {
        List<Class<? extends KompicsEvent>> resp = new ArrayList<>();
        resp.add(LibraryContentsEvent.Response.class);
        resp.add(LibraryElementEvent.Response.class);
        librarySyncIComp = create(DozySyncComp.class, new DozySyncComp.Init(LibraryPort.class, resp));
        connect(librarySyncIComp.getNegative(Timer.class), timerComp.getPositive(Timer.class), Channel.TWO_WAY);
        connect(librarySyncIComp.getNegative(LibraryPort.class), vodMockComp.getPositive(LibraryPort.class), Channel.TWO_WAY);
    }

    private void setWebserver() {
        Map<String, DozySyncI> synchronousInterfaces = new HashMap<>();
        synchronousInterfaces.put(DozyVoD.libraryDozyName, (DozySyncI) librarySyncIComp.getComponent());

        List<DozyResource> resources = new ArrayList<>();
        resources.add(new LibraryContentsREST());
        resources.add(new LibraryElementREST());

        webserver = new DropwizardDozy(synchronousInterfaces, resources);
    }

    private void startWebserver() {
        String[] args = new String[]{"server", config().getValue("webservice.server", String.class)};
        try {
            webserver.run(args);
        } catch (ConfigException ex) {
            LOG.error("{}configuration error:{}", logPrefix, ex.getMessage());
            throw new RuntimeException(ex);
        } catch (Exception ex) {
            LOG.error("{}dropwizard error:{}", logPrefix, ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    public static void main(String[] args) throws IOException {
        if (Kompics.isOn()) {
            Kompics.shutdown();
        }
        Kompics.createAndStart(MockLauncher.class, Runtime.getRuntime().availableProcessors(), 20); // Yes 20 is totally arbitrary
        try {
            Kompics.waitForTermination();
        } catch (InterruptedException ex) {
            System.exit(1);
        }
    }
}
