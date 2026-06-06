## Why

Tagging a release currently only creates a GitHub Release — artifacts are never published to Maven Central. The existing Gradle publishing config targets the legacy OSSRH host (`s01.oss.sonatype.org`), which Sonatype has sunset in favour of the Central Portal, so both the release path (missing) and the snapshot path (broken/dead host) need to move to the Central Portal.

## What Changes

- **Adopt the `com.vanniktech.maven.publish` plugin** to publish to the Central Portal, replacing the hand-rolled `publishing {}` / `signing {}` / `withJavadocJar()` / `withSourcesJar()` configuration in the root `build.gradle`. **BREAKING** for the build's publishing internals (no consumer-facing API change).
- **Release publishing (new):** a separate tag-triggered workflow runs `publishAndReleaseToMavenCentral`, signing artifacts with an in-memory GPG key and auto-releasing to Central. It runs **independently** of the existing GitHub Release workflow (both fire on the release tag; no ordering between them).
- **Snapshot publishing (migrated):** `publish-snapshots.yml` moves off the dead OSSRH host to the Central Portal snapshot repository via the same plugin.
- **GPG signing in CI:** signing now actually runs for releases (and is configured via in-memory key), which it never has before.
- **New CI secrets:** Central Portal token + GPG key/passphrase, consumed as `ORG_GRADLE_PROJECT_*` env vars.

## Capabilities

### New Capabilities
- `maven-central-publishing`: publishing signed release and snapshot artifacts to the Sonatype Central Portal via Gradle, triggered by CI, independently of GitHub Release creation.

### Modified Capabilities
<!-- None: no existing specs cover publishing. -->

## Impact

- Build: root `build.gradle` `configure(subprojects)` block — remove the manual publishing/signing/jar config, add `mavenPublishing { … }` (coordinates + POM + Central Portal + sign). Add the plugin to the `plugins {}` block (`apply false`) and apply it in subprojects.
- Workflows: new `publish-release.yml` (tag-triggered); update `publish-snapshots.yml` to the new plugin tasks/host. `create-release.yml` unchanged.
- Artifacts published: `org.ical4j:ical4j-integration-api`, `org.ical4j:ical4j-integration-mail` (every included subproject), each with sources + javadoc + signatures.
- Secrets: add `ORG_GRADLE_PROJECT_mavenCentralUsername`, `ORG_GRADLE_PROJECT_mavenCentralPassword`, `ORG_GRADLE_PROJECT_signingInMemoryKey`, `ORG_GRADLE_PROJECT_signingInMemoryKeyPassword`. The legacy `OSS_SONATYPE_*` secrets become unused.
- Versioning: axion-release derives the version from the tag; the publish job needs full tag history (`fetch-depth: 0`).
