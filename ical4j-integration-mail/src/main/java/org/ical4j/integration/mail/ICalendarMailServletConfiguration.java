package org.ical4j.integration.mail;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "iCalendar Serializer Servlet", description = "Servlet Configuration for an iCalendar serializer")
@interface ICalendarMailServletConfiguration {

    @AttributeDefinition(name = "alias", description = "Servlet alias")
    String alias() default "/serializer";
}
