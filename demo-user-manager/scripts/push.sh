#!/usr/bin/env bash
TARGET_ENV=$1
VERSION=$2
REGISTRY=ghcr.io/anotherkylefrom1994/spring-boot-practice

docker tag anotherkyle/user-manager:"$VERSION" "$REGISTRY"/user-manager:"$VERSION"
docker push "$REGISTRY"/user-manager:"$VERSION"
