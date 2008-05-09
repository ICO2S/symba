Instructions for writing apt-based books are available here: http://maven.apache.org/doxia/references/apt-format.html

The default maven command for this directory is doxia:render-books, which means that if you are running "mvn install"
in the parent directory, an empty jar will be created for this directory, but NO books. To make the books, you need
to run "mvn" (without a lifecycle option) in either this directory or the parent directory.