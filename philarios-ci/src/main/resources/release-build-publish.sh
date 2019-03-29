#!/usr/bin/env bash

TAG=$(git --no-pager tag --sort=-taggerdate | head -n 1)
gradle bintrayUpload -Pversion=${TAG} -PbintrayUser=${BINTRAY_USER} -PbintrayKey=${BINTRAY_KEY}