#!/bin/bash
binFolder=$(dirname "$0")

(${binFolder}/idam-create-caseworker.sh ccd-import ccd.docker.default@hmcts.net Pa55word11 Default CCD_Docker)

(${binFolder}/idam-create-caseworker.sh caseworker,cft-audit-investigator auditor@gmail.com)
(${binFolder}/idam-create-caseworker.sh caseworker,cft-service-logs service@gmail.com)

