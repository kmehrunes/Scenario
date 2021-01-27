#!/usr/bin/env bash

VERSION=0.1.0

echo "Creating bundle_build directory"

mkdir "bundle_build" || echo "bundle_build already exists"

echo "Copying all artifacts to bundle"

cp target/*.pom bundle_build/
cp target/*.asc bundle_build/

cp scenario/target/*.pom bundle_build/
cp scenario/target/*.jar bundle_build/
cp scenario/target/*.asc bundle_build/

echo "Switching to the bundle_build directory"

cd bundle_build || return 1

echo "Building the parent jar bundle"

jar -cvf scenario-parent-${VERSION}-bundle.jar \
          scenario-parent-pom-${VERSION}.pom \
          scenario-parent-pom-${VERSION}.pom.asc

echo "Parent jar content:"

jar tf scenario-parent-${VERSION}-bundle.jar

echo "Building the library jar bundle"

jar -cvf scenario-${VERSION}-bundle.jar \
          scenario-${VERSION}.pom scenario-${VERSION}.pom.asc \
          scenario-${VERSION}.jar scenario-${VERSION}.jar.asc \
          scenario-${VERSION}-javadoc.jar scenario-${VERSION}-javadoc.jar.asc \
          scenario-${VERSION}-sources.jar scenario-${VERSION}-sources.jar.asc

echo "Library jar content:"

jar tf scenario-${VERSION}-bundle.jar

echo "Going back to parent"

cd ..

echo "Done"
