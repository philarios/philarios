#!/usr/bin/env bash

mkdir -p ~/test-results/junit/
find . -type f -regex ".*/build/test-results/.*xml" -exec cp {} ~/test-results/junit/ \;

find . -type d -maxdepth 1 -name "philarios-*" -exec bash -c "mkdir -p ~/reports/{} && cp -r {}/build/reports/tests/test ~/reports/{}/junit" \;
find . -type d -maxdepth 1 -name "philarios-*" -exec bash -c "mkdir -p ~/reports/{} && cp -r {}/build/reports/jacoco/test ~/reports/{}/jacoco" \;