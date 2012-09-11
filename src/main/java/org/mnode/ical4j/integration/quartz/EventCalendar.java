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
package org.mnode.ical4j.integration.quartz;

import net.fortuna.ical4j.filter.PeriodRule;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.component.VEvent;

import org.quartz.Calendar;

public class EventCalendar implements Calendar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4125254628802633435L;
	
	private Calendar baseCalendar;
	
	private String description;
	
	private final VEvent event;
	
	public EventCalendar(VEvent event) {
		this.event = event;
	}
	
	public void setBaseCalendar(Calendar baseCalendar) {
		this.baseCalendar = baseCalendar;
	}

	public Calendar getBaseCalendar() {
		return baseCalendar;
	}

	public boolean isTimeIncluded(long timeStamp) {
		final DateTime dateTime = new DateTime(timeStamp);
		final PeriodRule rule = new PeriodRule(new Period(dateTime, dateTime));
		
		// Calendar returns exclusions..
		boolean timeIncluded = !rule.match(event);
		if (!timeIncluded && baseCalendar != null) {
			timeIncluded = baseCalendar.isTimeIncluded(timeStamp);
		}
		return timeIncluded;
	}

	public long getNextIncludedTime(long timeStamp) {
		return baseCalendar.getNextIncludedTime(timeStamp);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		return null;
	}

}
