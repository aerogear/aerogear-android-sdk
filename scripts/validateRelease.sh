
# full path to scripts directory
__dirname="$(cd "$(dirname "$0")" && pwd)"

# gradle.properties file
gradle_properties=$__dirname/../gradle.properties

TAG=$TAG
TAG_SYNTAX='^[0-9]+\.[0-9]+\.[0-9]+(-.+)*$'

# VERSION_NAME property in gradle.properties
VERSION_NAME=$(cat $gradle_properties | grep VERSION_NAME | head -1 | awk -F '=' '{print $2}')

# VERSION_CODE property in gradle.properties
VERSION_CODE=$(cat $gradle_properties | grep VERSION_CODE | head -1 | awk -F '=' '{print $2}')

# validate tag against TAG_SYNTAX
if [[ "$(echo $TAG | grep -E $TAG_SYNTAX)" == "" ]]; then
  echo "tag $TAG is invalid. Must be in the format x.y.z or x.y.z-SOME_TEXT"
  exit 1
fi

if [[ "$(echo $VERSION_CODE | grep -E '[0-9]+')" == "" ]]; then
  echo "VERSION_CODE $VERSION_CODE is invalid, must be a number"
fi

# validate that TAG == VERSION_NAME in gradle.properties
if [[ $TAG != $VERSION_NAME ]]; then
  echo "tag $TAG is not the same as VERSION_NAME $VERSION_NAME found in gradle.properties"
  exit 1
fi


