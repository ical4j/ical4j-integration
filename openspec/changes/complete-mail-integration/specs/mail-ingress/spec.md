## ADDED Requirements

### Requirement: Poll a mailbox for calendar objects

The system SHALL poll a mail store for calendar objects by connecting the store, opening the `INBOX` folder, and iterating its messages. For each message containing a `text/calendar` part (whether an attachment or an inline body), the system SHALL parse the calendar and pass it to the supplied consumer. The poll operation SHALL return `true` when one or more calendars were delivered to the consumer.

#### Scenario: Calendar parsed and consumed
- **WHEN** the inbox contains a message with a `text/calendar` attachment
- **THEN** the consumer receives the parsed calendar and `poll` returns `true`

#### Scenario: Inline calendar body handled
- **WHEN** the inbox contains a single-part message whose content type is `text/calendar`
- **THEN** the consumer receives the parsed calendar

#### Scenario: No new messages
- **WHEN** the inbox contains no messages with calendar content
- **THEN** the consumer is not invoked and `poll` returns `false`

### Requirement: Honour the poll timeout

The system SHALL bound the time spent waiting for messages by the supplied `timeout` value and return once the timeout elapses if no messages are available.

#### Scenario: Timeout elapses with empty inbox
- **WHEN** `poll` is called with a timeout and no messages arrive
- **THEN** `poll` returns within approximately the timeout and reports `false`

### Requirement: Remove processed messages on request

The system SHALL support removing processed messages from the mailbox. When `autoExpunge` is requested, consumed messages SHALL be flagged deleted and expunged; the `expunge(uid)` operation SHALL remove the identified message.

#### Scenario: Auto-expunge removes consumed message
- **WHEN** `poll` is called with `autoExpunge=true` and a calendar message is consumed
- **THEN** that message is removed from the inbox

#### Scenario: Expunge by identifier
- **WHEN** `expunge(uid)` is called for a message present in the inbox
- **THEN** that message is removed and the call returns `true`
