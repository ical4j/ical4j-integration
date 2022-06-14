package org.ical4j.integration.http.feed;

import com.rometools.modules.mediarss.MediaEntryModule;
import com.rometools.modules.mediarss.types.MediaContent;
import com.rometools.modules.mediarss.types.MediaGroup;
import com.rometools.modules.mediarss.types.UrlReference;
import com.rometools.rome.feed.module.Module;
import com.rometools.rome.feed.module.SyModule;
import com.rometools.rome.feed.synd.*;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VJournal;
import net.fortuna.ical4j.model.property.*;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.Date;
import java.util.List;

/**
 * Parse Atom feed XML documents to generate iCalendar object models.
 */
public class FeedParser {

    public Calendar parse(URL url) throws IOException, FeedException, URISyntaxException, ParseException {
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new InputSource(url.openStream()));

        Calendar calendar = new Calendar();
        setUid(calendar.getProperties(), feed.getUri());
        setName(calendar.getProperties(), feed.getTitle());
        setDescription(calendar.getProperties(), feed.getDescription());
        setSource(calendar.getProperties(), url.toExternalForm());
        setUrl(calendar.getProperties(), feed.getLink());
        setImage(calendar.getProperties(), feed.getImage());
        setLastModified(calendar.getProperties(), feed.getPublishedDate());
        for (Module module : feed.getModules()) {
            if (module instanceof SyModule) {
                SyModule syModule = (SyModule) module;
                if ("hourly".equals(syModule.getUpdatePeriod())) {
                    Duration updateDuration = Duration.of(syModule.getUpdateFrequency(), ChronoUnit.HOURS);
                    setRefreshInterval(calendar.getProperties(), updateDuration);
                }
            }
        }

        for (SyndEntry entry : feed.getEntries()) {
            calendar.add(createComponent(entry));
        }
        return calendar;
    }

    private PropertyList<Property> setUid(PropertyList<Property> properties, String uid) throws ParseException, IOException, URISyntaxException {
        properties.add(new Uid.Factory().createProperty(new ParameterList(), uid));
        return properties;
    }

    private PropertyList<Property> setName(PropertyList<Property> properties, String name) throws ParseException, IOException, URISyntaxException {
        properties.add(new Name.Factory().createProperty(new ParameterList(), name));
        return properties;
    }

    private PropertyList<Property> setSummary(PropertyList<Property> properties, String summary) throws ParseException, IOException, URISyntaxException {
        properties.add(new Summary.Factory().createProperty(new ParameterList(), summary));
        return properties;
    }

    private PropertyList<Property> setDescription(PropertyList<Property> properties, String description) throws ParseException, IOException, URISyntaxException {
        properties.add(new Description.Factory().createProperty(new ParameterList(), description));
        return properties;
    }

    private PropertyList<Property> setOrganizer(PropertyList<Property> properties, SyndPerson organizer) throws ParseException, IOException, URISyntaxException {
        properties.add(new Organizer.Factory().createProperty(new ParameterList(), organizer.getUri()));
        return properties;
    }

    private PropertyList<Property> setContact(PropertyList<Property> properties, String contact) throws ParseException, IOException, URISyntaxException {
        properties.add(new Contact.Factory().createProperty(new ParameterList(), contact));
        return properties;
    }

    private PropertyList<Property> setCategories(PropertyList<Property> properties, List<SyndCategory> categoryList) throws ParseException, IOException, URISyntaxException {
        TextList categories = new TextList();
        for (SyndCategory category : categoryList) {
            categories.add(category.getName());
        }
        properties.add(new Categories(categories));
        return properties;
    }

    private PropertyList<Property> setSource(PropertyList<Property> properties, String source) {
        properties.add(new Source.Factory().createProperty(new ParameterList(), source));
        return properties;
    }

    private PropertyList<Property> setUrl(PropertyList<Property> properties, String url) throws ParseException, IOException, URISyntaxException {
        properties.add(new Url.Factory().createProperty(new ParameterList(), url));
        return properties;
    }

    private PropertyList<Property> setImage(PropertyList<Property> properties, SyndImage image) {
        properties.add(new Image.Factory().createProperty(new ParameterList(), image.getUrl()));
        return properties;
    }

    private PropertyList<Property> setImage(PropertyList<Property> properties, MediaContent image) {
        properties.add(new Image(new ParameterList(), ((UrlReference) image.getReference()).getUrl().toString()));
        return properties;
    }

    private PropertyList<Property> setLastModified(PropertyList<Property> properties, Date date) {
        properties.add(new LastModified(new DateTime(date)));
        return properties;
    }

    private PropertyList<Property> setDtStamp(PropertyList<Property> properties, Date date) {
        properties.add(new DtStamp(new DateTime(date)));
        return properties;
    }

    private PropertyList<Property> setRefreshInterval(PropertyList<Property> properties, TemporalAmount refreshInterval) {
        properties.add(new RefreshInterval(new ParameterList(), refreshInterval));
        return properties;
    }

    private CalendarComponent createComponent(SyndEntry entry) throws ParseException, IOException, URISyntaxException {
        VJournal journal = new VJournal(false);
        setUid(journal.getProperties(), entry.getUri());
        setSummary(journal.getProperties(), entry.getTitle());
        setDescription(journal.getProperties(), entry.getDescription().getValue());
        if (!entry.getCategories().isEmpty()) {
            setCategories(journal.getProperties(), entry.getCategories());
        }
        setDtStamp(journal.getProperties(), entry.getPublishedDate());
        if (entry.getUpdatedDate() != null) {
            setLastModified(journal.getProperties(), entry.getUpdatedDate());
        }
        setUrl(journal.getProperties(), entry.getLink());
        if (!entry.getAuthors().isEmpty()) {
            setOrganizer(journal.getProperties(), entry.getAuthors().get(0));
        }
        if (entry.getAuthor() != null) {
            setContact(journal.getProperties(), entry.getAuthor());
        }
        for (Module module : entry.getModules()) {
            if (module instanceof MediaEntryModule) {
                MediaEntryModule mediaEntryModule = (MediaEntryModule) module;
                for (MediaGroup group : mediaEntryModule.getMediaGroups()) {
                    MediaContent defaultContent = group.getContents()[group.getDefaultContentIndex()];
                    if ("image".equals(defaultContent.getMedium())) {
                        setImage(journal.getProperties(), defaultContent);
                    }
                }
            }
        }
        return journal;
    }
}
