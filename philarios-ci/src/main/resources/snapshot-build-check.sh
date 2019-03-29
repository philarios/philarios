#!/usr/bin/env bash

gradle check jacocoTestReport -PbintrayUser=${BINTRAY_USER} -PbintrayKey=${BINTRAY_KEY}