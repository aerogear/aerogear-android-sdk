#!/usr/bin/env bash
echo "Development script to add example application to the repository"
mkdir example
git clone git@github.com:aerogear/android-showcase-template.git
cd android-showcase-template
cp -a app/. ../example
