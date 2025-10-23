
# Quick GitHub upload steps
1. Create a new repo on GitHub named `AttendanceTracker`.
2. In the repo click **Add file → Upload files** and drag the `.java` files and `README.md`.
3. Commit changes.
4. Or use git:
```bash
git init
git add .
git commit -m "Initial commit - AttendanceTracker"
git branch -M main
git remote add origin https://github.com/your-username/AttendanceTracker.git
git push -u origin main
