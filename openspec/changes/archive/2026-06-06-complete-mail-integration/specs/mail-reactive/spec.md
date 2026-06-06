## ADDED Requirements

### Requirement: Publish calendar objects as they arrive

The system SHALL provide a reactive `Publisher` that emits calendar objects to subscribers as matching messages arrive in the monitored mail folders. The publisher SHALL connect the mail store and open each monitored folder so that new-message events are delivered, parse `text/calendar` content from each new message, and submit the parsed calendar to subscribers.

#### Scenario: New message emitted to subscriber
- **WHEN** a subscriber is registered and a message with calendar content arrives in a monitored folder
- **THEN** the subscriber receives the parsed calendar

#### Scenario: Non-calendar message ignored
- **WHEN** a message without calendar content arrives in a monitored folder
- **THEN** no item is emitted to subscribers

### Requirement: Maintain a live connection for event delivery

The system SHALL maintain the folder connection (e.g. via an IMAP IDLE keepalive) for as long as the publisher is active so that message-count events continue to fire, and SHALL release the connection and stop the keepalive when the publisher is closed.

#### Scenario: Events continue over time
- **WHEN** the publisher has been active beyond an idle interval and a new message arrives
- **THEN** the subscriber still receives the parsed calendar

#### Scenario: Close releases resources
- **WHEN** the publisher is closed
- **THEN** the keepalive stops and the store and folders are closed
