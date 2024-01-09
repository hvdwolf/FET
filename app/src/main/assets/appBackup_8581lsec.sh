#!/system/bin/sh

# Harry van der Wolf, V1.0, 03-01-2022
# Backup script for uis7862, uis8518 and sc9853i
# V1.1 replace 7z for zip
# V1.1.2 
#   - correct zip command to not add /oem as folder prefix in the zip
#   - use busybox to be able to use tee for parallel output and log to file
# V1.2 Back to 7zip -store links as links; more verbose output
# V1.3 The Fyt Extra Tool version

/storage/emulated/0/7zzs a -r -tzip -snh -snl -bb1 -mx=0 -p048a02243bb74474b25233bda3cd02f8 /storage/sdcard1/BACKUP/AllAppUpdate.bin /oem/*

rm -rf /storage/emulated/0/lsec_updatesh
rm -rf /storage/emulated/0/lsec6316update
rm -rf /storage/emulated/0/7zzs

##############################################################################
# You will now find on your usb-stick inside the folder BACKUP
# four files:
#    AllAppUpdate.bin
#    config.txt
#    updatecfg.txt
#    lsec6316update
#
# If you copy these four files with a 6316_1.zip
# to a clean USB-disk, you can always restore
# your uis8581 (SC9863a) unit.
