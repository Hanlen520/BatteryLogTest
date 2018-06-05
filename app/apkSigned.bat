@echo off
java -jar signapk.jar platform.x509.pem platform.pk8 build\outputs\apk\debug\app-debug.apk app-release-signapk.apk 
adb -d install -r app-release-signapk.apk
pause
@echo on
	