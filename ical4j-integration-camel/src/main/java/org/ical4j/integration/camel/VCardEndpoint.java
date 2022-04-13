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
package org.ical4j.integration.camel;

import org.apache.camel.Consumer;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.support.DefaultPollingEndpoint;
import org.apache.camel.util.ObjectHelper;

public class VCardEndpoint extends DefaultPollingEndpoint {
	
    /**
     * Header key for the {@link net.fortuna.ical4j.vcard.VCard} object is stored on the in message on the exchange.
     */
    public static final String CALENDAR_FEED = "CamelVCardFeed";

	private String vCardUri;
	
	private boolean multipleCards;
	
	public VCardEndpoint() {
	}

	public VCardEndpoint(String endpointUri, VCardComponent vCardComponent, String vCardUri) {
        super(endpointUri, vCardComponent);
        this.vCardUri = vCardUri;
    }
	
	public Producer createProducer() throws Exception {
		throw new UnsupportedOperationException("VCardProducer is not implemented");
	}

    public Consumer createConsumer(Processor processor) throws Exception {
        ObjectHelper.notNull(vCardUri, "vCardUri");

        VCardPollingConsumer answer = new VCardPollingConsumer(this, processor);
        
        // ScheduledPollConsumer default delay is 500 millis and that is too often for polling a vCard,
        // so we override with a new default value. End user can override this value by providing a consumer.delay parameter
        answer.setDelay(VCardPollingConsumer.DEFAULT_CONSUMER_DELAY);
        configureConsumer(answer);
        return answer;
    }

	public boolean isSingleton() {
		return true;
	}

	public String getVCardUri() {
		return vCardUri;
	}

	public void setVCardUri(String uri) {
		this.vCardUri = uri;
	}

    public boolean isMultipleCards() {
		return multipleCards;
	}

	public void setMultipleCards(boolean multipleCards) {
		this.multipleCards = multipleCards;
	}

	public Exchange createExchange(Object vCard) {
        Exchange exchange = createExchange();
        exchange.getIn().setBody(vCard);
        return exchange;
    }

}
