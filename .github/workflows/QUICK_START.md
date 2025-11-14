# GitHub Actions Quick Start

Get automated testing running in **30 seconds**! No signup required.

## ⚡ 30-Second Setup

```bash
# Your workflow is already configured!
# Just push to GitHub:

git add .github/workflows/ci.yml
git commit -m "Add GitHub Actions CI"
git push origin main
```

**That's it!** Go to your repository → **Actions** tab to watch it run.

## What Just Happened?

GitHub Actions will now automatically:

1. ✅ **Run on every push** to any branch
2. ✅ **Run on every pull request**
3. ✅ **Test your code** (all 46 tests)
4. ✅ **Report results** in PR comments
5. ✅ **Cache dependencies** for faster builds

## First Build

### Expected Timeline

```
0:00 - Checkout code           (5 seconds)
0:05 - Set up Java 17           (10 seconds)
0:15 - Download dependencies    (2-3 minutes) ⏱️
3:00 - Run tests                (30 seconds)
3:30 - Upload reports           (10 seconds)
✅ Done!                        (~4 minutes total)
```

### Next Builds (With Cache)

```
0:00 - Checkout code           (5 seconds)
0:05 - Set up Java 17           (10 seconds)
0:15 - Restore cache            (30 seconds) ⚡
0:45 - Run tests                (30 seconds)
1:15 - Upload reports           (10 seconds)
✅ Done!                        (~1.5 minutes total)
```

## Viewing Your Build

### In the Actions Tab

1. Go to your GitHub repository
2. Click **Actions** tab (top menu)
3. See your workflow runs

### In Pull Requests

Test results appear automatically in PR comments:

```
✅ All checks have passed
• test — 46 tests passed
• integration-test — 15 tests passed
• build — Build successful
```

## Understanding the Workflow

### What Runs When

```
Every push/PR:
├─ test job
│  └─ Runs all 46 tests
│
├─ integration-test job
│  └─ Runs repository tests
│
└─ build job (main/develop only)
   └─ Builds JAR file
```

### Jobs Run in Parallel

```
    test (1-2 min)
         ↓
    integration-test (1-2 min)
         ↓
    build (30 sec) ← Only on main/develop
```

## Adding a Status Badge

Show your build status in README.md:

```markdown
![CI](https://github.com/YOUR_USERNAME/LibraryManagementAPI/workflows/CI/badge.svg)
```

Replace `YOUR_USERNAME` with your GitHub username.

**Result:**

![CI](https://github.com/username/repo/workflows/CI/badge.svg) ← Shows build status

## Common Scenarios

### Scenario 1: Push to Feature Branch

```bash
git checkout -b feature/new-feature
git commit -m "Add new feature"
git push origin feature/new-feature
```

**What happens:**
- ✅ Tests run automatically
- ✅ Results shown in GitHub
- ❌ Build job DOES NOT run (not main/develop)

### Scenario 2: Create Pull Request

```bash
# Create PR on GitHub
```

**What happens:**
- ✅ Tests run on PR
- ✅ Results posted as PR comment
- ✅ PR shows ✅ or ❌ status
- ❌ Build job DOES NOT run (not merged yet)

### Scenario 3: Merge to Main

```bash
git checkout main
git merge feature/new-feature
git push origin main
```

**What happens:**
- ✅ Tests run
- ✅ Integration tests run
- ✅ Build job runs (creates JAR)
- ✅ JAR stored as artifact

## Downloading Build Artifacts

1. Go to **Actions** → Select a completed run
2. Scroll down to **Artifacts** section
3. Download available artifacts:
   - `test-reports` - Test HTML reports
   - `integration-test-reports` - Integration test reports
   - `library-management-jar` - Built JAR (main/develop only)

## Troubleshooting

### ❌ Build Failed: Tests Failed

**Check what failed:**
1. Click on the failed workflow
2. Click on the failed job
3. Expand the "Run tests" step
4. See which test failed

**Fix locally:**
```bash
mvn clean test
```

### ❌ Build Failed: Workflow Syntax Error

**Validate syntax:**
```bash
# Check YAML is valid
cat .github/workflows/ci.yml
```

**Common issues:**
- Incorrect indentation (use 2 spaces)
- Missing quotes around values
- Typos in action names

### ⚠️ Build is Slow

**First build:** 3-4 minutes is normal (downloading deps)
**Second build:** Should be ~1-2 minutes (cache working)

**If still slow:**
- Check cache is working (look for "Cache restored" in logs)
- Maven might be re-downloading dependencies

## Next Steps

### Optional Enhancements

1. **Add code coverage** (see `ci-coverage.yml`):
   ```bash
   mv .github/workflows/ci-coverage.yml .github/workflows/ci.yml
   ```

2. **Set up Codecov** for coverage tracking:
   - Sign up at codecov.io
   - Add `CODECOV_TOKEN` to GitHub secrets
   - Uncomment Codecov step in `ci-coverage.yml`

3. **Add notifications:**
   - Slack integration
   - Discord webhooks
   - Email on failure only

4. **Deploy on success:**
   - Heroku deployment
   - Docker image build
   - AWS/GCP deployment

## Cheat Sheet

### View Logs
```
Repository → Actions → Select run → Click job → View logs
```

### Re-run Failed Build
```
Failed run → Re-run all jobs button (top right)
```

### Cancel Running Build
```
Running build → Cancel workflow button (top right)
```

### Download Artifacts
```
Completed run → Scroll to Artifacts → Click to download
```

### View Test Results
```
Completed run → See test summary at top
Pull Request → See checks section
```

## Free Tier Limits

**Private repos:** 2,000 minutes/month
- Your usage: ~2 min/build
- **You can run ~1,000 builds/month** ✅

**Public repos:** **UNLIMITED** ✅

## Why GitHub Actions > CircleCI

For your project:

| Aspect | GitHub Actions | CircleCI |
|--------|----------------|----------|
| Setup | ✅ Zero config | ❌ Signup + OAuth |
| Cost | ✅ 2,000 min free | 6,000 min free |
| Integration | ✅ Native | ❌ Third-party |
| PR Comments | ✅ Built-in | ❌ Requires setup |
| Artifacts | ✅ Free | ⚠️ Limited |
| UI | ✅ In GitHub | ❌ Separate site |

**GitHub Actions is the clear winner!** ✅

## Success Checklist

After your first build:

- [ ] Workflow runs successfully
- [ ] All 46 tests pass
- [ ] Test results visible in Actions tab
- [ ] Cache works on second build (faster)
- [ ] Status badge shows in README (if added)
- [ ] Artifacts available for download

## What's Running?

Your current setup tests:

```
✅ 16 UserRepository tests
✅ 15 BookRepository tests
✅ 15 BorrowingRepository tests
─────────────────────────────
✅ 46 total tests
```

**All running automatically on every commit!** 🎉

## Get Help

- **Not working?** Check workflow logs in Actions tab
- **Syntax error?** Validate YAML indentation
- **Tests failing?** Run `mvn test` locally first
- **Need more?** Check [README.md](.github/workflows/README.md)

---

**You're all set! Push your code and watch the magic happen.** ✨
