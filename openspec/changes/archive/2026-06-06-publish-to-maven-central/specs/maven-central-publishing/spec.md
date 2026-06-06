## ADDED Requirements

### Requirement: Publish signed release artifacts to Maven Central

When a release tag is pushed, the system SHALL publish every included subproject's artifacts — the main jar, sources jar, and javadoc jar — to the Sonatype Central Portal under the `org.ical4j` namespace, GPG-signed, and SHALL automatically release them to Maven Central without a manual portal step. The release tag pattern SHALL match `ical4j-integration-*` and exclude pre-release tags (`ical4j-integration-*-pre`).

#### Scenario: Release tag triggers a signed Central publish
- **WHEN** a tag `ical4j-integration-X.Y.Z` is pushed
- **THEN** each published module is uploaded to the Central Portal as `org.ical4j:<module>:X.Y.Z` with sources, javadoc, and GPG signatures, and automatically released

#### Scenario: Pre-release tags do not publish
- **WHEN** a tag ending in `-pre` is pushed
- **THEN** no release publish to Maven Central occurs

### Requirement: Release publishing is independent of GitHub Release creation

The release publish SHALL run as its own workflow that neither depends on nor blocks the GitHub Release workflow. A failure in one SHALL NOT prevent the other from completing.

#### Scenario: GitHub Release still succeeds if Central publish fails
- **WHEN** the Central publish fails for a release tag
- **THEN** the GitHub Release workflow still creates the release

#### Scenario: Central publish still runs if the GitHub Release fails
- **WHEN** the GitHub Release workflow fails for a release tag
- **THEN** the Central publish workflow still runs

### Requirement: Publish snapshots to the Central Portal snapshot repository

When changes are pushed to the `develop` branch, the system SHALL publish the snapshot artifacts to the Central Portal snapshot repository (not the sunset OSSRH host).

#### Scenario: Develop push publishes a snapshot
- **WHEN** a commit is pushed to `develop` with a `-SNAPSHOT` version
- **THEN** the snapshot artifacts are published to the Central Portal snapshot repository

### Requirement: Publishing credentials and signing key are supplied via CI secrets

The system SHALL read the Central Portal token and the GPG signing key/passphrase from environment-provided configuration (`ORG_GRADLE_PROJECT_mavenCentralUsername`, `ORG_GRADLE_PROJECT_mavenCentralPassword`, `ORG_GRADLE_PROJECT_signingInMemoryKey`, `ORG_GRADLE_PROJECT_signingInMemoryKeyPassword`) and SHALL NOT hard-code them. A publish SHALL fail clearly when required credentials are absent.

#### Scenario: Missing credentials fail the publish
- **WHEN** a publish runs without the required Central Portal or signing configuration
- **THEN** the publish fails with an error rather than publishing unsigned or anonymous artifacts

### Requirement: Release version is derived from the tag

The published release version SHALL be the clean (non-SNAPSHOT) version derived from the release tag by the axion-release plugin. The publish job SHALL check out full tag history so the version resolves correctly.

#### Scenario: Tag resolves to a clean release version
- **WHEN** the publish job runs for tag `ical4j-integration-X.Y.Z`
- **THEN** the artifacts are published with version `X.Y.Z` (no `-SNAPSHOT` suffix)
