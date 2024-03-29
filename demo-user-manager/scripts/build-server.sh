#!/usr/bin/env bash
TARGET_ENV=$1
VERSION=$2
TOKEN=$3

DOCKER_BUILDKIT=1 docker build -t anotherkyle/user-manager:"$VERSION" -f docker/Dockerfile --build-arg CACHEBUST="$(date +%s)" --build-arg TARGET_ENV="$TARGET_ENV" --build-arg TOKEN="$TOKEN" .
