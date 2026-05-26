#!/bin/sh
set -eu

ROOT_DIR="$(git rev-parse --show-toplevel)"
cd "$ROOT_DIR"

TARGET_DIR="${1:-iosApp/iosApp}"

if [ ! -d "$TARGET_DIR" ]; then
  echo "ios-style-check: target directory not found: $TARGET_DIR"
  exit 1
fi

violations=0

for file in $(find "$TARGET_DIR" -type f -name '*.swift' | sort); do
  if grep -q 'UNDERSCORE_ALLOW' "$file"; then
    continue
  fi

  if ! awk -v file="$file" '
    function report(name) {
      if (name ~ /_Previews$/ || name ~ /_Preview$/) {
        return
      }
      printf "%s:%d: declaration name `%s` must use camelCase or UpperCamelCase. Add UNDERSCORE_ALLOW only for token/resource constants.\n", file, NR, name
      found = 1
    }

    /^[[:space:]]*\/\// {
      next
    }

    /UNDERSCORE_ALLOW/ {
      next
    }

    {
      line = $0
      gsub(/"([^"\\]|\\.)*"/, "\"\"", line)

      if (match(line, /(^|[^A-Za-z0-9_])(struct|class|enum|protocol|actor)[[:space:]]+[A-Za-z][A-Za-z0-9]*_[A-Za-z0-9_]*/)) {
        declaration = substr(line, RSTART, RLENGTH)
        sub(/^.*(struct|class|enum|protocol|actor)[[:space:]]+/, "", declaration)
        report(declaration)
      }

      if (match(line, /(^|[^A-Za-z0-9_])(func|var|let|case)[[:space:]]+[A-Za-z][A-Za-z0-9]*_[A-Za-z0-9_]*/)) {
        declaration = substr(line, RSTART, RLENGTH)
        sub(/^.*(func|var|let|case)[[:space:]]+/, "", declaration)
        report(declaration)
      }
    }

    END {
      exit found ? 1 : 0
    }
  ' "$file"; then
    violations=1
  fi
done

for file in $(find "$TARGET_DIR" -type f -name '*.swift' | sort); do
  if ! awk -v file="$file" '
    function basename(path) {
      sub(/^.*\//, "", path)
      return path
    }

    function count_char(text, char,    i, count) {
      count = 0
      for (i = 1; i <= length(text); i += 1) {
        if (substr(text, i, 1) == char) {
          count += 1
        }
      }
      return count
    }

    function scrub(text) {
      gsub(/"([^"\\]|\\.)*"/, "\"\"", text)
      sub(/\/\/.*/, "", text)
      return text
    }

    function is_content_context() {
      return file_is_content_view || content_depth > 0
    }

    function report(reason) {
      printf "%s:%d: ContentView must not directly own, observe, or receive a ViewModel (%s). Keep ViewModel ownership in FeatureView.\n", file, NR, reason
      found = 1
    }

    BEGIN {
      file_is_content_view = basename(file) ~ /ContentView\.swift$/
      depth = 0
      content_depth = 0
      pending_content_type = 0
    }

    /^[[:space:]]*\/\// {
      next
    }

    {
      line = scrub($0)

      if (match(line, /(^|[^A-Za-z0-9_])(struct|class|actor)[[:space:]]+[A-Za-z][A-Za-z0-9]*ContentView([^A-Za-z0-9_]|$)/)) {
        pending_content_type = 1
      }

      opens = count_char(line, "{")
      closes = count_char(line, "}")

      if (pending_content_type && opens > 0) {
        content_depth = depth + opens - closes
        pending_content_type = 0
      }

      if (is_content_context()) {
        if (match(line, /@(StateObject|ObservedObject|EnvironmentObject)[^:]*:[[:space:]]*[A-Za-z][A-Za-z0-9]*ViewModel([^A-Za-z0-9_]|$)/)) {
          report("SwiftUI ViewModel property wrapper")
        } else if (match(line, /@(StateObject|ObservedObject|EnvironmentObject).*ViewModel[[:space:]]*\(/)) {
          report("SwiftUI ViewModel property wrapper")
        } else if (match(line, /(^|[,([:space:]])[A-Za-z_][A-Za-z0-9_]*[[:space:]]*:[[:space:]]*[A-Za-z][A-Za-z0-9]*ViewModel([^A-Za-z0-9_]|$)/)) {
          report("ViewModel-typed property or parameter")
        }
      }

      depth += opens - closes

      if (content_depth > 0 && depth < content_depth) {
        content_depth = 0
      }
    }

    END {
      exit found ? 1 : 0
    }
  ' "$file"; then
    violations=1
  fi
done

if [ "$violations" -ne 0 ]; then
  echo
  echo "ios-style-check: underscore is reserved for token/resource constants."
  echo "ios-style-check: prefer lowerCamelCase or UpperCamelCase for Swift declarations."
  echo "ios-style-check: keep ViewModel ownership in FeatureView, not ContentView."
  exit 1
fi

echo "ios-style-check: passed."
