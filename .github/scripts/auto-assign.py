#!/usr/bin/env python3
"""PR 변경 파일을 분석해 리뷰어 수를 결정하고 자동 배정한다."""

import json
import os
import random
import subprocess

import pathspec

REVIEWERS_POOL = ["thirfir", "Ddudduu", "elaus00", "Junhee8649"]
CRITICAL_PATHS_FILE = ".github/critical-paths"


def get_changed_files(repo: str, pr_number: str) -> list[str]:
    result = subprocess.run(
        [
            "gh", "api",
            f"repos/{repo}/pulls/{pr_number}/files",
            "--paginate",
            "--jq", ".[].filename",
        ],
        check=True, text=True, capture_output=True,
    )
    return [line for line in result.stdout.splitlines() if line]


def load_critical_patterns() -> list[str]:
    try:
        with open(CRITICAL_PATHS_FILE, encoding="utf-8") as f:
            return [
                line for line in f.read().splitlines()
                if line.strip() and not line.lstrip().startswith("#")
            ]
    except FileNotFoundError:
        return []


def is_critical(files: list[str], patterns: list[str]) -> bool:
    if not patterns or not files:
        return False
    spec = pathspec.PathSpec.from_lines("gitwildmatch", patterns)
    return any(spec.match_file(f) for f in files)


def pick_reviewers(author: str, count: int) -> list[str]:
    pool = [u for u in REVIEWERS_POOL if u != author]
    return random.sample(pool, min(count, len(pool)))


def assign_reviewers(repo: str, pr_number: str, reviewers: list[str]) -> None:
    if not reviewers:
        return
    subprocess.run(
        [
            "gh", "api",
            f"repos/{repo}/pulls/{pr_number}/requested_reviewers",
            "--method", "POST",
            "--input", "-",
        ],
        input=json.dumps({"reviewers": reviewers}),
        text=True, check=True,
    )


def assign_assignee(repo: str, pr_number: str, author: str) -> None:
    subprocess.run(
        [
            "gh", "api",
            f"repos/{repo}/issues/{pr_number}/assignees",
            "--method", "POST",
            "--input", "-",
        ],
        input=json.dumps({"assignees": [author]}),
        text=True, check=True,
    )


def post_comment(pr_number: str, critical: bool, reviewers: list[str]) -> None:
    label = "중대 변경" if critical else "일반 변경"
    required = 2 if critical else 1
    reviewer_str = ", ".join(f"@{r}" for r in reviewers) if reviewers else "(없음)"
    body = (
        f"**자동 리뷰어 배정** — {label}\n\n"
        f"- 필요 리뷰 수: **{required}명**\n"
        f"- 배정된 리뷰어: {reviewer_str}\n\n"
        f"_critical 경로 목록: `.github/critical-paths`_\n"
    )
    subprocess.run(
        ["gh", "pr", "comment", str(pr_number), "--body", body],
        check=True,
    )


def main() -> None:
    repo = os.environ["REPO"]
    pr_number = os.environ["PR_NUMBER"]
    author = os.environ["PR_AUTHOR"]

    files = get_changed_files(repo, pr_number)
    patterns = load_critical_patterns()
    critical = is_critical(files, patterns)
    count = 2 if critical else 1
    reviewers = pick_reviewers(author, count)

    print(f"changed_files={files}")
    print(f"critical={critical}")
    print(f"count={count}")
    print(f"reviewers={reviewers}")

    assign_reviewers(repo, pr_number, reviewers)
    assign_assignee(repo, pr_number, author)
    post_comment(pr_number, critical, reviewers)


if __name__ == "__main__":
    main()
