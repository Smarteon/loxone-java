# Environment Variables and Secrets Summary

This document provides a quick reference for all environment variables and GitHub secrets required for the automated release process.

## GitHub Secrets (Required)

These must be configured in **Settings → Secrets and variables → Actions** in the GitHub repository:

### 1. OSS_USER
- **Purpose**: Sonatype OSS username for publishing to Maven Central
- **Type**: Sonatype JIRA account username
- **How to obtain**: Create account at https://issues.sonatype.org/
- **Example**: `your-jira-username`

### 2. OSS_PASS
- **Purpose**: Sonatype OSS password for authentication
- **Type**: Sonatype JIRA account password
- **How to obtain**: Password from your JIRA account
- **Security**: Keep this secret secure, rotate regularly

### 3. SIGNING_KEY
- **Purpose**: GPG private key for signing Maven artifacts
- **Type**: ASCII-armored GPG private key, base64 encoded
- **How to obtain**: 
  ```bash
  # WARNING: This exports your private key. Handle with extreme care!
  # The output file should be immediately added to GitHub secrets and then securely deleted.
  gpg --export-secret-keys -a YOUR_KEY_ID | base64 -w0 > signing-key.txt
  
  # After copying to GitHub secrets:
  shred -u signing-key.txt  # Linux - securely delete
  # rm -P signing-key.txt   # macOS - securely delete
  ```
- **Requirements**: 
  - Must be a valid GPG/PGP key
  - Public key must be published to key servers
  - Must be base64 encoded for storage
- **Security**: Never commit this to version control or expose in logs

### 4. SIGNING_PASS
- **Purpose**: Passphrase for the GPG signing key
- **Type**: String passphrase
- **How to obtain**: The passphrase you set when creating your GPG key
- **Security**: Store securely, never commit to repository

### 5. SMARTEON_GIT_TOKEN
- **Purpose**: GitHub Personal Access Token for pushing release tags
- **Type**: GitHub PAT with `repo` scope
- **How to obtain**: 
  1. GitHub Settings → Developer settings → Personal access tokens
  2. Generate new token (classic)
  3. Select `repo` scope
- **Permissions Required**: Full control of private repositories

## Local Development Environment Variables (Optional)

For local testing of the release and publish process:

### Environment Variables

```bash
# WARNING: Setting environment variables in shell exposes them in:
# - Shell history (use `set +o history` BEFORE entering commands)
# - Process lists (visible to other users via ps/top)
# - Parent shell environment
#
# Recommended secure approach:
# 1. Start a new shell session with history disabled: bash --noprofile --norc -c "set +o history; bash"
# 2. Or disable history before entering commands: set +o history
# 3. Use a .env file that's in .gitignore
# 4. Clear shell history after: history -c && history -w
# 5. Unset variables after use: unset OSS_USER OSS_PASS SIGNING_KEY SIGNING_PASS

# Sonatype credentials
export OSS_USER="your-jira-username"
export OSS_PASS="your-jira-password"

# GPG signing - RECOMMENDED: read from secure file instead of inline command
# First, create the key file (see RELEASE_CHECKLIST.md for secure creation)
export SIGNING_KEY="$(cat /secure/path/signing-key.txt)"
export SIGNING_PASS="your-gpg-passphrase"

# ALTERNATIVE (less secure - exposes key in process list during execution):
# export SIGNING_KEY="$(gpg --export-secret-keys -a YOUR_KEY_ID | base64 -w0)"
```

### Alternative: Gradle Properties

Instead of environment variables, you can use `~/.gradle/gradle.properties`:

```properties
# Sonatype credentials (legacy support - environment variables preferred)
ossUser=your-jira-username
ossPass=your-jira-password

# GPG signing (file-based approach)
signing.keyId=YOUR_KEY_ID
signing.password=your-gpg-passphrase
signing.secretKeyRingFile=/path/to/.gnupg/secring.gpg
```

**Note**: The environment variable approach (used in CI/CD) is preferred as it's more secure.

## Gradle Tasks Reference

### Version and Release Tasks

```bash
# Check current version (derived from git tags)
./gradlew currentVersion

# Create a new release (increment patch version)
./gradlew release -Prelease.versionIncrementer=incrementPatch

# Create a new release (increment minor version)
./gradlew release -Prelease.versionIncrementer=incrementMinor

# Create a new release (increment major version)
./gradlew release -Prelease.versionIncrementer=incrementMajor

# Dry run release (doesn't create tags)
./gradlew release -Prelease.dryRun
```

### Publishing Tasks

```bash
# Publish to local Maven repository (testing)
./gradlew publishToMavenLocal

# Publish to Sonatype (requires OSS_USER and OSS_PASS)
./gradlew publishToSonatype

# Close and release staging repository
./gradlew closeAndReleaseSonatypeStagingRepository

# Full publish workflow (what CI/CD does)
./gradlew publishToSonatype closeAndReleaseSonatypeStagingRepository
```

## Security Best Practices

1. **Never commit secrets** to the repository
2. **Rotate credentials regularly**, especially if they may have been exposed
3. **Use environment variables** in CI/CD instead of storing in files
4. **Limit access** to GitHub secrets to repository administrators only
5. **Enable 2FA** on your Sonatype JIRA account
6. **Back up your GPG key** in a secure location
7. **Use a strong passphrase** for your GPG key

## Troubleshooting

### Missing Secret Error
```
Error: Secret OSS_USER not found
```
**Solution**: Ensure all required secrets are configured in GitHub repository settings

### GPG Signing Failed
```
Error: Invalid signing key
```
**Solution**: 
- Verify SIGNING_KEY is base64 encoded
- Verify SIGNING_PASS matches the key passphrase
- Test locally: `echo $SIGNING_KEY | base64 -d | gpg --import`

### Publishing Unauthorized
```
401 Unauthorized
```
**Solution**:
- Verify OSS_USER and OSS_PASS are correct
- Ensure your account has access to `cz.smarteon` group ID
- Check if your Sonatype account is active

### Version Already Exists
```
Tag X.Y.Z already exists
```
**Solution**:
- The version tag already exists in git
- Either delete the tag or increment to the next version
- Check: `git tag -l` to list all tags

## Support

For issues with:
- **Gradle build**: Check project documentation
- **Sonatype/Maven Central**: https://issues.sonatype.org/
- **GPG keys**: https://www.gnupg.org/documentation/
- **GitHub Actions**: GitHub repository Issues

## References

- [Sonatype OSSRH Guide](https://central.sonatype.org/publish/publish-guide/)
- [Maven Central Requirements](https://central.sonatype.org/publish/requirements/)
- [GPG Key Management](https://central.sonatype.org/publish/requirements/gpg/)
- [Axion Release Plugin](https://axion-release-plugin.readthedocs.io/)
- [Nexus Publish Plugin](https://github.com/gradle-nexus/publish-plugin)
