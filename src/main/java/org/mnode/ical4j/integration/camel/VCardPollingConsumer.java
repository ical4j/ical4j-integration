/**
 * Copyright (c) 2012, Ben Fortuna
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  o Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 *  o Neither the name of Ben Fortuna nor the names of any other contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.mnode.ical4j.integration.camel;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.vcard.VCard;
import net.fortuna.ical4j.vcard.VCardBuilder;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.ScheduledPollConsumer;

public class VCardPollingConsumer extends ScheduledPollConsumer {
	
    public static final long DEFAULT_CONSUMER_DELAY = 60 * 1000L;

	private final VCardEndpoint endpoint;
	
	public VCardPollingConsumer(VCardEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
        this.endpoint = endpoint;
	}
	
	@Override
	protected int poll() throws Exception {
        Object vCard;
        if (endpoint.isMultipleCards()) {
        	vCard = createVCards();
        }
        else {
        	vCard = createVCard();
        }
        if (vCard != null) {
            Exchange exchange = endpoint.createExchange(vCard);
            getProcessor().process(exchange);
            return 1;
        } else {
            return 0;
        }
	}

	private VCard createVCard() throws MalformedURLException, IOException, ParserException {
		return new VCardBuilder(new URL(endpoint.getVCardUri()).openStream()).build();
	}

	private List<VCard> createVCards() throws MalformedURLException, IOException, ParserException {
		return new VCardBuilder(new URL(endpoint.getVCardUri()).openStream()).buildAll();
	}
}
