#!/usr/bin/env bash
TOKEN=$1

docker run --rm \
  --env PRIVATE_TOKEN="$TOKEN" \
  -v ~/.m2:/root/.m2 \
  -v "$(pwd)":/usr/src/mymaven \
  -w /usr/src/mymaven \
  openjdk:17-slim-buster ./mvnw clean deploy -DskipTests
