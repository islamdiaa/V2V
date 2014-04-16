#!/bin/sh

PREFIX=/usr/lib
AVBIN_LIBRARY=libavbin.so.7

touch $PREFIX 2> /dev/null || echo "Insufficient priveleges; run sudo"
touch $PREFIX 2> /dev/null || exit 1

cp $AVBIN_LIBRARY $PREFIX/
ln -sf $AVBIN_LIBRARY $PREFIX/libavbin.so
chmod a+rx $PREFIX/$AVBIN_LIBRARY
/sbin/ldconfig
