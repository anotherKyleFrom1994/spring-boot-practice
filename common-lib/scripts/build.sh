#!/usr/bin/env bash
SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" &>/dev/null && pwd)

docker run --rm \
  -v ~/.m2:/root/.m2 \
  -v "$(cd "$SCRIPT_DIR"/.. && pwd)":/usr/src/mymaven \
  -w /usr/src/mymaven \
  openjdk:17-slim-buster ./mvnw clean install package -Dparallel=all -DperCoreThreadCount=false -DthreadCount=16 -DskipTests
