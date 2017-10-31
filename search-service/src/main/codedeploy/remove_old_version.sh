#!/bin/bash

FILE_NAME="/opt/search-service.jar"

if [ -f $FILE_NAME]
then
    rm $FILE_NAME
fi
