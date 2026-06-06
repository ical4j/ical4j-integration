## Context

`ical4j-integration-mail` exposes calendar-over-email transport via three flavors built on the `ical4j-integration-api` channel contracts:

- **Egress** — `JakartaMailSMTPChannel` (`EgressChannel<Calendar>`) + `EventAttachmentBuilder` (`Function<Calendar, Optional<MimeMessage>>`).
- **Ingress (poll)** — `JakartaMailPollingChannel` (`IngressChannel<Calendar>`) + `CalendarAttachmentProcessor` (`Function<MimeMessage, Optional<Calendar>>`).
- **Reactive** — `JakartaMailPublisher extends SubmissionPublisher<Calendar>` driven by JavaMail `MessageCountListener`.

Current state: egress mostly works; ingress is non-functional (store never connected, wrong folder, 1-based loop bug, always returns false, processor never returns the parsed calendar); the publisher is wired but never connects/opens/idles; tests assert nothing. Addressing is entirely caller-supplied today.

Constraints: Java module system (`module-info.java` per module), Jakarta Mail (`com.sun.mail:jakarta.mail`), iCal4j core model, SLF4J logging, Spock/Groovy tests with Testcontainers.

## Goals / Non-Goals

**Goals:**
- Correct, testable round-trip of calendar data over email for all three flavors.
- Derive `From`/`To`/`Cc` from `ORGANIZER`/`ATTENDEE` using the full iTIP (RFC 5546) `METHOD` matrix.
- Preserve a caller override: explicit addressing always wins over derivation.
- Real test coverage, including a mail-server round-trip.

**Non-Goals:**
- IMAP/POP3 provider-specific tuning beyond what JavaMail offers generically.
- Full iTIP scheduling state machine (sequence handling, REPLY aggregation, free/busy). We address *transport + addressing*, not calendar reconciliation.
- HTML email templating beyond the existing `STYLED-DESCRIPTION` passthrough.
- S/MIME signing or DKIM.

## Decisions

### D1: Addressing as a dedicated strategy, method-aware
Introduce a `RecipientStrategy` (working name) — `Function<Calendar, Optional<Addressing>>` — where `Addressing` carries `from`, `to[]`, `cc[]`. The default implementation branches on the calendar `METHOD`:

```
   METHOD            FROM            TO                CC
   ──────            ────            ──                ──
   REQUEST/CANCEL/   ORGANIZER       all ATTENDEEs     —
   ADD
   REPLY/COUNTER/    the ATTENDEE*   ORGANIZER         —
   REFRESH
   DECLINECOUNTER    ORGANIZER       the ATTENDEE*     —
   PUBLISH           ORGANIZER       (caller-supplied) —
```
\* attendee-originated methods: the relevant attendee is identified by `SENT-BY`/the local identity when known; otherwise the single ATTENDEE present, else left to caller override.

Rationale: keeps iTIP routing logic in one cohesive, unit-testable place; the builder stays focused on MIME assembly. Alternative (inline branching in `EventAttachmentBuilder`) rejected — couples MIME concerns to routing and is harder to test in isolation.

### D2: Caller override semantics
The builder derives addressing only for fields the caller has **not** already set on the `MimeMessage`. Concretely: derive, then apply derived `From`/recipients only where the message has none. This keeps the current integration-test usage (caller sets from/to) working unchanged. Alternative (override always) rejected — breaks back-compat and removes an escape hatch for non-iTIP sends.

### D3: CAL-ADDRESS → InternetAddress mapping
- Strip the `mailto:` scheme from the `CAL-ADDRESS` URI to get the email.
- Use the `CN` parameter as the personal name.
- `SENT-BY` on `ORGANIZER` overrides the `From` (sending on behalf of).
- Non-`mailto:` calendar user addresses (e.g. `urn:`, `http:`) are skipped.
- Attendee filtering: skip `CUTYPE=RESOURCE` and `CUTYPE=ROOM`; include all other roles by default.

### D4: Session ownership
`EventAttachmentBuilder` currently hardcodes `Session.getDefaultInstance(null)`. Move `Session` to an explicit constructor parameter on the builder (and ensure the SMTP channel uses the same session for `Transport`). Rationale: removes hidden global state and makes the SMTP host/port configuration deterministic. Alternative (keep default instance) rejected — fragile, order-dependent, untestable.

### D5: iTIP method on Content-Type
Set the `method` parameter on the calendar body part's content type (`text/calendar; method=REQUEST; charset=...`) derived from the calendar `METHOD`. Required for compliant clients (Outlook/Google) to treat the attachment as an invite rather than a plain `.ics` file.

### D6: Ingress polling correctness
Rewrite `JakartaMailPollingChannel.poll`:
- `store.connect(...)`, `getFolder("INBOX")`, `open(READ_WRITE)`.
- Iterate messages **1-based** (`getMessage(1..count)`), or use `folder.getMessages()`.
- Honour `timeout` (bounded wait/retry); return `true` when ≥1 calendar consumed.
- Implement `expunge(uid)` and the `autoExpunge` overload (set `DELETED` flag + `folder.expunge()` / `close(true)`).
- Close folder/store in a finally block.

### D7: Processor returns the calendar; handles raw bodies
Fix `CalendarAttachmentProcessor` to **return** the parsed `Calendar`. Walk parts for `Content-Type: text/calendar` (attachment *or* inline body), not only `ATTACHMENT` disposition. Single-part `text/calendar` messages must also be handled.

### D8: Reactive publisher actually fires
`JakartaMailPublisher` must connect the store and `open()` each folder, then run an IMAP IDLE keepalive loop (background executor calling `IMAPFolder.idle()`), so `MessageCountEvent`s are delivered. Provide a clean `close()` that stops the keepalive and closes resources.

### D9: Testing strategy
- Unit-test `RecipientStrategy` across every iTIP method (table-driven).
- Round-trip egress→ingress and egress→publisher against **GreenMail** (in-JVM SMTP+IMAP, supports IDLE) — chosen over MailHog because it provides IMAP and IDLE in-process, covering the poll and reactive-publisher paths without an external container. The existing MailHog-based SMTP integration test will be migrated to GreenMail.
- Give existing tests real assertions (verify the server received the message; verify parsed calendar UID/summary).

## Risks / Trade-offs

- **IMAP IDLE flakiness in CI** → use GreenMail in-process; bound idle with timeouts; fall back to poll-based assertions if IDLE proves unstable.
- **iTIP edge cases** (multiple attendees on REPLY, missing ORGANIZER, group addresses) → default conservatively and fall through to caller override; document unsupported cases rather than guessing.
- **Constructor signature changes** (Session, strategy) are **BREAKING** for direct API users → acceptable; module is pre-1.0 and not consumed outside this repo, but call it out in the changelog.
- **Test dependency footprint** → mail-server testcontainer/GreenMail is test-scope only; no runtime impact.

## Open Questions

- For attendee-originated methods with multiple ATTENDEEs and no local-identity hint, is "first attendee" acceptable, or should derivation return empty and force caller override? (Resolved: return empty and force caller override — see `mail-recipient-derivation` "Fall through" requirement.)
