# GitHub Actions CI/CD Setup

GitHub Actions provides automated testing and deployment for your repository. No signup required - it's built into GitHub!

## Quick Start (30 seconds!)

**Your workflow is already set up!** Just push your code to GitHub and it will automatically run.

```bash
git add .github/workflows/ci.yml
git commit -m "Add GitHub Actions CI"
git push
```

Then go to your repository → **Actions** tab to see the build running.

## What Runs Automatically

### On Every Push & Pull Request:

```
✅ Run all unit tests (46 tests)
✅ Run integration tests
✅ Publish test results in PR
✅ Cache Maven dependencies
✅ Upload test reports
```

### On Main/Develop Branch Only:

```
✅ Build JAR artifact
✅ Store artifact for 7 days
```

## Available Workflows

### 1. `ci.yml` - Production (Currently Active)

**Best for:** Most projects

**Jobs:**
- `test` - Runs all unit tests
- `integration-test` - Runs repository integration tests
- `build` - Builds JAR (main/develop only)

**Features:**
- ✅ Separate test jobs for better organization
- ✅ Test results published in PR comments
- ✅ Artifacts stored for download
- ✅ Maven caching (faster builds)

### 2. `ci-simple.yml` - Minimal

**Best for:** Small projects or getting started

**Jobs:**
- `test` - Runs all tests in one job

**Features:**
- ✅ Simplest configuration
- ✅ Quick setup
- ✅ Good for small projects

**To use:**
```bash
mv .github/workflows/ci.yml .github/workflows/ci-production.yml.bak
mv .github/workflows/ci-simple.yml .github/workflows/ci.yml
```

### 3. `ci-coverage.yml` - With Code Coverage

**Best for:** Projects requiring coverage metrics

**Jobs:**
- `test-with-coverage` - Runs tests + generates coverage
- `check-coverage` - Validates 50% minimum
- `build` - Builds JAR (main/develop only)

**Features:**
- ✅ JaCoCo coverage reports
- ✅ Coverage displayed in PR comments
- ✅ Coverage threshold enforcement
- ✅ Optional Codecov integration

**To use:**
```bash
mv .github/workflows/ci.yml .github/workflows/ci-production.yml.bak
mv .github/workflows/ci-coverage.yml .github/workflows/ci.yml
```

## Viewing Results

### Test Results in Pull Requests

Test results are automatically posted as PR comments:

```
✅ 46 tests passed
⏱️  Completed in 1m 23s

Details:
• UserRepositoryIntegrationTest: 16 passed
• BookRepositoryIntegrationTest: 15 passed
• BorrowingRepositoryIntegrationTest: 15 passed
```

### Test Results in Actions Tab

1. Go to your repository
2. Click **Actions** tab
3. Click on any workflow run
4. View test results, logs, and artifacts

### Downloading Test Reports

1. Go to Actions → Select a workflow run
2. Scroll to **Artifacts** section
3. Download:
   - `test-reports` - Surefire HTML reports
   - `coverage-reports` - JaCoCo coverage HTML (if using coverage workflow)
   - `library-management-jar` - Built JAR file (main/develop only)

## Status Badge

Add a build status badge to your `README.md`:

```markdown
![CI](https://github.com/YOUR_USERNAME/LibraryManagementAPI/workflows/CI/badge.svg)
```

Replace `YOUR_USERNAME` with your GitHub username.

**With branch:**
```markdown
![CI](https://github.com/YOUR_USERNAME/LibraryManagementAPI/workflows/CI/badge.svg?branch=main)
```

**Custom style:**
```markdown
[![CI Status](https://github.com/YOUR_USERNAME/LibraryManagementAPI/workflows/CI/badge.svg)](https://github.com/YOUR_USERNAME/LibraryManagementAPI/actions)
```

## GitHub Actions vs CircleCI

### Why GitHub Actions is Better for This Project:

| Feature | GitHub Actions | CircleCI |
|---------|---------------|----------|
| **Setup** | Zero config - built into GitHub | Requires signup + OAuth |
| **Free Tier** | 2,000 min/month (private)<br>**Unlimited (public)** | 6,000 min/month |
| **Integration** | Native GitHub integration | Third-party service |
| **PR Comments** | Built-in test results | Requires setup |
| **Artifacts** | Free storage (7-90 days) | Limited storage |
| **Caching** | Built-in Maven cache | Requires configuration |
| **Speed** | ~1-2 minutes | ~1-2 minutes |
| **UI** | Integrated in GitHub | Separate dashboard |

**Winner:** GitHub Actions ✅

## Advanced Features

### Matrix Builds

Test on multiple Java versions:

```yaml
jobs:
  test:
    runs-on: ubuntu-latest
    strategy:
      matrix:
        java: [17, 21]
    steps:
      - uses: actions/setup-java@v4
        with:
          java-version: ${{ matrix.java }}
      - run: mvn test
```

### Scheduled Builds

Run tests daily:

```yaml
on:
  schedule:
    - cron: '0 0 * * *'  # Every day at midnight
```

### Manual Triggers

Add manual workflow dispatch:

```yaml
on:
  workflow_dispatch:  # Adds "Run workflow" button in Actions tab
```

### Conditional Steps

Run steps only on main branch:

```yaml
- name: Deploy
  if: github.ref == 'refs/heads/main'
  run: echo "Deploying..."
```

## Environment Variables

### Built-in Variables

Available by default:
- `${{ github.repository }}` - Repository name
- `${{ github.ref }}` - Branch reference
- `${{ github.sha }}` - Commit SHA
- `${{ github.actor }}` - User who triggered
- `${{ github.event_name }}` - Event type (push, pull_request)

### Custom Secrets

Add secrets for sensitive data:

1. Go to repository → Settings → Secrets and variables → Actions
2. Click **New repository secret**
3. Add name and value

**Use in workflow:**
```yaml
- name: Deploy
  env:
    API_KEY: ${{ secrets.API_KEY }}
  run: ./deploy.sh
```

## Troubleshooting

### Tests Pass Locally but Fail in Actions

**Common causes:**

1. **Timezone differences:**
   ```yaml
   - name: Set timezone
     run: |
       sudo timedatectl set-timezone America/New_York
   ```

2. **Missing environment variables:**
   Add to workflow:
   ```yaml
   env:
     SPRING_PROFILES_ACTIVE: test
   ```

3. **Database differences:**
   Tests use H2 (in-memory), ensure SQL compatibility

### Workflow Not Triggering

**Check:**
- File is in `.github/workflows/` directory
- File has `.yml` or `.yaml` extension
- YAML syntax is correct
- Branch matches `on.push.branches`

**Validate locally:**
```bash
# Install act (local GitHub Actions runner)
brew install act

# Run workflow locally
act push
```

### Cache Not Working

**Solution:**
Cache is automatic with `actions/setup-java@v4` using `cache: maven`.

**To verify:**
Look for in workflow logs:
```
Cache restored successfully
```

**To clear cache:**
Go to Actions → Caches → Delete specific cache

### Out of Memory

**Increase Maven heap:**
```yaml
- name: Run tests
  env:
    MAVEN_OPTS: -Xmx2048m
  run: mvn test
```

## Performance Optimization

### Current Performance

With Maven caching:
- **First run:** ~3-4 minutes (downloads dependencies)
- **Subsequent runs:** ~1-2 minutes (uses cache)

### Optimization Tips

1. **Use caching** (already enabled):
   ```yaml
   - uses: actions/setup-java@v4
     with:
       cache: maven
   ```

2. **Skip unnecessary downloads:**
   ```yaml
   - run: mvn test --offline
   ```

3. **Run jobs in parallel** (already configured):
   - `test` and `integration-test` run simultaneously
   - `build` waits for both to complete

## Cost Analysis

### Free Tier Limits

**Private repositories:**
- 2,000 minutes/month
- Your usage: ~2 min/build
- **Builds per month:** ~1,000 ✅

**Public repositories:**
- **Unlimited minutes** ✅

### Reducing Usage

If you hit limits:

1. **Run on fewer branches:**
   ```yaml
   on:
     push:
       branches: [ main ]  # Only main, not develop
   ```

2. **Skip on draft PRs:**
   ```yaml
   jobs:
     test:
       if: github.event.pull_request.draft == false
   ```

3. **Manual approval for builds:**
   ```yaml
   environment:
     name: production
     # Requires manual approval
   ```

## Examples

### Example 1: Notify on Failure

Send Slack notification when tests fail:

```yaml
- name: Notify on failure
  if: failure()
  uses: 8398a7/action-slack@v3
  with:
    status: ${{ job.status }}
    webhook_url: ${{ secrets.SLACK_WEBHOOK }}
```

### Example 2: Deploy to Heroku

Deploy on successful build:

```yaml
- name: Deploy to Heroku
  if: github.ref == 'refs/heads/main'
  uses: akhileshns/heroku-deploy@v3.12.14
  with:
    heroku_api_key: ${{ secrets.HEROKU_API_KEY }}
    heroku_app_name: "your-app-name"
    heroku_email: "your-email@example.com"
```

### Example 3: Create Release

Auto-create GitHub release:

```yaml
- name: Create Release
  uses: softprops/action-gh-release@v1
  if: startsWith(github.ref, 'refs/tags/')
  with:
    files: target/*.jar
```

## Migration from CircleCI

Already done! Just:

1. ✅ GitHub Actions configured
2. ✅ Similar workflow structure
3. ✅ Same features (tests, caching, artifacts)

**You can delete `.circleci/` directory:**
```bash
git rm -r .circleci/
git commit -m "Migrate from CircleCI to GitHub Actions"
```

## Support

- **GitHub Actions Docs:** https://docs.github.com/en/actions
- **Actions Marketplace:** https://github.com/marketplace?type=actions
- **Community Forum:** https://github.community/
- **This Project:** Create an issue in your repository

## Next Steps

1. ✅ Push code to GitHub (workflow runs automatically)
2. ✅ Add status badge to README.md
3. ✅ (Optional) Enable code coverage (`ci-coverage.yml`)
4. ✅ (Optional) Set up Codecov for coverage tracking
5. ✅ (Optional) Add Slack/Discord notifications

**You're all set! GitHub Actions will now run on every push and PR.** 🚀
