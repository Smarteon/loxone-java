# Release Setup Guide

This document describes how to set up the automated release process for loxone-java.

## Prerequisites

Before you can publish releases, you need:

1. **Sonatype JIRA account** with permissions to publish to `cz.smarteon` group
2. **GPG key** for signing artifacts
3. **GitHub repository admin access** to configure secrets

## Step 1: Sonatype OSS Setup

If you don't already have access to publish to `cz.smarteon`:

1. Create a Sonatype JIRA account at https://issues.sonatype.org
2. Request access to the `cz.smarteon` group ID (or create a new ticket if this is the first time)
3. Wait for approval (usually takes 1-2 business days)

Reference: [Sonatype OSSRH Guide](https://central.sonatype.org/publish/publish-guide/)

## Step 2: GPG Key Setup

### Generate a GPG Key (if you don't have one)

```bash
# Generate a new key
gpg --gen-key

# Follow the prompts:
# - Use RSA and RSA
# - Key size: 4096 bits
# - Expiration: choose appropriate duration
# - Enter your name and email
# - Set a strong passphrase
```

### Export the GPG Key

```bash
# List your keys to find the key ID
gpg --list-secret-keys --keyid-format=long

# Export the private key (replace KEY_ID with your actual key ID)
gpg --export-secret-keys -a KEY_ID | base64 -w0 > signing-key.txt

# The signing-key.txt now contains your base64-encoded private key
```

### Publish Your Public Key

```bash
# Publish to key servers (replace KEY_ID with your actual key ID)
gpg --keyserver keyserver.ubuntu.com --send-keys KEY_ID
gpg --keyserver keys.openpgp.org --send-keys KEY_ID
```

Reference: [Working with PGP Signatures](https://central.sonatype.org/publish/requirements/gpg/)

## Step 3: Configure GitHub Secrets

Add the following secrets in your GitHub repository settings (**Settings** → **Secrets and variables** → **Actions**):

### Required Secrets

| Secret Name | Description | How to Obtain |
|------------|-------------|---------------|
| `OSS_USER` | Sonatype JIRA username | Your JIRA account username |
| `OSS_PASS` | Sonatype JIRA password | Your JIRA account password |
| `SIGNING_KEY` | GPG private key (base64) | Content of `signing-key.txt` from Step 2 |
| `SIGNING_PASS` | GPG key passphrase | The passphrase you set when creating the GPG key |
| `SMARTEON_GIT_TOKEN` | GitHub Personal Access Token | See below |

### Creating GitHub Personal Access Token

1. Go to GitHub **Settings** → **Developer settings** → **Personal access tokens** → **Tokens (classic)**
2. Click **Generate new token (classic)**
3. Select scopes:
   - `repo` (Full control of private repositories)
4. Click **Generate token**
5. Copy the token and add it as `SMARTEON_GIT_TOKEN` secret

## Step 4: Verify Setup

### Test Gradle Configuration

```bash
# Verify the build works
./gradlew clean build -x test

# Check version detection
./gradlew currentVersion

# Test signing configuration (will skip if not on a release version)
SIGNING_KEY="$(cat signing-key.txt)" SIGNING_PASS="your-passphrase" ./gradlew build
```

### Test Publishing (Optional)

```bash
# Publish to local Maven repository
./gradlew publishToMavenLocal

# Check the published artifacts
ls ~/.m2/repository/cz/smarteon/loxone-java/
```

## Step 5: First Release

Once all secrets are configured:

1. Go to **Actions** → **Loxone Java release**
2. Click **Run workflow**
3. Select `incrementPatch` for the first automated release
4. Click **Run workflow**

The workflow will:
- Create a new git tag (e.g., `2.11.1`)
- Build and test the project
- Sign all artifacts with your GPG key
- Publish to OSS Sonatype staging repository
- Automatically close and release the staging repository
- Artifacts will be available on Maven Central within ~10 minutes

## Troubleshooting

### Build Fails with "invalid signature"

- Verify `SIGNING_KEY` is base64-encoded correctly
- Ensure `SIGNING_PASS` matches your GPG key passphrase

### Publish Fails with "401 Unauthorized"

- Verify `OSS_USER` and `OSS_PASS` are correct
- Ensure your Sonatype account has permission for `cz.smarteon` group

### Tag Already Exists

- The release process will fail if the version tag already exists
- Delete the tag if needed: `git push --delete origin <tag-name>`

### Staging Repository Not Closing

- Check Sonatype Nexus UI: https://oss.sonatype.org/#stagingRepositories
- Look for validation errors in the "Activity" tab
- Common issues: missing signatures, invalid POM metadata

## Version Management

This project uses [axion-release](https://github.com/allegro/axion-release-plugin) for version management:

- Versions are derived from git tags
- Current version: `./gradlew currentVersion`
- When on a tagged commit: release version (e.g., `2.11.1`)
- When ahead of a tag: snapshot version (e.g., `2.11.2-SNAPSHOT`)

## Additional Resources

- [Axion Release Plugin Documentation](https://axion-release-plugin.readthedocs.io/)
- [Nexus Publish Plugin](https://github.com/gradle-nexus/publish-plugin)
- [Maven Central Publishing Guide](https://central.sonatype.org/publish/)
- [Sonatype Nexus Repository Manager](https://oss.sonatype.org/)
