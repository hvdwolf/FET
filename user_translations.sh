#!/bin/bash

# Micro script to prepare translation files for user translations

TRANS="../FET_user_translations"

mkdir -p ${TRANS}
rm -rf ${TRANS}/*
rsync -rP app/src/main/res/values* ${TRANS}/
rm -rf ${TRANS}/values-*dpi  ${TRANS}/values-night
rm -rf ${TRANS}/values/colors* ${TRANS}/values/dime* ${TRANS}/values/styl*

sed '/app_name/d' -i ${TRANS}/values/strings.xml
sed '/programmer/d' -i ${TRANS}/values/strings.xml
