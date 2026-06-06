# Releasing

Artifacts are published to [Maven Central](https://central.sonatype.com/) via the
[vanniktech maven-publish plugin](https://github.com/vanniktech/gradle-maven-publish-plugin),
targeting the Sonatype **Central Portal** under the `org.ical4j` namespace.

## How it works

| Trigger | Workflow | Result |
| --- | --- | --- |
| Push to `develop` | `publish-snapshots.yml` | Publishes `-SNAPSHOT` artifacts to the Central Portal snapshot repository |
| Push tag `ical4j-integration-X.Y.Z` (not `-pre`) | `publish-release.yml` | Publishes signed release artifacts and **auto-releases** them to Maven Central |
| Push tag `ical4j-integration-X.Y.Z` (not `-pre`) | `create-release.yml` | Creates the GitHub Release (runs independently of the Central publish) |

`publish-release.yml` can also be run manually via **workflow_dispatch**.

The release version is derived from the tag by the
[axion-release](https://github.com/allegro/axion-release-plugin) plugin, so the publish
jobs check out with `fetch-depth: 0` to make tags available.

## Required repository secrets

| Secret | Purpose |
| --- | --- |
| `CENTRAL_PORTAL_USERNAME` | Central Portal token username (from the user token at central.sonatype.com → Account → Generate User Token) |
| `CENTRAL_PORTAL_PASSWORD` | Central Portal token password |
| `GPG_SIGNING_KEY` | ASCII-armored GPG **private** key used to sign artifacts |
| `GPG_SIGNING_PASSWORD` | Passphrase for the GPG key |

These are consumed by Gradle as `ORG_GRADLE_PROJECT_*` environment variables
(`mavenCentralUsername`, `mavenCentralPassword`, `signingInMemoryKey`,
`signingInMemoryKeyPassword`).

### Generating the ASCII-armored signing key

```sh
# list keys to find the key id
gpg --list-secret-keys --keyid-format=long

# export the private key in ASCII-armored form (this is the value for GPG_SIGNING_KEY)
gpg --armor --export-secret-keys <KEY_ID>

# publish the public key so Central can verify signatures
gpg --keyserver keyserver.ubuntu.com --send-keys <KEY_ID>
```

Paste the full block (including the `-----BEGIN/END PGP PRIVATE KEY BLOCK-----` lines)
into the `GPG_SIGNING_KEY` secret.

## Cutting a release

1. Ensure `develop` is green and ready.
2. Create and push a release tag, e.g. `git tag ical4j-integration-1.2.0 && git push origin ical4j-integration-1.2.0`.
3. `publish-release.yml` signs and publishes to Maven Central; `create-release.yml` drafts the GitHub Release.

## Verifying locally

```sh
./gradlew publishToMavenLocal
```

inspects the generated POM, jars (main + sources + javadoc), and signatures under
`~/.m2/repository/org/ical4j/`.
