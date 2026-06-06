## 1. Build: adopt vanniktech maven-publish

- [x] 1.1 Add `com.vanniktech.maven.publish` to the root `plugins {}` block with `apply false` (pin a Gradle 8.4-compatible version)
- [x] 1.2 In `configure(subprojects)`, apply the plugin and remove the manual `withJavadocJar()`/`withSourcesJar()` and the `apply plugin: 'maven-publish'` / `'signing'` lines it supersedes
- [x] 1.3 Replace the `publishing { publications/repositories }` and `signing {}` blocks with `mavenPublishing { publishToMavenCentral(CENTRAL_PORTAL, automaticRelease=true); signAllPublications(); coordinates(group, project.name, version) }`
- [x] 1.4 Port the POM metadata (name, description, url, licenses, scm, developers) into `mavenPublishing { pom { … } }`

## 2. Verify locally

- [x] 2.1 Run `./gradlew publishToMavenLocal` and confirm each module produces main + sources + javadoc jars
- [x] 2.2 Inspect the generated POM for each module: coordinates `org.ical4j:<module>`, plus name/description/url/licenses/scm/developers
- [x] 2.3 Confirm the published main jar retains the bnd OSGi manifest
- [x] 2.4 Validate the in-memory GPG signing config locally (signatures produced for a release version)

## 3. Release workflow

- [x] 3.1 Add `publish-release.yml` triggered by `ical4j-integration-*` tags (excluding `*-pre`), Java 17, `fetch-depth: 0`
- [x] 3.2 Run `./gradlew publishAndReleaseToMavenCentral`, wiring the four `ORG_GRADLE_PROJECT_*` secrets as env
- [x] 3.3 Ensure it is independent of `create-release.yml` (no `needs`, no shared gating)
- [x] 3.4 Add a `workflow_dispatch` trigger for manual re-runs

## 4. Snapshot workflow

- [x] 4.1 Update `publish-snapshots.yml` to run the vanniktech publish task (Central Portal snapshot repo) instead of `./gradlew build -x test publish` to OSSRH
- [x] 4.2 Replace the legacy `MAVEN_*`/`GPR_*` env with the `ORG_GRADLE_PROJECT_*` Central Portal credentials; keep the `develop` trigger and Java 17

## 5. Secrets & docs

- [x] 5.1 Document the four required repository secrets (Central Portal token user/pass, GPG key, GPG passphrase) and how to generate the ASCII-armored key
- [x] 5.2 Note in the README/release docs that releases publish to Maven Central on tag
- [x] 5.3 Remove all OSSRH references from the build and workflows (s01 URLs, legacy `MAVEN_*`/`OSS_SONATYPE_*` env wiring)
- [x] 5.4 Run `openspec validate publish-to-maven-central --strict`
