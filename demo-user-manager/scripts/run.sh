#!/usr/bin/env bash
VERSION=$1
VERSION=$VERSION docker-compose up -d

docker run -p 8080:8080 --rm --link db:db anotherkyle/user-manager:$VERSION
