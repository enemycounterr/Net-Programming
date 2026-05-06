#!/bin/bash

function email_sender() {
  mvn exec:java -Dexec.mainClass="org.networks.subject8.practical8_1.HtmlEmailSenderApp"
}

"$@"