# lilz

lilz (pronounced "lil-zee") is a program that plays tetris.  This was started as a programming exercise with the following goals:

* utilize and manipulate an existing Java program
* learn clojure
* get familiar with basic artificial intelligence concepts

## Installation

1. Install [Leiningen](https://github.com/technomancy/leiningen)
2. I used Per Cederberg's [Java Tetris](http://www.percederberg.net/software/tetris/index.html).  I was unable to find a public repository for the .jar file so I use a local version.  On Ubuntu, after installing Maven and downloading tetris.jar, the following command will satisfy Leiningen's tetris dependency:

    $ mvn install:install-file -DgroupId=net.percederberg -DartifactId=tetris -Dversion=1.2.0 -Dpackaging=jar -Dfile=/path/to/tetris.jar

3. From the lilz/ directory:

    $ lein deps

## Usage

The simple thing to do is created a standalone .jar file that includes all the dependencies.  This takes a longer time to build but allows for simple execution:

    $ lein uberjar
    $ java -jar lilz-*-standalone.jar

This is great for creating a distributable file but for development I prefer to create a .jar which only contains the lilz project and then link to the dependent libraries:

    $ lein jar
    $ java -cp $CLOJURE_EXT/clojure.jar:/path/to/tetris.jar:./lilz-*.jar lilz.core


## License

I don't know too much about licensing but [Java Tetris](http://www.percederberg.net/software/tetris/index.html) is distributed under the [GNU GPL](http://www.gnu.org/copyleft/gpl.html).  I hope I'm not violating anything by distributing this code under the same license.
