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
package org.mnode.ical4j.integration.camel

import net.fortuna.ical4j.vcard.VCard
import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.Predicate
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.impl.DefaultCamelContext
import spock.lang.Shared
import spock.lang.Specification

class VCardComponentSpec extends Specification {

	@Shared CamelContext camelContext = new DefaultCamelContext()
	
	def setup() {
		camelContext.start()
	}
	
	def cleanup() {
		camelContext.stop()
	}

//	@Ignore	
	def 'test polling consumer'() {
		setup:
		RouteBuilder builder = new RouteBuilder() {
			@Override
			void configure() throws Exception {
				from('vcard:file:src/test/resources/vcard-rfc2426.vcf')
				.to("mock:result-1")
			}
		}
		camelContext.addRoutes builder
		
		expect:
		MockEndpoint mock = camelContext.getEndpoint("mock:result-1", MockEndpoint);
		mock.expectedMessageCount(1);
		mock.expectedMessagesMatches(new Predicate() {
			boolean matches(Exchange exchange) {
				exchange.getIn().body.class == VCard
			}
		})
		mock.assertIsSatisfied();
	}
	
	def 'test polling consumer with multiple cards'() {
		setup:
		RouteBuilder builder = new RouteBuilder() {
			@Override
			void configure() throws Exception {
				from('vcard:file:src/test/resources/vcard-rfc2426.vcf?multipleCards=true')
				.to("mock:result-2")
			}
		}
		camelContext.addRoutes builder
		
		expect:
		MockEndpoint mock = camelContext.getEndpoint("mock:result-2", MockEndpoint);
		mock.expectedMessageCount(1);
		mock.expectedMessagesMatches(new Predicate() {
			boolean matches(Exchange exchange) {
				exchange.getIn().body[0].class == VCard
			}
		})
		mock.assertIsSatisfied();
	}
}
