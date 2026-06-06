## Why

The `ical4j-integration-mail` module is only partially implemented. Egress (SMTP send) mostly works, but the ingress (polling) path is non-functional, the reactive publisher is inert scaffolding, recipient addressing must be set by the caller, and the tests assert nothing. As a result the module cannot reliably round-trip calendar data over email, which is its primary purpose.

## What Changes

- **Recipient derivation (new):** derive `From`/`To`/`Cc` from the calendar's `ORGANIZER` and `ATTENDEE` properties, following the full iTIP (RFC 5546) `METHOD` matrix so the message is routed in the correct direction (e.g. `REQUEST` → attendees, `REPLY`/`COUNTER` → organizer).
- **Caller override retained:** automatic derivation is the default; if the caller sets addressing explicitly, that wins. Keeps existing usage working.
- **Egress builder fixes:** set the iTIP `method` parameter on the calendar `Content-Type`; resolve the `Session` ownership (stop hardcoding `Session.getDefaultInstance(null)`); handle calendars beyond a single `VEVENT`.
- **Ingress polling fixes:** connect the store, open `INBOX`, correct the 1-based message loop, honour `timeout`, return whether messages were consumed, and implement `expunge`/`autoExpunge`.
- **Processor fix:** actually return the parsed `Calendar` (currently always returns `Optional.empty()`); handle raw `text/calendar` bodies, not only attachments.
- **Reactive publisher fix:** connect the store, open folders, and add an IMAP IDLE keepalive so `MessageCountEvent`s actually fire.
- **Tests:** replace no-op assertions with real ones; add MailHog/GreenMail round-trip coverage for poll and publish; add unit tests for recipient derivation across iTIP methods.

## Capabilities

### New Capabilities
- `mail-recipient-derivation`: deriving email envelope addressing (`From`/`To`/`Cc`) from iCalendar `ORGANIZER`/`ATTENDEE` properties using the iTIP `METHOD` matrix, with caller override.
- `mail-egress`: sending a calendar over SMTP — builds the MIME message, derives recipients by default, sets the iTIP `method` content-type parameter, takes an explicit `Session`.
- `mail-ingress`: polling a mailbox for calendar objects — connect/open `INBOX`, correct iteration, `timeout`, return value, `expunge`/`autoExpunge`.
- `mail-reactive`: a reactive `Publisher` that emits calendar objects as they arrive (store connect, folder open, IDLE keepalive).

### Modified Capabilities
<!-- None: openspec/specs/ is empty, so all behavior is captured as new specs. -->


## Impact

- Module: `ical4j-integration-mail` (`builder`, `processor`, channels, publisher).
- Classes: `EventAttachmentBuilder`, `CalendarAttachmentProcessor`, `JakartaMailSMTPChannel`, `JakartaMailPollingChannel`, `JakartaMailPublisher`; likely a new addressing/strategy type.
- Dependencies: `com.sun.mail:jakarta.mail`; test scope adds a mail server testcontainer (MailHog/GreenMail).
- API: constructor signatures may change (e.g. channels/builder taking a `Session` and addressing strategy). No consumers outside this repo are tracked.
