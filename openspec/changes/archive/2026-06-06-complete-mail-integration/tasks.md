## 1. Test harness

- [x] 1.1 Add GreenMail (SMTP + IMAP, in-JVM) at test scope to `ical4j-integration-mail/build.gradle` and version catalog
- [x] 1.2 Add a small GreenMail Spock base/helper for starting SMTP+IMAP and building test `Session`s

## 2. Recipient derivation

- [x] 2.1 Add an `Addressing` value type (`from`, `to[]`, `cc[]`) and a `RecipientStrategy` (`Function<Calendar, Optional<Addressing>>`)
- [x] 2.2 Implement CAL-ADDRESS → `InternetAddress` mapping: strip `mailto:`, use `CN` as personal name, skip non-`mailto:` addresses
- [x] 2.3 Implement the iTIP METHOD matrix (REQUEST/CANCEL/ADD, REPLY/COUNTER/REFRESH, DECLINECOUNTER, PUBLISH)
- [x] 2.4 Honour `ORGANIZER` `SENT-BY` for `From`; exclude `CUTYPE=RESOURCE`/`ROOM` attendees
- [x] 2.5 Return empty when addressing cannot be derived (fall through to caller override)
- [x] 2.6 Unit-test derivation across every iTIP method (table-driven)

## 3. Egress

- [x] 3.1 Give `EventAttachmentBuilder` an explicit `Session` (remove `Session.getDefaultInstance(null)`)
- [x] 3.2 Set the iTIP `method` parameter on the calendar body part content type
- [x] 3.3 Apply derived addressing only to fields the caller has not already set (caller override)
- [x] 3.4 Handle calendars with components beyond a single `VEVENT`
- [x] 3.5 Ensure `JakartaMailSMTPChannel` uses the same `Session` for transport
- [x] 3.6 Replace the no-op assertions in the SMTP unit + integration tests with real ones (assert the server received the message and addressing)

## 4. Ingress (polling)

- [x] 4.1 Fix `CalendarAttachmentProcessor` to return the parsed `Calendar`; handle inline `text/calendar` bodies as well as attachments
- [x] 4.2 Rewrite `JakartaMailPollingChannel.poll`: connect store, open `INBOX`, iterate 1-based, return `true` when calendars consumed
- [x] 4.3 Honour the `timeout` parameter
- [x] 4.4 Implement `expunge(uid)` and the `autoExpunge` overload; close folder/store in a finally block
- [x] 4.5 Add round-trip tests (egress → poll) against the test mail server, with assertions on parsed calendar and expunge

## 5. Reactive publisher

- [x] 5.1 Connect the store and open monitored folders in `JakartaMailPublisher`
- [x] 5.2 Add an IMAP IDLE keepalive loop on a background executor so `MessageCountEvent`s fire
- [x] 5.3 Parse `text/calendar` content and `submit` to subscribers; ignore non-calendar messages
- [x] 5.4 Implement clean `close()` that stops the keepalive and releases store/folders
- [x] 5.5 Add a round-trip test (egress → publisher subscriber receives calendar)

## 6. Wrap-up

- [x] 6.1 Update `module-info.java` exports if new packages are introduced (e.g. addressing)
- [x] 6.2 Run `openspec validate complete-mail-integration --strict` and the module test suite green
- [x] 6.3 Note BREAKING constructor signature changes (Session, strategy) in the changelog/README
