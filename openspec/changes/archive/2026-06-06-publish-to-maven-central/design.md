## Context

The project is a Gradle multi-module library (`ical4j-integration-api`, `ical4j-integration-mail`, with others commented out in `settings.gradle`). Publishing is configured once in the root `build.gradle` `configure(subprojects)` block:

- `maven-publish` + `signing` plugins applied to every subproject.
- A `publishing {}` block defines a `MavenPublication` per project (`from components.java`, POM via `pom.withXml`) and an OSSRH `maven` repository whose URL switches between `s01.oss.sonatype.org` snapshot and staging-deploy endpoints based on the version suffix.
- `signing { required { isReleaseVersion }; sign publications[name] }` — so signing is skipped for snapshots and has therefore never run in CI.
- `java { withJavadocJar(); withSourcesJar() }` produces the extra jars Central requires.
- Versions come from the `axion-release` plugin (`scmVersion`), driven by `ical4j-integration-*` git tags.

CI: `publish-snapshots.yml` runs `./gradlew build -x test publish` on pushes to `develop` (now on Java 17); `create-release.yml` creates a GitHub Release on a release tag but publishes no artifacts. OSSRH (`s01`) is sunset; the Central Portal (`central.sonatype.com`) is the replacement and the `org.ical4j` namespace is already migrated there.

## Goals / Non-Goals

**Goals:**
- Publish signed release artifacts to Maven Central (Central Portal) automatically on a release tag.
- Migrate snapshot publishing to the Central Portal snapshot repository.
- Make GPG signing run in CI via an in-memory key.
- Keep release artifact publishing independent of GitHub Release creation.

**Non-Goals:**
- Changing module coordinates, group, or which subprojects are published.
- Changing the version scheme (axion-release stays).
- Manual Nexus/Portal UI steps (auto-release instead).
- Publishing the currently-commented-out modules.

## Decisions

### D1: Use `com.vanniktech.maven.publish` targeting the Central Portal
Adopt the plugin and configure `publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)` + `signAllPublications()`. Rationale: it speaks the Portal API natively, generates sources/javadoc jars, signs, uploads, and closes+releases in one task — collapsing the three gaps (dead host, never-run signing, manual release) into one well-maintained plugin. Alternatives considered: (a) `io.github.gradle-nexus.publish-plugin` against the OSSRH Staging API shim — rejected as riding a deprecated bridge; (b) raw `maven-publish` to the Portal + manual release — rejected, not automated.

### D2: Replace the hand-rolled publishing/signing config
Remove `publishing { publications/repositories }`, `signing {}`, and the manual `withJavadocJar()/withSourcesJar()`; port the POM metadata (name, description, url, licenses, scm, developers) into `mavenPublishing { pom { … } }`. Coordinates become `coordinates(group, project.name, version)` so each subproject publishes as `org.ical4j:<module>:<version>`. The plugin is added to the root `plugins {}` with `apply false` and applied inside `configure(subprojects)`. Rationale: a single source of truth; avoids duplicate-jar conflicts between the manual config and the plugin.

### D3: Separate, tag-triggered release workflow
Add `publish-release.yml` triggered by the same tag pattern as `create-release.yml` (`ical4j-integration-*`, excluding `*-pre`). It runs `./gradlew publishAndReleaseToMavenCentral`. It does not depend on, gate, or get gated by `create-release.yml` — both react to the tag independently. Rationale: matches the requested independence; a failed Central publish doesn't block the GitHub Release and vice versa.

### D4: In-memory GPG signing via env
Use vanniktech's in-memory signing: `ORG_GRADLE_PROJECT_signingInMemoryKey` (ASCII-armored private key) and `ORG_GRADLE_PROJECT_signingInMemoryKeyPassword`. Central Portal credentials via `ORG_GRADLE_PROJECT_mavenCentralUsername`/`Password` (Portal token). Rationale: no keyring files on the runner; vanniktech reads `ORG_GRADLE_PROJECT_*` natively, so no `-P` wiring.

### D5: Snapshot publishing on Central Portal
Update `publish-snapshots.yml` to run the plugin's publish task; SNAPSHOT versions route to the Central Portal snapshot repository automatically. Keep the `develop` push trigger and Java 17. Rationale: the old path targets a dead host; consolidating on one mechanism keeps release and snapshot publishing consistent.

### D6: Full tag history in CI
The publish job checks out with `fetch-depth: 0` so axion-release sees tags and resolves the release version. Rationale: a shallow clone yields a `-SNAPSHOT`/dirty version, which would mis-route the publish.

## Risks / Trade-offs

- **build.gradle rewrite breaks publishing** → keep coordinates/POM identical to the current output; verify with `./gradlew publishToMavenLocal` and inspect the generated POM/jars before wiring CI.
- **bnd OSGi jar not included** → vanniktech publishes the `java` component, which is the bnd-enhanced jar; confirm the published jar retains the OSGi manifest.
- **Signing misconfigured in CI** (bad/locked key) → validate the in-memory key locally first; signing failures fail the publish loudly rather than publishing unsigned.
- **Secrets missing/rotated** → publish job fails fast; document the four required secrets. Legacy `OSS_SONATYPE_*` left in place but unused (remove later).
- **Auto-release publishes a bad version** → mitigated by `-pre` tags being excluded; a release tag is intentional. `publishToMavenLocal` dry-run reduces surprise.

## Open Questions

- ~~Should the legacy `OSS_SONATYPE_*` secrets and OSSRH references be removed in this change or a follow-up?~~ **Resolved:** remove all OSSRH references (URLs, env wiring) from the build and workflows in this change; the unused `OSS_SONATYPE_*` repository secrets are deleted manually afterward (out of band).
- ~~Do we want a manual `workflow_dispatch` trigger on `publish-release.yml`?~~ **Resolved:** yes — `publish-release.yml` includes a `workflow_dispatch` trigger alongside the tag trigger for manual re-runs.
