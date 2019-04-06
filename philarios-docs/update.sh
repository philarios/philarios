#!/usr/bin/env bash
set -eux

cd "$(dirname $0)"

rm -rf ../../philarios.github.io/*
rm Gemfile.lock
docker run --rm -w $PWD -v $PWD:$PWD jekyll/jekyll jekyll build
mv ./_site/* ../../philarios.github.io

cd ../../philarios.github.io
git add -A
git commit -m "Update documentation"
git push