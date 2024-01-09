#!/system/bin/sh
# FET = Fyt Extra Tool
rm -rf /data/dalvik-cache/profiles/xyz.hvdw.fytextratool*
rm -rf /data/dalvik-cache/arm64/*xyz.hvdw.fytextratool*
rm -rf /oem/priv-app/xyz.hvdw.fytextratool*
cp -r /storage/sdcard1/_fet/* /oem/priv-app/
chown -R 0.0 /oem/priv-app/xyz.hvdw.fytextratool
chmod 755 /oem/priv-app/xyz.hvdw.fytextratool
chmod 644 /oem/priv-app/xyz.hvdw.fytextratool/fyt_extra_tool.apk


##################################
#   Keep the USB key plugged     #
#    After the green message     #
#   wait at least (5) seconds    #
#   before remove the USB key    #
#                                #
##################################
