#!/usr/bin/env bash
TARGET_ENV=$1
VERSION=$2
TOKEN=$3

docker run --rm \
  --env PRIVATE_TOKEN="$TOKEN" \
  -v ~/.m2:/root/.m2 \
  -v "$(pwd)":/usr/src/mymaven \
  -w /usr/src/mymaven \
  openjdk:17-slim-buster ./mvnw clean install package -P client -DskipTests
