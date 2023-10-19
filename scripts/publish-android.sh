#!/usr/bin/env bash

ANDROID_DIR=../
LOG_OUTPUT=./tmp/portals-android.txt
PORTALS_VERSION=`grep '"version": ' $ANDROID_DIR/package.json | awk '{print $2}' | tr -d '",'`

# Get latest io.ionic:portals XML version info
PORTALS_PUBLISHED_URL="https://repo1.maven.org/maven2/io/ionic/portals/maven-metadata.xml"
PORTALS_PUBLISHED_DATA=$(curl -s $PORTALS_PUBLISHED_URL)
PORTALS_PUBLISHED_VERSION="$(perl -ne 'print and last if s/.*<latest>(.*)<\/latest>.*/\1/;' <<< $PORTALS_PUBLISHED_DATA)"

if [[ "$PORTALS_VERSION" == "$PORTALS_PUBLISHED_VERSION" ]]; then
    printf %"s\n\n" "Duplicate: a published Portals exists for version $PORTALS_VERSION, skipping..."
else
    # Make log dir if doesnt exist
    mkdir -p ./tmp

    # Export ENV variable used by Gradle for Versioning
    export PORTALS_VERSION
    export PORTALS_PUBLISH=true

    printf %"s\n" "Attempting to build and publish Portals version $PORTALS_VERSION"
    $ANDROID_DIR/gradlew -p $ANDROID_DIR clean build publishReleasePublicationToSonatypeRepository closeAndReleaseSonatypeStagingRepository --max-workers 1 -Pandroid.useAndroidX=true -Pandroid.enableJetifier=true > $LOG_OUTPUT 2>&1

    echo $RESULT

    if grep --quiet "BUILD SUCCESSFUL" $LOG_OUTPUT; then
        printf %"s\n" "Success: Ionic Portals published to MavenCentral."
    else
        printf %"s\n" "Error publishing, check $LOG_OUTPUT for more info! Manually review and release from the Sonatype Repository Manager may be necessary https://s01.oss.sonatype.org/"
        cat $LOG_OUTPUT
        exit 1
    fi

fi