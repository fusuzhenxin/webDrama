@echo off
setlocal
set concat_files=
for %%i in (%~1) do set "concat_files=!concat_files!file '%%i'%~nxi^
"
echo %concat_files% > concat.txt
ffmpeg -f concat -safe 0 -i concat.txt -c copy %2
del concat.txt
