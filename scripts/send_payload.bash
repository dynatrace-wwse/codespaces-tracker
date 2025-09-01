#!/bin/bash

#ENDPOINT_CODESPACES_TRACKER="http://localhost:8080/api/receive"
ENDPOINT_CODESPACES_TRACKER="https://codespaces-tracker.whydevslovedynatrace.com/api/receive"
CODESPACES_TRACKER_TOKEN=$(echo -n "ilovedynatrace" | base64)
RepositoryName="test-repo"
ERROR_COUNT=1
DURATION=10
INSTANTIATION_TYPE="local"
ARCH=$(arch)
CODESPACE_NAME="codespace_name"
DT_TENANT="dynatrace.com"

echo "sending payload to: $ENDPOINT_CODESPACES_TRACKER"
#curl -X POST $ENDPOINT_CODESPACES_TRACKER -H "Authorization: BadToken" -H "Content-Type: application/json"  -d '{"sender":"Alice","content":"Hello from Spring Boot!"}'

  curl -X POST $ENDPOINT_CODESPACES_TRACKER \
  -H "Content-Type: application/json" \
  -H "Authorization: $CODESPACES_TRACKER_TOKEN" \
  -d "{
  \"repository\": \"$GITHUB_REPOSITORY\",
  \"repository.name\": \"$RepositoryName\",
  \"codespace.errors\": \"$ERROR_COUNT\",
  \"codespace.creation\": \"$DURATION\",
  \"codespace.type\": \"$INSTANTIATION_TYPE\",
  \"codespace.arch\": \"$ARCH\",
  \"codespace.name\": \"$CODESPACE_NAME\",
  \"tenant\": \"$DT_TENANT\"
  }"
