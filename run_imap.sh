#!/bin/bash

function email_monitor() {
    mvn exec:java -Dexec.mainClass=org.networks.subject8.practical8_2.EmailMonitorApp
}

"$@"