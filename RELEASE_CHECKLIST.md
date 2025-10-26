# Release Setup Checklist

Use this checklist to ensure all requirements are met before using the automated release workflow.

## Pre-requisites Checklist

### ✓ Sonatype Account Setup
- [ ] Created Sonatype JIRA account at https://issues.sonatype.org/
- [ ] Requested access to `cz.smarteon` group ID (or verified existing access)
- [ ] Received approval from Sonatype (wait time: 1-2 business days)
- [ ] Can log in to https://oss.sonatype.org/ with JIRA credentials

### ✓ GPG Key Setup
- [ ] Generated GPG key (or have an existing one)
  ```bash
  gpg --gen-key
  ```
- [ ] Recorded key ID
  ```bash
  gpg --list-secret-keys --keyid-format=long
  ```
- [ ] Exported private key to base64
  ```bash
  # WARNING: This creates a file containing your private key.
  # Store it securely and delete after adding to GitHub secrets.
  # Never commit this file to version control.
  gpg --export-secret-keys -a KEY_ID | base64 -w0 > signing-key.txt
  
  # After copying to GitHub secrets, securely delete the file:
  # shred -u signing-key.txt  # On Linux
  # rm -P signing-key.txt      # On macOS
  ```
- [ ] Published public key to key servers
  ```bash
  gpg --keyserver keyserver.ubuntu.com --send-keys KEY_ID
  gpg --keyserver keys.openpgp.org --send-keys KEY_ID
  ```
- [ ] Saved passphrase securely

### ✓ GitHub Setup
- [ ] Have admin access to GitHub repository
- [ ] Created GitHub Personal Access Token with `repo` scope
- [ ] Configured all required GitHub Secrets:
  - [ ] `OSS_USER` - Sonatype username
  - [ ] `OSS_PASS` - Sonatype password
  - [ ] `SIGNING_KEY` - GPG key (base64 encoded)
  - [ ] `SIGNING_PASS` - GPG key passphrase
  - [ ] `SMARTEON_GIT_TOKEN` - GitHub PAT

## Verification Checklist

### ✓ Local Build Verification
- [ ] Project builds successfully
  ```bash
  ./gradlew clean build
  ```
- [ ] Current version is detected correctly
  ```bash
  ./gradlew currentVersion
  ```
- [ ] Can publish to local Maven repository
  ```bash
  ./gradlew publishToMavenLocal
  ```

### ✓ Publishing Configuration Verification
- [ ] Sonatype tasks are available (with OSS_USER and OSS_PASS set)
  ```bash
  OSS_USER=test OSS_PASS=test ./gradlew tasks --group=publishing
  ```
- [ ] Release tasks are available
  ```bash
  ./gradlew tasks --group=release
  ```

### ✓ Signing Configuration Verification
- [ ] Can import signing key locally
  ```bash
  echo "$SIGNING_KEY" | base64 -d | gpg --import
  ```
- [ ] Signing works (on non-SNAPSHOT version)
  ```bash
  # Create a test tag first
  git tag -a test-signing-1.0.0 -m "Test"
  SIGNING_KEY="..." SIGNING_PASS="..." ./gradlew build
  git tag -d test-signing-1.0.0
  ```

## GitHub Actions Workflow Verification

### ✓ Workflow File
- [ ] Workflow file exists: `.github/workflows/loxone-java-release.yml`
- [ ] YAML syntax is valid
- [ ] All required secrets are referenced in the workflow
- [ ] Workflow is visible in Actions tab

### ✓ First Release Test
- [ ] Navigate to Actions → Loxone Java release
- [ ] Workflow is available and can be manually triggered
- [ ] All required secrets are configured (workflow won't fail with missing secrets)

## Post-Setup Checklist

### ✓ Documentation
- [ ] Read RELEASE_SETUP.md for detailed instructions
- [ ] Read ENVIRONMENT_VARIABLES.md for secrets reference
- [ ] Understand version numbering (semantic versioning)
- [ ] Know how to trigger a release via GitHub Actions

### ✓ Team Communication
- [ ] Informed team about new release process
- [ ] Documented who has access to GitHub secrets
- [ ] Established process for key rotation
- [ ] Set up notifications for release workflow failures

## First Release Execution

When ready to perform the first automated release:

1. [ ] Ensure all checklist items above are complete
2. [ ] Go to Actions → Loxone Java release
3. [ ] Click "Run workflow"
4. [ ] Select version increment: `incrementPatch` (recommended for first run)
5. [ ] Click "Run workflow" button
6. [ ] Monitor the workflow execution
7. [ ] Verify artifacts appear in Sonatype staging repository
8. [ ] Verify artifacts are released to Maven Central (~10 minutes)
9. [ ] Verify tag is created in GitHub
10. [ ] Test downloading the artifact from Maven Central

## Troubleshooting Common Issues

### Issue: "Secret not found" in GitHub Actions
**Solution**: Verify all 5 secrets are configured in repository settings

### Issue: "401 Unauthorized" when publishing
**Solution**: 
- Verify OSS_USER and OSS_PASS are correct
- Check Sonatype account has access to cz.smarteon

### Issue: "Invalid signature" or signing fails
**Solution**:
- Verify SIGNING_KEY is base64 encoded
- Verify SIGNING_PASS matches the key passphrase
- Test key import locally

### Issue: "Tag already exists"
**Solution**:
- Tag already exists in repository
- Delete tag if needed: `git push --delete origin TAG_NAME`
- Or increment to next version

### Issue: Staging repository won't close
**Solution**:
- Check Nexus UI: https://oss.sonatype.org/#stagingRepositories
- Look for validation errors in Activity tab
- Common issues: missing signatures, invalid POM

## Maintenance Checklist

Perform these tasks periodically:

### Monthly
- [ ] Review GitHub Actions workflow runs for failures
- [ ] Check Maven Central for latest published versions
- [ ] Verify all secrets are still valid

### Quarterly  
- [ ] Rotate Sonatype password
- [ ] Review team access to secrets
- [ ] Update GitHub PAT if needed

### Annually
- [ ] Review and update GPG key if needed
- [ ] Audit release process documentation
- [ ] Update workflow dependencies (GitHub Actions versions)

## Support and Resources

- **Gradle Build Issues**: Check project README.md
- **Sonatype Issues**: https://issues.sonatype.org/
- **GPG Questions**: https://www.gnupg.org/documentation/
- **GitHub Actions**: Check workflow run logs

## Completion

Once all items are checked:
- ✅ Ready to perform automated releases
- ✅ Team is informed about the new process
- ✅ Documentation is complete and accessible
- ✅ First test release can be attempted

**Date Completed**: _______________
**Completed By**: _______________
**Verified By**: _______________
