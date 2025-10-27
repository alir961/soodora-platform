#!/bin/bash
set -euo pipefail

REPORT_PATH="$1"

if [[ ! -f "$REPORT_PATH" ]]; then
  echo "Error: Jacoco report file not found at $REPORT_PATH"
  exit 1
fi

echo -n "TOTAL COVERAGE = "
coverage=$(grep -o '<td class="ctr2">[0-9]*%</td>' "$REPORT_PATH" | head -n 1 | sed 's/.*>\([0-9]*\)%<.*/\1/')

if [[ -z "$coverage" ]]; then
  echo "Error: Could not extract coverage percentage."
  exit 1
fi

echo "$coverage%"
